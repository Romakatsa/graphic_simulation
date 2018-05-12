package com.ngeneration.graphic.engine.lwjgl_engine;

import com.ngeneration.graphic.engine.Shape;
import com.ngeneration.graphic.engine.drawers.Drawer;
import com.ngeneration.graphic.engine.enums.ColorEnum;

public interface GraphicEngine<ID> {
    ID createWindow(String title, int width, int height, ColorEnum background);

    void closeWindow(ID id);

    void beforeFrame();

    void render(double x, double y, double sx, double sy, double rotate, ColorEnum color, Shape shape, double opacity);

    void afterFrame(ID id);

    void shutdown();

    boolean isStarted();

    boolean isPaused();

    Drawer<ID> getDrawer();
}
