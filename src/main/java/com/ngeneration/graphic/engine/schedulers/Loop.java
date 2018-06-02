package com.ngeneration.graphic.engine.schedulers;

import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;
import com.ngeneration.graphic.engine.utils.MathUtils;

import java.util.function.BiConsumer;

import static java.lang.Math.abs;

public class Loop implements BiConsumer<RenderedComponent, Double> {
    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;

    public Loop(double rangeRadius) {
        this(-rangeRadius, rangeRadius, -rangeRadius, rangeRadius);
    }

    public Loop(double minX, double maxX, double minY, double maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    @Override
    public void accept(RenderedComponent component, Double deltaTime) {
        double x = component.getPosition().getX();
        double y = component.getPosition().getY();
        component.setPosition(
                new Vector(
                        MathUtils.loopValueInBounds(x, minX, maxX),
                        MathUtils.loopValueInBounds(y, minY, maxY)
                ));
    }


}
