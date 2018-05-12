package com.ngeneration.graphic.engine.physics;

import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface RenderedComponentStateRegularUpdater<T extends RenderedComponent> extends BiConsumer<T, Double> {

}
