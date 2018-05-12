package com.ngeneration.graphic.engine.schedulers;

import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Loop implements BiConsumer<RenderedComponent, Double> {
    @Override
    public void accept(RenderedComponent component, Double deltaTime) {
        component.setPosition(
                new Vector(Math.abs((component.getPosition().getX() + 150) % 100) - 50,
                        Math.abs((component.getPosition().getY() + 150) % 100) - 50));
    }
}
