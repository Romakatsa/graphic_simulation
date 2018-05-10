package com.ngeneration;

import com.ngeneration.ai.BrownianDriver;
import com.ngeneration.custom_rendered_components.Car;
import com.ngeneration.custom_rendered_components.Road;
import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;
import com.ngeneration.graphic.engine.enums.ColorEnum;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ComponentsFactory {

    public static Car.Builder aCar() {
        return new Car.Builder()
                .withColors(ColorEnum.RED)
                .withSize(Vector.one())
                .withPosition(Vector.one())
                .withSpeed(Vector.one())
                .withAcceleration(Vector.zero())
                .withDriver(new BrownianDriver(1, 1));
    }

    public static Road.Builder aVerticalRoadBound(double x, double y, double y2) {
        return new Road.Builder()
                .firstBoundPoint(new Vector(x, y))
                .nextBoundPoint(new Vector(x, y2));
    }

    public static Road.Builder aHorizontalRoadBound(double y, double x, double x2) {
        return new Road.Builder()
                .firstBoundPoint(new Vector(x, y))
                .nextBoundPoint(new Vector(x2, y));
    }

    public static Road.Builder aDirectRoad() {
        return new Road.Builder()
                .firstBoundPoint(com.ngeneration.graphic.engine.Vector.diag(1))
                .nextBoundPoint(com.ngeneration.graphic.engine.Vector.diag(2))
                .nextBoundPoint(com.ngeneration.graphic.engine.Vector.diag(3))
                .nextBoundPoint(com.ngeneration.graphic.engine.Vector.diag(4))
                .nextBoundPoint(com.ngeneration.graphic.engine.Vector.diag(5))
                .firstBoundPoint(new Vector(4, 0))
                .nextBoundPoint(new Vector(4, 10))
                .firstBoundPoint(new Vector(6, 0))
                .nextBoundPoint(new Vector(6, 10));
    }

    public static Set<RenderedComponent> populate(RenderedComponent component, int number) {
        HashSet<RenderedComponent> components = new HashSet<>();
        try {
            for (int i = 0; i < number; i++) {
                components.add(component.clone());
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return components;
    }

    public static <T extends RenderedComponent>Set<RenderedComponent> populate(T component, int number,
                                                                               BiConsumer<T, Integer>... transformation) {
        HashSet<RenderedComponent> components = new HashSet<>();
        try {
            for (int i = 0; i < number; i++) {
                T clone = (T) component.clone();
                for (BiConsumer<T, Integer> transform : transformation) {
                    transform.accept(clone, i);
                }
                components.add(clone);
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return components;
    }
}
