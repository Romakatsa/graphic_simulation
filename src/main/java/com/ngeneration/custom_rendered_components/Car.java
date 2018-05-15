package com.ngeneration.custom_rendered_components;

import com.ngeneration.ai.Driver;
import com.ngeneration.graphic.engine.Shape;
import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.drawablecomponents.Controllable;
import com.ngeneration.graphic.engine.drawablecomponents.PhysicalRenderedComponent;
import com.ngeneration.graphic.engine.drawablecomponents.Renderable;
import com.ngeneration.graphic.engine.enums.ColorEnum;
import com.ngeneration.graphic.engine.view.DrawContext;

import java.util.HashMap;
import java.util.Map;

public class Car extends PhysicalRenderedComponent {
    private Driver driver;

    Car(Builder builder) {
        super(builder.position, builder.size, builder.rotation, builder.colors, builder.opacity, builder.shapes);
        this.visible = builder.visible;
        this.speed = builder.speed;
        this.acceleration = builder.acceleration;
        this.driver = builder.driver;
        builder.contexts.forEach((context, layerNumber) -> context.put(layerNumber, this));
    }

    public static class Builder {
        Vector position;
        Vector size;
        Shape shapes;
        ColorEnum colors;
        double rotation;
        boolean visible = true;
        double opacity;
        Vector speed;
        Vector acceleration;
        Driver driver;
        Map<DrawContext, Integer> contexts = new HashMap<>();

        public Builder withPosition(Vector position) {
            this.position = position;
            return this;
        }

        public Builder withSize(Vector size) {
            this.size = size;
            return this;
        }

        public Builder withShapes(Shape shapes) {
            this.shapes = shapes;
            return this;
        }

        public Builder withColors(ColorEnum colors) {
            this.colors = colors;
            return this;
        }

        public Builder withRotation(double rotation) {
            this.rotation = rotation;
            return this;
        }

        public Builder withVisible(boolean visible) {
            this.visible = visible;
            return this;
        }

        public Builder withOpacity(double opacity) {
            this.opacity = opacity;
            return this;
        }

        public Builder withSpeed(Vector speed) {
            this.speed = speed;
            return this;
        }

        public Builder withAcceleration(Vector acceleration) {
            this.acceleration = acceleration;
            return this;
        }

        public Builder withDriver(Driver driver) {
            this.driver = driver;
            return this;
        }

        public Car build() {
            return new Car(this);
        }

        public Builder putInContext(int layerNumber, DrawContext context) {
            contexts.put(context, layerNumber);
            return this;
        }
    }
}
