package com.ngeneration.graphic.engine.drawers;

import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;
import com.ngeneration.graphic.engine.view.Window;

public abstract class Drawer<ID> {

    public void render(RenderedComponent component) {
        if (isDrawable(component)) {
            doRender(component);
        }
    }

    protected abstract void doRender(RenderedComponent component);

    protected abstract boolean isDrawable(RenderedComponent component);

    public abstract ID createAndBindToWindow(Window<ID> window);

    public abstract void startContinuouslyDrawing();
}
