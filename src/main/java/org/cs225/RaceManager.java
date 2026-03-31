package org.cs225;

import java.util.*;
import org.cs225.Track.*;

public class RaceManager {
    private ArrayList<Car> cars;
    private RaceClock clock;
    private String userPrediction;
    private Car winner;
    private boolean running;
    private boolean raceFinished;

    public RaceManager() {
        cars = new ArrayList<>();
        clock = new RaceClock();
        running = false;
        raceFinished = false;
    }

    //this sets up the race
    public void setupRace(Track track, int numCars) {
        cars.clear();
        winner = null;
        running = false;
        raceFinished = false;
        clock.reset();

        //creates the whole track
        //track.generateStops();
        track.generateLegs();

        //picks a finishing stop
        Stop finishStop = track.getStops().get(0);

        //this gets the fair starting positions
        ArrayList<Stop> startingStops =
                track.getFairStartingStops(finishStop, numCars);

        // TODO: Remove this fallback once teammate track generation always provides 4 fair starts.
        if (startingStops.size() < numCars) {
            startingStops = getFallbackStartingStops(track.getStops(), finishStop, numCars);
        }

        //builds the routes to each car
        for (int i = 0; i < startingStops.size(); i++) {
            Stop start = startingStops.get(i);
            Route route = track.buildRoute(start, finishStop);
            if (route == null){
                continue;
            }

            Car car = new Car(route.getStops(), route.getLegs(), "Car" + (i + 1), "car" + (i + 1) + ".png");
            cars.add(car);
        }
    }

    //starts the race
    public void startRace() {
        winner = null;
        raceFinished = false;
        clock.reset();

        if (cars.isEmpty()) {
            running = false;
            return;
        }

        running = true;
        clock.start();
    }

    //advances the simulation by one step
    public void updateTick() {
        if (!running) {
            return;
        }

        boolean allFinished = true;
        clock.tick();

        for (Car car : cars) {
            if (!car.isFinished()) {
                car.move();
            }

            if (car.isFinished()) {
                if (car.getFinishTime() == 0) {
                    car.setFinishTime(clock.getTime());
                }
            } else {
                allFinished = false;
            }
        }

        if (allFinished) {
            stopRace();
        }
    }

    //ends the race
    public void stopRace() {
        running = false;
        raceFinished = true;
        clock.pause();
        determineWinner();
    }

    //winner of the race
    public void determineWinner() {

        double bestTime = Double.MAX_VALUE;
        Car fastest = null;

        for (Car car : cars) {
            if (car.getFinishTime() < bestTime) {
                bestTime = car.getFinishTime();
                fastest = car;
            }
        }
        winner = fastest;
    }

    //gets the user prediction
    public void setUserPrediction(String prediction) {
        this.userPrediction = prediction;
    }

    //checks the user prediction
    public boolean checkPrediction() {
        if (winner == null) return false;
        return winner.getCarName().equals(userPrediction);
    }

    //results of the race
    public String getResults() {

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

    public ArrayList<Car> getCars() {
        return cars;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isRaceFinished() {
        return raceFinished;
    }

    public Car getWinner() {
        return winner;
    }

    private ArrayList<Stop> getFallbackStartingStops(ArrayList<Stop> allStops, Stop finishStop, int numCars) {
        ArrayList<Stop> fallbackStops = new ArrayList<>();

        for (Stop stop : allStops) {
            if (stop == finishStop) {
                continue;
            }

            fallbackStops.add(stop);

            if (fallbackStops.size() == numCars) {
                break;
            }
        }

        return fallbackStops;
    }
}
