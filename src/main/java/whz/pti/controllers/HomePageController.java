package whz.pti.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import whz.pti.models.*;
import whz.pti.services.*;
import whz.pti.utils.AppContext;
import whz.pti.utils.UserSession;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class HomePageController {
    private final ScenarioService scenarioService = AppContext.getInstance().getScenarioService();
    private final HomeService homeService = AppContext.getInstance().getHomeService();
    private final RoomService roomService = AppContext.getInstance().getRoomService();
    private final DeviceService deviceService = AppContext.getInstance().getDeviceService();
    private final DeviceTypeService deviceTypeService = AppContext.getInstance().getDeviceTypeService();
    private final DeviceStateLogService deviceStateLogService = AppContext.getInstance().getDeviceStateLogService();
    private  final AuthService authService = AppContext.getInstance().getAuthService();

    @FXML private Label userStatusLabel;
    @FXML private Button logoutButton;
    @FXML private ComboBox<Home> houseComboBox;
    @FXML private ListView<Room> roomListView;
    @FXML private Button settingsButton;

    @FXML private HBox scenariosContainer;
    @FXML private Label currentRoomLabel;
    @FXML private TilePane devicesTilePane;

    @FXML private TableView<DeviceStateLog> logTableView;
    @FXML private TableColumn<DeviceStateLog, String> timestampColumn;
    @FXML private TableColumn<DeviceStateLog, String> deviceColumn;
    @FXML private TableColumn<DeviceStateLog, String> actionColumn;
    @FXML private TableColumn<DeviceStateLog, String> userColumn;

    @FXML
    public void initialize() {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        timestampColumn.setCellValueFactory(cell -> {
            DeviceStateLog log = cell.getValue();
            if (log != null && log.getTime() != null) {
                String formattedTime = log.getTime().format(formatter);
                return new javafx.beans.property.SimpleStringProperty(formattedTime);
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        deviceColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getDevice() != null
                                ? getDeviceName(cell.getValue().getDevice())
                                : ""
                )
        );

        actionColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getState()
                )
        );

        userColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getUser() != null
                                ? getUserName(cell.getValue().getUser())
                                : ""
                )
        );


        roomListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldRoom, newRoom) -> {

                    if (newRoom != null) {
                        currentRoomLabel.setText(
                                "Geräte im Raum: " + newRoom.getName()
                        );

                        loadDevices(newRoom.getId());
                        loadLogs(newRoom.getId());
                    }
                });

        loadUserHomes();
        loadAndRenderScenarios();
    }

    private String getDeviceName(Long deviceID) {
        try {
            Optional<Device> device = deviceService.getDevice(deviceID);
            return device.get().getName();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getUserName(Long user_id) {
        Optional<User> device = authService.getUser(user_id);
        return device.get().getName();
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
            e.printStackTrace();
        }
    }


    private void loadUserHomes() {
        Long userId = UserSession.getCurrentUserId();

        if (userId == null || userId < 0) {
            return;
        }

        List<Home> homes = homeService.getHousesByUserId(userId);

        houseComboBox.getItems().clear();
        houseComboBox.getItems().addAll(homes);

        houseComboBox.setOnAction(event -> {
            Home selectedHome =
                    houseComboBox.getSelectionModel().getSelectedItem();

            if (selectedHome != null) {
                loadRooms(selectedHome.getId());
            }
        });

        if (!homes.isEmpty()) {
            houseComboBox.getSelectionModel().selectFirst();

            loadRooms(homes.get(0).getId());
        }
    }

    private void loadRooms(Long houseId) {

        List<Room> rooms =
                roomService.getRoomsByHouseId(houseId);

        roomListView.getItems().clear();
        roomListView.getItems().addAll(rooms);

        if (!rooms.isEmpty()) {

            roomListView
                    .getSelectionModel()
                    .selectFirst();

        }
    }

    private void loadDevices(Long roomId) {

        List<Device> devices =
                deviceService.getDevicesByRoomId(roomId);

        devicesTilePane.getChildren().clear();

        for (Device device : devices) {

            VBox card = createDeviceCard(device);

            devicesTilePane.getChildren().add(card);
        }
    }

    private void loadLogs(Long roomId) {


        List<DeviceStateLog> logs =
                deviceStateLogService.getLogsByRoomId(roomId);


        logTableView.getItems().setAll(logs);
    }

    private VBox createDeviceCard(Device device) {

        VBox card = new VBox(10);

        card.setPrefWidth(200);
        card.setPrefHeight(140);

        card.setStyle("""
        -fx-background-color: white;
        -fx-background-radius: 10;
        -fx-padding: 15;
        -fx-border-color: #dfe6e9;
        -fx-border-radius: 10;
        -fx-effect: dropshadow(
            three-pass-box,
            rgba(0,0,0,0.1),
            5,
            0,
            0,
            2
        );
        """);

        Label nameLabel =
                new Label(device.getName());

        nameLabel.setStyle(
                "-fx-font-size: 16;" +
                        "-fx-font-weight: bold;"
        );

        Label statusLabel =
                new Label(
                        device.isActive()
                                ? "🟢 Aktiv"
                                : "🔴 Inaktiv"
                );

        statusLabel.setStyle(
                device.isActive()
                        ? "-fx-text-fill: green;"
                        : "-fx-text-fill: red;"
        );

        Button toggleButton =
                new Button(
                        device.isActive()
                                ? "Ausschalten"
                                : "Einschalten"
                );

        Label typeLabel = new Label();
        DeviceType deviceType = deviceTypeService.getDeviceTypeByDeviceId(device.getId());
        if (deviceType != null) {
            typeLabel.setText(deviceType.getName());
        } else {
            typeLabel.setText("Unknown Type");
        }

        typeLabel.setStyle("""
        -fx-font-size: 11;
        -fx-text-fill: #7f8c8d;
        -fx-font-style: italic;
        """);

        toggleButton.setOnAction(event -> {
            boolean nextState = !device.isActive();
            Long currentUserId = whz.pti.utils.UserSession.getCurrentUserId();

            deviceService.updateDeviceState(device.getId(), nextState, currentUserId);

            device.setActive(nextState);

            Long currentRoomId = roomListView.getSelectionModel().getSelectedItem().getId();
            loadDevices(currentRoomId);
            loadLogs(currentRoomId);
        });

        card.getChildren().addAll(
                nameLabel,
                typeLabel,
                statusLabel,
                toggleButton
        );

        return card;
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