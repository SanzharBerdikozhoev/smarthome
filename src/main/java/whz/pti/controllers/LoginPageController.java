package whz.pti.controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import whz.pti.services.AuthService;
import whz.pti.services.implementation.AuthServiceImpl;
import whz.pti.utils.UserSession;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class LoginController {
    private AuthService authService = new AuthServiceImpl();

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label errorMessage;

    @FXML
    public void initialize() {
        loginButton.setOnAction(e -> handleLogin(
                usernameField, passwordField
        ));
    }

    private boolean validateLogin() {
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(e -> errorMessage.setText(""));
        delay.play();

        if (usernameField.getText().isEmpty()) {
            errorMessage.setText("Bitte geben Sie einen Benutzernamen ein.");
            return false;
        }

        if (passwordField.getText().isEmpty()) {
            errorMessage.setText("Bitte geben Sie ein Passwort ein.");
            return false;
        }

        return true;
    }

    private void handleLogin(TextField username, TextField password) {
        boolean valid = validateLogin();

        if(!valid) return;

        UserSession.currentUserId = authService.login(
                username.getText(),
                password.getText()
        );

        navigateToHomePage();
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
