package com.ngeneration.graphic.engine.view;

import com.ngeneration.graphic.engine.PlaceOnScreen;
import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.drawablecomponents.Line;
import com.ngeneration.graphic.engine.drawers.Drawer;
import com.ngeneration.graphic.engine.enums.Color;

public class RectDrawArea extends DrawArea {
    //    private Vector size;
    private final Line[] border = new Line[4];
    private double borderWidth = 1;
    private double borderOpacity;
    private Color borderColor;

    public RectDrawArea(Window holderWindow, Vector center, Vector size) {
        super(holderWindow);
        this.setSize(size);
        this.setPosition(center);
        updateBorder();
//                this.getPosition().minus(this.getSize().divide(2)));
    }

    private void updateBorder() {
        borderColor = Color.DARK_GREEN;
        borderOpacity = 0;

        Vector areaCenter = this.getPosition();
        Vector areaHalfSize = this.getSize().divide(2);
        Vector areaSizeX = new Vector(getSize().getX(), 0);
        Vector areaSizeY = new Vector(0, getSize().getY());

        Vector halfBorderShiftX = new Vector(borderWidth / 2, 0);
        Vector halfBorderShiftY = new Vector(0, borderWidth / 2);

        Vector topRightCorner = areaCenter.plus(areaHalfSize);
        Vector bottomRightCorner = areaCenter.plus(areaHalfSize).minus(areaSizeY);
        Vector topLeftCorner = areaCenter.plus(areaHalfSize).minus(areaSizeX);
        Vector bottomLeftCorner = areaCenter.minus(areaHalfSize);

        border[0] = new Line(
                topRightCorner.plus(halfBorderShiftX).plus(halfBorderShiftY),
                bottomRightCorner.plus(halfBorderShiftX).minus(halfBorderShiftY),
                borderWidth, borderColor, borderOpacity);
        border[1] = new Line(
                topRightCorner.plus(halfBorderShiftY).plus(halfBorderShiftX),
                topLeftCorner.plus(halfBorderShiftY).minus(halfBorderShiftX),
                borderWidth, borderColor, borderOpacity);
        border[2] = new Line(
                bottomLeftCorner.minus(halfBorderShiftX).minus(halfBorderShiftY),
                topLeftCorner.minus(halfBorderShiftX).plus(halfBorderShiftY),
                borderWidth, borderColor, borderOpacity);
        border[3] = new Line(
                bottomLeftCorner.minus(halfBorderShiftY).minus(halfBorderShiftX),
                bottomRightCorner.minus(halfBorderShiftY).plus(halfBorderShiftX),
                borderWidth, borderColor, borderOpacity);
    }

    public RectDrawArea(Window holderWindow, PlaceOnScreen place, double fractionX, double fractionY) {
        super(holderWindow);
        updateBorder();
        this.setSize(new Vector(100 * fractionX,
                100 * fractionY));
        double x = 0;
        double y = 0;
        if (place.isLeft()) {
            x = -50 + size.divide(2).getX();
        } else if (place.isRight()) {
            x = -50 + 100 - size.divide(2).getX();
        }
        if (place.isTop()) {
            y = -50 + 100 - size.divide(2).getY();
        } else if (place.isBottom()) {
            y = -50 + size.divide(2).getY();
        }
        setPosition(new Vector(x, y));
    }

    @Override
    public <T> void render(Drawer<T> drawer, DrawArea area) {
        if (visible) {
            super.render(drawer, area);
            for (Line aBorder : border) {
                aBorder.render(drawer, area);
            }
        }
    }

    @Override
    public void setSize(Vector size) {
        super.setSize(size);
        if (border != null) {
            updateBorder();
        }
    }

    @Override
    public void setPosition(Vector position) {
        super.setPosition(position);
        if (border != null) {
            updateBorder();
        }
    }

    public boolean withinAreaBounds(Vector position) {
        Vector halfSize = size.divide(2);
        position = position.coordinatewiseMultiplication(getZoomFactor()).plus(shift);
        return true;
//                getPosition().minus(halfSize).getX() <= position.getX()
//                        && position.getX() <= getPosition().plus(halfSize).getX()
//                        && getPosition().minus(halfSize).getY() <= position.getY()
//                        && position.getY() <= getPosition().plus(halfSize).getY();
    }
}
