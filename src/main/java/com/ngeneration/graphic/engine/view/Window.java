package com.ngeneration.graphic.engine.view;

import com.ngeneration.graphic.engine.Corner;
import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.drawers.Drawer;
import com.ngeneration.graphic.engine.drawers.LwjglDrawer;
import com.ngeneration.graphic.engine.enums.ColorEnum;
import com.ngeneration.graphic.engine.lwjgl_engine.GraphicEngine;

import java.util.ArrayList;
import java.util.List;

public class Window<ID> {

    private ID id;
    private String title;
    private Vector size;
    private ColorEnum background;
    private boolean active = false;
    private Thread thread;
    private final List<DrawArea> areas = new ArrayList<>();
    private GraphicEngine<ID> engine;

    public Window(ID id, String title, int x, int y, GraphicEngine<ID> engine) {
        this(id, title, new Vector(x, y), engine, ColorEnum.BLACK);
    }

    // TODO wrong access modifier
    public Window(ID id, String title, Vector size, GraphicEngine<ID> engine, ColorEnum background) {
        this.title = title;
        this.size = size;
        this.engine = engine;
        this.background = background;
    }

    public Window(String title, Vector size, GraphicEngine<ID> engine, ColorEnum background) {
        this.title = title;
        this.size = size;
        this.engine = engine;
        this.background = background;
    }

    public static <ID> Window create(String title, int x, int y, GraphicEngine<ID> engine) {
        return create(title, new Vector(x, y), engine, ColorEnum.BLACK);
    }

    public static <ID> Window create(String title, Vector size, GraphicEngine<ID> engine, ColorEnum background) {
        Window<ID> window = new Window<>(title, size, engine, background);
        Drawer<ID> drawer = engine.getDrawer();
        ID id = drawer.createAndBindToWindow(window);
        window.setId(id);
        drawer.startContinuouslyDrawing();
        return window;
    }

    private void setId(ID id) {
        this.id = id;
    }

    public void close() {
        areas.forEach(DrawArea::close);
        areas.clear();
        engine.closeWindow(id);
    }

    public DrawArea allocateArea(Corner corner, double fractionX, double fractionY) {
        RectDrawArea area = new RectDrawArea(this, corner, fractionX, fractionY);
        areas.add(area);
        return area;
    }

    public GraphicEngine<ID> getEngine() {
        return engine;
    }

    public List<DrawArea> getAreas() {
        return areas;
    }

    public ColorEnum getBackground() {
        return background;
    }

    public Vector getSize() {
        return size;
    }

    public ID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return (int) size.getX();
    }

    public int getHeight() {
        return (int) size.getY();
    }

    public boolean isActive() {
        return active;
    }
}
