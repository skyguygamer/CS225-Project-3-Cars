package org.cs225.Tristan;

import java.util.Random;

public class Leg {
    private Stop start;
    private Stop end;

    private double distance; //in meters

    private double minSpeed; //in meters / second
    private double maxSpeed; //in meters / second

    //private int seed = 1;
    private Random randomizer = new Random();

    public Leg(Stop start, Stop end) {
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
}