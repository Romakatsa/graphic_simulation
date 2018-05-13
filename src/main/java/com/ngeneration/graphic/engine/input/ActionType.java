package com.ngeneration.graphic.engine.input;

public class ActionType {
    public static final int NO_ACTION                           = 0b0000000001;
    public static final int CLICKED                             = 0b0000000110;
    public static final int PRESSED                             = 0b0000000100;
    public static final int RELEASED                            = 0b0000001000;
    public static final int PRESSED_ONE_SECOND                  = 0b0000010100;
    public static final int PRESSED_TWO_SECONDS                 = 0b0000100100;
    public static final int PRESSED_50_MILLISECONDS             = 0b0001000100;
    public static final int PRESSED_200_MILLISECONDS            = 0b0010000100;
    public static final int PRESSED_EVERY_200_MILLISECONDS      = 0b0100000000;
}
