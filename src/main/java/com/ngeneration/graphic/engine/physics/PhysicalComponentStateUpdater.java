package com.ngeneration.graphic.engine.physics;

import com.ngeneration.graphic.engine.drawablecomponents.PhysicalRenderedComponent;

public class PhysicalComponentStateUpdater
        implements RenderedComponentStateUpdater<PhysicalRenderedComponent> { //todo rename "Updater"

    private final double TIME_DELTA = 1; //todo: probably should create map<Component, lastUpdate> ?

    @Override
    public void accept(PhysicalRenderedComponent component) {
        component.setSpeed(component.getSpeed()
                .plus(component.getAcceleration().multiple(TIME_DELTA)));
        component.setPosition(component.getPosition()
                .plus(component.getSpeed().multiple(TIME_DELTA)));
    }
}
