package com.ngeneration.graphic.engine.view;

import com.ngeneration.graphic.engine.Corner;
import com.ngeneration.graphic.engine.Vector;

public class RectDrawArea extends DrawArea {
    private Vector center;
    private Vector size;

    public RectDrawArea(Window holderWindow, Vector center, Vector size) {
        super(holderWindow);
        this.size = size;
        this.center = center;
    }

    public RectDrawArea(Window holderWindow, Corner corner, double fractionX, double fractionY) {
        super(holderWindow);
        this.size = new Vector(holderWindow.getSize().getX() * fractionX,
                holderWindow.getSize().getY() * fractionY);
        double x;
        double y;
        if (corner.isLeft()) {
            x = 0 + size.divide(2).getX();
        } else {
            x = holderWindow.getSize().getX() - size.divide(2).getX();
        }
        if (corner.isTop()) {
            y = holderWindow.getSize().getY() - size.divide(2).getY();
        } else {
            y = 0 + size.divide(2).getY();
        }
        center = new Vector(x, y);
    }


    protected boolean withinAreaBounds(Vector position) {
        return
                center.minus(size).getX() <= position.getX()
                        && position.getX() <= center.plus(size).getX()
                        && center.minus(size).getY() <= position.getY()
                        && position.getY() <= center.plus(size).getY();
    }
}
