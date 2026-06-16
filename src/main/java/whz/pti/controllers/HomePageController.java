package whz.pti.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class HomePageController {
    private final ScenarioService scenarioService = AppContext.getInstance().getScenarioService();
    private final HomeService homeService = AppContext.getInstance().getHomeService();
    private final RoomService roomService = AppContext.getInstance().getRoomService();
    private final DeviceService deviceService = AppContext.getInstance().getDeviceService();
    private final DeviceTypeService deviceTypeService = AppContext.getInstance().getDeviceTypeService();
    private final DeviceScenarioService deviceScenarioService = AppContext.getInstance().getDeviceScenarioService();
    private final DeviceStateLogService deviceStateLogService = AppContext.getInstance().getDeviceStateLogService();
    private final AuthService authService = AppContext.getInstance().getAuthService();

    @FXML private ComboBox<Home> houseComboBox;
    @FXML private ListView<Room> roomListView;

    @FXML private Button addHomeButton;
    @FXML private Button addRoomButton;
    @FXML private Button addDeviceButton;
    @FXML private Button addScenarioButton;

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
                        cell.getValue().getState().name()
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
                        currentRoomLabel.setText("Geräte im Raum: " + newRoom.getName());
                        loadDevices(newRoom.getId());
                        loadLogs(newRoom.getId());
                    }
                });

        houseComboBox.setOnContextMenuRequested(event -> {
            Home selectedHome = houseComboBox.getSelectionModel().getSelectedItem();
            if (selectedHome != null) {
                ContextMenu menu = new ContextMenu();
                MenuItem deleteItem = new MenuItem("Haus löschen");
                deleteItem.setOnAction(e -> deleteSelectedHome());
                menu.getItems().add(deleteItem);
                menu.show(houseComboBox, event.getScreenX(), event.getScreenY());
            }
        });

        loadUserHomes();
        loadAndRenderScenarios();

        addHomeButton.setOnAction(e -> openAddHomeDialog());
        addRoomButton.setOnAction(e -> openAddRoomDialog());
        addDeviceButton.setOnAction(e -> openAddDeviceDialog());
        addScenarioButton.setOnAction(e -> openAddScenarioDialog());

        if (isReadOnly()) {
            addHomeButton.setVisible(false);
            addHomeButton.setManaged(false);

            addRoomButton.setVisible(false);
            addRoomButton.setManaged(false);

            addDeviceButton.setVisible(false);
            addDeviceButton.setManaged(false);

            addScenarioButton.setVisible(false);
            addScenarioButton.setManaged(false);
        }
    }

    private boolean isReadOnly() {
        Role role = UserSession.getCurrentUser().getRole();
        return role == Role.READER;
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
            Long currentUserId = UserSession.getCurrentUserId();
            if (currentUserId == null) {
                return;
            }

            List<Scenario> scenarios = scenarioService.getScenariosByUserId(currentUserId);

            if (scenarios.isEmpty()) {
                Label noScenariosLabel = new Label("Keine Szenarien verfügbar");
                noScenariosLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
                scenariosContainer.getChildren().add(noScenariosLabel);
                return;
            }

            for (Scenario scenario : scenarios) {
                String displayName = scenario.getName() != null ? scenario.getName() : "Szenario #" + scenario.getId();
                HBox scenarioBox = new HBox(5);
                Button scenarioButton = new Button("▶ " + displayName);
                scenarioButton.getStyleClass().add("scenario-button");

                setButtonStyle(scenarioButton, scenario.getIsActive());
                scenarioButton.setOnAction(event -> handleScenarioClick(scenario, scenarioButton));

                Button deleteButton = new Button("✕");
                deleteButton.getStyleClass().add("delete-button-small");
                deleteButton.setOnAction(event -> deleteScenario(scenario));

                if (!isReadOnly()) {
                    scenarioBox.getChildren().addAll(scenarioButton, deleteButton);
                }else {
                    scenariosContainer.getChildren().add(scenarioBox);
                    scenarioButton.setDisable(true);
                }
                scenarioBox.getChildren().addAll(scenarioButton, deleteButton);
                scenariosContainer.getChildren().add(scenarioBox);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUserHomes() {
        Long userId = UserSession.getCurrentUserId();
        if (userId == null || userId < 0) return;

        List<Home> homes = homeService.getHousesByUserId(userId);
        houseComboBox.getItems().clear();
        houseComboBox.getItems().addAll(homes);

        if (homes.isEmpty()) {
            roomListView.getItems().clear();
            devicesTilePane.getChildren().clear();
            currentRoomLabel.setText("Geräte im Raum: (Kein Haus vorhanden)");
            return;
        }

        houseComboBox.setOnAction(event -> {
            Home selectedHome = houseComboBox.getSelectionModel().getSelectedItem();
            if (selectedHome != null) {
                loadRooms(selectedHome.getId());
            }
        });

        houseComboBox.getSelectionModel().selectFirst();
        loadRooms(homes.get(0).getId());
    }

    private void loadRooms(Long houseId) {
        List<Room> rooms = roomService.getRoomsByHouseId(houseId);
        roomListView.getItems().clear();
        roomListView.getItems().addAll(rooms);

        if (!rooms.isEmpty()) {
            roomListView.getSelectionModel().selectFirst();
        }
    }

    private void loadDevices(Long roomId) {
        List<Device> devices = deviceService.getDevicesByRoomId(roomId);
        devicesTilePane.getChildren().clear();

        for (Device device : devices) {
            VBox card = createDeviceCard(device);
            devicesTilePane.getChildren().add(card);
        }
    }

    private void loadLogs(Long roomId) {
        List<DeviceStateLog> logs = deviceStateLogService.getLogsByRoomId(roomId);
        logTableView.getItems().setAll(logs);
    }

    private VBox createDeviceCard(Device device) {
        VBox card = new VBox(8);
        card.getStyleClass().add("device-card");
        card.setPrefWidth(200);
        card.setPrefHeight(140);

        Label nameLabel = new Label(device.getName());
        nameLabel.getStyleClass().add("device-name");

        Label typeLabel = new Label();
        DeviceType deviceType = deviceTypeService.getDeviceTypeByDeviceId(device.getId());
        if (deviceType != null) {
            typeLabel.setText(deviceType.getName());
        } else {
            typeLabel.setText("Unknown Type");
        }
        typeLabel.getStyleClass().add("device-type");

        Label statusLabel = new Label(device.isActive() ? "🟢 Aktiv" : "🔴 Inaktiv");
        statusLabel.getStyleClass().add(device.isActive() ? "device-status-on" : "device-status-off");

        Button toggleButton = new Button(device.isActive() ? "Ausschalten" : "Einschalten");
        toggleButton.getStyleClass().add(device.isActive() ? "device-toggle-on" : "device-toggle-off");

        if (isReadOnly()) {
            toggleButton.setDisable(true);
        } else {
            toggleButton.setOnAction(event -> {
                boolean nextState = !device.isActive();
                Long currentUserId = whz.pti.utils.UserSession.getCurrentUserId();

                deviceService.updateDeviceState(device.getId(), nextState, currentUserId);
                device.setActive(nextState);

                Room currentRoom = roomListView.getSelectionModel().getSelectedItem();
                if (currentRoom != null) {
                    loadDevices(currentRoom.getId());
                    loadLogs(currentRoom.getId());
                }
            });
        }

        card.getChildren().addAll(
                nameLabel,
                typeLabel,
                statusLabel,
                toggleButton
        );

        if (!isReadOnly()) {
            Button deleteButton = new Button("Löschen");
            deleteButton.getStyleClass().add("delete-button");
            deleteButton.setOnAction(event -> deleteDevice(device));
            card.getChildren().add(deleteButton);
        }

        return card;
    }

    private void handleScenarioClick(Scenario scenario, Button button) {
        boolean newStatus = !scenario.getIsActive();
        scenario.setIsActive(newStatus);
        scenarioService.update(scenario, scenario);

        Long currentUserId = UserSession.getCurrentUserId();
        scenarioService.executeScenario(scenario, currentUserId);
        setButtonStyle(button, newStatus);

        Room room = roomListView.getSelectionModel().getSelectedItem();
        if (room != null) {
            loadDevices(room.getId());
            loadLogs(room.getId());
        }
    }

    private void setButtonStyle(Button button, boolean isActive) {
        button.getStyleClass().removeAll("scenario-green", "scenario-blue");
        if (isActive) {
            button.getStyleClass().add("scenario-green");
        } else {
            button.getStyleClass().add("scenario-blue");
        }
    }

    private Dialog<ButtonType> createBaseDialog(String title, String headerText) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        if (devicesTilePane.getScene() != null) {
            dialog.getDialogPane().getStylesheets().addAll(devicesTilePane.getScene().getStylesheets());
        }
        return dialog;
    }

    private Label formLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    private void styleInput(Control control) {
        control.getStyleClass().add("form-input");
    }

    private void openAddHomeDialog() {
        Dialog<ButtonType> dialog = createBaseDialog("Neues Haus", "Haus hinzufügen");
        TextField addressField = new TextField();
        addressField.setPromptText("Straße, Hausnummer");
        styleInput(addressField);

        TextField townField = new TextField();
        townField.setPromptText("Stadt");
        styleInput(townField);

        TextField zipField = new TextField();
        zipField.setPromptText("Postleitzahl");
        styleInput(zipField);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 20));
        grid.add(formLabel("Adresse"), 0, 0); grid.add(addressField, 1, 0);
        grid.add(formLabel("Stadt"), 0, 1); grid.add(townField, 1, 1);
        grid.add(formLabel("PLZ"), 0, 2); grid.add(zipField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String address = addressField.getText().trim();
            String town = townField.getText().trim();
            String zip = zipField.getText().trim();

            if (address.isEmpty() || town.isEmpty() || zip.isEmpty()) {
                showError("Bitte alle Felder ausfüllen.");
                return;
            }

            Long userId = UserSession.getCurrentUserId();
            User currentUser = authService.getUser(userId).orElse(null);

            Home newHome = new Home(null, address, town, zip, currentUser);
            homeService.save(newHome);
            loadUserHomes();
        }
    }

    private void openAddRoomDialog() {
        Home selectedHome = houseComboBox.getSelectionModel().getSelectedItem();
        if (selectedHome == null) {
            showError("Bitte zuerst ein Haus auswählen.");
            return;
        }

        Dialog<ButtonType> dialog = createBaseDialog("Neuer Raum", "Raum zu \"" + selectedHome + "\" hinzufügen");
        TextField nameField = new TextField();
        nameField.setPromptText("z. B. Wohnzimmer");
        styleInput(nameField);

        TextField floorField = new TextField();
        floorField.setPromptText("z. B. Erdgeschoss");
        styleInput(floorField);

        TextField squareField = new TextField();
        squareField.setPromptText("z. B. 25.5");
        styleInput(squareField);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 20));
        grid.add(formLabel("Name"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(formLabel("Etage"), 0, 1); grid.add(floorField, 1, 1);
        grid.add(formLabel("Fläche (m²)"), 0, 2); grid.add(squareField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String name = nameField.getText().trim();
            String floor = floorField.getText().trim();
            String squareText = squareField.getText().trim();

            if (name.isEmpty() || floor.isEmpty() || squareText.isEmpty()) {
                showError("Bitte alle Felder ausfüllen.");
                return;
            }

            double square;
            try {
                square = Double.parseDouble(squareText.replace(",", "."));
            } catch (NumberFormatException ex) {
                showError("Fläche muss eine Zahl sein.");
                return;
            }

            if (square <= 0) {
                showError("Fläche muss größer als 0 sein.");
                return;
            }

            Room newRoom = new Room(null, name, floor, square, selectedHome);
            roomService.save(newRoom);
            loadRooms(selectedHome.getId());
        }
    }

    private void openAddDeviceDialog() {
        Room selectedRoom = roomListView.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            showError("Bitte zuerst einen Raum auswählen.");
            return;
        }

        Dialog<ButtonType> dialog = createBaseDialog("Neues Gerät", "Gerät zu \"" + selectedRoom.getName() + "\" hinzufügen");
        TextField nameField = new TextField();
        nameField.setPromptText("z. B. Philips Hue Lampe");
        styleInput(nameField);

        ComboBox<DeviceType> deviceTypeComboBox = new ComboBox<>();
        deviceTypeComboBox.getItems().addAll(deviceTypeService.getDeviceTypes());
        deviceTypeComboBox.setPromptText("Gerätetyp wählen...");
        styleInput(deviceTypeComboBox);

        CheckBox activeCheckBox = new CheckBox("Aktiv");
        activeCheckBox.setSelected(true);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 20));
        grid.add(formLabel("Name"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(formLabel("Typ"), 0, 1); grid.add(deviceTypeComboBox, 1, 1);
        grid.add(formLabel("Status"), 0, 2); grid.add(activeCheckBox, 1, 2);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String name = nameField.getText().trim();
            DeviceType deviceType = deviceTypeComboBox.getSelectionModel().getSelectedItem();

            if (name.isEmpty() || deviceType == null) {
                showError("Bitte Name und Gerätetyp angeben.");
                return;
            }

            Device newDevice = new Device(null, name, selectedRoom, deviceType, LocalDate.now(), activeCheckBox.isSelected(), null, null);
            deviceService.save(newDevice);
            loadDevices(selectedRoom.getId());
        }
    }

    private void openAddScenarioDialog() {
        Home selectedHome = houseComboBox.getSelectionModel().getSelectedItem();
        if (selectedHome == null) {
            showError("Bitte zuerst ein Haus auswählen.");
            return;
        }

        Dialog<ButtonType> dialog = createBaseDialog("Neues Szenario", "Szenario hinzufügen");
        TextField nameField = new TextField();
        nameField.setPromptText("z. B. Morgenroutine");
        styleInput(nameField);

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Kurze Beschreibung");
        styleInput(descriptionField);

        TextField startTimeField = new TextField();
        startTimeField.setPromptText("HH:mm");
        styleInput(startTimeField);

        TextField endTimeField = new TextField();
        endTimeField.setPromptText("HH:mm");
        styleInput(endTimeField);

        CheckBox activeCheckBox = new CheckBox("Aktiv");
        activeCheckBox.setSelected(true);

        ListView<Device> deviceListView = new ListView<>();
        deviceListView.setPrefHeight(140);
        deviceListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        for (Room room : roomService.getRoomsByHouseId(selectedHome.getId())) {
            deviceListView.getItems().addAll(deviceService.getDevicesByRoomId(room.getId()));
        }

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(formLabel("Name"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(formLabel("Beschreibung"), 0, 1); grid.add(descriptionField, 1, 1);
        grid.add(formLabel("Startzeit"), 0, 2); grid.add(startTimeField, 1, 2);
        grid.add(formLabel("Endzeit"), 0, 3); grid.add(endTimeField, 1, 3);
        grid.add(formLabel("Status"), 0, 4); grid.add(activeCheckBox, 1, 4);
        grid.add(formLabel("Geräte"), 0, 5); grid.add(deviceListView, 1, 5);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            String startText = startTimeField.getText().trim();
            String endText = endTimeField.getText().trim();

            List<Device> selectedDevices = new java.util.ArrayList<>(deviceListView.getSelectionModel().getSelectedItems());

            if (name.isEmpty() || startText.isEmpty() || endText.isEmpty()) {
                showError("Bitte Name, Startzeit und Endzeit angeben.");
                return;
            }

            if (selectedDevices.isEmpty()) {
                showError("Bitte mindestens ein Gerät auswählen.");
                return;
            }

            LocalTime startTime; LocalTime endTime;
            try {
                startTime = LocalTime.parse(startText);
                endTime = LocalTime.parse(endText);
            } catch (Exception ex) {
                showError("Zeit muss im Format HH:mm angegeben werden.");
                return;
            }

            Scenario newScenario = new Scenario(null, name, activeCheckBox.isSelected(), startTime, endTime, null);
            newScenario.setDescription(description.isEmpty() ? null : description);
            scenarioService.save(newScenario);

            Scenario savedScenario = scenarioService.getScenarios().stream()
                    .filter(s -> name.equals(s.getName())).findFirst().orElse(null);

            if (savedScenario == null || savedScenario.getId() == null) {
                showError("Szenario konnte nicht gespeichert werden.");
                return;
            }

            for (Device device : selectedDevices) {
                DeviceScenario link = new DeviceScenario(null, device, savedScenario, DeviceScenarioRole.OUTPUT);
                deviceScenarioService.save(link);
            }
            loadAndRenderScenarios();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler"); alert.setHeaderText(null); alert.setContentText(message);
        alert.showAndWait();
    }

    private void deleteSelectedHome() {
        Home selectedHome = houseComboBox.getSelectionModel().getSelectedItem();
        if (selectedHome == null) {
            showError("Bitte zuerst ein Haus auswählen.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Haus löschen"); confirm.setHeaderText("Haus wirklich löschen?");
        confirm.setContentText("Das Haus \"" + selectedHome + "\" wird gelöscht.\nAlle Räume und Geräte werden ebenfalls gelöscht.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                homeService.delete(selectedHome);
                roomListView.getItems().clear();
                devicesTilePane.getChildren().clear();
                logTableView.getItems().clear();
                loadUserHomes();
                loadAndRenderScenarios();
            } catch (Exception ex) {
                showError("Haus konnte nicht gelöscht werden."); ex.printStackTrace();
            }
        }
    }

    private void deleteDevice(Device device) {
        if (device == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Gerät löschen"); confirm.setHeaderText("Gerät wirklich löschen?");
        confirm.setContentText("Das Gerät \"" + device.getName() + "\" wird gelöscht.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                deviceService.delete(device);
                Room selectedRoom = roomListView.getSelectionModel().getSelectedItem();
                if (selectedRoom != null) {
                    loadDevices(selectedRoom.getId());
                    loadLogs(selectedRoom.getId());
                }
                loadAndRenderScenarios();
            } catch (Exception ex) {
                showError("Gerät konnte nicht gelöscht werden."); ex.printStackTrace();
            }
        }
    }

    private void deleteScenario(Scenario scenario) {
        if (scenario == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Szenario löschen"); confirm.setHeaderText("Szenario wirklich löschen?");
        confirm.setContentText("Das Szenario \"" + scenario.getName() + "\" wird gelöscht.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                scenarioService.delete(scenario);
                loadAndRenderScenarios();
                Room selectedRoom = roomListView.getSelectionModel().getSelectedItem();
                if (selectedRoom != null) {
                    loadDevices(selectedRoom.getId());
                    loadLogs(selectedRoom.getId());
                }
            } catch (Exception ex) {
                showError("Szenario konnte nicht gelöscht werden."); ex.printStackTrace();
            }
        }
    }
}