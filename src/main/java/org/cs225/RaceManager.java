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
    private int ticksPerSecond;

    public RaceManager(int ticksPerSecond) {
        cars = new ArrayList<>();
        running = false;
        raceFinished = false;
        this.ticksPerSecond = ticksPerSecond;
        clock = new RaceClock(ticksPerSecond);
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

        ArrayList<Route> routeTemplates = createFixedRouteTemplates(track);

        if (routeTemplates.isEmpty()) {
            return;
        }

        Collections.shuffle(routeTemplates);

        // Each car gets one of the four hardcoded route templates in random order.
        for (int i = 0; i < numCars && i < routeTemplates.size(); i++) {
            Route route = routeTemplates.get(i);

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

    public double getElapsedTime() {
        return clock.getTime();
    }

    private ArrayList<Route> createFixedRouteTemplates(Track track) {
        ArrayList<Route> routeTemplates = new ArrayList<>();

        if (track.getStops().size() < 8) {
            return routeTemplates;
        }

        // Hardcoded main-checkpoint routes over the fixed loop:
        // D -> A -> B -> C
        addRouteIfValid(routeTemplates, buildFixedRoute(track, 6, 7, 0, 1, 2, 3, 4));
        // A -> B -> C -> D
        addRouteIfValid(routeTemplates, buildFixedRoute(track, 0, 1, 2, 3, 4, 5, 6));
        // B -> C -> D -> A
        addRouteIfValid(routeTemplates, buildFixedRoute(track, 2, 3, 4, 5, 6, 7, 0));
        // C -> D -> A -> B
        addRouteIfValid(routeTemplates, buildFixedRoute(track, 4, 5, 6, 7, 0, 1, 2));

        return routeTemplates;
    }

    private Route buildFixedRoute(Track track, int... stopIndices) {
        ArrayList<Stop> routeStops = new ArrayList<>();
        ArrayList<Leg> routeLegs = new ArrayList<>();
        ArrayList<Stop> trackStops = track.getStops();

        for (int stopIndex : stopIndices) {
            if (stopIndex < 0 || stopIndex >= trackStops.size()) {
                return null;
            }

            routeStops.add(trackStops.get(stopIndex));
        }

        for (int i = 0; i < routeStops.size() - 1; i++) {
            Stop currentStop = routeStops.get(i);
            Stop nextStop = routeStops.get(i + 1);
            Leg leg = track.getLeg(currentStop, nextStop);

            if (leg == null) {
                return null;
            }

            routeLegs.add(leg);
        }

        return new Route(routeStops, routeLegs);
    }

    private void addRouteIfValid(ArrayList<Route> routeTemplates, Route route) {
        if (route != null) {
            routeTemplates.add(route);
        }
    }
}
