package whz.pti.utils.xml;

import java.io.File;
import java.lang.reflect.Field;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import whz.pti.models.*;
import whz.pti.utils.AppContext;
import whz.pti.utils.annotations.Column;
import whz.pti.utils.annotations.ForeignKey;
import whz.pti.utils.annotations.ManyToMany;

public class XmlExporter {

    /** Exportiert eine einzelne Tabelle als XML-Datei. */
    public static void export(
        Iterable<?> data,
        Class<?> entityClass,
        File outputFile
    ) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        Element root = doc.createElement("export");
        root.setAttribute("table", entityClass.getSimpleName());
        doc.appendChild(root);

        String entityTag =
            Character.toLowerCase(entityClass.getSimpleName().charAt(0)) +
            entityClass.getSimpleName().substring(1);

        for (Object entity : data) {
            root.appendChild(
                buildEntityElement(doc, entity, entityClass, entityTag)
            );
        }

        writeToFile(doc, outputFile);
    }

    /** Exportiert alle Tabellen der Datenbank in eine einzige XML-Datei. */
    public static void exportDatabase(AppContext ctx, File outputFile)
        throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        Element root = doc.createElement("database");
        doc.appendChild(root);

        appendSection(
            doc,
            root,
            "users",
            "user",
            ctx.getUserRepo().getAll(),
            User.class
        );
        appendSection(
            doc,
            root,
            "rooms",
            "room",
            ctx.getRoomRepo().getAll(),
            Room.class
        );
        appendSection(
            doc,
            root,
            "homes",
            "home",
            ctx.getHomeRepo().getAll(),
            Home.class
        );
        appendSection(
            doc,
            root,
            "scenarios",
            "scenario",
            ctx.getScenarioRepo().getAll(),
            Scenario.class
        );
        appendSection(
            doc,
            root,
            "devices",
            "device",
            ctx.getDeviceRepo().getAll(),
            Device.class
        );
        appendSection(
            doc,
            root,
            "deviceScenarios",
            "deviceScenario",
            ctx.getDeviceScenarioRepo().getAll(),
            DeviceScenario.class
        );
        appendSection(
            doc,
            root,
            "deviceStateLogs",
            "deviceStateLog",
            ctx.getDeviceStateLogRepo().getAll(),
            DeviceStateLog.class
        );
        appendSection(
            doc,
            root,
            "deviceTypes",
            "deviceType",
            ctx.getDeviceTypeRepo().getAll(),
            DeviceType.class
        );
        appendSection(
            doc,
            root,
            "deviceUsers",
            "deviceUser",
            ctx.getDeviceUserRepo().getAll(),
            DeviceUser.class
        );

        writeToFile(doc, outputFile);
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private static void appendSection(
        Document doc,
        Element root,
        String groupTag,
        String entityTag,
        Iterable<?> data,
        Class<?> entityClass
    ) throws Exception {
        Element section = doc.createElement(groupTag);
        for (Object entity : data) {
            section.appendChild(
                buildEntityElement(doc, entity, entityClass, entityTag)
            );
        }
        root.appendChild(section);
    }

    private static Element buildEntityElement(
        Document doc,
        Object entity,
        Class<?> entityClass,
        String entityTag
    ) throws Exception {
        Element entityElement = doc.createElement(entityTag);

        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ManyToMany.class)) continue;

            field.setAccessible(true);
            Object value = field.get(entity);

            String tagName;
            String textValue;

            if (field.isAnnotationPresent(ForeignKey.class)) {
                ForeignKey fk = field.getAnnotation(ForeignKey.class);
                tagName = fk.column();
                if (value == null) {
                    textValue = "";
                } else {
                    try {
                        Field idField = value.getClass().getDeclaredField("id");
                        idField.setAccessible(true);
                        Object id = idField.get(value);
                        textValue = id != null ? id.toString() : "";
                    } catch (NoSuchFieldException e) {
                        textValue = value.toString();
                    }
                }
            } else if (field.isAnnotationPresent(Column.class)) {
                Column col = field.getAnnotation(Column.class);
                tagName = col.name();
                textValue = value != null ? value.toString() : "";
            } else {
                tagName = field.getName();
                textValue = value != null ? value.toString() : "";
            }

            Element fieldElement = doc.createElement(tagName);
            fieldElement.setTextContent(textValue);
            entityElement.appendChild(fieldElement);
        }

        return entityElement;
    }

    private static void writeToFile(Document doc, File outputFile)
        throws Exception {
        Transformer transformer =
            TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(
            "{http://xml.apache.org/xslt}indent-amount",
            "2"
        );
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.transform(new DOMSource(doc), new StreamResult(outputFile));
    }
}
