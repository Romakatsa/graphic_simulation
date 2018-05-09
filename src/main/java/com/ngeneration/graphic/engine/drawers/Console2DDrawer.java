package com.ngeneration.graphic.engine.drawers;

import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;
import com.ngeneration.graphic.engine.view.Window;

public class Console2DDrawer extends Drawer {
    private final int width;
    private final int height;

    public Console2DDrawer() {
        this.width = 900;
        this.height = 500;
    }

    @Override
    public void doRender(RenderedComponent component) {
        double x = Math.abs(component.getPosition().getX());
        double y = Math.abs(component.getPosition().getY());
        int resolution = 20;
        for (int i = 0; i < resolution; i++) {
            for (int j = 0; j < resolution; j++) {
                if ((double) j / resolution < x / width && (double) (j + 1) / resolution > x / width
                        && (double) i / resolution < y / height && (double) (i + 1) / resolution > y / height) {
                    System.out.print("X");
                } else {
                    System.out.print(".");
                }
            }
            System.out.println();
        }
//        System.out.println(" " + component.getPosition());
    }

    @Override
    public boolean isDrawable(RenderedComponent component) {
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
