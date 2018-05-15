package com.ngeneration;

import com.ngeneration.graphic.engine.lwjgl_engine.GraphicEngine;
import com.ngeneration.graphic.engine.lwjgl_engine.LwjglGraphicEngine;
import com.ngeneration.graphic.engine.view.DrawArea;
import com.ngeneration.graphic.engine.view.DrawContext;
import com.ngeneration.graphic.engine.view.Window;

class LwjglSimuationFacade {
    public static DrawContext createWindowAndGetContext(String title, int x, int y) {
        GraphicEngine<Long> engine = LwjglGraphicEngine.getInstance();
        Window<Long> mainWindow = Window.<Long>create(title, x, y, engine);
        DrawArea backgroundArea = mainWindow.allocateFullScreenArea();
        DrawContext everything = new DrawContext("everything");
        backgroundArea.addContext(everything);
        return everything;
    }
}
