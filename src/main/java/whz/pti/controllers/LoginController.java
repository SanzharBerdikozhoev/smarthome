package whz.pti.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import whz.pti.services.AuthService;

public class LoginController {
    private AuthService loginService;

    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private Button loginButton;

    @FXML
    public void initialize() {
        loginButton.setOnAction(e -> handleLogin(
                usernameField, passwordField
        ));
    }

    private void handleLogin(TextField username, TextField password) {
        loginService.login(
                username.getText(),
                password.getText()
        );
    }
}
