package com.ngeneration.graphic.engine.drawers;

import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;
import com.ngeneration.graphic.engine.view.DrawArea;
import com.ngeneration.graphic.engine.view.Window;

public class ConsoleDrawer extends Drawer {
    @Override
    protected RenderedComponent transform(RenderedComponent component, DrawArea holder) {
        return component;
    }

    public void doRender(RenderedComponent component, DrawArea holder) {
        System.out.println("\tDraw component:");
        System.out.println("\t\t" + component.getPosition());
        System.out.println("\t\t" + component.getSize());
    }

    @Override
    public boolean isDrawable(RenderedComponent component, DrawArea holder) {
        return component != null;
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
