package com.ngeneration.ai;

import com.ngeneration.custom_rendered_components.Car;
import com.ngeneration.graphic.engine.physics.RenderedComponentStateRegularUpdater;

public interface Driver extends RenderedComponentStateRegularUpdater<Car> {
    @Override
    void accept(Car component, Double deltaTime);
}
