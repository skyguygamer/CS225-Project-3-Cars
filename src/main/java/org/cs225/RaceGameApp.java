/**
 * @author Gabriel
 * @author Matthew
 * 
 * Controls the various scenes of the program as well as runs the loop that handles the game logic
 * 
 */

package org.cs225;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cs225.GUI.*;
import org.cs225.Track.*;

public class RaceGameApp extends Application {

    public static final int INTRO_SCENE = 0;
    public static final int RACE_SCENE = 1;
    public static final int RESULTS_SCENE = 2;

    public static final int TICKS_PER_SECOND = 100;
    private final long TICK_LENGTH = TimeUnit.SECONDS.toNanos(1L) / TICKS_PER_SECOND;
    private long startTime = System.nanoTime();

    private static final Point2D[] TRACK_POINTS = {
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

    private static final double WINDOW_WIDTH = 1000;
    private static final double WINDOW_HEIGHT = 700;
    private static final int TRACK_PANEL_WIDTH = 960;
    private static final int TRACK_PANEL_HEIGHT = 500;
    private static final int TRACK_STOP_COUNT = 8;
    private static final int CAR_COUNT = 4;
    private static final String[] CAR_NAMES = {"Car 1", "Car 2", "Car 3", "Car 4"};

    private Stage primaryStage;

    // TODO: Feed these views with real controller/model data once teammate integration is ready.
    private IntroView introView;
    private RaceView raceView;
    private ResultsView resultsView;

    private Scene introScene;
    private Scene raceScene;
    private Scene resultsScene;

    private RaceManager gameRace;
    private AnimationTimer animator;
    private int predictedCarIndex = -1;

    /**
     * Starts the application and shows the intro scene.
     *
     * @param stage the primary JavaFX stage
     */
    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        buildScenes();
        animator = createAnimator();

        primaryStage.setTitle("Project 3 - Racing Simulator");
        changeScene(INTRO_SCENE);
        primaryStage.show();
    }

    private void buildScenes() {
        introView = new IntroView(this);
        raceView = new RaceView(this);
        resultsView = new ResultsView(this);

        introScene = new Scene(introView.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);
        raceScene = new Scene(raceView.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);
        resultsScene = new Scene(resultsView.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    /**
     * Switches the main window to the requested scene.
     *
     * @param sceneNumber the scene constant to display
     */
    public void changeScene(int sceneNumber) {
        if (sceneNumber == INTRO_SCENE) {
            primaryStage.setScene(introScene);
        } else if (sceneNumber == RACE_SCENE) {
            primaryStage.setScene(raceScene);
        } else if (sceneNumber == RESULTS_SCENE) {
            primaryStage.setScene(resultsScene);
        } else {
            throw new IllegalArgumentException("Unknown scene number: " + sceneNumber);
        }
    }

    private AnimationTimer createAnimator() {
        return new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Each animation pulse advances the race once, then redraws the scene.
                long currentTime = System.nanoTime();
                if( TICK_LENGTH <= currentTime - startTime)
                {
                    update();
                    render();
                    startTime = currentTime;
                };

                if (gameRace != null && gameRace.isRaceFinished()) {
                    raceView.setRaceStatus("Race finished.");
                    showResults();
                }
            }
        };
    }

    /**
     * Sets up and starts a new race for the selected predicted winner.
     *
     * @param selectedCarIndex the predicted winner chosen in the intro view
     */
    public void startRace(int selectedCarIndex) {
        predictedCarIndex = selectedCarIndex;

        gameRace = new RaceManager();
        gameRace.setupRace(new Track(TRACK_PANEL_WIDTH, TRACK_PANEL_HEIGHT, TRACK_POINTS), CAR_COUNT);

        if (selectedCarIndex >= 0 && selectedCarIndex < gameRace.getCars().size()) {
            gameRace.setUserPrediction(gameRace.getCars().get(selectedCarIndex).getCarName());
        }

        gameRace.startRace();

        raceView.resetForNewRace();
        raceView.setRaceManager(gameRace);
        raceView.setProgressCarIndex(selectedCarIndex);
        raceView.setPredictedRouteCarIndex(selectedCarIndex);
        raceView.setPredictedCarName(CAR_NAMES[selectedCarIndex]);
        raceView.renderFromRaceManager();
        resultsView.clearResults();

        if (gameRace.isRunning()) {
            animator.start();
        } else {
            raceView.setRaceStatus("Race could not start.");
        }

        changeScene(RACE_SCENE);
    }

    /**
     * Stops the animation loop and switches to the results screen.
     */
    public void showResults() {
        if (animator != null) {
            animator.stop();
        }

        if (gameRace != null && gameRace.getWinner() != null) {
            loadResultsFromRace();
        } else {
            if (predictedCarIndex >= 0) {
                resultsView.setPredictionSummaryText(
                        "Race ended before official results were available."
                );
            } else {
                resultsView.setPredictionSummaryText("No prediction was selected before the race.");
            }

            loadPlaceholderResults();
        }
        changeScene(RESULTS_SCENE);
    }

