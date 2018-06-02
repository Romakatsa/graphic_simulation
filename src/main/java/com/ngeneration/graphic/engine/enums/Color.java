package com.ngeneration.graphic.engine.enums;

import com.sun.org.apache.regexp.internal.RE;

public class Color {

    public static final Color WHITE = new Color(0.8f, 0.8f, 0.8f);
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color RED = new Color(.733f, 0.223f, 0.168f);
    public static final Color BLUE = new Color(0.223f, 0.733f, 0.168f);
    public static final Color GREEN = new Color(0.168f, 0.223f, 0.733f);

    public static final Color DARK_RED = new Color(0.4, 0.05, 0.05);
    public static final Color DARK_GREEN = new Color(0.068f, 0.423f, 0.333f);
    public static final Color DARK_BLUE = new Color(0.05, 0.05, 0.4f);

    private double red;
    private double green;
    private double blue;

    public Color(double red, double green, double blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public double getRed() {
        return red;
    }

    public double getGreen() {
        return green;
    }

    public double getBlue() {
        return blue;
    }

    public static Color[] values() {
        return new Color[]{
                WHITE, BLACK, RED, GREEN, BLUE, DARK_GREEN, DARK_BLUE, DARK_RED
        };
    }

    enum Default {
        WHITE(0.8f, 0.8f, 0.8f),
        BLACK(0, 0, 0),
        RED(0.733f, 0.223f, 0.168f),
        BLUE(0.223f, 0.733f, 0.168f),
        GREEN(0.168f, 0.223f, 0.733f),

        DARK_RED(0.4, 0.05, 0.05), DARK_GREEN(0.068f, 0.423f, 0.333f),
        DARK_BLUE(0.05, 0.05, 0.4f);

        private double red;
        private double green;
        private double blue;

        Default(double red, double green, double blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
    }

}
