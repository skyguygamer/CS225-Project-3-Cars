package org.cs225.GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.cs225.RaceGameApp;

/**
 * Displays the end-of-race outcome, prediction result, and summary stats for
 * each car.
 *
 * @author Gabriel Ferreira
 */
public class ResultsView {

    private static final int RESULT_ROW_COUNT = 4;
    private static final String DEFAULT_ROUTE_TEXT = "-";

    private final VBox root;
    private final Label predictionSummaryLabel;
    private final Label[] placeLabels;
    private final StackPane[] carSpriteHolders;
    private final Label[] carNameLabels;
    private final Label[] routeLabels;
    private final Label[] timeLabels;
    private final Label[] averageSpeedLabels;
    private final Label[] topSpeedLabels;

    /**
     * Creates the results view layout.
     *
     * @param app the main JavaFX application
     */
    public ResultsView(RaceGameApp app) {
        root = new VBox(18);
        predictionSummaryLabel = new Label("Race results will appear here.");
        placeLabels = new Label[RESULT_ROW_COUNT];
        carSpriteHolders = new StackPane[RESULT_ROW_COUNT];
        carNameLabels = new Label[RESULT_ROW_COUNT];
        routeLabels = new Label[RESULT_ROW_COUNT];
        timeLabels = new Label[RESULT_ROW_COUNT];
        averageSpeedLabels = new Label[RESULT_ROW_COUNT];
        topSpeedLabels = new Label[RESULT_ROW_COUNT];

        buildLayout(app);
        showPlaceholderResults();
    }

    private void buildLayout(RaceGameApp app) {
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: whitesmoke;");

        Label titleLabel = new Label("Results Screen");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        predictionSummaryLabel.setStyle(
                "-fx-font-size: 16px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-color: white;"
                        + "-fx-border-color: lightgray;"
                        + "-fx-border-radius: 8;"
                        + "-fx-background-radius: 8;"
                        + "-fx-padding: 14;"
        );
        predictionSummaryLabel.setWrapText(true);
        predictionSummaryLabel.setMaxWidth(760);

        GridPane resultsGrid = createResultsGrid();

        Label noteLabel = new Label(
                "Results are listed in finishing order with each car's elapsed time, average speed, and top speed."
        );
        noteLabel.setWrapText(true);
        noteLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666;");
        noteLabel.setMaxWidth(760);

        Button restartButton = new Button("Restart Race");
        restartButton.setOnAction(event -> {
            app.restartRace();
        });

        HBox buttonRow = new HBox(restartButton);
        buttonRow.setAlignment(Pos.CENTER);

        root.getChildren().addAll(titleLabel, predictionSummaryLabel, resultsGrid, noteLabel, buttonRow);
    }

    private GridPane createResultsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(18);
        grid.setVgap(12);
        grid.setPadding(new Insets(18));
        grid.setMaxWidth(920);
        grid.setStyle(
                "-fx-background-color: white;"
                        + "-fx-border-color: lightgray;"
                        + "-fx-border-radius: 8;"
                        + "-fx-background-radius: 8;"
        );

        addHeaderLabel(grid, "Place", 0);
        addHeaderLabel(grid, "Car", 1);
        addHeaderLabel(grid, "Route", 2);
        addHeaderLabel(grid, "Elapsed Time", 3);
        addHeaderLabel(grid, "Avg Speed", 4);
        addHeaderLabel(grid, "Top Speed", 5);

        for (int row = 0; row < RESULT_ROW_COUNT; row++) {
            placeLabels[row] = createValueLabel();
            carSpriteHolders[row] = createSpriteHolder(row);
            carNameLabels[row] = createValueLabel();
            routeLabels[row] = createRouteLabel();
            timeLabels[row] = createValueLabel();
            averageSpeedLabels[row] = createValueLabel();
            topSpeedLabels[row] = createValueLabel();

            grid.add(placeLabels[row], 0, row + 1);
            grid.add(new HBox(10, carSpriteHolders[row], carNameLabels[row]), 1, row + 1);
            grid.add(routeLabels[row], 2, row + 1);
            grid.add(timeLabels[row], 3, row + 1);
            grid.add(averageSpeedLabels[row], 4, row + 1);
            grid.add(topSpeedLabels[row], 5, row + 1);
        }

