package com.ngeneration.graphic.engine.schedulers;

import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;

import java.util.function.Consumer;

public class Loop implements Consumer<RenderedComponent> {
    @Override
    public void accept(RenderedComponent component) {
        component.setPosition(
                new Vector(Math.abs((component.getPosition().getX()+100) % 100) - 50,
                        Math.abs((component.getPosition().getY()+100) % 100) - 50));
    }
}
