package org.cs225;

public class RaceClock{
    private double time;
    private boolean running;

    //this controls much time passes per tick
    private double tickSize;

    public RaceClock(int ticksPerSecond) {
        this.time = 0;
        this.running = false;
        this.tickSize = 1.0/ticksPerSecond;
    }

    public void start() {
        running = true;
    }

    public void pause() {
        running = false;
    }

    public void resume() {
        running = true;
    }

    public void reset() {
        time = 0;
        running = false;
    }

    public void tick() {
        if (running) {
            time += tickSize;
        }
    }

    public double getTime() {
        return time;
    }

    public void setTickSize(double tickSize) {
        this.tickSize = tickSize;
    }
}