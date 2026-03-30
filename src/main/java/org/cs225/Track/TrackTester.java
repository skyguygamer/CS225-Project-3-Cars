package org.cs225.Track;

/*
    This class tests Track, Leg, Stop, and Route and shows what the track should look like.

    It also provides examples for how methods are to be used
 */

import java.util.ArrayList;

public class TrackTester {

    private static final int GRID_WIDTH = 150;
    private static final int GRID_HEIGHT = 40;

    // ANSI colors
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";

    public static void main(String[] args) {
        //Makign track
        Track track = new Track(800, 600, 15);

        //For making the same race over and over u can add a seed as a 4th parameter
        Track track1 = new Track(800, 600, 10, 100);

        //Generates stops and legs
        track.generateStops();
        track.generateLegs();

        System.out.println(CYAN + "----- Stops -----" + RESET);
        for (Stop stop : track.getStops()) {
            System.out.println(stop);
        }

        System.out.println("\n" + CYAN + "----- Legs -----" + RESET);
        for (Leg leg : track.getLegs()) {
            System.out.println(leg);
        }

        if (track.getStops().size() < 2) {
            System.out.println("Not enough stops to test.");
            return;
        }

        Stop finishStop = track.getStops().get(0);
        int numCars = 4;

        System.out.println("\nFinish Stop: " + YELLOW + finishStop.getName() + RESET);

        //Calculates starting stops and makes sure that they are one for each car.
        // It also makes sure no one route is overpowered over others
        ArrayList<Stop> fairStartingStops = track.getFairStartingStops(finishStop, numCars);



        System.out.println("\n" + CYAN + "----- Fair Starting Stops -----" + RESET);
        if (fairStartingStops.isEmpty()) {
            System.out.println("No fair starting stops found for " + numCars + " cars.");
        } else {
            for (int i = 0; i < fairStartingStops.size(); i++) {
                Stop stop = fairStartingStops.get(i);
                System.out.println("Car " + (i + 1) + " starts at " + YELLOW + stop.getName() + RESET);
            }
        }

        ArrayList<Route> routes = new ArrayList<Route>();


        //Shows all routes
        System.out.println("\n" + CYAN + "----- Routes From Fair Starts -----" + RESET);
        for (int i = 0; i < fairStartingStops.size(); i++) {
            Stop startStop = fairStartingStops.get(i);
            Route route = track.buildRoute(startStop, finishStop);

            if (route == null) {
                System.out.println("No route found from " + startStop.getName() + " to " + finishStop.getName());
            } else {
                routes.add(route);

                System.out.println("Car " + (i + 1) + " route from "
                        + YELLOW + startStop.getName() + RESET + " to "
                        + YELLOW + finishStop.getName() + RESET + ":");
                System.out.println(route);
                System.out.println("Number of legs: " + route.getNumLegs());
                System.out.println("TotalDistance in m: " + route.getTotalDistance());
                System.out.println();
            }
        }

        //Everything else is purely visual purposes

        System.out.println("\n" + CYAN + "----- ENTIRE TRACK MAP -----" + RESET);
        printWholeTrack(track);


        System.out.println("\n" + CYAN + "----- INDIVIDUAL CAR ROUTES -----" + RESET);
        for (int i = 0; i < routes.size(); i++) {
            System.out.println();
            System.out.println(CYAN + "Car " + (i + 1) + " Route" + RESET);
            printSingleRoute(track, routes.get(i), i);
        }
    }




    //This is purely testing purposes and visual

    public static void printWholeTrack(Track track) {
        String[][] grid = new String[GRID_HEIGHT][GRID_WIDTH];
        fillGrid(grid);

        for (Leg leg : track.getLegs()) {
            int x1 = scaleX(leg.getStart().getxPos(), track);
            int y1 = scaleY(leg.getStart().getyPos(), track);
            int x2 = scaleX(leg.getEnd().getxPos(), track);
            int y2 = scaleY(leg.getEnd().getyPos(), track);

            drawTrackLine(grid, x1, y1, x2, y2);
        }

        for (Stop stop : track.getStops()) {
            int x = scaleX(stop.getxPos(), track);
            int y = scaleY(stop.getyPos(), track);
            placeStop(grid, x, y, stop.getName().charAt(0));
        }

        printGridWithBorder(grid);
        System.out.println(WHITE + "Legend: " + RESET
                + YELLOW + "A/B/C... = stops  "
                + BLUE + "- | / \\ + = track" + RESET);
    }


    public static void printSingleRoute(Track track, Route route, int routeIndex) {
        String[][] grid = new String[GRID_HEIGHT][GRID_WIDTH];
        fillGrid(grid);

        for (Leg leg : track.getLegs()) {
            int x1 = scaleX(leg.getStart().getxPos(), track);
            int y1 = scaleY(leg.getStart().getyPos(), track);
            int x2 = scaleX(leg.getEnd().getxPos(), track);
            int y2 = scaleY(leg.getEnd().getyPos(), track);

            drawTrackLine(grid, x1, y1, x2, y2);
        }

        for (Leg leg : route.getLegs()) {
            int x1 = scaleX(leg.getStart().getxPos(), track);
            int y1 = scaleY(leg.getStart().getyPos(), track);
            int x2 = scaleX(leg.getEnd().getxPos(), track);
            int y2 = scaleY(leg.getEnd().getyPos(), track);

            drawRouteLine(grid, x1, y1, x2, y2, routeIndex);
        }

        for (Stop stop : track.getStops()) {
            int x = scaleX(stop.getxPos(), track);
            int y = scaleY(stop.getyPos(), track);
            placeStop(grid, x, y, stop.getName().charAt(0));
        }

        printGridWithBorder(grid);
        System.out.println(WHITE + "Legend: " + RESET
                + YELLOW + "A/B/C... = stops  "
                + getRouteColor(routeIndex) + getRouteSymbol(routeIndex) + " = car " + (routeIndex + 1) + " route  "
                + BLUE + "- | / \\ + = track" + RESET);
    }

