package com.ngeneration.graphic.engine.drawablecomponents;

import com.ngeneration.graphic.engine.Shape;
import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.enums.Color;

public class Line extends RenderedComponent {
    private double width;
//    protected

    public Line(Vector beginPoint, Vector endPoint, double width, Color colors, double opacity) {
        this(beginPoint.plus(endPoint.minus(beginPoint).divide(2)),
                new Vector(width, beginPoint.minus(endPoint).module() + width),
                colors, beginPoint.minus(endPoint).toPolar().getRadian(), opacity);
    }

    public Line(Vector position, Vector size, Color colors, double rotation, double opacity) {
        super(position, size, rotation, colors, opacity, Shape.RECT);
        this.width = size.getX();
    }


    public void changePosition(Vector beginPoint, Vector endPoint) {
        setPosition(beginPoint.plus(endPoint.minus(beginPoint).divide(2)));
        setSize(new Vector(width, beginPoint.minus(endPoint).module()));
        setRotation(beginPoint.minus(endPoint).toPolar().getRadian());
    }
}
