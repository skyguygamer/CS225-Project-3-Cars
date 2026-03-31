package org.cs225.GUI;

/*
    Gabriel worked on this class

    This class is 
 */

import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import org.cs225.Car;
import org.cs225.RaceGameApp;
import org.cs225.RaceManager;

public class RaceView {

    private static final boolean DEBUG_TRACK_GEOMETRY = false;

    private static final Point2D[] PLACEHOLDER_TRACK_POINTS = {
            // Main checkpoint A
            new Point2D(480, 30),
            // Mini-point between A and B
            new Point2D(637, 219),
            // Main checkpoint B
            new Point2D(880, 250),
            // Mini-point between B and D
            new Point2D(723, 439),
            // Main checkpoint D
            new Point2D(480, 470),
            // Mini-point between D and C
            new Point2D(323, 281),
            // Main checkpoint C
            new Point2D(80, 250),
            // Mini-point between C and A
            new Point2D(237, 61)
    };

    private static final int[] MAIN_CHECKPOINT_INDICES = {0, 2, 4, 6};
    private static final String[] MAIN_CHECKPOINT_LABELS = {"A", "B", "D", "C"};
    private static final Point2D[] MAIN_CHECKPOINT_LABEL_OFFSETS = {
            new Point2D(-6, -18),
            new Point2D(18, -6),
            new Point2D(-6, 20),
            new Point2D(-30, -6)
    };
    private static final Color[] DEBUG_POINT_COLORS = {
            Color.CRIMSON,
            Color.DARKORANGE,
            Color.DODGERBLUE,
            Color.SEAGREEN,
            Color.MEDIUMPURPLE,
            Color.SIENNA,
            Color.DEEPPINK,
            Color.TEAL
    };

    private final BorderPane root;
    private final Pane trackPane;
    private final Label raceStatusLabel;
    private final Label predictionLabel;
    private final Label mousePositionLabel;
    private final StackPane[] carMarkers;
    private final Polyline routeOverlay;
    private RaceManager raceManager;

    public RaceView(RaceGameApp app) {
        root = new BorderPane();
        trackPane = new Pane();
        raceStatusLabel = new Label("Placeholder race in progress.");
        predictionLabel = new Label("Predicted winner: none selected.");
        mousePositionLabel = new Label("Mouse: (--, --)");
        carMarkers = new StackPane[MAIN_CHECKPOINT_INDICES.length];
        routeOverlay = new Polyline();

        buildLayout(app);
        drawPlaceholderTrack();
        resetForNewRace();
    }

    private void buildLayout(RaceGameApp app) {
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: whitesmoke;");

        Label titleLabel = new Label("Race Screen");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        raceStatusLabel.setStyle("-fx-font-size: 16px;");
        predictionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");

        VBox headerBox = new VBox(8, titleLabel, raceStatusLabel, predictionLabel);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        trackPane.setPrefSize(960, 500);
        trackPane.setMinSize(960, 500);
        trackPane.setStyle(
                "-fx-background-color: white;"
                        + "-fx-border-color: #cccccc;"
                        + "-fx-border-width: 2;"
                        + "-fx-background-radius: 10;"
                        + "-fx-border-radius: 10;"
        );

        Label noteLabel = new Label(
                "TODO: Replace this ordered placeholder loop with teammate Stop, Track, and Route data."
        );
        noteLabel.setWrapText(true);
        noteLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666;");

        mousePositionLabel.setStyle(
                "-fx-font-size: 12px;"
                        + "-fx-font-family: 'Consolas';"
                        + "-fx-background-color: rgba(255,255,255,0.9);"
                        + "-fx-padding: 4 8 4 8;"
                        + "-fx-background-radius: 6;"
                        + "-fx-border-color: #bbbbbb;"
                        + "-fx-border-radius: 6;"
        );
        mousePositionLabel.setVisible(DEBUG_TRACK_GEOMETRY);
        mousePositionLabel.setManaged(DEBUG_TRACK_GEOMETRY);

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        Button finishRaceButton = new Button("Finish Placeholder Race");
        finishRaceButton.setOnAction(event -> {
            // TODO: When real simulation timing is ready, let the controller decide when the race ends.
            app.showResults();
        });

        HBox footerBox = new HBox(15, finishRaceButton, noteLabel, footerSpacer, mousePositionLabel);
        footerBox.setAlignment(Pos.CENTER_LEFT);

        root.setTop(headerBox);
        root.setCenter(trackPane);
        root.setBottom(footerBox);

        BorderPane.setMargin(headerBox, new Insets(0, 0, 18, 0));
        BorderPane.setMargin(trackPane, new Insets(0, 0, 18, 0));

        configureMouseTracking();
    }

