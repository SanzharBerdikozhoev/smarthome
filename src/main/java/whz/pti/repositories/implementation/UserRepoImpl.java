package whz.pti.repositories.implementation;

import whz.pti.models.User;
import whz.pti.repositories.UserRepo;
import whz.pti.utils.PasswordService;
import whz.pti.utils.annotations.Column;
import whz.pti.utils.annotations.ForeignKey;
import whz.pti.utils.annotations.ManyToMany;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class UserRepoImpl extends GeneralRepoImpl<User> implements UserRepo {

    public UserRepoImpl() {
        super("users");
    }

    @Override
    public User save(User user) {
        try {
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                String hashedPassword = PasswordService.hashPassword(user.getPassword());;
                user.setPassword(hashedPassword);
            }

            Map<String, Object> fields = unmapEntity(user);
            fields.remove("id");

            String columns = String.join(", ", fields.keySet());
            String placeholders = String.join(", ", fields.keySet().stream().map(c -> "?").toList());

            String sql = String.format("INSERT INTO users (%s) VALUES (%s)", columns, placeholders);

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
    public User updateById(User user, Long id) {
        try {
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                if (!user.getPassword().startsWith("$2a$")) {
                    String hashedPassword = PasswordService.hashPassword(user.getPassword());
                    user.setPassword(hashedPassword);
                }
            }

            Map<String, Object> fields = unmapEntity(user);
            fields.remove("id");

            StringBuilder setClause = new StringBuilder();
            fields.keySet().forEach(column -> setClause.append(column).append(" = ?, "));
            String setSql = setClause.substring(0, setClause.length() - 2);

            String sql = String.format("UPDATE users SET %s WHERE id = ?", setSql);

            try (Connection conn = dbManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                int index = 1;
                for (Object value : fields.values()) {
                    setStatementValue(stmt, index++, value);
                }

                stmt.setLong(index, id);

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    return getById(id).orElse(null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, Object> unmapEntity(User entity) throws Exception {
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
}