    public static void printGridWithBorder(String[][] grid) {
        System.out.print(WHITE + "  +" + RESET);
        for (int x = 0; x < GRID_WIDTH; x++) {
            System.out.print(WHITE + "-" + RESET);
        }
        System.out.println(WHITE + "+" + RESET);

        for (int y = 0; y < GRID_HEIGHT; y++) {
            System.out.print(WHITE + "  |" + RESET);
            for (int x = 0; x < GRID_WIDTH; x++) {
                System.out.print(grid[y][x]);
            }
            System.out.println(WHITE + "|" + RESET);
        }

        System.out.print(WHITE + "  +" + RESET);
        for (int x = 0; x < GRID_WIDTH; x++) {
            System.out.print(WHITE + "-" + RESET);
        }
        System.out.println(WHITE + "+" + RESET);
    }

    public static void fillGrid(String[][] grid) {
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                grid[y][x] = " ";
            }
        }
    }

    public static int scaleX(double x, Track track) {
        double scaled = x / track.getWidth() * (GRID_WIDTH - 1);
        int result = (int) Math.round(scaled);

        if (result < 0) {
            result = 0;
        }
        if (result >= GRID_WIDTH) {
            result = GRID_WIDTH - 1;
        }

        return result;
    }

    public static int scaleY(double y, Track track) {
        double scaled = y / track.getHeight() * (GRID_HEIGHT - 1);
        int result = (int) Math.round(scaled);

        if (result < 0) {
            result = 0;
        }
        if (result >= GRID_HEIGHT) {
            result = GRID_HEIGHT - 1;
        }

        return result;
    }

    public static void drawTrackLine(String[][] grid, int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        if (steps == 0) {
            placeTrackChar(grid, x1, y1, '-');
            return;
        }

        char lineChar = chooseLineChar(dx, dy);

        for (int i = 0; i <= steps; i++) {
            double t = (double) i / (double) steps;
            int x = (int) Math.round(x1 + dx * t);
            int y = (int) Math.round(y1 + dy * t);

            placeTrackChar(grid, x, y, lineChar);
        }
    }

    public static void drawRouteLine(String[][] grid, int x1, int y1, int x2, int y2, int routeIndex) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        char symbol = getRouteSymbol(routeIndex);

        if (steps == 0) {
            placeRouteChar(grid, x1, y1, symbol, routeIndex);
            return;
        }

        for (int i = 0; i <= steps; i++) {
            double t = (double) i / (double) steps;
            int x = (int) Math.round(x1 + dx * t);
            int y = (int) Math.round(y1 + dy * t);

            placeRouteChar(grid, x, y, symbol, routeIndex);
        }
    }

    public static char chooseLineChar(int dx, int dy) {
        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);

        if (absDx > absDy * 2) {
            return '-';
        }

        if (absDy > absDx * 2) {
            return '|';
        }

        if ((dx > 0 && dy > 0) || (dx < 0 && dy < 0)) {
            return '\\';
        }

        return '/';
    }

    public static void placeTrackChar(String[][] grid, int x, int y, char c) {
        if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) {
            return;
        }

        String current = stripColor(grid[y][x]);

        if (current.length() == 1 && Character.isLetter(current.charAt(0))) {
            return;
        }

        if (current.equals(" ")) {
            grid[y][x] = BLUE + c + RESET;
            return;
        }

        if (isRouteSymbol(current)) {
            return;
        }

        if (!current.equals(String.valueOf(c))) {
            grid[y][x] = BLUE + "+" + RESET;
        }
    }

    public static void placeRouteChar(String[][] grid, int x, int y, char c, int routeIndex) {
        if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) {
            return;
        }

        String current = stripColor(grid[y][x]);

        if (current.length() == 1 && Character.isLetter(current.charAt(0))) {
            return;
        }

        grid[y][x] = getRouteColor(routeIndex) + c + RESET;
    }

    public static void placeStop(String[][] grid, int x, int y, char stopChar) {
        if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) {
            return;
        }

        grid[y][x] = YELLOW + stopChar + RESET;
    }

    public static String getRouteColor(int routeIndex) {
        int mod = routeIndex % 4;

        if (mod == 0) {
            return RED;
        }
        if (mod == 1) {
            return GREEN;
        }
        if (mod == 2) {
            return MAGENTA;
        }
        return CYAN;
    }

    public static char getRouteSymbol(int routeIndex) {
        int mod = routeIndex % 4;

        if (mod == 0) {
            return '*';
        }
        if (mod == 1) {
            return '#';
        }
        if (mod == 2) {
            return '@';
        }
        return '%';
    }

    public static boolean isRouteSymbol(String s) {
        return s.equals("*") || s.equals("#") || s.equals("@") || s.equals("%");
    }

    public static String stripColor(String value) {
        return value.replaceAll("\u001B\\[[;\\d]*m", "");
    }
}