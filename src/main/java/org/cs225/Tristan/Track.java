package org.cs225.Tristan;

import java.util.ArrayList;
import java.util.Random;

public class Track {
    private ArrayList<Leg> legs;
    private ArrayList<Stop> stops;

    private int width;
    private int height;

    private int numStops;

    private Random randomizer;

    //private Stop finishLine;
    //private ArrayList<Stop> startingStops;


    //Given number of stops or random

    public Track(int screenWidth, int screenHeight, int numStops) {
        legs = new ArrayList<Leg>();
        stops = new ArrayList<Stop>();

        this.width = screenWidth;
        this.height = screenHeight;

        this.numStops = numStops;

        this.randomizer = new Random();
    }


    //Constructor with seed
    public Track(int width, int height, int numStops, int randomSeed) {
        this(width, height, numStops);

        this.randomizer = new Random(randomSeed);
    }


    //Generates random stops within bounds
    public void generateStops() {
        stops.clear();

        int margin = 50; //used to keep stops away from edges

        for (int i = 0; i < numStops; i++) {
            double x = randomizer.nextDouble() * (width - 2 * margin) + margin; // generates x val of stop
            double y = randomizer.nextDouble() * (height - 2 * margin) + margin; // generate y val of stop

            String name = String.valueOf((char) ('A' + i));

            Stop stop = new Stop(x, y, name);
            stops.add(stop);
        }
    }

    //Generates random list of legs using previously generated stops
    public void generateLegs() {
        legs.clear();

        if (stops.size() < 2) {
            System.out.println("Stops were not generated!");
            return;
        }

        for (int i = 0; i < stops.size(); i++) {
            Stop start = stops.get(i);
            Stop end = stops.get((i + 1) % stops.size());

            Leg leg = new Leg(start, end);
            legs.add(leg);
        }
    }

    //Leg exists method?

/*
    //Assigns starting Stops and the Finish line stop
    public void assignStartAndFinishStops() {
        startingStops.clear();

        if (stops.size() < 2) {
            return;
        }

        int finishIndex = randomizer.nextInt(stops.size());
        finishLine = stops.get(finishIndex);

        for (Stop stop : stops) {
            if (stop != finishLine) {
                startingStops.add(stop);
            }
        }
    }

 */

    //FInd route for starting and ending stop
}