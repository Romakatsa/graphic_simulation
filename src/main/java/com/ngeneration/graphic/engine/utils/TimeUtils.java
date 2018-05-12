package com.ngeneration.graphic.engine.utils;

public class TimeUtils {
    public static long millis() {
        return System.nanoTime() / 1_000_000;
    }
}
