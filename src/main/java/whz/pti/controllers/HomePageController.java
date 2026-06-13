package whz.pti.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import whz.pti.models.Scenario;
import whz.pti.services.ScenarioService;
import whz.pti.utils.AppContext;
import whz.pti.utils.UserSession;

import java.io.IOException;
import java.util.List;

public class HomePageController {
    private final ScenarioService scenarioService = AppContext.getInstance().getScenarioService();

    @FXML private Label userStatusLabel;
    @FXML private Button logoutButton;
    @FXML private ComboBox<?> houseComboBox;
    @FXML private ListView<?> roomListView;
    @FXML private Button settingsButton;

    @FXML private HBox scenariosContainer;
    @FXML private Label currentRoomLabel;
    @FXML private TilePane devicesTilePane;

    @FXML private TableView<?> logTableView;
    @FXML private TableColumn<?, ?> timestampColumn;
    @FXML private TableColumn<?, ?> deviceColumn;
    @FXML private TableColumn<?, ?> actionColumn;
    @FXML private TableColumn<?, ?> userColumn;

    @FXML
    public void initialize() {
        loadAndRenderScenarios();

        logoutButton.setOnAction(e -> handleLogout());
    }

    private void handleLogout() {
        UserSession.clearSession();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginPage.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logoutButton.getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Fehler beim Laden von LoginPage.fxml");
        }
    }

    private void loadAndRenderScenarios() {
        scenariosContainer.getChildren().clear();

        try {
            List<Scenario> scenarios = scenarioService.getScenarios();

            if (scenarios.isEmpty()) {
                Label noScenariosLabel = new Label("Keine Szenarien verfügbar");
                noScenariosLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
                scenariosContainer.getChildren().add(noScenariosLabel);
                return;
            }

            for (Scenario scenario : scenarios) {
                String displayName = scenario.getDeviceName() != null ? scenario.getDeviceName() : "Szenario #" + scenario.getId();

                Button scenarioButton = new Button("▶ " + displayName);

                setButtonStyle(scenarioButton, scenario.getIsActive());

                scenarioButton.setOnAction(event -> handleScenarioClick(scenario, scenarioButton));

                scenariosContainer.getChildren().add(scenarioButton);
            }

        } catch (Exception e) {
            System.err.println("Fehler beim Laden der Szenarien aus der Datenbank!");
            e.printStackTrace();
        }
    }

    private void setUserStatusLabel() {

    }

    private void handleScenarioClick(Scenario scenario, Button button) {
        boolean newStatus = !scenario.getIsActive();
        scenario.setIsActive(newStatus);

        scenarioService.update(scenario, scenario);

        setButtonStyle(button, newStatus);

        System.out.println("Szenario '" + scenario.getDeviceName() + "' ist jetzt: " + (newStatus ? "AKTIV" : "INAKTIV"));
    }

    private void setButtonStyle(Button button, boolean isActive) {
        if (isActive) {
            button.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        } else {
            button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        }
    }
}