    private void drawPlaceholderTrack() {
        Polyline trackOutline = new Polyline();
        addClosedLoopPoints(trackOutline);
        trackOutline.setStroke(Color.DIMGRAY);
        trackOutline.setStrokeWidth(30);
        trackOutline.setFill(null);
        trackOutline.setOpacity(0.5);

        Polyline centerLine = new Polyline();
        addClosedLoopPoints(centerLine);
        centerLine.setStroke(Color.BLACK);
        centerLine.setStrokeWidth(2.5);
        centerLine.getStrokeDashArray().addAll(12.0, 10.0);
        centerLine.setFill(null);

        routeOverlay.setStroke(Color.GOLDENROD);
        routeOverlay.setStrokeWidth(5);
        routeOverlay.getStrokeDashArray().addAll(10.0, 8.0);
        routeOverlay.setVisible(false);
        routeOverlay.setFill(null);

        trackPane.getChildren().addAll(trackOutline, centerLine, routeOverlay);

        for (int i = 0; i < PLACEHOLDER_TRACK_POINTS.length; i++) {
            Point2D trackPoint = PLACEHOLDER_TRACK_POINTS[i];

            if (isMainCheckpointIndex(i)) {
                int checkpointNumber = i / 2;
                drawMainCheckpoint(trackPoint, checkpointNumber);
            } else {
                drawMiniPoint(trackPoint);
            }
        }

        for (int i = 0; i < carMarkers.length; i++) {
            StackPane carMarker = createCarMarker(i);
            carMarkers[i] = carMarker;
            trackPane.getChildren().add(carMarker);
        }

        if (DEBUG_TRACK_GEOMETRY) {
            drawDebugGeometryOverlay();
        }
    }

    private StackPane createCarMarker(int carIndex) {
        Node carGraphic = CarSpriteLoader.createCarGraphic(
                CarSpriteLoader.getSpriteForIndex(carIndex),
                CarSpriteLoader.DEFAULT_SPRITE_WIDTH,
                CarSpriteLoader.DEFAULT_SPRITE_HEIGHT
        );

        Label carLabel = new Label("C" + (carIndex + 1));
        carLabel.setStyle(
                "-fx-font-size: 10px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-color: rgba(255,255,255,0.85);"
                        + "-fx-padding: 2 5 2 5;"
                        + "-fx-background-radius: 10;"
                        + "-fx-border-color: black;"
                        + "-fx-border-radius: 10;"
        );
        carLabel.setTranslateY(-26);

        StackPane carMarker = new StackPane(carGraphic, carLabel);
        carMarker.setMinSize(CarSpriteLoader.DEFAULT_SPRITE_WIDTH, CarSpriteLoader.DEFAULT_SPRITE_HEIGHT);
        carMarker.setPrefSize(CarSpriteLoader.DEFAULT_SPRITE_WIDTH, CarSpriteLoader.DEFAULT_SPRITE_HEIGHT);
        carMarker.setMaxSize(CarSpriteLoader.DEFAULT_SPRITE_WIDTH, CarSpriteLoader.DEFAULT_SPRITE_HEIGHT);
        return carMarker;
    }

