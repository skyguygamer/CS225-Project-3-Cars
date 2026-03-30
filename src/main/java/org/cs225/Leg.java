package org.cs225;
/*
    Tristan worked on this class

    This class is the Leg class and is the line between a starting stop and an end stop.
    It holds each stop and its distance, and also holds min and max speed for possible usage.
 */

import java.util.Objects;
import java.util.Random;

public class Leg {
    private Stop start;
    private Stop end;

    private double distance; //in meters

    private double minSpeed; //in meters / second
    private double maxSpeed; //in meters / second

    //private int seed = 1;
    private Random randomizer;

    public Leg(Stop start, Stop end) {
        randomizer = new Random();
        this.start = start;
        this.end = end;

        distance = Math.sqrt(Math.pow(end.getxPos() - start.getxPos(), 2) + Math.pow(end.getyPos() - start.getyPos(), 2));

        this.minSpeed = randomizer.nextDouble()*5 + 5; //min speed would be between 5(inclus) meters and 10(exclus)
        this.maxSpeed = randomizer.nextDouble()*25; //max speed would be 25 meters per second
    }


    //Second leg constructor for use with selecting specific seed
    public Leg(Stop start, Stop end, int randomSeed) {
        randomizer = new Random(randomSeed);

        this.start = start;
        this.end = end;

        distance = Math.sqrt(Math.pow(end.getxPos() - start.getxPos(), 2) + Math.pow(end.getyPos() - start.getyPos(), 2));

        this.minSpeed = randomizer.nextDouble()*5 + 5; //min speed would be between 5(inclus) meters and 10(exclus)
        this.maxSpeed = randomizer.nextDouble()*25; //max speed would be 25 meters per second
    }

    public double getDistance() {
        return distance;
    }

    public Stop getStart() {
        return start;
    }

    public Stop getEnd() {
        return end;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getMinSpeed() {
        return minSpeed;
    }

    @Override
    public String toString() {
        StringBuilder sbuild = new StringBuilder();
        sbuild.append("Leg{");
        sbuild.append("startStop=").append(start.toString());
        sbuild.append("endStop=").append(end.toString());
        sbuild.append(", maxSpeed=").append(maxSpeed);
        sbuild.append(", minSpeed=").append(minSpeed);
        sbuild.append(", distance=").append(distance);
        sbuild.append("}");

        return sbuild.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof Leg other)) return false;

        return this.start == other.start &&
                this.end == other.end &&
                this.distance == other.distance &&
                this.maxSpeed == other.maxSpeed &&
                this.minSpeed == other.minSpeed &&
                this.randomizer == other.randomizer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, distance, minSpeed, maxSpeed, randomizer);
    }
}