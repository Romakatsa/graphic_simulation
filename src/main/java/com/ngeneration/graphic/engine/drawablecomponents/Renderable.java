package com.ngeneration.graphic.engine.drawablecomponents;

import com.ngeneration.graphic.engine.drawers.Drawer;

public interface Renderable {

    <T> void render(Drawer<T> drawer);

}
