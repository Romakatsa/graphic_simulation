package com.ngeneration.graphic.engine.exceptions;

import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;

public class ComponentNotFoundException extends IllegalStateException {
    private final RenderedComponent component;

    public ComponentNotFoundException(RenderedComponent component) {
        this.component = component;
    }

    @Override
    public String getMessage() {
        return "Such component not found: " + component;
    }
}
