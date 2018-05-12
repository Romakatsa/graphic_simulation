package com.ngeneration.custom_rendered_components;

import com.ngeneration.graphic.engine.Shape;
import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;
import com.ngeneration.graphic.engine.enums.ColorEnum;

import java.util.*;

public class Road extends RenderedComponent {
    private final Set<List<Vector>> bounds;

    Road(Builder builder) {
        super(Vector.zero(), Vector.one(), 0, ColorEnum.DARK_GREEN, 0, Shape.RECT);
        this.bounds = Collections.unmodifiableSet(builder.bounds);
    }

    public Set<List<Vector>> getBounds() {
        return bounds;
    }

    // compositor??
    public static class Builder {
        private final Set<List<Vector>> bounds = new HashSet<>();
        private List<Vector> currentBound = new ArrayList<>();

        public Builder firstBoundPoint(Vector point) {
            ArrayList<Vector> newBound = new ArrayList<>();
            currentBound = newBound;
            currentBound.add(point);
            bounds.add(newBound);
            return this;
        }

        public Builder nextBoundPoint(Vector point) {
            currentBound.add(point);
            return this;
        }

        public Road build() {
            return new Road(this);
        }
    }
}
