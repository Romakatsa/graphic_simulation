package com.ngeneration.ai;

import com.ngeneration.custom_rendered_components.Car;
import com.ngeneration.graphic.engine.Vector;

public class BrownianDriver implements Driver {
    private double speedModule;
    private double speedRotation;

    public BrownianDriver(double speedModule, double speedRotation) {
        this.speedModule = speedModule;
        this.speedRotation = speedRotation;
    }

    @Override
    public void accept(Car component) {
        computeSpeedDirection(component);
        computeSpeedModule(component); //todo don`t like 'compute'. Look smth like enrich, assign, ...

    }

    private void computeSpeedModule(Car component) {
        component.setSpeed(new Vector.PolarCoordinateSystemVector(
                component.getRotation(), speedModule)
                .toFlatCartesianVector());
    }

    private void computeSpeedDirection(Car component) {
        double diverseX = (component.getPosition().getX() - 50) / 50;
        double diverseY = (component.getPosition().getY() - 50) / 50;
        component.setRotation(component.getRotation() + speedRotation * (Math.random() - 0.5) * Math.PI * 2 );
    }
}
