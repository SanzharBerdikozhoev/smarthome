package whz.pti.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

import java.util.Optional;

public final class AlertHelper {
    private AlertHelper() {}

    public static Optional<ButtonType> show(
            Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        DialogPane dialogPane = alert.getDialogPane();
        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);

        if(cancelButton != null) {
            cancelButton.setText("Abbrechen");
        }

        return alert.showAndWait();
    }

    public static void error(String header, String content) {
        show(Alert.AlertType.ERROR, "Fehler", header, content);
    }

    public static void success(String header, String content) {
        show(Alert.AlertType.INFORMATION, "Erfolg", header, content);
    }

    public static Optional<ButtonType> confirm(String header, String content) {
        return show(Alert.AlertType.CONFIRMATION, "Bestätigung", header, content);
    }
}
