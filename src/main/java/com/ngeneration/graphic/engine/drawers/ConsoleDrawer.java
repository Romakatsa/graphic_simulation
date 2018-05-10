package com.ngeneration.graphic.engine.drawers;

import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;
import com.ngeneration.graphic.engine.view.DrawArea;
import com.ngeneration.graphic.engine.view.Window;

public class ConsoleDrawer extends Drawer {
    public void doRender(RenderedComponent component, DrawArea area) {
        System.out.println("\tDraw component:");
        System.out.println("\t\t" + component.getPosition());
        System.out.println("\t\t" + component.getSize());
    }

    @Override
    public boolean isDrawable(RenderedComponent component) {
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
