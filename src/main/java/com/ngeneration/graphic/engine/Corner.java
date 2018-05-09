package com.ngeneration.graphic.engine;

public enum Corner {
    TOP_LEFT(Direction.TOP, Direction.LEFT),
    TOP_RIGHT(Direction.TOP, Direction.RIGHT),
    BOTTOM_LEFT(Direction.BOTTOM, Direction.LEFT),
    BOTTOM_RIGHT(Direction.BOTTOM, Direction.RIGHT);

    private final Direction vertical;
    private final Direction horizontal;

    Corner(Direction vertical, Direction horizontal) {
        if (vertical.isVertical() && horizontal.isHorizontal()) {
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

    public boolean isLeft() {
        return horizontal == Direction.LEFT;
    }

}