    public Parent getRoot() {
        return root;
    }

    public void setRaceStatus(String statusText) {
        raceStatusLabel.setText(statusText);
    }

    public void setPredictedCarName(String carName) {
        predictionLabel.setText("Predicted winner: " + carName);
    }

    public void setRaceManager(RaceManager manager) {
        raceManager = manager;
    }

    public void renderFromRaceManager() {
        if (raceManager == null) {
            return;
        }

        List<Car> cars = raceManager.getCars();
        int visibleCarCount = Math.min(cars.size(), carMarkers.length);

        for (int i = 0; i < visibleCarCount; i++) {
            Car car = cars.get(i);
            updateCarPosition(i, car.getXPos(), car.getYPos());
        }
    }

    public void updateCarPosition(int carIndex, double centerX, double centerY) {
        validateCarIndex(carIndex);

        StackPane carMarker = carMarkers[carIndex];
        carMarker.setLayoutX(centerX - (carMarker.getPrefWidth() / 2.0));
        carMarker.setLayoutY(centerY - (carMarker.getPrefHeight() / 2.0));
    }

    public void showPredictedRoute(List<Point2D> routePoints) {
        routeOverlay.getPoints().clear();

        if (routePoints == null || routePoints.isEmpty()) {
            routeOverlay.setVisible(false);
            return;
        }

        for (Point2D point : routePoints) {
            routeOverlay.getPoints().addAll(point.getX(), point.getY());
        }

        routeOverlay.setVisible(true);
    }

    public void clearPredictedRoute() {
        routeOverlay.getPoints().clear();
        routeOverlay.setVisible(false);
    }

    public void resetForNewRace() {
        setRaceStatus("Placeholder race in progress.");
        predictionLabel.setText("Predicted winner: none selected.");
        clearPredictedRoute();

        for (int i = 0; i < MAIN_CHECKPOINT_INDICES.length; i++) {
            Point2D startingPoint = PLACEHOLDER_TRACK_POINTS[MAIN_CHECKPOINT_INDICES[i]];
            updateCarPosition(i, startingPoint.getX(), startingPoint.getY());
        }
    }

    private void addClosedLoopPoints(Polyline polyline) {
        for (Point2D trackPoint : PLACEHOLDER_TRACK_POINTS) {
            polyline.getPoints().addAll(trackPoint.getX(), trackPoint.getY());
        }

        Point2D firstPoint = PLACEHOLDER_TRACK_POINTS[0];
        polyline.getPoints().addAll(firstPoint.getX(), firstPoint.getY());
    }

    private void drawMainCheckpoint(Point2D checkpointPoint, int checkpointNumber) {
        Polygon checkpointMarker = new Polygon(
                checkpointPoint.getX(), checkpointPoint.getY() - 12,
                checkpointPoint.getX() + 12, checkpointPoint.getY(),
                checkpointPoint.getX(), checkpointPoint.getY() + 12,
                checkpointPoint.getX() - 12, checkpointPoint.getY()
        );
        checkpointMarker.setFill(Color.web("#fff2b3"));
        checkpointMarker.setStroke(Color.BLACK);
        checkpointMarker.setStrokeWidth(2.5);

        Point2D labelOffset = MAIN_CHECKPOINT_LABEL_OFFSETS[checkpointNumber];
        Label checkpointLabel = new Label(MAIN_CHECKPOINT_LABELS[checkpointNumber]);
        checkpointLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        checkpointLabel.setLayoutX(checkpointPoint.getX() + labelOffset.getX());
        checkpointLabel.setLayoutY(checkpointPoint.getY() + labelOffset.getY());

        trackPane.getChildren().addAll(checkpointMarker, checkpointLabel);
    }

