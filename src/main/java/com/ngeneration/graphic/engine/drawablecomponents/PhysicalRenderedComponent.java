package com.ngeneration.graphic.engine.drawablecomponents;

import com.ngeneration.graphic.engine.Shape;
import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.enums.Color;

public class PhysicalRenderedComponent extends RenderedComponent {
    protected Vector speed;
    protected Vector acceleration;

    public PhysicalRenderedComponent(Vector position, Vector size, double rotation,
                                     Color colors, double opacity, Shape shapes) {
        super(position, size, rotation, colors, opacity, shapes);
    }

    public PhysicalRenderedComponent(Vector position, Vector size, double rotation,
                                     Color colors, double opacity, Shape shapes,
                                     Vector speed, Vector acceleration) {
        super(position, size, rotation, colors, opacity, shapes);
        this.speed = speed;
        this.acceleration = acceleration;
    }

    public void setSpeed(Vector speed) {
        this.speed = speed;
    }

    public void setAcceleration(Vector acceleration) {
        this.acceleration = acceleration;
    }

    public Vector getSpeed() {
        return speed;
    }

    public Vector getAcceleration() {
        return acceleration;
    }
}
