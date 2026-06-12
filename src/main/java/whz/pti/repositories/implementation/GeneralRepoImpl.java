package whz.pti.repositories.implementation;

import whz.pti.repositories.GeneralRepo;
import whz.pti.utils.DBConnection;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.*;

public class GeneralRepoImpl<T> implements GeneralRepo<T> {
    protected final DBConnection dbManager = DBConnection.getInstance();
    protected final Class<T> clazz;
    protected final String tableName;

    @SuppressWarnings("unchecked")
    public GeneralRepoImpl() {
        this.clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
        this.tableName = clazz.getSimpleName().toLowerCase();
    }

    @SuppressWarnings("unchecked")
    public GeneralRepoImpl(String tableName) {
        this.clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
        this.tableName = tableName;
    }

    @Override
    public Optional<T> getById(Long id) {
        String sql = String.format("SELECT * FROM %s WHERE id = ?", tableName);

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Optional<T> getByField(String field, Object value) {
        // Конвертируем имя поля Java в snake_case на случай, если передан camelCase
        String sqlColumn = field.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
        String sql = String.format("SELECT * FROM %s WHERE %s = ?", tableName, sqlColumn);

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (value instanceof String) {
                stmt.setString(1, (String) value);
            } else if (value instanceof Long) {
                stmt.setLong(1, (Long) value);
            } else if (value instanceof Integer) {
                stmt.setInt(1, (Integer) value);
            } else if (value instanceof Boolean) {
                stmt.setBoolean(1, (Boolean) value);
            } else {
                stmt.setObject(1, value);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Iterable<T> getAll() {
        List<T> resultList = new ArrayList<>();
        String sql = String.format("SELECT * FROM %s", tableName);

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                resultList.add(mapRow(rs));
            }
            return resultList;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    @Override
    public Iterable<T> getPage(int page, int pageSize) {
        List<T> resultList = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        // В MS SQL Server для смещения обязательно использовать ORDER BY
        String sql = String.format("SELECT * FROM %s ORDER BY id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY", tableName);

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, offset);
            stmt.setInt(2, pageSize);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    resultList.add(mapRow(rs));
                }
                return resultList;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    @Override
    public T save(T entity) {
        try {
            Map<String, Object> fields = unmapEntity(entity);
            fields.remove("id"); // Первичный ключ генерируется в БД (IDENTITY)

            String columns = String.join(", ", fields.keySet());
            String placeholders = String.join(", ", fields.keySet().stream().map(c -> "?").toList());

            String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, placeholders);

            try (Connection conn = dbManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                int index = 1;
                for (Object value : fields.values()) {
                    stmt.setObject(index++, value);
                }

                stmt.executeUpdate();

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long generatedId = generatedKeys.getLong(1);
                        return getById(generatedId).orElse(null);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Iterable<T> saveAll(Iterable<T> entities) {
        if (entities == null) return Collections.emptyList();

        Iterator<T> iterator = entities.iterator();
        if (!iterator.hasNext()) {
            return Collections.emptyList();
        }

        try {
            T firstEntity = iterator.next();
            Map<String, Object> firstFields = unmapEntity(firstEntity);
            firstFields.remove("id");

            String columns = String.join(", ", firstFields.keySet());
            String placeholders = String.join(", ", firstFields.keySet().stream().map(c -> "?").toList());
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, placeholders);

            List<Long> generatedIds = new ArrayList<>();

            try (Connection conn = dbManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                conn.setAutoCommit(false);

                addEntityToBatch(stmt, firstEntity);

                while (iterator.hasNext()) {
                    addEntityToBatch(stmt, iterator.next());
                }

                stmt.executeBatch();

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    while (generatedKeys.next()) {
                        generatedIds.add(generatedKeys.getLong(1));
                    }
                }

                conn.commit();
                conn.setAutoCommit(true);

            } catch (SQLException e) {
                e.printStackTrace();
                return Collections.emptyList();
            }

            List<T> savedEntities = new ArrayList<>();
            for (Long id : generatedIds) {
                getById(id).ifPresent(savedEntities::add);
            }

            return savedEntities;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    @Override
    public T update(T newEntity, T entityToUpdate) {
        try {
            // Извлекаем новые данные для обновления
            Map<String, Object> fields = unmapEntity(newEntity);

            // Получаем ID из старого объекта
            Map<String, Object> oldFields = unmapEntity(entityToUpdate);
            Object idValue = oldFields.get("id");

            if (idValue == null) {
                throw new IllegalArgumentException("Es ist nicht möglich, ein Objekt ohne ID zu aktualisieren");
            }

            fields.remove("id");

            String setClause = String.join(", ", fields.keySet().stream().map(c -> c + " = ?").toList());
            String sql = String.format("UPDATE %s SET %s WHERE id = ?", tableName, setClause);

            try (Connection conn = dbManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                int index = 1;
                for (Object value : fields.values()) {
                    stmt.setObject(index++, value);
                }

                stmt.setObject(index, idValue);

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    return getById((Long) idValue).orElse(null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public T updateById(T newEntity, Long id) {
        if (newEntity == null || id == null) {
            return null;
        }

        try {
            Map<String, Object> fieldsToUpdate = unmapEntity(newEntity);
            fieldsToUpdate.remove("id");

            if (fieldsToUpdate.isEmpty()) {
                return null;
            }

            String setClause = String.join(", ", fieldsToUpdate.keySet().stream().map(c -> c + " = ?").toList());
            String sql = String.format("UPDATE %s SET %s WHERE id = ?", tableName, setClause);

            try (Connection conn = dbManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                int index = 1;
                for (Object value : fieldsToUpdate.values()) {
                    stmt.setObject(index++, value);
                }

                stmt.setLong(index, id);

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    return getById(id).orElse(null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void delete(Object entity) {
        if (entity == null) {
            return;
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> fields = unmapEntity((T) entity);
            Object idValue = fields.get("id");

            if (idValue instanceof Long) {
                deleteById((Long) idValue);
            } else if (idValue instanceof Integer) {
                deleteById(((Integer) idValue).longValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            return;
        }

        String sql = String.format("DELETE FROM %s WHERE id = ?", tableName);

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("Warnung: Der Eintrag mit der ID " + id + " in der Tabelle " + tableName + " wurde nicht gefunden.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Löschen des Datensatzes. Möglicherweise ist dieser mit anderen Daten verknüpft.", e);
        }
    }

    private void addEntityToBatch(PreparedStatement stmt, T entity) throws Exception {
        Map<String, Object> fields = unmapEntity(entity);
        fields.remove("id");

        int index = 1;
        for (Object value : fields.values()) {
            stmt.setObject(index++, value);
        }
        stmt.addBatch();
    }

    private T mapRow(ResultSet rs) throws Exception {
        T entity = clazz.getDeclaredConstructor().newInstance();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            String javaFieldName = field.getName();
            String sqlColumnName = javaFieldName.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();

            try {
                Object value = rs.getObject(sqlColumnName);

                if (value != null) {
                    if (field.getType() == Long.class && value instanceof Integer) {
                        field.set(entity, ((Integer) value).longValue());
                    } else if (field.getType() == UUID.class && value instanceof String) {
                        field.set(entity, UUID.fromString((String) value));
                    } else {
                        field.set(entity, value);
                    }
                }
            } catch (SQLException e) {
                // Игнорируем поля, которых нет в ResultSet (например, кастомные DTO поля)
            }
        }
        return entity;
    }

    private Map<String, Object> unmapEntity(T entity) throws Exception {
        Map<String, Object> fieldsMap = new LinkedHashMap<>();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            String javaFieldName = field.getName();
            String sqlColumnName = javaFieldName.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();

            fieldsMap.put(sqlColumnName, field.get(entity));
        }

        return fieldsMap;
    }
}
