package com.ngeneration.graphic.engine.view;

import com.ngeneration.graphic.engine.Shape;
import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;
import com.ngeneration.graphic.engine.enums.Color;

import java.util.ArrayList;
import java.util.List;

public abstract class DrawArea extends RenderedComponent implements AutoCloseable {

    protected final List<DrawContext> contexts = new ArrayList<>();
    protected final Window holderWindow;
    protected Vector zoomFactor = Vector.one();
    protected Vector shift = Vector.zero();
    protected double rotationRadian;

    public DrawArea(Window holderWindow) {
        super(Vector.zero(), Vector.diag(100), 0, Color.WHITE, 0, Shape.RECT);
        this.holderWindow = holderWindow;
    }

    public abstract boolean withinAreaBounds(Vector position);

    public List<DrawContext> getContexts() {
        return contexts;
    }

    public void addContext(DrawContext context) {
        contexts.add(context);
    }

    public void close() {
    }

    public void setZoomFactor(double factor) {
        this.zoomFactor = Vector.one().multiple(factor);
//        this.size = Vector.diag(100).multiple(factor);
    }

    public void setZoom(Vector zoomFactor) {
        this.zoomFactor = zoomFactor;
//        this.size = zoomFactor.multiple(100);
    }

    public void setShift(Vector shift) {
        this.shift = shift;
//        this.position = shift;
    }

    public Vector getZoomFactor() {
        return zoomFactor;
    }

    public Vector getShift() {
        return shift;
    }

    public double getRotationRadian() {
        return rotationRadian;
    }

    public void setRotationRadian(double rotationRadian) {
        this.rotationRadian = rotationRadian;
//        this.rotation = rotationRadian;
    }
}
