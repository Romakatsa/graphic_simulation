package com.ngeneration.graphic.engine.input;

import java.util.Arrays;

/**
 * Created by Serega on 15.06.2017.
 */
public class MouseEvent {
    private final int[] keys;
    private final Runnable runnable;
    private final int triggerActionType;

    private int currentActionType;
    private double lastActionTime;
    private long clickingStartAt;

    public MouseEvent(Runnable runnable, int triggerActionType, int... keys) {
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

    @Override
    public String toString() {
        return "Mouse{" +
                "keys=" + Arrays.toString(keys) +
                ", triggerActionType=" + triggerActionType +
                ", currentActionType=" + currentActionType +
                '}';
    }
}
