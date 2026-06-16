package whz.pti.controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import whz.pti.models.SafeUser;
import whz.pti.services.AuthService;
import whz.pti.utils.AlertHelper;
import whz.pti.utils.AppContext;
import whz.pti.utils.PageSwitcher;
import whz.pti.utils.UserSession;


public class LoginPageController {
    private final AuthService authService = AppContext.getInstance().getAuthService();

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
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

        SafeUser currentUser;

        try {
            currentUser = authService.login(
                    username.getText(),
                    password.getText()
            );
        } catch (Exception e) {
            AlertHelper.error("Fehler bei Anmeldung", e.getMessage());

            username.clear();
            password.clear();

            return;
        }

        UserSession.setSession(currentUser);

        if(currentUser.getRole() == whz.pti.models.Role.ADMIN) {
            PageSwitcher.switchTo("TablePanelPage", loginButton);
            return;
        }

        PageSwitcher.switchTo("HomePage", loginButton);
    }
}
