package com.ngeneration.graphic.engine.drawablecomponents;

import com.ngeneration.graphic.engine.Vector;

public class PhysicalRenderedComponent extends RenderedComponent {
    protected Vector speed;
    protected Vector acceleration;

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
