package whz.pti.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import whz.pti.models.*;
import whz.pti.utils.AppContext;
import whz.pti.repositories.GeneralRepo;
import whz.pti.utils.UserSession;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TablePanelPageController {
    @FXML
    private ListView<String> tablesListView;
    @FXML
    private Label lblCurrentTableName;
    @FXML
    private TableView<Object> genericTable;
    @FXML
    private HBox actionButtonsContainer;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;

    private final ObservableList<String> tables = FXCollections.observableArrayList();

    private GeneralRepo<Object> currentRepo;
    private Class<?> currentClass;

    @FXML
    public void initialize() {
        applyPermissions();

        if(UserSession.isAdmin()) {
            tables.add("Users");
        }

        tables.add("Room");
        tables.add("Home");
        tables.add("Scenario");
        tables.add("Device");
        tables.add("DeviceScenario");
        tables.add("DeviceStateLog");
        tables.add("DeviceType");
        tables.add("DeviceUser");

        tablesListView.setItems(tables);

        tablesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                lblCurrentTableName.setText("Tabelle: " + newVal);
                AppContext context = AppContext.getInstance();

                switch (newVal) {
                    case "Users" -> loadTable(context.getUserRepo(), User.class);
                    case "Room" -> loadTable(context.getRoomRepo(), Room.class);
                    case "Home" -> loadTable(context.getHomeRepo(), Home.class);
                    case "Scenario" -> loadTable(context.getScenarioRepo(), Scenario.class);
                    case "Device" -> loadTable(context.getDeviceRepo(), Device.class);
                    case "DeviceScenario" -> loadTable(context.getDeviceScenarioRepo(), DeviceScenario.class);
                    case "DeviceStateLog" -> loadTable(context.getDeviceStateLogRepo(), DeviceStateLog.class);
                    case "DeviceType" -> loadTable(context.getDeviceTypeRepo(), DeviceType.class);
                    case "DeviceUser" -> loadTable(context.getDeviceUserRepo(), DeviceUser.class);
                }
            }
        });
    }

    private void applyPermissions() {
        boolean isAdmin = UserSession.isAdmin();

        actionButtonsContainer.setVisible(isAdmin);
        btnAdd.setDisable(!isAdmin);
        btnEdit.setDisable(!isAdmin);
        btnDelete.setDisable(!isAdmin);
    }

    @SuppressWarnings("unchecked")
    public void loadTable(GeneralRepo<?> repo, Class<?> entityClass) {
        this.currentRepo = (GeneralRepo<Object>) repo;
        this.currentClass = entityClass;

        genericTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        genericTable.getColumns().clear();
        genericTable.getItems().clear();

        for (Field field : entityClass.getDeclaredFields()) {
            String fieldName = field.getName();

            String prettyHeader = fieldName.replaceAll("([a-z])([A-Z])", "$1 $2");
            prettyHeader = prettyHeader.substring(0, 1).toUpperCase() + prettyHeader.substring(1);

            TableColumn<Object, Object> column = new TableColumn<>(prettyHeader);
            column.setCellValueFactory(new PropertyValueFactory<>(fieldName));

            if (fieldName.equalsIgnoreCase("id")) {
                column.setStyle("-fx-alignment: CENTER;");
            }

            genericTable.getColumns().add(column);
        }

        List<Object> data = (List<Object>) currentRepo.getAll();
        genericTable.setItems(FXCollections.observableArrayList(data));

        javafx.application.Platform.runLater(() -> {
            for (TableColumn<Object, ?> col : genericTable.getColumns()) {
                autoFitColumnWidth(col);
            }
        });
    }

    private void autoFitColumnWidth(TableColumn<Object, ?> column) {
        javafx.scene.text.Text t = new javafx.scene.text.Text(column.getText());
        double maxW = t.getLayoutBounds().getWidth() + 30.0;

        int rowsToCheck = Math.min(genericTable.getItems().size(), 15);
        for (int i = 0; i < rowsToCheck; i++) {
            if (column.getCellData(i) != null) {
                t.setText(column.getCellData(i).toString());
                double cellW = t.getLayoutBounds().getWidth() + 25.0;
                if (cellW > maxW) {
                    maxW = cellW;
                }
            }
        }

        column.setPrefWidth(maxW);
    }

    @FXML
    public void handleAdd() {
        if (currentClass == null || currentRepo == null) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Neuen Eintrag hinzufügen in " + currentClass.getSimpleName());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox form = new VBox(10);
        form.setStyle("-fx-padding: 15; -fx-min-width: 300;");
        Map<Field, TextField> fieldsMap = new HashMap<>();

        for (Field field : currentClass.getDeclaredFields()) {
            if (field.getName().equalsIgnoreCase("id")) continue;

            Label label = new Label(field.getName() + ":");
            TextField textField = new TextField();
            form.getChildren().addAll(label, textField);

            fieldsMap.put(field, textField);
        }

        dialog.getDialogPane().setContent(form);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Object newEntity = currentClass.getDeclaredConstructor().newInstance();

                    fillEntityFromForm(newEntity, fieldsMap);

                    Object savedObject = currentRepo.save(newEntity);
                    if (savedObject != null) {
                        genericTable.getItems().add(savedObject);
                    }
                } catch (Exception e) {
                    showErrorAlert("Fehler beim Speichern der Daten: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    @SuppressWarnings("unchecked")
    public void handleEdit() {
        Object selectedItem = genericTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showWarningAlert("Bitte wählen Sie die Zeile aus, die Sie ändern möchten.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Eintrag bearbeiten in " + currentClass.getSimpleName());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox form = new VBox(10);
        form.setStyle("-fx-padding: 15; -fx-min-width: 300;");
        Map<Field, TextField> fieldsMap = new HashMap<>();

        try {
            Object idValue = null;

            for (Field field : currentClass.getDeclaredFields()) {
                field.setAccessible(true);
                Object currentValue = field.get(selectedItem);

                if (field.getName().equalsIgnoreCase("id")) {
                    idValue = currentValue;
                    continue;
                }

                Label label = new Label(field.getName() + ":");
                TextField textField = new TextField(currentValue != null ? currentValue.toString() : "");
                form.getChildren().addAll(label, textField);

                fieldsMap.put(field, textField);
            }

            dialog.getDialogPane().setContent(form);
            final Object finalId = idValue;

            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK && finalId != null) {
                    try {
                        Object updatedEntity = currentClass.getDeclaredConstructor().newInstance();
                        fillEntityFromForm(updatedEntity, fieldsMap);

                        Object result = currentRepo.updateById(updatedEntity, (Long) finalId);
                        if (result != null) {
                            loadTable(currentRepo, currentClass);
                        }
                    } catch (Exception e) {
                        showErrorAlert("Fehler beim Aktualisieren der Daten: " + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDelete() {
        Object selectedItem = genericTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showWarningAlert("Bitte wählen Sie die Zeile aus, die gelöscht werden soll.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Möchten Sie diesen Eintrag wirklich löschen?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    currentRepo.delete(selectedItem);
                    genericTable.getItems().remove(selectedItem);
                } catch (RuntimeException e) {
                    showErrorAlert(e.getMessage());
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void fillEntityFromForm(Object entity, Map<Field, TextField> fieldsMap) throws Exception {
        for (Map.Entry<Field, TextField> entry : fieldsMap.entrySet()) {
            Field field = entry.getKey();
            field.setAccessible(true);
            String textValue = entry.getValue().getText().trim();

            if (textValue.isEmpty()) {
                field.set(entity, null);
                continue;
            }

            Class<?> fieldType = field.getType();

            if (fieldType == Integer.class || fieldType == int.class) {
                field.set(entity, Integer.parseInt(textValue));
            } else if (fieldType == Long.class || fieldType == long.class) {
                field.set(entity, Long.parseLong(textValue));
            } else if (fieldType == Double.class || fieldType == double.class) {
                field.set(entity, Double.parseDouble(textValue));
            } else if (fieldType == Boolean.class || fieldType == boolean.class) {
                field.set(entity, Boolean.parseBoolean(textValue));
            } else if (fieldType.isEnum()) {
                field.set(entity, Enum.valueOf((Class<Enum>) fieldType, textValue.toUpperCase()));
            } else {
                field.set(entity, textValue);
            }
        }
    }

    private void showWarningAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING, content, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showErrorAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR, content, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}