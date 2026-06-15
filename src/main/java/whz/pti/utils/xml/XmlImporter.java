package whz.pti.utils.xml;

import java.io.File;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import whz.pti.models.*;
import whz.pti.repositories.GeneralRepo;
import whz.pti.utils.AppContext;
import whz.pti.utils.annotations.Column;
import whz.pti.utils.annotations.ForeignKey;
import whz.pti.utils.annotations.ManyToMany;

/**
 * Importiert Entitäten aus einer XML-Datei in die Datenbank.
 *
 * Erwartetes XML-Format (Reihenfolge beachten, da FK-Abhängigkeiten bestehen):
 * <pre>{@code
 * <import>
 *   <user>
 *     <username>bob</username>
 *     <email>bob@example.com</email>
 *     <role>READER</role>
 *     <password_hash>passwort123</password_hash>
 *   </user>
 *   <home>
 *     <address>Hauptstr. 1</address>
 *     <town>Berlin</town>
 *     <zip_code>10115</zip_code>
 *     <user_id>1</user_id>
 *   </home>
 *   <room>
 *     <name>Wohnzimmer</name>
 *     <floor>Erdgeschoss</floor>
 *     <square>30.5</square>
 *     <home_id>1</home_id>
 *   </room>
 *   <deviceType>
 *     <name>Sensor</name>
 *     <description>Allgemeiner Sensor</description>
 *   </deviceType>
 *   <scenario>
 *     <name>Abendmodus</name>
 *     <description>Abendautomatisierung</description>
 *     <isActive>true</isActive>
 *     <start_time>18:00:00</start_time>
 *     <end_time>22:00:00</end_time>
 *   </scenario>
 * </import>
 * }</pre>
 */
public class XmlImporter {

    /**
     * Liest eine XML-Datei ein und importiert die enthaltenen Entitäten in die Datenbank.
     *
     * @param file die zu importierende XML-Datei
     * @throws Exception bei Parse- oder Datenbankfehlern
     */
    public static void importFromFile(File file) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        doc.getDocumentElement().normalize();

        AppContext context = AppContext.getInstance();

        // Reihenfolge einhalten, da FK-Abhängigkeiten bestehen
        Map<String, EntityImportConfig> configMap = new LinkedHashMap<>();
        configMap.put(
            "user",
            new EntityImportConfig(User.class, context.getUserRepo())
        );
        configMap.put(
            "home",
            new EntityImportConfig(Home.class, context.getHomeRepo())
        );
        configMap.put(
            "room",
            new EntityImportConfig(Room.class, context.getRoomRepo())
        );
        configMap.put(
            "deviceType",
            new EntityImportConfig(
                DeviceType.class,
                context.getDeviceTypeRepo()
            )
        );
        configMap.put(
            "scenario",
            new EntityImportConfig(Scenario.class, context.getScenarioRepo())
        );
        configMap.put(
            "device",
            new EntityImportConfig(Device.class, context.getDeviceRepo())
        );
        configMap.put(
            "deviceUser",
            new EntityImportConfig(
                DeviceUser.class,
                context.getDeviceUserRepo()
            )
        );
        configMap.put(
            "deviceScenario",
            new EntityImportConfig(
                DeviceScenario.class,
                context.getDeviceScenarioRepo()
            )
        );
        configMap.put(
            "deviceStateLog",
            new EntityImportConfig(
                DeviceStateLog.class,
                context.getDeviceStateLogRepo()
            )
        );

        for (Map.Entry<
            String,
            EntityImportConfig
        > entry : configMap.entrySet()) {
            String tag = entry.getKey();
            EntityImportConfig cfg = entry.getValue();
            NodeList nodes = doc.getElementsByTagName(tag);

            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE) continue;
                Element element = (Element) node;
                importEntity(element, cfg.entityClass, cfg.repo);
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void importEntity(
        Element element,
        Class<?> entityClass,
        GeneralRepo repo
    ) throws Exception {
        Object entity = entityClass.getDeclaredConstructor().newInstance();

        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ManyToMany.class)) continue;
            field.setAccessible(true);

            String columnName = resolveColumnName(field);
            NodeList children = element.getElementsByTagName(columnName);
            if (children.getLength() == 0) continue;

            String textValue = children.item(0).getTextContent().trim();
            if (textValue.isEmpty()) continue;

            if (field.isAnnotationPresent(ForeignKey.class)) {
                ForeignKey fk = field.getAnnotation(ForeignKey.class);
                GeneralRepo<?> relatedRepo = resolveRepo(fk.repoClass());
                if (relatedRepo != null) {
                    relatedRepo
                        .getById(Long.parseLong(textValue))
                        .ifPresent(ref -> {
                            try {
                                field.set(entity, ref);
                            } catch (Exception ignored) {}
                        });
                }
            } else {
                setFieldValue(field, entity, textValue);
            }
        }

        repo.save(entity);
    }

    private static String resolveColumnName(Field field) {
        if (field.isAnnotationPresent(ForeignKey.class)) {
            return field.getAnnotation(ForeignKey.class).column();
        }
        if (field.isAnnotationPresent(Column.class)) {
            return field.getAnnotation(Column.class).name();
        }
        return field.getName();
    }

    private static void setFieldValue(Field field, Object entity, String text)
        throws Exception {
        Class<?> type = field.getType();
        if (type == String.class) {
            field.set(entity, text);
        } else if (type == Long.class || type == long.class) {
            field.set(entity, Long.parseLong(text));
        } else if (type == Integer.class || type == int.class) {
            field.set(entity, Integer.parseInt(text));
        } else if (type == Double.class || type == double.class) {
            field.set(entity, Double.parseDouble(text));
        } else if (type == Boolean.class || type == boolean.class) {
            field.set(entity, Boolean.parseBoolean(text));
        } else if (type == LocalDate.class) {
            field.set(entity, LocalDate.parse(text));
        } else if (type == LocalTime.class) {
            field.set(entity, LocalTime.parse(text));
        } else if (type == LocalDateTime.class) {
            field.set(entity, LocalDateTime.parse(text.replace(" ", "T")));
        } else if (type.isEnum()) {
            field.set(entity, Enum.valueOf((Class<Enum>) type, text));
        }
    }

    private static GeneralRepo<?> resolveRepo(Class<?> repoClass) {
        AppContext context = AppContext.getInstance();
        String name = repoClass.getSimpleName();
        if (name.contains("UserRepo")) return context.getUserRepo();
        if (name.contains("HomeRepo")) return context.getHomeRepo();
        if (name.contains("RoomRepo")) return context.getRoomRepo();
        if (name.contains("DeviceTypeRepo")) return context.getDeviceTypeRepo();
        if (name.contains("ScenarioRepo")) return context.getScenarioRepo();
        if (name.contains("DeviceRepo")) return context.getDeviceRepo();
        return null;
    }

    private static class EntityImportConfig {

        final Class<?> entityClass;
        final GeneralRepo<?> repo;

        EntityImportConfig(Class<?> entityClass, GeneralRepo<?> repo) {
            this.entityClass = entityClass;
            this.repo = repo;
        }
    }
}
