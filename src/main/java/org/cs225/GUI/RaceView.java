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
import org.cs225.Track.Stop;

public class RaceView {

    private static final boolean DEBUG_TRACK_GEOMETRY = false; // switch to true to enable helpful debugging tools
    private static final boolean DEBUG_RACE_HUD = false;

    private static final Point2D[] PLACEHOLDER_TRACK_POINTS = {
            // Main checkpoint A
            new Point2D(480, 30),
            // Mini-point between A and B
            new Point2D(637, 219),
            // Main checkpoint B
            new Point2D(880, 250),
            // Mini-point between B and C
            new Point2D(723, 439),
            // Main checkpoint C
            new Point2D(480, 470),
            // Mini-point between C and D
            new Point2D(323, 281),
            // Main checkpoint D
            new Point2D(80, 250),
            // Mini-point between D and A
            new Point2D(237, 61)
    };

    private static final int[] MAIN_CHECKPOINT_INDICES = {0, 2, 4, 6};
    private static final String[] MAIN_CHECKPOINT_LABELS = {"A", "B", "C", "D"};
    private static final Point2D[] MAIN_CHECKPOINT_LABEL_OFFSETS = {
            new Point2D(-6, -30),
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
    private static final Color[] DEBUG_CAR_MARKER_COLORS = {
            Color.DODGERBLUE,
            Color.CRIMSON,
            Color.FORESTGREEN,
            Color.DARKVIOLET
    };
    private static final String[] DEBUG_CAR_TEXT_COLORS = {
            "#1565c0",
            "#c62828",
            "#2e7d32",
            "#6a1b9a"
    };

    private final BorderPane root;
    private final StackPane trackContainer;
    private final Pane trackPane;
    private final Label raceStatusLabel;
    private final Label predictionLabel;
    private final Label mousePositionLabel;
    private final VBox debugHudBox;
    private final Label debugClockLabel;
    private final StackPane[] carMarkers;
    private final Circle[] targetMarkers;
    private final Label[] carDebugLabels;
    private final Polyline routeOverlay;
    private RaceManager raceManager;
    private int progressCarIndex;
    private int predictedRouteCarIndex;

    public RaceView(RaceGameApp app) {
        root = new BorderPane();
        trackContainer = new StackPane();
        trackPane = new Pane();
        raceStatusLabel = new Label("Placeholder race in progress.");
        predictionLabel = new Label("Predicted winner: none selected.");
        mousePositionLabel = new Label("Mouse: (--, --)");
        debugHudBox = new VBox(6);
        debugClockLabel = new Label("Clock: 0.00s | running=false | finished=false");
        carMarkers = new StackPane[MAIN_CHECKPOINT_INDICES.length];
        targetMarkers = new Circle[MAIN_CHECKPOINT_INDICES.length];
        carDebugLabels = new Label[MAIN_CHECKPOINT_INDICES.length];
        routeOverlay = new Polyline();
        progressCarIndex = -1;
        predictedRouteCarIndex = -1;

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

        trackContainer.setPrefSize(960, 500);
        trackContainer.setMinSize(960, 500);
        trackContainer.setAlignment(Pos.TOP_LEFT);
        trackContainer.getChildren().add(trackPane);

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
        root.setCenter(trackContainer);
        root.setBottom(footerBox);

        BorderPane.setMargin(headerBox, new Insets(0, 0, 18, 0));
        BorderPane.setMargin(trackContainer, new Insets(0, 0, 18, 0));

        configureDebugHud();
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

        for (int i = 0; i < targetMarkers.length; i++) {
            Circle targetMarker = createTargetMarker(i);
            targetMarkers[i] = targetMarker;
            trackPane.getChildren().add(targetMarker);
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

    public void setProgressCarIndex(int carIndex) {
        progressCarIndex = carIndex;
    }

    public void setPredictedRouteCarIndex(int carIndex) {
        predictedRouteCarIndex = carIndex;
    }

    public void setRaceManager(RaceManager manager) {
        raceManager = manager;

        if (manager == null) {
            resetDebugHud();
            clearPredictedRoute();
        }
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

        updatePredictedRouteOverlay(cars);
        updateRaceProgressLabel(cars);
        updateDebugHud(cars);
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
        progressCarIndex = -1;
        predictedRouteCarIndex = -1;

        for (int i = 0; i < MAIN_CHECKPOINT_INDICES.length; i++) {
            Point2D startingPoint = PLACEHOLDER_TRACK_POINTS[MAIN_CHECKPOINT_INDICES[i]];
            updateCarPosition(i, startingPoint.getX(), startingPoint.getY());
        }

        resetDebugHud();
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

    private void configureDebugHud() {
        debugHudBox.setPrefWidth(325);
        debugHudBox.setMinWidth(325);
        debugHudBox.setMaxWidth(325);
        debugHudBox.setSpacing(8);
        debugHudBox.setPadding(new Insets(12));
        debugHudBox.setStyle(
                "-fx-background-color: rgba(255,255,255,0.96);"
                        + "-fx-background-radius: 12;"
                        + "-fx-border-color: #bdbdbd;"
                        + "-fx-border-radius: 12;"
                        + "-fx-border-width: 1.5;"
        );
        debugHudBox.setVisible(DEBUG_RACE_HUD);
        debugHudBox.setManaged(DEBUG_RACE_HUD);
        debugHudBox.setMouseTransparent(true);

        Label debugTitleLabel = new Label("Live Race Debug");
        debugTitleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        debugClockLabel.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-font-family: 'Consolas';"
                        + "-fx-font-weight: bold;"
        );
        debugClockLabel.setWrapText(true);

        Label debugLegendLabel = new Label(
                "Each row shows a car's full route, current target, live speed, and finish state."
        );
        debugLegendLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #555555;");
        debugLegendLabel.setWrapText(true);

        debugHudBox.getChildren().addAll(debugTitleLabel, debugClockLabel, debugLegendLabel);

        for (int i = 0; i < carDebugLabels.length; i++) {
            Label carDebugLabel = createCarDebugLabel(i);
            carDebugLabels[i] = carDebugLabel;
            debugHudBox.getChildren().add(carDebugLabel);
        }

        if (DEBUG_RACE_HUD) {
            StackPane.setAlignment(debugHudBox, Pos.TOP_RIGHT);
            StackPane.setMargin(debugHudBox, new Insets(14));
            trackContainer.getChildren().add(debugHudBox);
        }
    }

    private Label createCarDebugLabel(int carIndex) {
        Label carDebugLabel = new Label();
        carDebugLabel.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-font-family: 'Consolas';"
                        + "-fx-background-color: rgba(248,248,248,0.98);"
                        + "-fx-padding: 8 10 8 10;"
                        + "-fx-background-radius: 8;"
                        + "-fx-border-color: #d0d0d0;"
                        + "-fx-border-radius: 8;"
        );
        carDebugLabel.setTextFill(Color.web(DEBUG_CAR_TEXT_COLORS[carIndex]));
        carDebugLabel.setWrapText(true);
        carDebugLabel.setPrefWidth(301);
        carDebugLabel.setMinHeight(Region.USE_PREF_SIZE);
        carDebugLabel.setMaxWidth(Double.MAX_VALUE);
        return carDebugLabel;
    }

    private Circle createTargetMarker(int carIndex) {
        Circle targetMarker = new Circle(14);
        targetMarker.setFill(Color.TRANSPARENT);
        targetMarker.setStroke(DEBUG_CAR_MARKER_COLORS[carIndex]);
        targetMarker.setStrokeWidth(3);
        targetMarker.setVisible(false);
        targetMarker.setMouseTransparent(true);
        return targetMarker;
    }

    private void updateDebugHud(List<Car> cars) {
        if (!DEBUG_RACE_HUD) {
            return;
        }

        debugClockLabel.setText(
                "Clock: " + formatDebugNumber(raceManager.getElapsedTime()) + "s"
                        + " | running=" + raceManager.isRunning()
                        + " | finished=" + raceManager.isRaceFinished()
        );

        for (int i = 0; i < carDebugLabels.length; i++) {
            if (i < cars.size()) {
                Car car = cars.get(i);
                carDebugLabels[i].setText(buildCarDebugText(i, car));
                updateTargetMarker(i, car.getCurrentTargetStop());
            } else {
                carDebugLabels[i].setText("C" + (i + 1) + " | no car data");
                targetMarkers[i].setVisible(false);
            }
        }
    }

    private void resetDebugHud() {
        if (!DEBUG_RACE_HUD) {
            return;
        }

        debugClockLabel.setText("Clock: 0.00s | running=false | finished=false");

        for (int i = 0; i < carDebugLabels.length; i++) {
            carDebugLabels[i].setText("C" + (i + 1) + " | waiting for route data");
            targetMarkers[i].setVisible(false);
        }
    }

    private void updateTargetMarker(int carIndex, Stop targetStop) {
        Circle targetMarker = targetMarkers[carIndex];

        if (targetStop == null) {
            targetMarker.setVisible(false);
            return;
        }

        targetMarker.setCenterX(targetStop.getxPos());
        targetMarker.setCenterY(targetStop.getyPos());
        targetMarker.setVisible(true);
    }

    private void updatePredictedRouteOverlay(List<Car> cars) {
        if (predictedRouteCarIndex < 0 || predictedRouteCarIndex >= cars.size()) {
            clearPredictedRoute();
            return;
        }

        Car predictedCar = cars.get(predictedRouteCarIndex);
        List<Stop> routeStops = predictedCar.getPathway();
        int nextStopIndex = predictedCar.getCurrentStopIndex() + 1;

        routeOverlay.getPoints().clear();

        if (predictedCar.isFinished() || nextStopIndex >= routeStops.size()) {
            routeOverlay.setVisible(false);
            return;
        }

        routeOverlay.getPoints().addAll(predictedCar.getXPos(), predictedCar.getYPos());

        for (int i = nextStopIndex; i < routeStops.size(); i++) {
            Stop stop = routeStops.get(i);
            routeOverlay.getPoints().addAll(stop.getxPos(), stop.getyPos());
        }

        routeOverlay.setVisible(true);
    }

    private void updateRaceProgressLabel(List<Car> cars) {
        if (cars.isEmpty()) {
            setRaceStatus("Race progress unavailable.");
            return;
        }

        int activeProgressCarIndex = progressCarIndex;
        if (activeProgressCarIndex < 0 || activeProgressCarIndex >= cars.size()) {
            activeProgressCarIndex = 0;
        }

        Car trackedCar = cars.get(activeProgressCarIndex);
        Stop currentStop = trackedCar.getCurrentStop();
        Stop targetStop = trackedCar.getCurrentTargetStop();

        if (trackedCar.isFinished()) {
            setRaceStatus(
                    "Progress: finished at " + formatStopName(currentStop)
            );
            return;
        }

        int currentLegNumber = trackedCar.getCurrentLegIndex() + 1;
        int totalLegCount = trackedCar.getTotalLegCount();

        setRaceStatus(
                "Progress: " + formatStopName(currentStop)
                        + " -> " + formatStopName(targetStop)
                        + " (" + currentLegNumber + "/" + totalLegCount + ")"
        );
    }

    private String buildCarDebugText(int carIndex, Car car) {
        String legProgress;

        if (car.isFinished()) {
            legProgress = car.getTotalLegCount() + "/" + car.getTotalLegCount();
        } else {
            legProgress = (car.getCurrentLegIndex() + 1) + "/" + car.getTotalLegCount();
        }

        return "C" + (carIndex + 1)
                + " | leg " + legProgress
                + " | target " + formatStopName(car.getCurrentTargetStop())
                + " | speed " + formatDebugNumber(car.getSpeed())
                + "\nroute: " + car.getRouteSummary()
                + "\nfrom " + formatStopName(car.getCurrentStop())
                + " | xy=(" + Math.round(car.getXPos()) + ", " + Math.round(car.getYPos()) + ")"
                + " | done=" + car.isFinished();
    }

    private String formatStopName(Stop stop) {
        if (stop == null) {
            return "FINISH";
        }

        return stop.getName();
    }

    private String formatDebugNumber(double value) {
        return String.format("%.2f", value);
    }

    private void validateCarIndex(int carIndex) {
        if (carIndex < 0 || carIndex >= carMarkers.length) {
            throw new IllegalArgumentException("Unknown car index: " + carIndex);
        }
    }
}