    private void drawMiniPoint(Point2D miniPoint) {
        Circle miniPointMarker = new Circle(miniPoint.getX(), miniPoint.getY(), 6);
        miniPointMarker.setFill(Color.WHITE);
        miniPointMarker.setStroke(Color.BLACK);
        miniPointMarker.setStrokeWidth(1.5);

        trackPane.getChildren().add(miniPointMarker);
    }

    private void drawDebugGeometryOverlay() {
        for (int i = 0; i < PLACEHOLDER_TRACK_POINTS.length; i++) {
            Point2D trackPoint = PLACEHOLDER_TRACK_POINTS[i];
            Color debugColor = DEBUG_POINT_COLORS[i];

            Circle debugRing = new Circle(trackPoint.getX(), trackPoint.getY(), isMainCheckpointIndex(i) ? 18 : 13);
            debugRing.setFill(Color.TRANSPARENT);
            debugRing.setStroke(debugColor);
            debugRing.setStrokeWidth(2.5);

            Circle debugCenterDot = new Circle(trackPoint.getX(), trackPoint.getY(), 4.5);
            debugCenterDot.setFill(debugColor);
            debugCenterDot.setStroke(Color.BLACK);
            debugCenterDot.setStrokeWidth(1.0);

            Label debugLabel = createDebugPointLabel(i, trackPoint, debugColor);

            trackPane.getChildren().addAll(debugRing, debugCenterDot, debugLabel);
        }
    }

    private Label createDebugPointLabel(int pointIndex, Point2D trackPoint, Color debugColor) {
        String pointName = getDebugPointName(pointIndex);
        String pointType = isMainCheckpointIndex(pointIndex) ? "main checkpoint" : "mini-point";

        Label debugLabel = new Label(
                pointIndex + " | " + pointName + " | " + pointType
                        + "\n(" + Math.round(trackPoint.getX()) + ", " + Math.round(trackPoint.getY()) + ")"
        );
        debugLabel.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-color: rgba(255,255,255,0.88);"
                        + "-fx-padding: 4 6 4 6;"
                        + "-fx-background-radius: 6;"
        );
        debugLabel.setTextFill(debugColor);

        Point2D labelOffset = getDebugLabelOffset(pointIndex);
        debugLabel.setLayoutX(trackPoint.getX() + labelOffset.getX());
        debugLabel.setLayoutY(trackPoint.getY() + labelOffset.getY());
        return debugLabel;
    }

    private String getDebugPointName(int pointIndex) {
        if (isMainCheckpointIndex(pointIndex)) {
            return MAIN_CHECKPOINT_LABELS[pointIndex / 2];
        }

        return "m" + ((pointIndex / 2) + 1);
    }

    private Point2D getDebugLabelOffset(int pointIndex) {
        return switch (pointIndex) {
            case 0 -> new Point2D(18, 10);
            case 1 -> new Point2D(16, -18);
            case 2 -> new Point2D(-128, -6);
            case 3 -> new Point2D(16, 6);
            case 4 -> new Point2D(18, -46);
            case 5 -> new Point2D(-110, 6);
            case 6 -> new Point2D(18, -6);
            case 7 -> new Point2D(-110, -18);
            default -> new Point2D(12, 12);
        };
    }

    private void configureMouseTracking() {
        if (!DEBUG_TRACK_GEOMETRY) {
            return;
        }

        trackPane.setOnMouseMoved(event -> mousePositionLabel.setText(
                "Mouse: (" + Math.round(event.getX()) + ", " + Math.round(event.getY()) + ")"
        ));

        trackPane.setOnMouseExited(event -> mousePositionLabel.setText("Mouse: (--, --)"));
    }

    private boolean isMainCheckpointIndex(int pointIndex) {
        return pointIndex % 2 == 0;
    }

    private void validateCarIndex(int carIndex) {
        if (carIndex < 0 || carIndex >= carMarkers.length) {
            throw new IllegalArgumentException("Unknown car index: " + carIndex);
        }
    }
}
