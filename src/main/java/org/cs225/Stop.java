package org.cs225;

/*
    Tristan worked on this class.

    This class contains information for each stop. Its xPos, yPos and its name.
    It also has an isVisible boolean option for other uses.
 */

import java.util.Objects;

public class Stop {
    private double xPos;
    private double yPos;

    private String name;
    private boolean isVisible;

    //private boolean isStartingPoint;

    public Stop(double xPos, double yPos, String name) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.name = name;

        this.isVisible = true;
        //this.isStartingPoint = false;
    }

    /*public boolean isStartingPoint() {
        return isStartingPoint;
    }

     */

    /*public void setStartingPoint(boolean isStartingPoint) {
        this.isStartingPoint = isStartingPoint;
    }

     */


    public double getxPos() {
        return xPos;
    }

    public double getyPos() {
        return yPos;
    }
    public String getName() {
        return name;
    }

    public boolean getVisibility() {
        return isVisible;
    }

    public void setVisibility(boolean isVisible) {
        this.isVisible = isVisible;
    }

    @Override
    public String toString() {
        StringBuilder sbuild = new StringBuilder();
        sbuild.append("Stop{");
        sbuild.append("name=").append(this.name);
        sbuild.append(", xPos=").append(xPos);
        sbuild.append(", yPos=").append(yPos);
        sbuild.append(", visibility=").append(isVisible);
        //sbuild.append(", startingPoint=").append(isStartingPoint);
        sbuild.append("}");

        return sbuild.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof Stop other)) return false;

        return this.xPos == other.xPos &&
                this.yPos == other.yPos &&
                Objects.equals(this.name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(xPos, yPos, name, isVisible);
    }
}