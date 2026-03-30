package org.cs225.Track;

/*
    Tristan worked on this class

    This class is the Track class and holds the entire race as its stops, its legs, and the possible routes.
    It manageds the stops and legs between them. It randomizes the stops and their legs while also producing routes which
    have possible starting stops
 */

import java.util.*;

public class Track {
    private ArrayList<Leg> legs;
    private ArrayList<Stop> stops;

    private int width;
    private int height;

    private int numStops;

    private Random randomizer;

    private int randomSeed = -1;

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
        this.randomSeed = randomSeed;
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

            Leg leg;
            if (this.randomSeed != -1) {
                leg = new Leg(start, end, randomSeed);
            } else {
                leg = new Leg(start, end);
            }
            legs.add(leg);
        }
    }

    //Gets connected stops at one stop
    public ArrayList<Stop> getConnectedStops(Stop stop) {
        ArrayList<Stop> connectedStops = new ArrayList<Stop>();

        for (Leg leg : legs) {
            if (leg.getStart() == stop) {
                connectedStops.add(leg.getEnd());
            } else if (leg.getEnd() == stop) {
                connectedStops.add(leg.getStart());
            }
        }

        return connectedStops;
    }

    //Leg exists method?
    public Leg getLeg(Stop start, Stop end) {
        for (Leg leg : legs) {
            if ((leg.getStart() == start && leg.getEnd() == end) ||
                    (leg.getStart() == end && leg.getEnd() == start)) {
                return leg;
            }
        }
        return null;
    }





    //ROute builder
    public Route buildRoute(Stop start, Stop end) {
        if (start == null || end == null) {
            return null;
        }

        if (!stops.contains(start) || !stops.contains(end)) {
            return null;
        }

        Queue<Stop> queue = new LinkedList<Stop>();
        ArrayList<Stop> visited = new ArrayList<Stop>();
        HashMap<Stop, Stop> previousStops = new HashMap<Stop, Stop>();

        queue.add(start);
        visited.add(start);
        previousStops.put(start, null);

        boolean found = false;

        while (!queue.isEmpty()) {
            Stop current = queue.remove();

            if (current == end) {
                found = true;
                break;
            }

            ArrayList<Stop> neighbors = getConnectedStops(current);

            for (Stop neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    previousStops.put(neighbor, current);
                }
            }
        }

        if (!found) {
            return null;
        }

        ArrayList<Stop> routeStops = new ArrayList<Stop>();
        Stop current = end;

        while (current != null) {
            routeStops.add(0, current);
            current = previousStops.get(current);
        }

        ArrayList<Leg> routeLegs = new ArrayList<Leg>();

        for (int i = 0; i < routeStops.size() - 1; i++) {
            Stop currentStop = routeStops.get(i);
            Stop nextStop = routeStops.get(i + 1);

            Leg leg = getLeg(currentStop, nextStop);

            if (leg == null) {
                return null;
            }

            routeLegs.add(leg);
        }

        return new Route(routeStops, routeLegs);
    }

    public ArrayList<Leg> getLegs() {
        return legs;
    }

    public ArrayList<Stop> getStops() {
        return stops;
    }

    public int getWidth() {return width;}
    public int getHeight() {return height;}

    //Get fair starting stops
    public ArrayList<Stop> getFairStartingStops(Stop finishStop, int numCars) {
        ArrayList<Stop> fairStops = new ArrayList<Stop>();

        if (finishStop == null || numCars <= 0) {
            return fairStops;
        }

        ArrayList<Stop> candidates = new ArrayList<Stop>();
        ArrayList<Double> distances = new ArrayList<Double>();
        ArrayList<Integer> legCounts = new ArrayList<Integer>();

        for (Stop stop : stops) {
            if (stop == finishStop) {
                continue;
            }

            Route route = buildRoute(stop, finishStop);

            if (route == null) {
                continue;
            }

            if (route.getNumLegs() < 3) {
                continue;
            }

            candidates.add(stop);
            distances.add(route.getTotalDistance());
            legCounts.add(route.getNumLegs());
        }

        if (candidates.size() < numCars) {
            return fairStops;
        }

        ArrayList<Stop> bestGroup = new ArrayList<Stop>();
        double[] bestScore = {Double.MAX_VALUE};

        chooseBestFairGroup(
                candidates,
                distances,
                legCounts,
                numCars,
                0,
                new ArrayList<Stop>(),
                new ArrayList<Double>(),
                new ArrayList<Integer>(),
                bestGroup,
                bestScore
        );

        return bestGroup;
    }

    //Helper class to get fair starting stops
    private void chooseBestFairGroup(ArrayList<Stop> candidates,
                                     ArrayList<Double> distances,
                                     ArrayList<Integer> legCounts,
                                     int numCars,
                                     int startIndex,
                                     ArrayList<Stop> currentStops,
                                     ArrayList<Double> currentDistances,
                                     ArrayList<Integer> currentLegCounts,
                                     ArrayList<Stop> bestGroup,
                                     double[] bestScore) {

        if (currentStops.size() == numCars) {
            double score = calculateFairnessScore(currentDistances, currentLegCounts);

            if (score < bestScore[0]) {
                bestScore[0] = score;
                bestGroup.clear();
                bestGroup.addAll(currentStops);
            }

            return;
        }

        for (int i = startIndex; i < candidates.size(); i++) {
            currentStops.add(candidates.get(i));
            currentDistances.add(distances.get(i));
            currentLegCounts.add(legCounts.get(i));

            chooseBestFairGroup(
                    candidates,
                    distances,
                    legCounts,
                    numCars,
                    i + 1,
                    currentStops,
                    currentDistances,
                    currentLegCounts,
                    bestGroup,
                    bestScore
            );

            currentStops.remove(currentStops.size() - 1);
            currentDistances.remove(currentDistances.size() - 1);
            currentLegCounts.remove(currentLegCounts.size() - 1);
        }
    }

    //Helper class to get fair starting stops
    private double calculateFairnessScore(ArrayList<Double> distances,
                                          ArrayList<Integer> legCounts) {
        double avgDistance = 0.0;
        double avgLegs = 0.0;

        for (double distance : distances) {
            avgDistance += distance;
        }
        avgDistance /= distances.size();

        for (int legs : legCounts) {
            avgLegs += legs;
        }
        avgLegs /= legCounts.size();

        double distanceDeviation = 0.0;
        double legDeviation = 0.0;

        for (double distance : distances) {
            distanceDeviation += Math.abs(distance - avgDistance);
        }

        for (int legs : legCounts) {
            legDeviation += Math.abs(legs - avgLegs);
        }

        double maxDistance = distances.get(0);
        double minDistance = distances.get(0);

        for (double distance : distances) {
            if (distance > maxDistance) {
                maxDistance = distance;
            }
            if (distance < minDistance) {
                minDistance = distance;
            }
        }

        double distanceSpread = maxDistance - minDistance;

        return distanceDeviation + (legDeviation * 200.0) + distanceSpread;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Track track)) return false;

        return width == track.width &&
                height == track.height &&
                numStops == track.numStops &&
                randomSeed == track.randomSeed &&
                Objects.equals(legs, track.legs) &&
                Objects.equals(stops, track.stops) &&
                Objects.equals(randomizer, track.randomizer);
    }

    @Override
    public String toString() {
        return "Track{" +
                "legs=" + legs +
                ", stops=" + stops +
                ", width=" + width +
                ", height=" + height +
                ", numStops=" + numStops +
                ", randomizer=" + randomizer +
                ", randomSeed=" + randomSeed +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(legs, stops, width, height, numStops, randomizer, randomSeed);
    }


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