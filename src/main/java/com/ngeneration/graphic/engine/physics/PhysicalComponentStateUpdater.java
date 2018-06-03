package com.ngeneration.graphic.engine.physics;

import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.drawablecomponents.PhysicalRenderedComponent;

public class PhysicalComponentStateUpdater
        implements RenderedComponentStateRegularUpdater<PhysicalRenderedComponent> { //todo rename "Updater"

    private static final boolean COMPLEX_MOVEMENT_MODE_ENABLED = false;
    private static final double FRICTION_RATE = 10d;
    private static final double SPEED_LOSS_RATE = 2d;
    private static final double TIME_DELTA = 1; //todo: probably should createPanelAndGetController map<Component, lastUpdate> ?

    @Override
    public void accept(PhysicalRenderedComponent component, Double deltaTime) {
        component.setRotation(component.getRotation()
                + component.getRotationPower() * deltaTime);
        component.setSpeed(component.getSpeed()
                .plus(component.getAcceleration().multiple(deltaTime).multiple(component.getSize().getX())));
        component.setPosition(component.getPosition()
                .plus(component.getSpeed().multiple(deltaTime)));

        component.setRotationPower(0);
        component.setAcceleration(Vector.zero());
        Vector speed = component.getSpeed();
        if (COMPLEX_MOVEMENT_MODE_ENABLED) {
            if (!speed.equals(Vector.zero())) {
                Vector speedInMoveDirection = speed.projectOn(
                        new Vector.Polar(component.getRotation(), 1).toFlatCartesianVector());
                Vector restSpeed = speed.projectRestOn(
                        new Vector.Polar(component.getRotation(), 1)
                                .toFlatCartesianVector());
                Vector newSpeed =
                        speedInMoveDirection
//                                .plus(new Vector.Polar(speedInMoveDirection.angle(), restSpeed.module()*0.5).toFlatCartesianVector())
                                .minus(
                                        speedInMoveDirection.multiple(SPEED_LOSS_RATE * deltaTime))
//                            .minus(new Vector.Polar(speedInMoveDirection.angle(),
//                                    Math.min(0.01 * deltaTime, speedInMoveDirection.module())).toFlatCartesianVector())
                                .plus(restSpeed.minus(
                                        restSpeed.multiple(FRICTION_RATE * deltaTime))
//                                    .minus(new Vector.Polar(restSpeed.angle(),
//                                            Math.min(0.01 * deltaTime, restSpeed.module())).toFlatCartesianVector())
                                );
                component.setSpeed(newSpeed);
            }
        } else {
            if (!speed.equals(Vector.zero())) {
                double angle = component.getSpeed().angle();
                double module = component.getSpeed().module();
                component.setSpeed(new Vector.Polar(angle, module)
                        .toFlatCartesianVector().minus(component.getSpeed().multiple(SPEED_LOSS_RATE * deltaTime)));

            }
        }
//        component.setSpeed(component.getSpeed().minus(
//                component.getSpeed().multiple(0.2d * deltaTime)));
    }
}
