package org.cs225.Tristan;

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
}