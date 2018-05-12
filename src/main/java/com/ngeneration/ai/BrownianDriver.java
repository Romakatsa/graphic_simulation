package com.ngeneration.ai;

import com.ngeneration.custom_rendered_components.Car;
import com.ngeneration.graphic.engine.Vector;

public class BrownianDriver implements Driver {
    private double speedModule;
    private double twistExtent;

    public BrownianDriver(double speedModule, double speedRotation) {
        this.speedModule = speedModule;
        this.twistExtent = speedRotation;
    }

    @Override
    public void accept(Car component, Double deltaTime) {
        computeSpeedDirection(component, deltaTime);
        computeSpeedModule(component, deltaTime); //todo don`t like 'compute'. Look smth like enrich, assign, ...

    }

    private void computeSpeedModule(Car component, double deltaTime) {
        component.setSpeed(new Vector.PolarCoordinateSystemVector(
                component.getRotation(), speedModule * deltaTime)
                .toFlatCartesianVector());
    }

    private void computeSpeedDirection(Car component, double deltaTime) {
        double diverseX = (component.getPosition().getX() - 50) / 50;
        double diverseY = (component.getPosition().getY() - 50) / 50;
        component.setRotation(component.getRotation()
                + twistExtent * (Math.random() - 0.5) * Math.PI * 2 * deltaTime);
    }

    public void changeSpeedModule(double value) {
        speedModule = value;
    }

    public void changeTwist(double value) {
        twistExtent = value;
    }

    public double getSpeedDegree() {
        return speedModule;
    }

    public double getTwistExtentDegree() {
        return twistExtent;
    }

    public double getSpeedModule() {
        return speedModule;
    }

    public double getTwistExtent() {
        return twistExtent;
    }
}
