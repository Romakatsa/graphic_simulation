package com.ngeneration.custom_rendered_components;

import com.ngeneration.graphic.engine.Shape;
import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.drawablecomponents.Line;
import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;
import com.ngeneration.graphic.engine.drawers.Drawer;
import com.ngeneration.graphic.engine.enums.Color;
import com.ngeneration.graphic.engine.view.DrawArea;

import java.util.*;

public class Road extends RenderedComponent {
    private final Set<List<Vector>> bounds;
    private final Set<Line> lines = new HashSet<>();

    Road(BoundsBuilder builder) {
        super(Vector.zero(), Vector.one(), 0, Color.DARK_GREEN, 0, Shape.RECT);
        this.bounds = Collections.unmodifiableSet(builder.bounds);
        for (List<Vector> points : builder.bounds) {
            for (int i = 1; i < points.size(); i++) {
                this.lines.add(new Line(points.get(i - 1), points.get(i), 0.5, Color.BLACK, 0));
            }
        }
    }

    Road(Builder builder) {
        super(Vector.zero(), Vector.one(), 0, Color.DARK_GREEN, 0, Shape.RECT);
        this.bounds = Collections.unmodifiableSet(builder.bounds);
        for (List<Vector> points : builder.bounds) {
            for (int i = 1; i < points.size(); i++) {
                this.lines.add(new Line(points.get(i - 1), points.get(i), 0.5, Color.BLACK, 0));
            }
        }
    }

    @Override
    public <T> void render(Drawer<T> drawer, DrawArea area) {
        lines.forEach(line -> line.render(drawer, area));
    }

    public Set<List<Vector>> getBounds() {
        return bounds;
    }

    // compositor??
    public static class BoundsBuilder {
        private final Set<List<Vector>> bounds = new HashSet<>();
        private List<Vector> currentBound = new ArrayList<>();

        public BoundsBuilder firstBoundPoint(Vector point) {
            ArrayList<Vector> newBound = new ArrayList<>();
            currentBound = newBound;
            currentBound.add(point);
            bounds.add(newBound);
            return this;
        }

        public BoundsBuilder nextBoundPoint(Vector point) {
            currentBound.add(point);
            return this;
        }

        public Road build() {
            return new Road(this);
        }
    }

    public static class Builder {
        private final Set<List<Vector>> bounds = new HashSet<>();
        private List<Vector> leftBound = new ArrayList<>();
        private List<Vector> rightBound = new ArrayList<>();
        private double currentWidth;

        public Builder withWidth(double width) {
            this.currentWidth = width;
            return this;
        }

        public Builder firstPoint(double x, double y) {
            return firstPoint(new Vector(x, y));
        }

        public Builder nextPoint(double x, double y) {
            return nextPoint(new Vector(x, y));
        }

        public Builder firstPoint(Vector point) {
            leftBound = new ArrayList<>();
            rightBound = new ArrayList<>();
            Vector leftBoundPoint = point.plus(new Vector(0, currentWidth / 2));
            Vector rightBoundPoint = point.minus(new Vector(0, currentWidth / 2));
            leftBound.add(leftBoundPoint);
            rightBound.add(rightBoundPoint);
            bounds.add(leftBound);
            bounds.add(rightBound);
            return this;
        }

        public Builder nextPoint(Vector point) {
            Vector lastLeftPoint = leftBound.get(leftBound.size() - 1);
            Vector lastRightPoint = rightBound.get(rightBound.size() - 1);
            Vector center = lastLeftPoint.plus(lastRightPoint.minus(lastLeftPoint).divide(2));
            Vector plus = point.minus(center);
            Vector newLeftPoint = lastLeftPoint.plus(plus);
            Vector newRightPoint = lastRightPoint.plus(plus);

            Vector.PolarCoordinateSystemVector polar;
            polar = plus.toPolar();
            polar.setRadian(polar.getRadian() - Math.PI / 2);
            polar.setModule(currentWidth/2);
            newLeftPoint = center.plus(plus.minus(polar.toFlatCartesianVector()));
            newRightPoint = center.plus(plus.plus(polar.toFlatCartesianVector()));

//                    polar.setModule(newLeftPoint.minus(center).module() - currentWidth / 2);
//            newLeftPoint = newLeftPoint.plus(polar.toFlatCartesianVector());

//            newRightPoint = newRightPoint.minus(polar.toFlatCartesianVector());

//            Vector.PolarCoordinateSystemVector polar;
//            polar = newLeftPoint.minus(center).toPolar();
//            polar.setRadian(plus.toPolar().getRadian());
//            newLeftPoint = polar.toFlatCartesianVector().plus(center);
//            polar = newRightPoint.minus(center).toPolar();
//            polar.setRadian(plus.toPolar().getRadian());
//            newRightPoint = polar.toFlatCartesianVector().plus(center);
            leftBound.add(newLeftPoint);
            rightBound.add(newRightPoint);
            return this;
        }

        public Road build() {
            return new Road(this);
        }
    }
}
