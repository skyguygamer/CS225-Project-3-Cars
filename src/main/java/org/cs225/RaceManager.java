package org.cs225;

import org.cs225.Track.Leg;
import org.cs225.Track.Stop;

import java.util.*;

public class RaceManager {
    private List<Car> cars;
    private double startTime;
    private String userPrediction;
    private Car winner;
    private boolean running;
    private RaceClock clock;

    //constructor
    public RaceManager() {
        this.cars = new ArrayList<>();
        this.clock = new RaceClock();
        this.running = false;
    }

    //this sets up the race
    public void setupRace(int numCars, List<Stop> stops) {
        //this just checks to make sure there is no empty stops
        if (stops == null || stops.size() < numCars) {
            throw new IllegalArgumentException("Not enough stops.");
        }

        //this sets up the paths and the legs for each car
        for (int i = 0; i < numCars; i++) {
            ArrayList<Stop> path = new ArrayList<>();
            ArrayList<Leg> legs = new ArrayList<>();

            //gives a unique start position to each car
            for (int j = 0; j < stops.size(); j++) {
                path.add(stops.get((i + j) % stops.size()));
            }

            //this sets up the Legs
            for (int k = 0; k < path.size() - 1; k++) {
                legs.add(new Leg(path.get(k), path.get(k + 1)));
            }

            Car car = new Car(path,legs, "Car " + i, "car" + i + ".png");
            cars.add(car);
        }
    }

    //this starts the race
    public void startRace() {
        running = true;
        clock.reset();
        clock.start();

        startTime = System.currentTimeMillis();
        gameLoop();
    }

    //this gets the cars to move through the loop
    public void gameLoop() {

        while (running) {

            boolean allFinished = true;

            clock.tick();
            for (Car car : cars) {

                if (!car.isFinished()) {
                    car.move();
                    allFinished = false;
                } else {
                    // set finish time ONCE
                    if (car.getFinishTime() == 0) {
                        car.setFinishTime(clock.getTime());
                    }
                }
            }

            if (allFinished) {
                endRace();
            }
            
            //this makes the loop sleep for 16 milliseconds, which means about 60 fps
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //ends the race
    public void endRace() {
        running = false;
        clock.pause();
        determineWinner();
    }

    //pauses the race
    public void pauseRace() {
        clock.pause();
        running = false;
    }

    //resumes the race
    public void resumeRace(){
        running = true;
        clock.resume();
        gameLoop();
    }

    //figures out who won
    public Car determineWinner() {
        Car fastest = null;
        double bestTime = Double.MAX_VALUE;

        for (Car car : cars) {
            if (car.getFinishTime() < bestTime) {
                bestTime = car.getFinishTime();
                fastest = car;
            }
        }
        winner = fastest;
        return winner;
    }

    //gets the user prediction
    public void setUserPrediction(String prediction) {
        this.userPrediction = prediction;
    }

    //checks the user prediction
    public boolean checkUserPrediction() {
        if (winner == null) return false;
        return winner.getCarName().equals(userPrediction);
    }

    //this shows the results
    public String showResults() {
        StringBuilder sb = new StringBuilder();

        sb.append("Winner: ").append(winner.getCarName()).append("\n\n");
        for (Car car : cars) {
            sb.append(car.getCarName())
              .append(" | Time: ").append(car.getFinishTime())
              .append(" | Distance: ").append(car.getDistance())
              .append("\n");
        }

        return sb.toString();
    }

    public List<Car> getCars() {
        return cars;
    }
}