    /**
     * Resets app state and returns to the intro screen.
     */
    public void restartRace() {
        if (animator != null) {
            animator.stop();
        }

        predictedCarIndex = -1;
        gameRace = null;
        introView.clearSelection();
        raceView.setRaceManager(null);
        raceView.resetForNewRace();
        resultsView.showPlaceholderResults();

        // TODO: Reset shared controller/model state here once the final controller owns race setup.
        changeScene(INTRO_SCENE);
    }

    private void loadResultsFromRace() {
        Car winner = gameRace.getWinner();

        if (predictedCarIndex >= 0 && predictedCarIndex < CAR_NAMES.length) {
            resultsView.setPredictionSummary(
                    CAR_NAMES[predictedCarIndex],
                    formatCarDisplayName(winner.getCarName()),
                    gameRace.checkPrediction()
            );
        } else {
            resultsView.setPredictionSummaryText(
                    "No prediction was selected before the race. Winner: " + formatCarDisplayName(winner.getCarName())
            );
        }

        List<Car> carsInOriginalOrder = new ArrayList<>(gameRace.getCars());
        List<Car> rankedCars = new ArrayList<>(carsInOriginalOrder);
        // Sort a copy so we can show finish order without losing each car's original index.
        rankedCars.sort(Comparator.comparingDouble(Car::getFinishTime));

        for (int row = 0; row < rankedCars.size() && row < CAR_NAMES.length; row++) {
            Car car = rankedCars.get(row);
            // The original index is still used to pick the correct sprite color in the results table.
            int carIndex = carsInOriginalOrder.indexOf(car);

            resultsView.setResultRow(
                    row,
                    Math.max(carIndex, 0),
                    formatPlace(row + 1),
                    formatCarDisplayName(car.getCarName()),
                    formatRaceTime(car.getFinishTime()),
                    formatAverageSpeed(car),
                    formatTopSpeed(car)
            );
        }

        if (predictedCarIndex >= 0) {
            // TODO: Replace this placeholder overlay with the teammate-provided route once route data is exposed.
            raceView.showPredictedRoute(createPlaceholderRouteForCar(predictedCarIndex));
        }
    }

    private void loadPlaceholderResults() {
        resultsView.setResultRow(0, 1, "1st", CAR_NAMES[1], "01:18.42", "92 mph", "118 mph");
        resultsView.setResultRow(1, 3, "2nd", CAR_NAMES[3], "01:20.10", "89 mph", "114 mph");
        resultsView.setResultRow(2, 0, "3rd", CAR_NAMES[0], "01:22.03", "87 mph", "110 mph");
        resultsView.setResultRow(3, 2, "4th", CAR_NAMES[2], "01:24.91", "84 mph", "107 mph");

        // TODO: When the real route data exists, consider showing the predicted route on RaceView
        // before switching to the results scene.
        if (predictedCarIndex >= 0) {
            raceView.showPredictedRoute(createPlaceholderRouteForCar(predictedCarIndex));
        }
    }

    private List<Point2D> createPlaceholderRouteForCar(int carIndex) {
        if (carIndex == 0) {
            return List.of(
                    new Point2D(480, 30),
                    new Point2D(637, 219),
                    new Point2D(880, 250),
                    new Point2D(723, 439)
            );
        } else if (carIndex == 1) {
            return List.of(
                    new Point2D(880, 250),
                    new Point2D(723, 439),
                    new Point2D(480, 470),
                    new Point2D(323, 281)
            );
        } else if (carIndex == 2) {
            return List.of(
                    new Point2D(480, 470),
                    new Point2D(323, 281),
                    new Point2D(80, 250),
                    new Point2D(237, 61)
            );
        }

        return List.of(
                new Point2D(80, 250),
                new Point2D(237, 61),
                new Point2D(480, 30),
                new Point2D(637, 219)
        );
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Advances the simulation by one step from the JavaFX animation loop.
     */
    public void update() {
        if (gameRace == null) {
            return;
        }

        gameRace.updateTick();
    }

    /**
     * Re-renders the race scene from the latest race data.
     */
    public void render() {
        raceView.renderFromRaceManager();
    }

    private String formatPlace(int placeNumber) {
        return switch (placeNumber) {
            case 1 -> "1st";
            case 2 -> "2nd";
            case 3 -> "3rd";
            default -> placeNumber + "th";
        };
    }

    private String formatRaceTime(double totalSeconds) {
        int minutes = (int) (totalSeconds / 60);
        double seconds = totalSeconds - (minutes * 60);
        return String.format("%02d:%05.2f", minutes, seconds);
    }

    private String formatAverageSpeed(Car car) {
        if (car.getFinishTime() <= 0) {
            return "-";
        }

        // TODO: Replace generic numeric formatting once teammate speed units are finalized.
        double averageSpeed = (car.getDistance() / car.getFinishTime()) /TICKS_PER_SECOND;
        return String.format("%.2f", averageSpeed);
    }

    private String formatTopSpeed(Car car) {
        // TODO: Replace generic numeric formatting once teammate speed units are finalized.
        return String.format("%.2f", car.getTopSpeed());
    }

    private String formatCarDisplayName(String carName) {
        if (carName != null && carName.matches("Car\\d+")) {
            return "Car " + carName.substring(3);
        }

        return carName;
    }
}
