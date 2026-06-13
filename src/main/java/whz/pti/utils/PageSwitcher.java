package whz.pti.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PageSwitcher {
    public static void switchTo(String page, Node graphicNode) {
        try {
            FXMLLoader loader = new FXMLLoader(PageSwitcher.class.getResource("/view/"+page+".fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) graphicNode.getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Fehler beim Laden von LoginPage.fxml");
        }
    }
}
