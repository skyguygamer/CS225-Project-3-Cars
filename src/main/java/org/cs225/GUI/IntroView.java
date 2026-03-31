package org.cs225.GUI;

/*
    Gabriel worked on this class

    This class is 
 */
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.cs225.RaceGameApp;

public class IntroView {

    private static final String[] DEFAULT_CAR_NAMES = {"Car 1", "Car 2", "Car 3", "Car 4"};

    private final VBox root;
    private final ToggleGroup carSelectionGroup;
    private final Button startRaceButton;
    private final Label selectionStatusLabel;
    private final HBox[] optionRows;

    public IntroView(RaceGameApp app) {
        carSelectionGroup = new ToggleGroup();
        startRaceButton = new Button("Start Race");
        selectionStatusLabel = new Label("Select the car you think will win.");
        root = new VBox(20);
        optionRows = new HBox[DEFAULT_CAR_NAMES.length];

        buildLayout(app);
    }

    private void buildLayout(RaceGameApp app) {
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: whitesmoke;");

        Label titleLabel = new Label("Project 3 - Racing Simulator");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

        Label subtitleLabel = new Label("Choose one car as your predicted winner before the race begins.");
        subtitleLabel.setStyle("-fx-font-size: 16px;");
        subtitleLabel.setWrapText(true);

        VBox selectionBox = new VBox(14);
        selectionBox.setFillWidth(true);
        selectionBox.setMaxWidth(520);

        for (int i = 0; i < DEFAULT_CAR_NAMES.length; i++) {
            selectionBox.getChildren().add(createCarOptionRow(i, DEFAULT_CAR_NAMES[i]));
        }

        selectionStatusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #444444;");
        selectionStatusLabel.setWrapText(true);

        carSelectionGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            updateSelectionStatus();
            startRaceButton.setDisable(newToggle == null);
            updateOptionRowStyles();
        });

        startRaceButton.setDisable(true);
        startRaceButton.setMinWidth(180);
        startRaceButton.setOnAction(event -> {
            int selectedCarIndex = getSelectedCarIndex();
            if (selectedCarIndex >= 0) {
                // TODO: If the team adds RaceController, call the controller here instead of the app directly.
                app.startRace(selectedCarIndex);
            }
        });

        Button clearSelectionButton = new Button("Clear Selection");
        clearSelectionButton.setOnAction(event -> clearSelection());

        HBox buttonRow = new HBox(12, startRaceButton, clearSelectionButton);
        buttonRow.setAlignment(Pos.CENTER);

        Label integrationNoteLabel = new Label(
                "TODO: Replace these default car names with teammate model data when Car objects are available."
        );
        integrationNoteLabel.setWrapText(true);
        integrationNoteLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666;");
        integrationNoteLabel.setMaxWidth(520);

        root.getChildren().addAll(
                titleLabel,
                subtitleLabel,
                selectionBox,
                selectionStatusLabel,
                buttonRow,
                integrationNoteLabel
        );
    }

    private HBox createCarOptionRow(int carIndex, String carName) {
        RadioButton carRadioButton = new RadioButton();
        carRadioButton.setToggleGroup(carSelectionGroup);
        carRadioButton.setUserData(carIndex);

        Node carGraphic = CarSpriteLoader.createCarGraphic(
                CarSpriteLoader.getSpriteForIndex(carIndex),
                CarSpriteLoader.DEFAULT_SPRITE_WIDTH,
                CarSpriteLoader.DEFAULT_SPRITE_HEIGHT
        );

        StackPane spriteHolder = new StackPane(carGraphic);
        spriteHolder.setMinSize(
                CarSpriteLoader.DEFAULT_SPRITE_WIDTH,
                CarSpriteLoader.DEFAULT_SPRITE_HEIGHT
        );
        spriteHolder.setPrefSize(
                CarSpriteLoader.DEFAULT_SPRITE_WIDTH,
                CarSpriteLoader.DEFAULT_SPRITE_HEIGHT
        );

        Label carNameLabel = new Label(carName);
        carNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label carDescriptionLabel = new Label(
                CarSpriteLoader.getSpriteForIndex(carIndex).getDisplayName() + " sprite preview."
        );
        carDescriptionLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");

        VBox textBox = new VBox(4, carNameLabel, carDescriptionLabel);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox optionRow = new HBox(14, carRadioButton, spriteHolder, textBox, spacer);
        optionRow.setAlignment(Pos.CENTER_LEFT);
        optionRow.setPadding(new Insets(14));
        optionRow.setStyle(getOptionRowStyle(false));

        optionRow.setOnMouseClicked(event -> carRadioButton.setSelected(true));
        optionRows[carIndex] = optionRow;
        return optionRow;
    }

    private void updateSelectionStatus() {
        int selectedCarIndex = getSelectedCarIndex();

        if (selectedCarIndex >= 0) {
            selectionStatusLabel.setText("Current prediction: " + DEFAULT_CAR_NAMES[selectedCarIndex]);
        } else {
            selectionStatusLabel.setText("Select the car you think will win.");
        }
    }

    private void updateOptionRowStyles() {
        int selectedCarIndex = getSelectedCarIndex();

        for (int i = 0; i < optionRows.length; i++) {
            if (optionRows[i] != null) {
                optionRows[i].setStyle(getOptionRowStyle(i == selectedCarIndex));
            }
        }
    }

    private String getOptionRowStyle(boolean selected) {
        if (selected) {
            return "-fx-background-color: #eaf3ff;"
                    + "-fx-border-color: #4a90e2;"
                    + "-fx-border-width: 2;"
                    + "-fx-border-radius: 8;"
                    + "-fx-background-radius: 8;";
        }

        return "-fx-background-color: white;"
                + "-fx-border-color: lightgray;"
                + "-fx-border-width: 1;"
                + "-fx-border-radius: 8;"
                + "-fx-background-radius: 8;";
    }

    public Parent getRoot() {
        return root;
    }

    public int getSelectedCarIndex() {
        Toggle selectedToggle = carSelectionGroup.getSelectedToggle();

        if (selectedToggle == null) {
            return -1;
        }

        return (int) selectedToggle.getUserData();
    }

    public void clearSelection() {
        carSelectionGroup.selectToggle(null);
        updateSelectionStatus();
        updateOptionRowStyles();
        startRaceButton.setDisable(true);
    }

    public void setSelectedCarIndex(int selectedCarIndex) {
        for (Toggle toggle : carSelectionGroup.getToggles()) {
            if ((int) toggle.getUserData() == selectedCarIndex) {
                carSelectionGroup.selectToggle(toggle);
                updateSelectionStatus();
                updateOptionRowStyles();
                startRaceButton.setDisable(false);
                return;
            }
        }

        throw new IllegalArgumentException("Unknown car index: " + selectedCarIndex);
    }
}
