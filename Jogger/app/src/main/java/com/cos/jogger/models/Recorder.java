package com.cos.jogger.models;

/**
 * Created by admin1 on 10/21/2015.
 */
public class Recorder {

    public enum State{
        Running, Paused
    }
    private int hour;
    private int minute;
    private int second;
    private int milliseconds;
    private int distanceInKilometers;
    private int distanceInMeters;
    private int paceInKilometersPerSecond;
    private int paceInMetersPerSecond;
    private State state;

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(int milliseconds) {
        this.milliseconds = milliseconds;
    }

    public int getDistanceInKilometers() {
        return distanceInKilometers;
    }

    public void setDistanceInKilometers(int distanceInKilometers) {
        this.distanceInKilometers = distanceInKilometers;
    }

    public int getDistanceInMeters() {
        return distanceInMeters;
    }

    public void setDistanceInMeters(int distanceInMeters) {
        this.distanceInMeters = distanceInMeters;
    }

    public int getPaceInKilometersPerSecond() {
        return paceInKilometersPerSecond;
    }

    public void setPaceInKilometersPerSecond(int paceInKilometersPerSecond) {
        this.paceInKilometersPerSecond = paceInKilometersPerSecond;
    }

    public int getPaceInMetersPerSecond() {
        return paceInMetersPerSecond;
    }

    public void setPaceInMetersPerSecond(int paceInMetersPerSecond) {
        this.paceInMetersPerSecond = paceInMetersPerSecond;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
