package com.ngeneration.ai;

import com.ngeneration.custom_rendered_components.Car;
import com.ngeneration.graphic.engine.Vector;

public class DummyDriver implements Driver {

    @Override
    public void accept(Car component) {
        component.setRotation(0);
        component.setSpeed(Vector.diag(10));
    }
}
