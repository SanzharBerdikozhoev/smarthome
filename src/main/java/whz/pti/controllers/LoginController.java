package whz.pti.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import whz.pti.services.AuthService;

import java.io.IOException;

public class LoginController {
    private AuthService loginService;

    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private Button loginButton;

    @FXML
    public void initialize() {
        loginButton.setOnAction(e -> navigateToHomePage());
//        loginButton.setOnAction(e -> handleLogin(
//                usernameField, passwordField
//        ));
    }

    private void handleLogin(TextField username, TextField password) {
        loginService.login(
                username.getText(),
                password.getText()
        );
    }


    private void navigateToHomePage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/homePage.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) loginButton.getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Fehler beim Laden von homePage.fxml");
        }
    }
}
