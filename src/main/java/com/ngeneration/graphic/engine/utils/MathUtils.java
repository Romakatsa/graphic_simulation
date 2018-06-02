package com.ngeneration.graphic.engine.utils;

import com.ngeneration.graphic.engine.Vector;

import java.util.Arrays;

import static java.lang.Math.abs;

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

    public static Vector intersection(Vector p1, Vector p2, Vector p3, Vector p4) {
        return intersection(p1, p2, p3, p4, false);
    }

    public static Vector intersection(Vector p1, Vector p2, Vector p3, Vector p4, boolean infiniteLine) {
        double x1 = p1.getX();
        double x2 = p2.getX();
        double x3 = p3.getX();
        double x4 = p4.getX();
        double y1 = p1.getY();
        double y2 = p2.getY();
        double y3 = p3.getY();
        double y4 = p4.getY();
        double xIntersection
                = (((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4))
                / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)));
//                = (x1 / (x2 - x1) * (y2 - y1) - y1
//                - x3 / (x4 - x3) * (y4 - y3) + y3)
//                / (1d / (x2 - x1) - 1d / (x4 - x3));
        double yIntersection = (((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4))
                / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)));
        if (Double.isNaN(xIntersection) || Double.isNaN(yIntersection)
                || Double.isInfinite(xIntersection) || Double.isInfinite(yIntersection)) {
            return null;
        }
        if (!infiniteLine) {
            if ((
                    p1.getX() > p2.getX()
                            && (p1.getX() <= xIntersection || xIntersection <= p2.getX()))
                    ||
                    (p1.getX() < p2.getX()
                            && (xIntersection <= p1.getX() || p2.getX() <= xIntersection))
                    ||
                    (p1.getY() > p2.getY()
                            && (p1.getY() <= yIntersection || yIntersection <= p2.getY()))
                    ||
                    (p1.getY() < p2.getY()
                            && (yIntersection <= p1.getY() || p2.getY() <= yIntersection))

                    ||
                    (p3.getX() > p4.getX()
                            && (p3.getX() <= xIntersection || xIntersection <= p4.getX()))
                    ||
                    (p3.getX() < p4.getX()
                            && (xIntersection <= p3.getX() || p4.getX() <= xIntersection))
                    ||
                    (p3.getY() > p4.getY()
                            && (p3.getY() <= yIntersection || yIntersection <= p4.getY()))
                    ||
                    (p3.getY() < p4.getY()
                            && (yIntersection <= p3.getY() || p4.getY() <= yIntersection))
                    ) {
                return null;
            }
        }
        return new Vector(xIntersection, yIntersection);
    }

    public static double loopValueInBounds(double value, double min, double max) {
        double delta = max - min;
        return abs((value + abs(min) + abs(delta)) % delta) - abs(min);
    }

    public static int loopValueInBounds(int value, int min, int max) {
        if (min > max) {
            int tmp = max;
            max = min;
            min = tmp;
        }
        int delta = max - min + 1;
        if (value < min) {
            return max - abs((value - min) % (delta)) + 1;
        } else if (value > max) {
            return min + abs((value - max) % (delta)) - 1;
        } else {
            return value;
        }
    }
}
