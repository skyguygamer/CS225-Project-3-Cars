package org.cs225;
import java.util.ArrayList;

import org.cs225.GUI.RaceView;
import org.cs225.Track.Route;
import org.cs225.Track.Track;
public class RaceManager {
    private RaceView raceView;
    private ArrayList<Car> cars;
    private RaceClock clock;
    private String userPrediction;
    private Car winner;
    private boolean running;

    public RaceManager() {
        cars = new ArrayList<>();
        clock = new RaceClock();
        running = false;
    }

    //this sets up the race
    public void setupRace(Track track, int numCars) {

        //creates the whole track
        track.generateStops();
        track.generateLegs();

        //gets unique fair routes for each car with unique start and finish points
        ArrayList<Route> uniqueRoutes = track.getFairUniqueRoutesForCars(numCars);

        //builds a car for each route
        for (int i = 0; i < uniqueRoutes.size(); i++) {
            Route route = uniqueRoutes.get(i);
            Car car = new Car(route.getStops(), route.getLegs(), "Car" + (i + 1), "car" + (i + 1) + ".png");
            cars.add(car);
        }
    }

    //starts the race
    public void startRace() {
        running = true;
        clock.reset();
        clock.start();
    }

    //race loop
    public void update() {
        if (!running) return;

        clock.tick();
        boolean allFinished = true;

       for (int i = 0; i < cars.size(); i++) {
        Car car = cars.get(i);
    
        if (!car.isFinished()) {
            car.move();
            raceView.updateCarPosition(i, car.getXPos(), car.getYPos());
            allFinished = false;        
        } else {
            if (car.getFinishTime() == 0) {
            car.setFinishTime(clock.getTime());
        }
    }
}

        if (allFinished) {
            stopRace();
    }
    }
    //ends the race
    public void stopRace() {
        running = false;
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
}