/**
 * @author Gabriel
 * @author Matthew
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
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.cs225.GUI.*;
import org.cs225.Track.*;

public class RaceGameApp extends Application {

    public static final int INTRO_SCENE = 0;
    public static final int RACE_SCENE = 1;
    public static final int RESULTS_SCENE = 2;

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
    private static final String[] CAR_NAMES = {"Car 1", "Car 2", "Car 3", "Car 4"};
    private static final int PLACEHOLDER_WINNER_INDEX = 1;

    private Stage primaryStage;

    // TODO: Feed these views with real controller/model data once teammate integration is ready.
    private IntroView introView;
    private RaceView raceView;
    private ResultsView resultsView;

    private Scene introScene;
    private Scene raceScene;
    private Scene resultsScene;

    private RaceManager gameRace;    

    //Race loop variables and objects
    AnimationTimer animator;
    private int predictedCarIndex = -1;

    private final long TICKSPERSECOND = 1;
    private final long TICKLENGTH = TimeUnit.SECONDS.toNanos(1L) / TICKSPERSECOND;
    private long startTime = System.nanoTime();
    Label label;
    Integer counter = 0;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        buildScenes();

        primaryStage.setTitle("Project 3 - Racing Simulator");
        changeScene(INTRO_SCENE);
        primaryStage.show();

        animator = new AnimationTimer() 
        {
            @Override
            public void handle(long arg0)
            {
                long currentTime = System.nanoTime();
                if( TICKLENGTH <= currentTime - startTime)
                {
                    update();
                    render();
                    startTime = currentTime;
                }
            }
        };
    }

    private void buildScenes() {
        introView = new IntroView(this);
        raceView = new RaceView(this);
        resultsView = new ResultsView(this);

        introScene = new Scene(introView.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);
        raceScene = new Scene(raceView.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);
        resultsScene = new Scene(resultsView.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);
    }

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

    public void startRace(int selectedCarIndex) {
        predictedCarIndex = selectedCarIndex;

        gameRace = new RaceManager();
        gameRace.setupRace(new Track(TRACK_PANEL_WIDTH, TRACK_PANEL_HEIGHT, TRACK_POINTS), CAR_COUNT);

        if (selectedCarIndex >= 0 && selectedCarIndex < gameRace.getCars().size()) {
            gameRace.setUserPrediction(gameRace.getCars().get(selectedCarIndex).getCarName());
        }

        gameRace.startRace();

        raceView.resetForNewRace();
        raceView.setPredictedCarName(CAR_NAMES[selectedCarIndex]);
        raceView.setRaceStatus("Placeholder race in progress for " + CAR_NAMES[selectedCarIndex] + ".");

        startTime = System.nanoTime();
        
        animator.start();
        // TODO: Tell RaceController / RaceManager to begin the real race here.
        // TODO: As teammate simulation data arrives, update RaceView through methods like
        // updateCarPosition(...) and showPredictedRoute(...).
        changeScene(RACE_SCENE);
    }

    public void showResults() {
        boolean predictionWasCorrect = predictedCarIndex == PLACEHOLDER_WINNER_INDEX;

        if (predictedCarIndex >= 0) {
            resultsView.setPredictionSummary(
                    CAR_NAMES[predictedCarIndex],
                    CAR_NAMES[PLACEHOLDER_WINNER_INDEX],
                    predictionWasCorrect
            );
        } else {
            resultsView.setPredictionSummaryText("No prediction was selected before the race.");
        }

        loadPlaceholderResults();

        // TODO: Replace placeholder winner/stat data with real race results from the controller.
        changeScene(RESULTS_SCENE);
    }

    public void restartRace() {
        predictedCarIndex = -1;
        introView.clearSelection();
        raceView.resetForNewRace();
        resultsView.showPlaceholderResults();

        // TODO: Reset the simulation through RaceController / RaceManager here.
        changeScene(INTRO_SCENE);
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

    public static void main(String[] args) {
        launch(args);
    }

        //When this is called, call the RaceManager update method. This should update the position of all objects in the race
    //Currently this is just updating a counter to show functionality
    public void update()
    {
        counter++;
        return;
    }

    //When this is called, call SceneApp update/render method. This should update the scene to match the current game state
    //Currently this is just showing an updating counter to show functionality
    public void render()
    {
        label.setText(counter.toString());
        return;
    }
}
