package com.ngeneration.graphic.engine.enums;

public enum ColorEnum {
  WHITE(0.8f, 0.8f, 0.8f),
  BLACK(0, 0, 0),
  RED(0.733f, 0.223f, 0.168f),
  BLUE(0.223f, 0.733f, 0.168f),
  GREEN(0.168f, 0.223f, 0.733f),

  DARK_RED(0.4, 0.05, 0.05), DARK_GREEN(0.068f, 0.423f, 0.333f)
  ;

  private double red;
  private double green;
  private double blue;

  ColorEnum(double red, double green, double blue) {
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
}
