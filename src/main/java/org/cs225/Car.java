package org.cs225;

import org.cs225.Track.Leg;
import org.cs225.Track.Stop;

import java.util.*;
public class Car {
    private String carName;
    private List<Stop> pathway;
    private List<Leg> legPathway;
    private int currentStopIndex;
    private int currentLegIndex;
    private double xPos;
    private double yPos;
    private double speed;
    private double topSpeed;
    private double distance;
    private double finishTime;
    private String sprite;

    public Car(List<Stop> stops, List<Leg> legs, String carName, String sprite){
        //these two following conditionals make sure that the lists aren't empty
        if (stops == null || stops.size() == 0){
            throw new IllegalArgumentException("Pathway must have at least one stop.");
        }

        if (legs == null || legs.size() == 0){
            throw new IllegalArgumentException("Pathway must have at least one leg.");
        }

        this.pathway = stops;
        this.legPathway = legs;
        this.sprite = sprite;
        this.carName = carName;

        //sets the starting point at first stop
        this.xPos = pathway.get(0).getxPos();
        this.yPos = pathway.get(0).getyPos();

        this.distance = 0;
        this.speed = 0;
        this.topSpeed = 0;
        this.finishTime = 0;
        this.currentStopIndex = 0;
        this.currentLegIndex = 0;
    }

public void move() {
    //this makes sure that the car doesn't move if it has already completed all the Legs.
    if (currentLegIndex >= legPathway.size()) {
        return;
    }

    //this gets the current leg the car is on
    Leg currentLeg = legPathway.get(currentLegIndex);

    //determines the speed based on the Leg
    if (speed == 0) {
        double min = currentLeg.getMinSpeed();
        double max = currentLeg.getMaxSpeed();
        speed = min + (Math.random() * (max - min));

        if (speed > topSpeed) {
            topSpeed = speed;
        }
    }

    //gets the next stop (target)
    Stop next = currentLeg.getEnd();

    //this gets the direction of where the car needs to go in
    double dx = next.getxPos() - xPos;
    double dy = next.getyPos() - yPos;

    //this calculates the distance to the target stop
    double length = Math.sqrt(dx * dx + dy * dy);

    //if the car is already at stop, move to the next leg
    if (length == 0) {
        currentLegIndex++;
        currentStopIndex++;
        speed = 0;
        return;
    }

    //this makes sure that the car doesn't just teleport to the next stop 
    dx /= length;
    dy /= length;

    //this makes sure the car moves toward the stop based on the speed
    xPos += dx * speed;
    yPos += dy * speed;

    //this updates the distance traveled
    distance += speed;

    //this checks if we have reached or passed the stop
    double remaining = Math.sqrt(Math.pow(next.getxPos() - xPos, 2) + Math.pow(next.getyPos() - yPos, 2));

    if (remaining < speed) {
        //this will get it to the stop
        xPos = next.getxPos();
        yPos = next.getyPos();

        //onto the next stop and leg
        currentLegIndex++;
        currentStopIndex++;

        //this resets speed for next leg
        speed = 0;
    }
}

    //setters
    public void setSpeed(double carSpeed){
        this.speed = carSpeed;

        if (carSpeed > this.topSpeed){
            this.topSpeed = carSpeed;
        }
    }

    public void setCarName(String new_name){
        this.carName = new_name;
    }


    public void setFinishTime(double time) {
        this.finishTime = time;
    }

    //getters
    public String getCarName(){
        return this.carName;
    }

    public double getXPos() {
        return xPos;
    }

    public double getYPos() {
        return yPos;
    }

    public double getDistance() {
        return distance;
    }

    public double getSpeed() {
        return speed;
    }

    public double getTopSpeed() {
        return topSpeed;
    }

    public double getFinishTime() {
        return finishTime;
    }

    public String getSprite() {
        return sprite;
    }

    public boolean isFinished() {
        return currentLegIndex >= legPathway.size();
    }
}
