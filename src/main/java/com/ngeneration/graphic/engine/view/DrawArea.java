package com.ngeneration.graphic.engine.view;

import com.ngeneration.graphic.engine.Vector;

import java.util.ArrayList;
import java.util.List;

public abstract class DrawArea implements AutoCloseable {

    protected final List<DrawContext> contexts = new ArrayList<>();
    protected final Window holderWindow;
    protected Vector zoomFactor = Vector.one();
    protected Vector shift = Vector.zero();

    public DrawArea(Window holderWindow) {
        this.holderWindow = holderWindow;
    }

    protected abstract boolean withinAreaBounds(Vector position);

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
    }

    public void setZoom(Vector zoomFactor) {
        this.zoomFactor = zoomFactor;
    }

    public void setShift(Vector shift) {
        this.shift = shift;
    }

    public Vector getZoomFactor() {
        return zoomFactor;
    }

    public Vector getShift() {
        return shift;
    }
}
