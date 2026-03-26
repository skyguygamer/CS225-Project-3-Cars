import java.util.*;
public class Car {
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

    public Car(List<Stop> stops, List<Leg> legs, String sprite){
        //these conditionals make sure that the lists aren't empty
        if (stops == null || stops.size() == 0){
            throw new IllegalArgumentException("Pathway must have at least one stop.");
        }

        if (legs == null || legs.size() == 0){
            throw new IllegalArgumentException("Pathway must have at least one leg.");
        }

        this.pathway = stops;
        this.legPathway = legs;
        this.sprite = sprite;

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


    public void setSpeed(double carSpeed){
        this.speed = carSpeed;

        if (carSpeed > this.topSpeed){
            this.topSpeed = carSpeed;
        }
    }

    public void setFinishTime(double time) {
        this.finishTime = time;
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
