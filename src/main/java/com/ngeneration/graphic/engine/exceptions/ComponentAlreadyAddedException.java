package com.ngeneration.graphic.engine.exceptions;

import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;

public class ComponentAlreadyAddedException extends IllegalStateException {
    private final RenderedComponent component;

    public ComponentAlreadyAddedException(RenderedComponent component) {
        this.component = component;
    }

    @Override
    public String getMessage() {
        return "Such component is already exists: " + component;
    }
}
