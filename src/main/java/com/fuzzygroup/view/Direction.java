package com.fuzzygroup.view;

import static java.lang.Math.PI;

public enum Direction {
    RIGHT, TOP, LEFT, BOTTOM, NONE;

    public static Direction of(double radian) {
        if (radian < 0) {
            radian = radian + (Math.abs((int) ((int) radian / (2 * PI))) + 1) * (2 * PI);
        }
        if ((radian < PI / 4 && radian >= 0)
                || (radian < 2 * PI && radian >= PI / 4 * 7)) {
            return Direction.RIGHT;
        } else if ((radian < 3 * PI / 4 && radian >= 1 * PI / 4)) {
            return Direction.TOP;
        } else if ((radian < 5 * PI / 4 && radian >= 3 * PI / 4)) {
            return Direction.LEFT;
        } else if ((radian < 7 * PI / 4 && radian >= 5 * PI / 4)) {
            return Direction.BOTTOM;
        } else return Direction.NONE;

    }

    public int intValue() {
        switch (this) {
            case RIGHT:
                return 0;
            case TOP:
                return 1;
            case LEFT:
                return 2;
            case BOTTOM:
                return 3;
            default:
                return -1;
        }
    }
}
