package org.cs225;

import java.util.concurrent.TimeUnit;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class SceneController extends Application {

    private final long TICKSPERSECOND = 1;
    private final long TICKLENGTH = TimeUnit.SECONDS.toNanos(1L) / TICKSPERSECOND;
    private long startTime = System.nanoTime();
    Label label;
    Integer counter = 0;

    @Override
    public void start(Stage stage) {
        label = new Label("JavaFX is working!");


        StackPane root = new StackPane();
        root.getChildren().add(label);

        Scene scene = new Scene(root, 400, 200);

        stage.setTitle("JavaFX Test");
        stage.setScene(scene);
        stage.show();

        //This timer will run in the background.
        //Every time the time between now and the previous frame exceed our tick length, update and rerender the game
        AnimationTimer animator = new AnimationTimer() 
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

        //We could move have it so that this only starts when the race scene is active, act then have it stop when the scene ends
        animator.start();

    }

    public static void main(String[] args) {
        launch();
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