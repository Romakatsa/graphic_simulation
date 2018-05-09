package com.ngeneration.graphic.engine.utils;

import java.util.Arrays;

public class MathUtils {
    public static double round(double number, int order) {
        if (order > 19) {
            try {
                throw new Exception("capacity is more then 10^19.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ((double) Math.round(number * Math.pow(10, order))) / Math.pow(10, order);
    }

    public static double round(double number) {
        return round(number, 2);
    }

    public static double max(double... args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty");
        }
        return Arrays.stream(args).max().getAsDouble();
    }

    public static double min(double... args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty");
        }
        return Arrays.stream(args).min().getAsDouble();
    }
}
