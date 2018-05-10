package com.ngeneration.graphic.engine.drawers;

import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;
import com.ngeneration.graphic.engine.enums.ColorEnum;
import com.ngeneration.graphic.engine.lwjgl_engine.LwjglGraphicEngine;
import com.ngeneration.graphic.engine.report.ReportBuilder;
import com.ngeneration.graphic.engine.view.DrawArea;
import com.ngeneration.graphic.engine.view.DrawContext;
import com.ngeneration.graphic.engine.view.Window;

public class LwjglDrawer extends Drawer<Long> {
    private Window<Long> window;
    private LwjglGraphicEngine engine = new LwjglGraphicEngine();
    private boolean cleanScreenBeforeDrawing = true;
    //    private Condition shouldCreateWindow = lock.newCondition();
    //    private Condition shouldDraw = lock.newCondition();
    //    private Condition windowIsCreated = lock.newCondition();
    //    private Lock lock = new ReentrantLock();

    private final Object shouldCreateWindowMonitor = new Object();
    private final Object shouldDrawMonitor = new Object();
    private final Object windowIsCreatedMonitor = new Object();
    private boolean shouldCreateWindow = false;
    private boolean shouldDraw = false;
    private boolean windowIsCreated = false;

    public LwjglDrawer() {
//        this.window = window;
//        this.engine = window.getEngine();
        Thread drawThread = new Thread(() -> {
            try {
                synchronized (shouldCreateWindowMonitor) {
                    while (!shouldCreateWindow) {
                        shouldCreateWindowMonitor.wait();
                    }
                }
                windowId = engine.createWindow(window.getTitle(), window.getWidth(), window.getHeight(), window.getBackground());
                synchronized (windowIsCreatedMonitor) {
                    windowIsCreated = true;
                    windowIsCreatedMonitor.notifyAll();
                }
                synchronized (shouldDrawMonitor) {
                    while (!shouldDraw) {
                        shouldDrawMonitor.wait();
                    }
                }
                while (!Thread.currentThread().isInterrupted()
                        && engine.isStarted()) {
                    if (cleanScreenBeforeDrawing) {
                        engine.beforeFrame();
                    }
                    if (!engine.isPaused()) {
                        renderFrame();
                    }
                    engine.afterFrame(window.getId());
                }
            } catch (InterruptedException e) {
            }
            engine.closeWindow(window.getId());
            engine.shutdown();
        }, "lwjgl-drawer-thread");
        drawThread.start();
    }

    public void startContinuouslyDrawing() {
        synchronized (shouldDrawMonitor) {
            shouldDraw = true;
            shouldDrawMonitor.notifyAll();
        }
    }

    private void renderFrame() {
        ReportBuilder.TreeReport report = new ReportBuilder.TreeReport();
//        StringBuilder report = new StringBuilder("===================== NEW FRAME =====================\n");
        for (DrawArea area : window.getAreas()) {
            for (DrawContext context : area.getContexts()) {
                for (DrawContext.Layer layer : context.getLayers()) {
                    synchronized (layer.getComponents()) {
                        for (RenderedComponent component : layer.getComponents()) {
                            report.append("AREA", area, 0);
                            report.append(context.getName(), context, 1);
                            report.append("layer#" + layer.getNumber(), layer, 2);
                            report.append("" + component);

// TODO color

                            showAreaBounds(area);
                            render(component, area);
                        }
                    }
                }
            }
        }
        System.out.println(report.build());
    }

    private void showAreaBounds(DrawArea area) {
        RenderedComponent areaBounds1 = new RenderedComponent();
        RenderedComponent areaBounds2 = new RenderedComponent();
        RenderedComponent areaBounds3 = new RenderedComponent();
        RenderedComponent areaBounds4 = new RenderedComponent();
        areaBounds1.setPosition(area.getShift().plus(new Vector(50, 0)));
        areaBounds2.setPosition(area.getShift().plus(new Vector(-50, 0)));
        areaBounds3.setPosition(area.getShift().plus(new Vector(0, 50)));
        areaBounds4.setPosition(area.getShift().plus(new Vector(0, -50)));
        areaBounds3.setSize(new Vector(1, area.getZoomFactor().getY()*100));
        areaBounds4.setSize(new Vector(1, area.getZoomFactor().getY()*100));
        areaBounds1.setSize(new Vector(area.getZoomFactor().getX()*100, 1));
        areaBounds2.setSize(new Vector(area.getZoomFactor().getX()*100, 1));
        showAreaBounds(areaBounds1);
        showAreaBounds(areaBounds2);
        showAreaBounds(areaBounds3);
        showAreaBounds(areaBounds4);
        render(areaBounds1, area);
        render(areaBounds2, area);
        render(areaBounds3, area);
        render(areaBounds4, area);
    }
    private void showAreaBounds(RenderedComponent bound) {
        bound.setColors(ColorEnum.GREEN);
    }


    public void render(RenderedComponent component, DrawArea area) {
        if (isDrawable(component)) {
            doRender(component, area);
        }
    }

    protected void doRender(RenderedComponent component, DrawArea area) {
//        component.render();
//        System.out.println("Draw: " + component);
//        AssertUtils.notNull(component.getPosition());
//        AssertUtils.notNull(component.getSize());
        if (component.getPosition() != null
                && component.getSize() != null) {
            Vector position
                    = renderPosition(component.getPosition(), area.getZoomFactor(), area.getShift());
            engine.render(position.getX(),
                    position.getY(),
                    component.getSize().getX() * area.getZoomFactor().getX(),
                    component.getSize().getY() * area.getZoomFactor().getY(),
                    component.getRotation(), component.getColors(), component.getShapes());
        }
    }

    private Vector renderPosition(Vector position, Vector zoom, Vector shift) {
        return position.coordinatewiseMultiplication(zoom).plus(shift).plus(Vector.diag(50));
    }

    protected boolean isDrawable(RenderedComponent component) {
        return true;// TODO
    }

    private volatile Long windowId;

    @Override
    public Long createAndBindToWindow(Window<Long> window) {
        this.window = window;
        synchronized (shouldCreateWindowMonitor) {
            shouldCreateWindow = true;
            shouldCreateWindowMonitor.notifyAll();
        }
        try {
            synchronized (windowIsCreatedMonitor) {
                while (!windowIsCreated) {
                    windowIsCreatedMonitor.wait();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return windowId;
    }
}
