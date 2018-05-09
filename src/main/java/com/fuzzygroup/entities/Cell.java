package com.fuzzygroup.entities;

import com.fuzzygroup.view.Shape;
import com.fuzzygroup.view.Direction;
import com.fuzzygroup.view.VisualRepresentationEntity;
import com.fuzzygroup.view.enums.ColorEnum;
import com.ngeneration.graphic.engine.ThreeVector;

public class Cell extends VisualRepresentationEntity {

    private Direction direction = Direction.NONE;
    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Cell() {
        super(new ThreeVector(0, 0, 0), new ThreeVector(0, 0, 0), new ThreeVector(0, 0, 0),
                new ThreeVector(3, 3, 3),
                Shape.TRIANGLE, ColorEnum.WHITE, true);
    }

    public Cell(ThreeVector position, ThreeVector speed, ThreeVector acceleration, ThreeVector size,
                Shape shapes, ColorEnum colors, boolean visible) {
        super(position, speed, acceleration, size, shapes, colors, visible);
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        if (direction == Direction.RIGHT) {
            rotation = 0;
        } else if (direction == Direction.TOP) {
            rotation = Math.PI / 2;
        } else if (direction == Direction.LEFT) {
            rotation = Math.PI;
        } else if (direction == Direction.BOTTOM) {
            rotation = 3 * Math.PI / 2;
        }
        this.direction = direction;
    }

    @Override
    public void recomputeState() {
        if (matchMask(DefaultMasks.SELECTED)) {
            setColors(ColorEnum.BLUE);
        } else {
            setColors(ColorEnum.WHITE);
        }
        if (direction == Direction.NONE) {
            rotation = 0;
            colors = ColorEnum.DARK_RED;
            shapes = Shape.RECT;
        } else {
            shapes = Shape.RECT_2;

        }
    }

}
