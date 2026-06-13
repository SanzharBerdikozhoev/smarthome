package whz.pti.repositories.implementation;

import whz.pti.repositories.GeneralRepo;
import whz.pti.utils.DBConnection;
import whz.pti.utils.annotations.Column;
import whz.pti.utils.annotations.ForeignKey;
import whz.pti.utils.annotations.ManyToMany;

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
            fields.remove("id");

            String columns = String.join(", ", fields.keySet());
            String placeholders = String.join(", ", fields.keySet().stream().map(c -> "?").toList());

            String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, placeholders);

            try (Connection conn = dbManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                int index = 1;
                for (Object value : fields.values()) {
                    setStatementValue(stmt, index++, value);
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
            Map<String, Object> fields = unmapEntity(newEntity);

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
                    setStatementValue(stmt, index++, value);
                }

                setStatementValue(stmt, index, idValue);

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
                    setStatementValue(stmt, index++, value);
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
            setStatementValue(stmt, index++, value);
        }
        stmt.addBatch();
    }

    private void setStatementValue(PreparedStatement stmt, int index, Object value) throws SQLException {
        if (value == null) {
            stmt.setObject(index, null);
        } else if (value instanceof String stringValue) {
            stmt.setString(index, stringValue);
        } else if (value instanceof Long longValue) {
            stmt.setLong(index, longValue);
        } else if (value instanceof Integer integerValue) {
            stmt.setInt(index, integerValue);
        } else if (value instanceof Boolean booleanValue) {
            stmt.setBoolean(index, booleanValue);
        } else if (value instanceof Enum<?> enumValue) {
            stmt.setString(index, enumValue.name());
        } else if (value instanceof java.time.LocalDate localDateValue) {
            stmt.setDate(index, java.sql.Date.valueOf(localDateValue));
        } else if (value instanceof java.time.LocalDateTime localDateTimeValue) {
            stmt.setTimestamp(index, java.sql.Timestamp.valueOf(localDateTimeValue));
        } else if (value instanceof java.time.LocalTime localTimeValue) {
            stmt.setTime(index, java.sql.Time.valueOf(localTimeValue));
        } else if (value instanceof UUID uuidValue) {
            stmt.setString(index, uuidValue.toString());
        } else {
            stmt.setObject(index, value);
        }
    }

    private T mapRow(ResultSet rs) throws Exception {
        T entity = clazz.getDeclaredConstructor().newInstance();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            String javaFieldName = field.getName();
            Column columnAnnotation = field.getAnnotation(Column.class);
            String sqlColumnName = (columnAnnotation != null)
                    ? columnAnnotation.name()
                    : javaFieldName.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();

            ForeignKey fk = field.getAnnotation(ForeignKey.class);
            if (fk != null) {
                try {
                    Object fkValue = rs.getObject(fk.column());
                    if (fkValue != null) {
                        Long fkId = fkValue instanceof Integer
                                ? ((Integer) fkValue).longValue()
                                : (Long) fkValue;

                        Object relatedInstance =fk.repoClass()
                                        .getDeclaredConstructor()
                                        .newInstance();
                        if (relatedInstance instanceof GeneralRepo<?> relatedRepo) {
                            relatedRepo.getById(fkId).ifPresent(relate->{
                                try {
                                    field.set(entity, relate);
                                }catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                } catch (SQLException ignored) {}
                continue;
            }

            try {
                Object value = rs.getObject(sqlColumnName);

                if (value != null) {
                    if (field.getType() == Long.class && value instanceof Integer) {
                        field.set(entity, ((Integer) value).longValue());
                    } else if (field.getType() == UUID.class && value instanceof String) {
                        field.set(entity, UUID.fromString((String) value));
                    } else if (field.getType().isEnum() && value instanceof String) {
                        @SuppressWarnings("unchecked")
                        Class<Enum> enumType = (Class<Enum>) field.getType();

                        String dbValue = ((String) value).toUpperCase().trim();

                        try {
                            Enum enumValue = Enum.valueOf(enumType, dbValue);
                            field.set(entity, enumValue);
                        } catch (IllegalArgumentException e) {
                            System.err.println("Warnung: Unbekannter Enum-Wert in DB: " + dbValue + " für Feld " + javaFieldName);
                            field.set(entity, null);
                        }
                    } else if (field.getType() == java.time.LocalTime.class && value instanceof java.sql.Time) {
                        field.set(entity, ((java.sql.Time) value).toLocalTime());
                    } else if (field.getType() == java.time.LocalDateTime.class && value instanceof java.sql.Timestamp) {
                        field.set(entity, ((java.sql.Timestamp) value).toLocalDateTime());
                    } else if (field.getType() == java.time.LocalDate.class && value instanceof java.sql.Date) {
                        field.set(entity, ((java.sql.Date) value).toLocalDate());
                    } else {
                        field.set(entity, value);
                    }
                }
            } catch (SQLException e) {

            }
        }
        return entity;
    }

    private Map<String, Object> unmapEntity(T entity) throws Exception {
        Map<String, Object> fieldsMap = new LinkedHashMap<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ManyToMany.class)) {
                continue;
            }
            field.setAccessible(true);

            if (field.isAnnotationPresent(ForeignKey.class)) {
                ForeignKey fk = field.getAnnotation(ForeignKey.class);
                Object related = field.get(entity);
                if (related != null) {
                    Field idField = related.getClass().getDeclaredField("id");
                    idField.setAccessible(true);
                    fieldsMap.put(fk.column(), idField.get(related));
                } else {
                    fieldsMap.put(fk.column(), null);
                }
                continue;
            }

            String javaFieldName = field.getName();
            Column columnAnnotation = field.getAnnotation(Column.class);
            String sqlColumnName = (columnAnnotation != null)
                    ? columnAnnotation.name()
                    : javaFieldName.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();

            fieldsMap.put(sqlColumnName, field.get(entity));
        }

        return fieldsMap;
    }

    private List<Object> fetchManyToMany(
            String joinTable,
            String joinColumn,
            String inverseColumn,
            Class<?> repoClass,
            Long currentId
    ) {
        List<Object> result = new ArrayList<>();

        String sql = String.format(
                "SELECT %s FROM %s WHERE %s = ?",
                inverseColumn, joinTable, joinColumn
        );

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, currentId);

            try (ResultSet rs = stmt.executeQuery()) {
                Object repoInstance = repoClass.getDeclaredConstructor().newInstance();

                if (repoInstance instanceof GeneralRepo<?> relatedRepo) {
                    while (rs.next()) {
                        Long relatedId = rs.getLong(1);
                        relatedRepo.getById(relatedId).ifPresent(result::add);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
