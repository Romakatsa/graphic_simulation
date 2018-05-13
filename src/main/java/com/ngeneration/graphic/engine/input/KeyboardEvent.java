package com.ngeneration.graphic.engine.input;


import java.util.Arrays;

public class KeyboardEvent {
    private final int[] keys;
    private final Runnable runnable;
    private final int triggerActionType;

    private int currentActionType;
    private double lastActionTime;
    private long clickingStartAt;
    private long lastIntervalTrigerMilliseconds;

    public KeyboardEvent(Runnable runnable, int triggerActionType, int... keys) {
        this.keys = keys;
        this.triggerActionType = triggerActionType;
        this.runnable = runnable;
        this.currentActionType = ActionType.NO_ACTION;
        this.lastActionTime = System.currentTimeMillis();
    }

    public int[] getKeys() {
        return keys;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public int getTriggerActionType() {
        return triggerActionType;
    }

    public int getCurrentActionType() {
        return currentActionType;
    }

    public void setCurrentActionType(int newActionType) {
        if (this.currentActionType != newActionType) {
            if ((newActionType & ActionType.CLICKED) != 0) {
                clickingStartAt = System.currentTimeMillis();
            }
            lastActionTime = System.currentTimeMillis();
        }
        this.currentActionType = newActionType;
    }

    public void run() {
        runnable.run();
    }

    public double getLastActionTime() {
        return lastActionTime;
    }

    public long getClickingStartAt() {
        return clickingStartAt;
    }

    public long getLastIntervalTrigerMilliseconds() {
        return lastIntervalTrigerMilliseconds;
    }

    public void resetLastIntervalTrigerMilliseconds() {
        this.lastIntervalTrigerMilliseconds = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Keyboard{" +
                "keys=" + Arrays.toString(keys) +
                ", triggerActionType=" + triggerActionType +
                ", currentActionType=" + currentActionType +
                '}';
    }
}
