package com.ngeneration.ai;

import com.ngeneration.custom_rendered_components.Car;
import com.ngeneration.graphic.engine.physics.RenderedComponentStateUpdater;

public interface Driver extends RenderedComponentStateUpdater<Car> {
    @Override
    void accept(Car component);
}
