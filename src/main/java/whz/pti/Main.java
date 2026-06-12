package whz.pti;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        AnchorPane startPage = (AnchorPane) FXMLLoader.load(
                        getClass().getResource("/view/LoginPage.fxml")
        );

        stage.setTitle("Smarthome DB");
        stage.setScene(new Scene(startPage));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}