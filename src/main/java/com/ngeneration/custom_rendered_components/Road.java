package com.ngeneration.custom_rendered_components;

import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Road extends RenderedComponent {

    Road(Builder builder) {

    }

    // compositor??
    public static class Builder {
        private final Set<List<Vector>> bounds = new HashSet<>();
        private final List<Vector> currentBound = new ArrayList<>();

        public Builder firstBoundPoint(Vector point) {
            ArrayList<Vector> newBound = new ArrayList<>();
            newBound.add(point);
            bounds.add(newBound);
            return this;
        }

        public Builder nextBoundPoint(Vector point) {
            return this;
        }

        public Road build() {
            return new Road(this);
        }
    }
}
