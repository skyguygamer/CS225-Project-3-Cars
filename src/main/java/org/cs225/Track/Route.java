package org.cs225.Track;

/*
    Tristan worked on this class.

    This class containg information regarding a possible fair route for a car to stop and end. It contains a list of legs and stops.
    It provides many methods for use of finding out what the next stop is and different stops.
    The classes main use is for a car to be assigned a route and then for when the car moves, you use the information provided here in order to
    move the car to the next stop and or finding out if its at the end stop
 */

import java.util.ArrayList;
import java.util.Objects;

public class Route {
    private ArrayList<Stop> stops;
    private ArrayList<Leg> legs;


    public Route(ArrayList<Stop> stops, ArrayList<Leg> routeLegs) {
        this.stops = stops;
        this.legs = routeLegs;
    }

    public ArrayList<Leg> getLegs() {return legs;}

    public ArrayList<Stop> getStops() {
        return stops;
    }

    public Stop getStartStop() {
        return stops.getFirst();
    }

    public Stop getEndStop() {
        return stops.getLast();
    }

    public int getNumStops() {

        return stops.size();
    }

    public int getNumLegs() {
        return legs.size();
    }

    public double getTotalDistance() {
        double totalDistance = 0;
        for (Leg l : legs) {
            totalDistance += l.getDistance();
        }
        return totalDistance;
    }

    @Override
    public String toString() {
        StringBuilder sbuild = new StringBuilder();

        sbuild.append("Route{");

        for (int i = 0; i < stops.size(); i++) {
            sbuild.append(stops.get(i).getName());

            if (i < stops.size() - 1) {
                sbuild.append(" -> ");
            }
        }

        sbuild.append("}");

        return sbuild.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof Route other)) return false;

        return this.stops.equals(other.stops) && this.legs.equals(other.legs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stops, legs);
    }
}