        return grid;
    }

    private void addHeaderLabel(GridPane grid, String text, int columnIndex) {
        Label headerLabel = new Label(text);
        headerLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        grid.add(headerLabel, columnIndex, 0);
    }

    private Label createValueLabel() {
        Label valueLabel = new Label();
        valueLabel.setStyle("-fx-font-size: 14px;");
        return valueLabel;
    }

    private Label createRouteLabel() {
        Label routeLabel = createValueLabel();
        // Route text can be longer than the other columns, so allow wrapping here.
        routeLabel.setWrapText(true);
        routeLabel.setMaxWidth(250);
        return routeLabel;
    }

    private StackPane createSpriteHolder(int carIndex) {
        StackPane spriteHolder = new StackPane();
        spriteHolder.setMinSize(CarSpriteLoader.DEFAULT_SPRITE_WIDTH, CarSpriteLoader.DEFAULT_SPRITE_HEIGHT);
        spriteHolder.setPrefSize(CarSpriteLoader.DEFAULT_SPRITE_WIDTH, CarSpriteLoader.DEFAULT_SPRITE_HEIGHT);
        spriteHolder.setMaxSize(CarSpriteLoader.DEFAULT_SPRITE_WIDTH, CarSpriteLoader.DEFAULT_SPRITE_HEIGHT);
        spriteHolder.getChildren().setAll(
                CarSpriteLoader.createCarGraphic(
                        CarSpriteLoader.getSpriteForIndex(carIndex),
                        CarSpriteLoader.DEFAULT_SPRITE_WIDTH,
                        CarSpriteLoader.DEFAULT_SPRITE_HEIGHT
                )
        );
        return spriteHolder;
    }

    /**
     * Returns the root node for this scene.
     *
     * @return the results view root
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * Updates the prediction summary shown above the results table.
     *
     * @param predictedCarName the car chosen by the user
     * @param winnerName the actual winning car
     * @param predictionWasCorrect whether the prediction matched the winner
     */
    public void setPredictionSummary(String predictedCarName, String winnerName, boolean predictionWasCorrect) {
        predictionSummaryLabel.setText(
                "Predicted winner: " + predictedCarName
                        + "\nActual winner: " + winnerName
                        + "\nPrediction result: " + (predictionWasCorrect ? "Correct" : "Incorrect")
        );
    }

    /**
     * Sets custom summary text for the prediction area.
     *
     * @param summaryText the text to display
     */
    public void setPredictionSummaryText(String summaryText) {
        predictionSummaryLabel.setText(summaryText);
    }

    public void setResultRow(
            int rowIndex,
            int carIndex,
            String placeText,
            String carName,
            String timeText,
            String averageSpeedText,
            String topSpeedText
    ) {
        // Older calls can still use this shorter version until route text is fully wired in.
        setResultRow(
                rowIndex,
                carIndex,
                placeText,
                carName,
                DEFAULT_ROUTE_TEXT,
                timeText,
                averageSpeedText,
                topSpeedText
        );
    }

    public void setResultRow(
            int rowIndex,
            int carIndex,
            String placeText,
            String carName,
            String routeText,
            String timeText,
            String averageSpeedText,
            String topSpeedText
    ) {
        validateRowIndex(rowIndex);

        placeLabels[rowIndex].setText(placeText);
        setRowSprite(rowIndex, carIndex);
        carNameLabels[rowIndex].setText(carName);
        routeLabels[rowIndex].setText(routeText);
        timeLabels[rowIndex].setText(timeText);
        averageSpeedLabels[rowIndex].setText(averageSpeedText);
        topSpeedLabels[rowIndex].setText(topSpeedText);
    }

    /**
     * Shows placeholder row values until real race results are loaded.
     */
    public void showPlaceholderResults() {
        predictionSummaryLabel.setText("Finish the race to see whether the prediction was correct.");

        setResultRow(0, 0, "1st", "Car 1", "Assigned when race starts", "Placeholder", "Placeholder", "Placeholder");
        setResultRow(1, 1, "2nd", "Car 2", "Assigned when race starts", "Placeholder", "Placeholder", "Placeholder");
        setResultRow(2, 2, "3rd", "Car 3", "Assigned when race starts", "Placeholder", "Placeholder", "Placeholder");
        setResultRow(3, 3, "4th", "Car 4", "Assigned when race starts", "Placeholder", "Placeholder", "Placeholder");
    }

    /**
     * Clears the results table to its empty state.
     */
    public void clearResults() {
        predictionSummaryLabel.setText("Race results will appear here.");

        for (int row = 0; row < RESULT_ROW_COUNT; row++) {
            setResultRow(row, row, "-", "-", DEFAULT_ROUTE_TEXT, "-", "-", "-");
        }
    }

    private void setRowSprite(int rowIndex, int carIndex) {
        validateRowIndex(rowIndex);

        // Rebuild the sprite so the row matches the car currently shown in that place.
        Node spriteGraphic = CarSpriteLoader.createCarGraphic(
                CarSpriteLoader.getSpriteForIndex(carIndex),
                CarSpriteLoader.DEFAULT_SPRITE_WIDTH,
                CarSpriteLoader.DEFAULT_SPRITE_HEIGHT
        );

        carSpriteHolders[rowIndex].getChildren().setAll(spriteGraphic);
    }

    private void validateRowIndex(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= RESULT_ROW_COUNT) {
            throw new IllegalArgumentException("Unknown result row: " + rowIndex);
        }
    }
}
