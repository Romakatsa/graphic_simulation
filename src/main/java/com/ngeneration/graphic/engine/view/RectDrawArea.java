package com.ngeneration.graphic.engine.view;

import com.ngeneration.graphic.engine.PlaceOnScreen;
import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.drawablecomponents.Line;
import com.ngeneration.graphic.engine.drawers.Drawer;
import com.ngeneration.graphic.engine.enums.ColorEnum;

public class RectDrawArea extends DrawArea {
    //    private Vector size;
    private final Line[] border = new Line[4];
    private double borderWidth = 3;
    private double borderOpacity;
    private ColorEnum borderColor;

    public RectDrawArea(Window holderWindow, Vector center, Vector size) {
        super(holderWindow);
        this.setSize(size);
        this.setPosition(center);
        updateBorder();
//                this.getPosition().minus(this.getSize().divide(2)));
    }

    private void updateBorder() {
        borderColor = ColorEnum.DARK_GREEN;
        borderOpacity = 0;
        border[0] = new Line(this.getPosition().plus(this.getSize().divide(2)),
                this.getPosition().plus(this.getSize().divide(2)).minus(new Vector(0, getSize().getX())),
                borderWidth, borderColor, borderOpacity);
        border[1] = new Line(this.getPosition().plus(this.getSize().divide(2)),
                this.getPosition().plus(this.getSize().divide(2)).minus(new Vector(getSize().getX(), 0)),
                borderWidth, borderColor, borderOpacity);
        border[2] = new Line(this.getPosition().minus(this.getSize().divide(2)),
                this.getPosition().minus(this.getSize().divide(2)).plus(new Vector(0, getSize().getY())),
                borderWidth, borderColor, borderOpacity);
        border[3] = new Line(this.getPosition().minus(this.getSize().divide(2)),
                this.getPosition().minus(this.getSize().divide(2)).plus(new Vector(getSize().getX(), 0)),
                borderWidth, borderColor, borderOpacity);
        System.out.println("border[3] = " + border[3]);
    }

    public RectDrawArea(Window holderWindow, PlaceOnScreen place, double fractionX, double fractionY) {
        super(holderWindow);
        updateBorder();
        this.setSize(new Vector(100 * fractionX,
                100 * fractionY));
        double x;
        double y;
        if (place.isLeft()) {
            x = -50 + size.divide(2).getX();
        } else {
            x = -50 +100 - size.divide(2).getX();
        }
        if (place.isTop()) {
            y = -50 +100 - size.divide(2).getY();
        } else {
            y = -50 + size.divide(2).getY();
        }
        setPosition(new Vector(x, y));
        System.out.println("getSize() = " + getSize());
        System.out.println("getPosition() = " + getPosition());
    }

    @Override
    public <T> void render(Drawer<T> drawer, DrawArea area) {
        super.render(drawer, area);
        for (Line aBorder : border) {
            aBorder.render(drawer, area);
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
