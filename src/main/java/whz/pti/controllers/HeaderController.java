package whz.pti.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import whz.pti.utils.PageSwitcher;
import whz.pti.utils.UserSession;

public class HeaderController {
    @FXML private Label userStatusLabel;
    @FXML private Button logoutButton;
    @FXML private Button homePageButton;
    @FXML private Button tablePanelButton;

    @FXML
    public void initialize() {
        userStatusLabel.setText("Angemeldet als: " + UserSession.getCurrentUser().getName());
        logoutButton.setOnAction(e -> handleLogout());
        homePageButton.setOnAction(e -> PageSwitcher.switchTo("HomePage", homePageButton));
        tablePanelButton.setOnAction(e -> PageSwitcher.switchTo("TablePanelPage", tablePanelButton));
    }

    private void handleLogout() {
        UserSession.clearSession();

        PageSwitcher.switchTo("LoginPage", logoutButton);
    }
}
