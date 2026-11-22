package ua.edu.nupp.kpp.client;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainApp extends Application {

    private final ApiClient api = new ApiClient();

    private final TableView<FieldDto> fieldsTable = new TableView<>();
    private final TableView<ObservationDto> obsTable = new TableView<>();
    private final TableView<ProblemDto> problemsTable = new TableView<>();
    private final TableView<TreatmentActionDto> actionsTable = new TableView<>();

    @Override
    public void start(Stage stage) {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabPane.getTabs().addAll(
                createFieldsTab(),
                createObservationsTab(),
                createProblemsTab(),
                createActionsTab()
        );

        Scene scene = new Scene(tabPane, 1000, 620);
        stage.setTitle("Моніторинг сільськогосподарських полів (Клієнт)");
        stage.setScene(scene);
        stage.show();

        refreshAll();
    }

    // 1. ПОЛЯ
    private Tab createFieldsTab() {
        TableColumn<FieldDto, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));

        TableColumn<FieldDto, String> nameCol = new TableColumn<>("Назва");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        TableColumn<FieldDto, String> cropCol = new TableColumn<>("Культура");
        cropCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCropType()));

        TableColumn<FieldDto, Double> areaCol = new TableColumn<>("Площа (га)");
        areaCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getArea()));

        fieldsTable.getColumns().addAll(idCol, nameCol, cropCol, areaCol);
        fieldsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TextField nameInput = new TextField();
        nameInput.setPromptText("Назва поля");
        TextField cropInput = new TextField();
        cropInput.setPromptText("Культура");
        TextField areaInput = new TextField();
        areaInput.setPromptText("Площа (напр. 120.5)");

        fieldsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, sel) -> {
            if (sel != null) {
                nameInput.setText(sel.getName());
                cropInput.setText(sel.getCropType());
                areaInput.setText(String.valueOf(sel.getArea()));
            }
        });

        Button addBtn = new Button("Додати");
        addBtn.setOnAction(e -> safe(() -> {
            Map<String, String> p = new LinkedHashMap<>();
            p.put("action", "add");
            p.put("name", nameInput.getText());
            p.put("cropType", cropInput.getText());
            p.put("area", areaInput.getText());
            api.postForm("/fields", p);
            refreshFields();
        }));

        Button updateBtn = new Button("Змінити");
        updateBtn.setOnAction(e -> safe(() -> {
            FieldDto sel = fieldsTable.getSelectionModel().getSelectedItem();
            if (sel == null) { showError("Виберіть запис"); return; }
            Map<String, String> p = new LinkedHashMap<>();
            p.put("action", "update");
            p.put("id", String.valueOf(sel.getId()));
            p.put("name", nameInput.getText());
            p.put("cropType", cropInput.getText());
            p.put("area", areaInput.getText());
            api.postForm("/fields", p);
            refreshFields();
        }));

        Button deleteBtn = new Button("Видалити");
        deleteBtn.setOnAction(e -> safe(() -> {
            FieldDto sel = fieldsTable.getSelectionModel().getSelectedItem();
            if (sel == null) { showError("Виберіть запис"); return; }
            api.postForm("/fields", Map.of("action", "delete", "id", String.valueOf(sel.getId())));
            refreshFields();
        }));

        Button refreshBtn = new Button("Оновити");
        refreshBtn.setOnAction(e -> refreshFields());

        HBox row1 = new HBox(8, nameInput, cropInput, areaInput);
        HBox row2 = new HBox(8, addBtn, updateBtn, deleteBtn, refreshBtn);
        VBox form = new VBox(8, row1, row2);
        form.setPadding(new Insets(10));

        BorderPane pane = new BorderPane(fieldsTable);
        pane.setBottom(form);
        return new Tab("Поля", pane);
    }

    // 2. СПОСТЕРЕЖЕННЯ
    private Tab createObservationsTab() {
        TableColumn<ObservationDto, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));

        TableColumn<ObservationDto, Long> fieldIdCol = new TableColumn<>("ID поля");
        fieldIdCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getFieldId()));

        TableColumn<ObservationDto, String> dateCol = new TableColumn<>("Дата");
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDate()));

        TableColumn<ObservationDto, String> descCol = new TableColumn<>("Опис");
        descCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));

        TableColumn<ObservationDto, Double> f1Col = new TableColumn<>("F1");
        f1Col.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getF1Score()));

        TableColumn<ObservationDto, Double> f2Col = new TableColumn<>("F2");
        f2Col.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getF2Score()));

        TableColumn<ObservationDto, Double> f3Col = new TableColumn<>("F3");
        f3Col.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getF3Score()));

        obsTable.getColumns().addAll(idCol, fieldIdCol, dateCol, descCol, f1Col, f2Col, f3Col);
        obsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TextField fieldIdInput = new TextField();
        fieldIdInput.setPromptText("ID поля");
        fieldIdInput.setPrefWidth(75);
        TextField dateInput = new TextField();
        dateInput.setPromptText("YYYY-MM-DD");
        dateInput.setPrefWidth(105);
        TextField descInput = new TextField();
        descInput.setPromptText("Опис спостереження");
        TextField f1Input = new TextField();
        f1Input.setPromptText("F1");
        f1Input.setPrefWidth(60);
        TextField f2Input = new TextField();
        f2Input.setPromptText("F2");
        f2Input.setPrefWidth(60);
        TextField f3Input = new TextField();
        f3Input.setPromptText("F3");
        f3Input.setPrefWidth(60);

        obsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, sel) -> {
            if (sel != null) {
                fieldIdInput.setText(String.valueOf(sel.getFieldId()));
                dateInput.setText(sel.getDate());
                descInput.setText(sel.getDescription());
                f1Input.setText(String.valueOf(sel.getF1Score()));
                f2Input.setText(String.valueOf(sel.getF2Score()));
                f3Input.setText(String.valueOf(sel.getF3Score()));
            }
        });

        Button addBtn = new Button("Додати");
        addBtn.setOnAction(e -> safe(() -> {
            Map<String, String> p = new LinkedHashMap<>();
            p.put("action", "add");
            p.put("fieldId", fieldIdInput.getText());
            p.put("date", dateInput.getText());
            p.put("description", descInput.getText());
            p.put("f1Score", f1Input.getText());
            p.put("f2Score", f2Input.getText());
            p.put("f3Score", f3Input.getText());
            api.postForm("/observations", p);
            refreshObservations();
        }));

        Button updateBtn = new Button("Змінити");
        updateBtn.setOnAction(e -> safe(() -> {
            ObservationDto sel = obsTable.getSelectionModel().getSelectedItem();
            if (sel == null) { showError("Виберіть запис"); return; }
            Map<String, String> p = new LinkedHashMap<>();
            p.put("action", "update");
            p.put("id", String.valueOf(sel.getId()));
            p.put("fieldId", fieldIdInput.getText());
            p.put("date", dateInput.getText());
            p.put("description", descInput.getText());
            p.put("f1Score", f1Input.getText());
            p.put("f2Score", f2Input.getText());
            p.put("f3Score", f3Input.getText());
            api.postForm("/observations", p);
            refreshObservations();
        }));

        Button deleteBtn = new Button("Видалити");
        deleteBtn.setOnAction(e -> safe(() -> {
            ObservationDto sel = obsTable.getSelectionModel().getSelectedItem();
            if (sel == null) { showError("Виберіть запис"); return; }
            api.postForm("/observations", Map.of("action", "delete", "id", String.valueOf(sel.getId())));
            refreshObservations();
        }));

        Button refreshBtn = new Button("Оновити");
        refreshBtn.setOnAction(e -> refreshObservations());

        Button importCsvBtn = new Button("Імпорт CSV (тест)");
        importCsvBtn.setOnAction(e -> safe(() -> {
            ObservationDto obs = readTestObservation();
            Map<String, String> p = new LinkedHashMap<>();
            p.put("action", "add");
            p.put("fieldId", String.valueOf(obs.getFieldId()));
            p.put("date", obs.getDate());
            p.put("description", obs.getDescription());
            p.put("f1Score", String.valueOf(obs.getF1Score()));
            p.put("f2Score", String.valueOf(obs.getF2Score()));
            p.put("f3Score", String.valueOf(obs.getF3Score()));
            api.postForm("/observations", p);
            refreshObservations();
        }));

        HBox row1 = new HBox(8, fieldIdInput, dateInput, descInput, f1Input, f2Input, f3Input);
        HBox row2 = new HBox(8, addBtn, updateBtn, deleteBtn, refreshBtn, importCsvBtn);
        VBox form = new VBox(8, row1, row2);
        form.setPadding(new Insets(10));

        BorderPane pane = new BorderPane(obsTable);
        pane.setBottom(form);
        return new Tab("Спостереження", pane);
    }

    // 3. ПРОБЛЕМИ
    private Tab createProblemsTab() {
        TableColumn<ProblemDto, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));

        TableColumn<ProblemDto, Long> obsIdCol = new TableColumn<>("ID спостереження");
        obsIdCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getObservationId()));

        TableColumn<ProblemDto, Double> f1Col = new TableColumn<>("F1");
        f1Col.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getF1Score()));

        TableColumn<ProblemDto, Double> f2Col = new TableColumn<>("F2");
        f2Col.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getF2Score()));

        TableColumn<ProblemDto, Double> f3Col = new TableColumn<>("F3");
        f3Col.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getF3Score()));

        TableColumn<ProblemDto, Double> riskCol = new TableColumn<>("Інтегральний показник");
        riskCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getRiskIndex()));

        TableColumn<ProblemDto, String> stateCol = new TableColumn<>("Стан");
        stateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getState()));

        problemsTable.getColumns().addAll(idCol, obsIdCol, f1Col, f2Col, f3Col, riskCol, stateCol);
        problemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TextField obsIdInput = new TextField();
        obsIdInput.setPromptText("ID спостереження");
        obsIdInput.setPrefWidth(130);
        TextField f1Input = new TextField();
        f1Input.setPromptText("F1");
        f1Input.setPrefWidth(65);
        TextField f2Input = new TextField();
        f2Input.setPromptText("F2");
        f2Input.setPrefWidth(65);
        TextField f3Input = new TextField();
        f3Input.setPromptText("F3");
        f3Input.setPrefWidth(65);

        ComboBox<ProblemState> stateBox = new ComboBox<>(FXCollections.observableArrayList(ProblemState.values()));
        stateBox.setPromptText("Новий стан");

        problemsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, sel) -> {
            if (sel != null) {
                obsIdInput.setText(String.valueOf(sel.getObservationId()));
                f1Input.setText(String.valueOf(sel.getF1Score()));
                f2Input.setText(String.valueOf(sel.getF2Score()));
                f3Input.setText(String.valueOf(sel.getF3Score()));
                try {
                    stateBox.setValue(ProblemState.valueOf(sel.getState()));
                } catch (Exception ignored) {
                }
            }
        });

        Button addBtn = new Button("Додати");
        addBtn.setOnAction(e -> safe(() -> {
            Map<String, String> p = new LinkedHashMap<>();
            p.put("action", "add");
            p.put("observationId", obsIdInput.getText());
            p.put("f1Score", f1Input.getText());
            p.put("f2Score", f2Input.getText());
            p.put("f3Score", f3Input.getText());
            api.postForm("/problems", p);
            refreshProblems();
        }));

        Button updateBtn = new Button("Змінити");
        updateBtn.setOnAction(e -> safe(() -> {
            ProblemDto sel = problemsTable.getSelectionModel().getSelectedItem();
            if (sel == null) { showError("Виберіть запис"); return; }
            Map<String, String> p = new LinkedHashMap<>();
            p.put("action", "update");
            p.put("id", String.valueOf(sel.getId()));
            p.put("observationId", obsIdInput.getText());
            p.put("f1Score", f1Input.getText());
            p.put("f2Score", f2Input.getText());
            p.put("f3Score", f3Input.getText());
            api.postForm("/problems", p);
            refreshProblems();
        }));

        Button transitionBtn = new Button("Змінити стан");
        transitionBtn.setOnAction(e -> safe(() -> {
            ProblemDto sel = problemsTable.getSelectionModel().getSelectedItem();
            if (sel == null) { showError("Виберіть запис"); return; }
            if (stateBox.getValue() == null) { showError("Виберіть стан"); return; }
            Map<String, String> p = new LinkedHashMap<>();
            p.put("action", "transition");
            p.put("id", String.valueOf(sel.getId()));
            p.put("state", stateBox.getValue().name());
            api.postForm("/problems", p);
            refreshProblems();
        }));

        Button deleteBtn = new Button("Видалити");
        deleteBtn.setOnAction(e -> safe(() -> {
            ProblemDto sel = problemsTable.getSelectionModel().getSelectedItem();
            if (sel == null) { showError("Виберіть запис"); return; }
            api.postForm("/problems", Map.of("action", "delete", "id", String.valueOf(sel.getId())));
            refreshProblems();
        }));

        Button refreshBtn = new Button("Оновити");
        refreshBtn.setOnAction(e -> refreshProblems());

        HBox row1 = new HBox(8, obsIdInput, f1Input, f2Input, f3Input, stateBox);
        HBox row2 = new HBox(8, addBtn, updateBtn, transitionBtn, deleteBtn, refreshBtn);
        VBox form = new VBox(8, row1, row2);
        form.setPadding(new Insets(10));

        BorderPane pane = new BorderPane(problemsTable);
        pane.setBottom(form);
        return new Tab("Проблеми", pane);
    }

    // 4. ЗАХОДИ
    private Tab createActionsTab() {
        TableColumn<TreatmentActionDto, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));

        TableColumn<TreatmentActionDto, Long> problemIdCol = new TableColumn<>("ID проблеми");
        problemIdCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getProblemId()));

        TableColumn<TreatmentActionDto, String> descCol = new TableColumn<>("Опис заходу");
        descCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));

        TableColumn<TreatmentActionDto, String> dateCol = new TableColumn<>("Планова дата");
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPlannedDate()));

        actionsTable.getColumns().addAll(idCol, problemIdCol, descCol, dateCol);
        actionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TextField problemIdInput = new TextField();
        problemIdInput.setPromptText("ID проблеми");
        problemIdInput.setPrefWidth(100);
        TextField descInput = new TextField();
        descInput.setPromptText("Опис заходу");
        TextField dateInput = new TextField();
        dateInput.setPromptText("YYYY-MM-DD");
        dateInput.setPrefWidth(110);

        actionsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, sel) -> {
            if (sel != null) {
                problemIdInput.setText(String.valueOf(sel.getProblemId()));
                descInput.setText(sel.getDescription());
                dateInput.setText(sel.getPlannedDate());
            }
        });

        Button addBtn = new Button("Додати");
        addBtn.setOnAction(e -> safe(() -> {
            Map<String, String> p = new LinkedHashMap<>();
            p.put("action", "add");
            p.put("problemId", problemIdInput.getText());
            p.put("description", descInput.getText());
            p.put("plannedDate", dateInput.getText());
            api.postForm("/actions", p);
            refreshActions();
        }));

        Button updateBtn = new Button("Змінити");
        updateBtn.setOnAction(e -> safe(() -> {
            TreatmentActionDto sel = actionsTable.getSelectionModel().getSelectedItem();
            if (sel == null) { showError("Виберіть запис"); return; }
            Map<String, String> p = new LinkedHashMap<>();
            p.put("action", "update");
            p.put("id", String.valueOf(sel.getId()));
            p.put("problemId", problemIdInput.getText());
            p.put("description", descInput.getText());
            p.put("plannedDate", dateInput.getText());
            api.postForm("/actions", p);
            refreshActions();
        }));

        Button deleteBtn = new Button("Видалити");
        deleteBtn.setOnAction(e -> safe(() -> {
            TreatmentActionDto sel = actionsTable.getSelectionModel().getSelectedItem();
            if (sel == null) { showError("Виберіть запис"); return; }
            api.postForm("/actions", Map.of("action", "delete", "id", String.valueOf(sel.getId())));
            refreshActions();
        }));

        Button refreshBtn = new Button("Оновити");
        refreshBtn.setOnAction(e -> refreshActions());

        HBox row1 = new HBox(8, problemIdInput, descInput, dateInput);
        HBox row2 = new HBox(8, addBtn, updateBtn, deleteBtn, refreshBtn);
        VBox form = new VBox(8, row1, row2);
        form.setPadding(new Insets(10));

        BorderPane pane = new BorderPane(actionsTable);
        pane.setBottom(form);
        return new Tab("Заходи", pane);
    }

    private ObservationDto readTestObservation() throws IOException {
        Path file = Path.of("sample-data", "observations.csv");
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        if (lines.size() < 2) {
            throw new IllegalArgumentException("CSV-файл не містить даних");
        }
        String line = lines.get(1);
        String[] parts = line.split(";", -1);
        if (parts.length != 6) {
            throw new IllegalArgumentException("Некоректна кількість стовпців: " + parts.length);
        }

        ObservationDto obs = new ObservationDto();
        obs.setFieldId(Long.parseLong(parts[0].trim()));
        obs.setDate(parts[1].trim());
        obs.setDescription(parts[2].trim());
        obs.setF1Score(Double.parseDouble(parts[3].trim()));
        obs.setF2Score(Double.parseDouble(parts[4].trim()));
        obs.setF3Score(Double.parseDouble(parts[5].trim()));
        return obs;
    }

    private void refreshAll() {
        refreshFields();
        refreshObservations();
        refreshProblems();
        refreshActions();
    }

    private void refreshFields() {
        safe(() -> fieldsTable.setItems(FXCollections.observableArrayList(api.getFields())));
    }

    private void refreshObservations() {
        safe(() -> obsTable.setItems(FXCollections.observableArrayList(api.getObservations())));
    }

    private void refreshProblems() {
        safe(() -> problemsTable.setItems(FXCollections.observableArrayList(api.getProblems())));
    }

    private void refreshActions() {
        safe(() -> actionsTable.setItems(FXCollections.observableArrayList(api.getActions())));
    }

    @FunctionalInterface
    private interface Action {
        void run() throws Exception;
    }

    private void safe(Action action) {
        try {
            action.run();
        } catch (Exception ex) {
            String msg = (ex.getMessage() != null && !ex.getMessage().isBlank())
                    ? ex.getMessage()
                    : ex.getClass().getSimpleName() + " (перевірте, чи запущений Tomcat)";
            showError(msg);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}