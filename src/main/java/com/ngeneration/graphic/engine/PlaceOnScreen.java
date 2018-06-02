package com.ngeneration.graphic.engine;

public enum PlaceOnScreen {
    TOP_LEFT_CORNER(Direction.TOP, Direction.LEFT),
    TOP_RIGHT_CORNER(Direction.TOP, Direction.RIGHT),
    BOTTOM_LEFT_CORNER(Direction.BOTTOM, Direction.LEFT),
    BOTTOM_RIGHT_CORNER(Direction.BOTTOM, Direction.RIGHT),
    LEFT(Direction.NONE, Direction.LEFT),
    RIGHT(Direction.NONE, Direction.RIGHT),
    TOP(Direction.TOP, Direction.NONE),
    BOTTOM(Direction.BOTTOM, Direction.NONE),
    CENTER(Direction.NONE, Direction.NONE);

    private final Direction vertical;
    private final Direction horizontal;

    PlaceOnScreen(Direction vertical, Direction horizontal) {
        if ((vertical.isVertical() || vertical.isNone())
                && (horizontal.isHorizontal() || horizontal.isNone())) {
            this.vertical = vertical;
            this.horizontal = horizontal;
        } else {
            throw new IllegalArgumentException("Vertical direction is '" + vertical + "'," +
                    " horizontal direction is '" + horizontal + "'");
        }
    }

    public boolean isTop() {
        return vertical == Direction.TOP;
    }

    public boolean isRight() {
        return vertical == Direction.RIGHT;
    }

    public boolean isLeft() {
        return horizontal == Direction.LEFT;
    }

    public boolean isBottom() {
        return vertical == Direction.BOTTOM;
    }
}
