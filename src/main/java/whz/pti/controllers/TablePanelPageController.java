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
import java.util.*;

public class TablePanelPageController {
    @FXML
    private ListView<String> tablesListView;
    @FXML
    private Label labelCurrentTableName;
    @FXML
    private TableView<Object> genericTable;
    @FXML
    private HBox actionButtonsContainer;
    @FXML
    private Button buttonAdd;
    @FXML
    private Button buttonEdit;
    @FXML
    private Button buttonDelete;

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

        genericTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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

            Class<?> fieldType = field.getType();
            if (fieldType == boolean.class || fieldType == Boolean.class) {
                column.setCellFactory(tc -> new TableCell<Object, Object>() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            if (item instanceof String && "-".equals(item)) {
                                setText("-");
                            } else if (item instanceof Boolean) {
                                setText((boolean) item ? "Yes" : "No");
                            } else {
                                setText(item.toString());
                            }
                        }
                    }
                });
            }

            genericTable.getColumns().add(column);
        }

        List<Object> data = (List<Object>) currentRepo.getAll();
        genericTable.setItems(FXCollections.observableArrayList(data));

        javafx.application.Platform.runLater(() -> {
            ObservableList<TableColumn<Object, ?>> columns = genericTable.getColumns();
            if (columns.isEmpty()) return;

            for (int i = 0; i < columns.size(); i++) {
                TableColumn<Object, ?> col = columns.get(i);

                autoFitColumnWidth(col);
                double textWidth = col.getPrefWidth();

                col.setMinWidth(textWidth);
                col.setMaxWidth(Double.MAX_VALUE);
            }

            genericTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            genericTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
            } catch (Exception ignored) {
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
        form.setStyle("-fx-padding: 15; -fx-min-width: 350;");
        Map<Field, Control> fieldsMap = new HashMap<>();

        List<Field> keyFields = determineKeyFields(currentClass);
        boolean hasSingleId = keyFields.size() == 1 && keyFields.get(0).getName().equalsIgnoreCase("id");

        for (Field field : currentClass.getDeclaredFields()) {
            if (hasSingleId && field.getName().equalsIgnoreCase("id")) {
                continue;
            }

            Label label = new Label(field.getName() + ":");
            Control inputControl = createInputControlForField(field, null);

            form.getChildren().addAll(label, inputControl);
            fieldsMap.put(field, inputControl);
        }

        dialog.getDialogPane().setContent(form);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Object newEntity = currentClass.getDeclaredConstructor().newInstance();
                    fillEntityFromForm(newEntity, fieldsMap);

                    try {
                        currentRepo.save(newEntity);
                    } catch (Exception e) {
                        if (!hasSingleId && e.getMessage() != null && e.getMessage().contains("id")) {
                            System.out.println("Ложное исключение генерации ID успешно проигнорировано для " + currentClass.getSimpleName());
                        } else {
                            throw e;
                        }
                    }

                    loadTable(currentRepo, currentClass);
                } catch (Exception e) {
                    showErrorAlert("Fehler beim Speichern: " + e.getMessage());
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

        List<Field> keyFields = determineKeyFields(currentClass);
        boolean hasSingleId = keyFields.size() == 1 && keyFields.get(0).getName().equalsIgnoreCase("id");

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Eintrag bearbeiten in " + currentClass.getSimpleName());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox form = new VBox(10);
        form.setStyle("-fx-padding: 15; -fx-min-width: 350;");
        Map<Field, Control> fieldsMap = new HashMap<>();

        Map<Field, Object> oldKeyValues = new HashMap<>();

        try {
            Object singleIdValue = null;

            for (Field field : currentClass.getDeclaredFields()) {
                field.setAccessible(true);
                Object currentValue = field.get(selectedItem);

                if (hasSingleId && field.getName().equalsIgnoreCase("id")) {
                    singleIdValue = currentValue;
                    continue;
                }

                if (!hasSingleId && keyFields.contains(field)) {
                    oldKeyValues.put(field, currentValue);
                }

                Label label = new Label(field.getName() + ":");
                Control inputControl = createInputControlForField(field, currentValue);

                form.getChildren().addAll(label, inputControl);
                fieldsMap.put(field, inputControl);
            }

            dialog.getDialogPane().setContent(form);
            final Object finalId = singleIdValue;

            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        Object updatedEntity = currentClass.getDeclaredConstructor().newInstance();
                        fillEntityFromForm(updatedEntity, fieldsMap);

                        if (isDuplicateCompositeKey(selectedItem, updatedEntity)) {
                            System.out.println("Keine Änderungen festgestellt. Funktion wird abgebrochen.");
                            return;
                        }

                        if (hasSingleId) {
                            Field idField = currentClass.getDeclaredField("id");
                            idField.setAccessible(true);
                            idField.set(updatedEntity, finalId);

                            currentRepo.updateById(updatedEntity, (Long) finalId);
                        } else {
                            List<?> allExistingItems = (List<?>) currentRepo.getAll();
                            boolean duplicateExists = false;

                            for (Object existingItem : allExistingItems) {
                                if (isDuplicateCompositeKey(existingItem, updatedEntity)) {
                                    duplicateExists = true;
                                    break;
                                }
                            }

                            if (duplicateExists) {
                                showErrorAlert("Diese Verknüpfung existiert bereits in der Datenbank! " +
                                        "Änderung abgebrochen, um Datenverlust zu verhindern.");
                                return;
                            }

                            currentRepo.delete(selectedItem);
                            currentRepo.save(updatedEntity);
                        }

                        loadTable(currentRepo, currentClass);

                    } catch (Exception e) {
                        showErrorAlert("Fehler beim Aktualisieren: " + e.getMessage());
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

        if (fieldType == boolean.class || fieldType == Boolean.class) {
            ComboBox<String> booleanComboBox = new ComboBox<>();
            booleanComboBox.setItems(FXCollections.observableArrayList("Yes", "No"));
            booleanComboBox.setMaxWidth(Double.MAX_VALUE);

            if (currentValue != null) {
                boolean val = (boolean) currentValue;
                booleanComboBox.getSelectionModel().select(val ? "Yes" : "No");
            } else {
                booleanComboBox.getSelectionModel().select("No");
            }

            return booleanComboBox;
        }

        if (field.isAnnotationPresent(whz.pti.utils.annotations.ManyToMany.class)) {
            whz.pti.utils.annotations.ManyToMany anno = field.getAnnotation(whz.pti.utils.annotations.ManyToMany.class);

            ListView<Object> listView = new ListView<>();
            listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            listView.setPrefHeight(120);

            try {
                Class<?> repoClass = anno.repoClass();

                GeneralRepo<?> relatedRepo = getRepoFromContextByClass(repoClass);

                if (relatedRepo != null) {
                    List<Object> allAvailableItems = new ArrayList<>();
                    for (Object item : relatedRepo.getAll()) {
                        allAvailableItems.add(item);
                    }
                    listView.setItems(FXCollections.observableArrayList(allAvailableItems));

                    if (currentValue instanceof Collection<?> currentConnections) {
                        for (Object item : allAvailableItems) {
                            if (currentConnections.contains(item)) {
                                listView.getSelectionModel().select(item);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return listView;
        }

        if (field.isAnnotationPresent(ForeignKey.class)) {
            ComboBox<Object> comboBox = new ComboBox<>();
            comboBox.setMaxWidth(Double.MAX_VALUE);

            AppContext context = AppContext.getInstance();
            GeneralRepo<?> relatedRepo = null;

            if (fieldType == User.class) relatedRepo = context.getUserRepo();
            else if (fieldType == Room.class) relatedRepo = context.getRoomRepo();
            else if (fieldType == Home.class) relatedRepo = context.getHomeRepo();
            else if (fieldType == Scenario.class) relatedRepo = context.getScenarioRepo();
            else if (fieldType == DeviceScenario.class) relatedRepo = context.getDeviceScenarioRepo();
            else if (fieldType == DeviceUser.class) relatedRepo = context.getDeviceUserRepo();
            else if (fieldType == DeviceStateLog.class) relatedRepo = context.getDeviceStateLogRepo();
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

        if (fieldType.isEnum()) {
            ComboBox<Object> comboBox = new ComboBox<>();
            comboBox.setMaxWidth(Double.MAX_VALUE);

            Object[] enumConstants = fieldType.getEnumConstants();
            comboBox.setItems(FXCollections.observableArrayList(enumConstants));

            if (currentValue != null) {
                comboBox.getSelectionModel().select(currentValue);
            } else if (enumConstants.length > 0) {
                comboBox.getSelectionModel().select(0);
            }
            return comboBox;
        }

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

            if ((fieldType == boolean.class || fieldType == Boolean.class) && control instanceof ComboBox) {
                ComboBox<String> comboBox = (ComboBox<String>) control;
                String selectedValue = comboBox.getSelectionModel().getSelectedItem();

                boolean booleanValue = "Yes".equals(selectedValue);
                field.set(entity, booleanValue);
                continue;
            }

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

            if (control instanceof ListView<?> listView) {
                List<?> selectedItems = new ArrayList<>(listView.getSelectionModel().getSelectedItems());

                field.set(entity, selectedItems);
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
                } else if (fieldType == java.time.LocalTime.class) {
                    try {
                        field.set(entity, java.time.LocalTime.parse(textValue));
                    } catch (java.time.format.DateTimeParseException e) {
                        throw new RuntimeException("Falsches Zeitformat für " + field.getName() + ". Bitte hh:mm nutzen.");
                    }
                } else if (fieldType == java.time.LocalDateTime.class) {
                    try {
                        String normalizedValue = textValue.replace(" ", "T");

                        if (normalizedValue.length() == 16) {
                            normalizedValue += ":00";
                        }

                        field.set(entity, java.time.LocalDateTime.parse(normalizedValue));
                    } catch (java.time.format.DateTimeParseException e) {
                        throw new RuntimeException("Falsches Format für " + field.getName() + ". Bitte 'YYYY-MM-DD hh:mm' nutzen.");
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

    private GeneralRepo<?> getRepoFromContextByClass(Class<?> repoClass) {
        AppContext context = AppContext.getInstance();

        if (repoClass.getSimpleName().equals("DeviceRepository") || repoClass.getSimpleName().contains("DeviceRepo")) {
            return context.getDeviceRepo();
        }
        if (repoClass.getSimpleName().equals("ScenarioRepository") || repoClass.getSimpleName().contains("ScenarioRepo")) {
            return context.getScenarioRepo();
        }
        if (repoClass.getSimpleName().equals("UserRepository") || repoClass.getSimpleName().contains("UserRepo")) {
            return context.getUserRepo();
        }
        if (repoClass.getSimpleName().equals("RoomRepository") || repoClass.getSimpleName().contains("RoomRepo")) {
            return context.getRoomRepo();
        }
        if (repoClass.getSimpleName().equals("HomeRepository") || repoClass.getSimpleName().contains("HomeRepo")) {
            return context.getHomeRepo();
        }
        if (repoClass.getSimpleName().equals("DeviceTypeRepository") || repoClass.getSimpleName().contains("DeviceTypeRepo")) {
            return context.getDeviceTypeRepo();
        }

        return null;
    }

    private List<Field> determineKeyFields(Class<?> clazz) {
        List<Field> keyFields = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equalsIgnoreCase("id")) {
                keyFields.add(field);
                return keyFields;
            }
        }

        for (Field field : clazz.getDeclaredFields()) {
            Class<?> type = field.getType();
            if (!type.isPrimitive() && type != String.class &&
                    type != java.time.LocalDate.class && type != java.time.LocalTime.class &&
                    type != java.time.LocalDateTime.class && !type.isEnum() &&
                    type != Integer.class && type != Long.class && type != Double.class) {

                keyFields.add(field);
            }
        }

        return keyFields;
    }

    private boolean isDuplicateCompositeKey(Object dbEntity, Object newEntity) throws Exception {
        for (Field field : currentClass.getDeclaredFields()) {
            field.setAccessible(true);
            Object dbValue = field.get(dbEntity);
            Object newValue = field.get(newEntity);

            if (dbValue == null && newValue == null) {
                continue;
            }

            if ((dbValue == null && newValue != null) || (dbValue != null && newValue == null)) {
                return false;
            }

            if (!field.getType().isPrimitive() && field.getType() != String.class &&
                    !field.getType().getName().startsWith("java.time") &&
                    field.getType() != Integer.class && field.getType() != Long.class && field.getType() != Double.class) {

                try {
                    Field idField = dbValue.getClass().getDeclaredField("id");
                    idField.setAccessible(true);

                    Object dbId = idField.get(dbValue);
                    Object newId = idField.get(newValue);

                    if (dbId != null && !dbId.equals(newId)) {
                        return false;
                    }
                } catch (NoSuchFieldException ignored) {
                    if (!dbValue.equals(newValue)) {
                        return false;
                    }
                }
            } else {
                if (!dbValue.equals(newValue)) {
                    return false;
                }
            }
        }

        return true;
    }
}