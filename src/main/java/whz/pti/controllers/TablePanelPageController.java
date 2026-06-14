package whz.pti.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import whz.pti.models.*;
import whz.pti.utils.AppContext;
import whz.pti.repositories.GeneralRepo;
import whz.pti.utils.UserSession;
import whz.pti.utils.annotations.ForeignKey;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TablePanelPageController {
    @FXML private ListView<String> tablesListView;
    @FXML private Label labelCurrentTableName;
    @FXML private TableView<Object> genericTable;
    @FXML private HBox actionButtonsContainer;
    @FXML private Button buttonAdd;
    @FXML private Button buttonEdit;
    @FXML private Button buttonDelete;

    private final ObservableList<String> tables = FXCollections.observableArrayList();
    private GeneralRepo<Object> currentRepo;
    private Class<?> currentClass;

    @FXML
    public void initialize() {
        applyPermissions();

        if (UserSession.isAdmin()) {
            tables.add("Users");
        }

        tables.addAll("Room", "Home", "Scenario", "Device", "DeviceScenario", "DeviceStateLog", "DeviceType", "DeviceUser");
        tablesListView.setItems(tables);

        tablesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                labelCurrentTableName.setText("Tabelle: " + newVal);
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
        buttonAdd.setDisable(!isAdmin);
        buttonEdit.setDisable(!isAdmin);
        buttonDelete.setDisable(!isAdmin);
    }

    @SuppressWarnings("unchecked")
    public void loadTable(GeneralRepo<?> repo, Class<?> entityClass) {
        this.currentRepo = (GeneralRepo<Object>) repo;
        this.currentClass = entityClass;

        genericTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        genericTable.getColumns().clear();
        genericTable.getItems().clear();

        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(whz.pti.utils.annotations.ManyToMany.class)) continue;

            String fieldName = field.getName();
            String prettyHeader = fieldName.replaceAll("([a-z])([A-Z])", "$1 $2");
            prettyHeader = prettyHeader.substring(0, 1).toUpperCase() + prettyHeader.substring(1);

            TableColumn<Object, Object> column = new TableColumn<>(prettyHeader);

            column.setCellValueFactory(cellData -> {
                try {
                    Object item = cellData.getValue();
                    field.setAccessible(true);
                    Object value = field.get(item);
                    return new javafx.beans.property.SimpleObjectProperty<>(value != null ? value : "-");
                } catch (Exception e) {
                    return new javafx.beans.property.SimpleObjectProperty<>("[Fehler]");
                }
            });

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
            try {
                Object cellData = column.getCellData(i);
                if (cellData != null) {
                    t.setText(cellData.toString());
                    double cellW = t.getLayoutBounds().getWidth() + 25.0;
                    if (cellW > maxW) maxW = cellW;
                }
            } catch (Exception ignored) {}
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
        form.setStyle("-fx-padding: 15; -fx-min-width: 350;");
        Map<Field, Control> fieldsMap = new HashMap<>(); // Используем абстрактный Control

        for (Field field : currentClass.getDeclaredFields()) {
            if (field.getName().equalsIgnoreCase("id") || field.isAnnotationPresent(whz.pti.utils.annotations.ManyToMany.class)) continue;

            Label label = new Label(field.getName() + ":");
            Control inputControl = createInputControlForField(field, null); // Создаем нужный инпут

            form.getChildren().addAll(label, inputControl);
            fieldsMap.put(field, inputControl);
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
                        loadTable(currentRepo, currentClass);
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
        form.setStyle("-fx-padding: 15; -fx-min-width: 350;");
        Map<Field, Control> fieldsMap = new HashMap<>();

        try {
            Object idValue = null;

            for (Field field : currentClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(whz.pti.utils.annotations.ManyToMany.class)) continue;
                field.setAccessible(true);
                Object currentValue = field.get(selectedItem);

                if (field.getName().equalsIgnoreCase("id")) {
                    idValue = currentValue;
                    continue;
                }

                Label label = new Label(field.getName() + ":");
                Control inputControl = createInputControlForField(field, currentValue);

                form.getChildren().addAll(label, inputControl);
                fieldsMap.put(field, inputControl);
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

    private Control createInputControlForField(Field field, Object currentValue) {
        Class<?> fieldType = field.getType();

        // СЦЕНАРИЙ 1: Поле является Foreign Key
        if (field.isAnnotationPresent(ForeignKey.class)) {
            ComboBox<Object> comboBox = new ComboBox<>();
            comboBox.setMaxWidth(Double.MAX_VALUE);

            AppContext context = AppContext.getInstance();
            GeneralRepo<?> relatedRepo = null;

            if (fieldType == User.class) relatedRepo = context.getUserRepo();
            else if (fieldType == Room.class) relatedRepo = context.getRoomRepo();
            else if (fieldType == Home.class) relatedRepo = context.getHomeRepo();
            else if (fieldType == Scenario.class) relatedRepo = context.getScenarioRepo();
            else if (fieldType == Device.class) relatedRepo = context.getDeviceRepo();
            else if (fieldType == DeviceType.class) relatedRepo = context.getDeviceTypeRepo();

            if (relatedRepo != null) {
                Iterable<?> iterableItems = relatedRepo.getAll();
                List<Object> availableItems = new java.util.ArrayList<>();
                for (Object item : iterableItems) {
                    availableItems.add(item);
                }
                comboBox.setItems(FXCollections.observableArrayList(availableItems));

                if (currentValue != null) {
                    comboBox.getSelectionModel().select(currentValue);
                }
            }
            return comboBox;
        }

        // СЦЕНАРИЙ 2: Поле является ENUM (Новая логика)
        if (fieldType.isEnum()) {
            ComboBox<Object> comboBox = new ComboBox<>();
            comboBox.setMaxWidth(Double.MAX_VALUE);

            // Получаем все константы этого Enum (например: [USER, ADMIN] или [ON, OFF])
            Object[] enumConstants = fieldType.getEnumConstants();
            comboBox.setItems(FXCollections.observableArrayList(enumConstants));

            // Если редактируем — выбираем текущее значение
            if (currentValue != null) {
                comboBox.getSelectionModel().select(currentValue);
            } else if (enumConstants.length > 0) {
                // По умолчанию выбираем первый элемент из списка, чтобы поле не было пустым
                comboBox.getSelectionModel().select(0);
            }
            return comboBox;
        }

        // СЦЕНАРИЙ 3: Обычное текстовое поле
        TextField textField = new TextField();
        if (currentValue != null) {
            textField.setText(currentValue.toString());
        }
        return textField;
    }

    @SuppressWarnings("unchecked")
    private void fillEntityFromForm(Object entity, Map<Field, Control> fieldsMap) throws Exception {
        for (Map.Entry<Field, Control> entry : fieldsMap.entrySet()) {
            Field field = entry.getKey();
            field.setAccessible(true);
            Control control = entry.getValue();
            Class<?> fieldType = field.getType();

            if (control instanceof ComboBox<?> comboBox) {
                Object selectedValue = comboBox.getSelectionModel().getSelectedItem();

                if (selectedValue == null) {
                    field.set(entity, null);
                    continue;
                }

                if (fieldType.isEnum()) {
                    field.set(entity, Enum.valueOf((Class<Enum>) fieldType, selectedValue.toString()));
                } else {
                    field.set(entity, selectedValue);
                }
                continue;
            }

            if (control instanceof TextField textField) {
                String textValue = textField.getText().trim();

                if (textValue.isEmpty()) {
                    field.set(entity, null);
                    continue;
                }

                if (fieldType == Integer.class || fieldType == int.class) {
                    field.set(entity, Integer.parseInt(textValue));
                } else if (fieldType == Long.class || fieldType == long.class) {
                    field.set(entity, Long.parseLong(textValue));
                } else if (fieldType == Double.class || fieldType == double.class) {
                    field.set(entity, Double.parseDouble(textValue));
                } else if (fieldType == Boolean.class || fieldType == boolean.class) {
                    field.set(entity, Boolean.parseBoolean(textValue));
                } else if (fieldType == java.time.LocalDate.class) {
                    try {
                        field.set(entity, java.time.LocalDate.parse(textValue));
                    } catch (java.time.format.DateTimeParseException e) {
                        throw new RuntimeException("Falsches Datumsformat für " + field.getName() + ". Bitte YYYY-MM-DD nutzen.");
                    }
                } else {
                    field.set(entity, textValue);
                }
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