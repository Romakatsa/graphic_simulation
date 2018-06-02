package com.ngeneration.graphic.engine.drawers;

import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;
import com.ngeneration.graphic.engine.view.DrawArea;
import com.ngeneration.graphic.engine.view.Window;

public class Console1DDrawer extends Drawer {
    @Override
    protected RenderedComponent transform(RenderedComponent component, DrawArea holder) {
        return component;
    }

    public void doRender(RenderedComponent component, DrawArea holder) {
        double position = component.getPosition().getX();
        for (int i = 0; i < Math.abs(position / 10); i++) {
            System.out.print(".");
        }
        System.out.println(" " + component.getPosition());
    }

    @Override
    public boolean isDrawable(RenderedComponent component, DrawArea holder) {
        return component != null
                && component.getPosition() != null
                && component.getSize() != null;
    }

    @Override
    public Object createAndBindToWindow(Window window) {
        return null;//TODO
    }

    @Override
    public void startContinuouslyDrawing() {
        //TODO
    }
}
