package com.ngeneration.graphic.engine.drawers;

import com.ngeneration.graphic.engine.Shape;
import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;
import com.ngeneration.graphic.engine.enums.ColorEnum;
import com.ngeneration.graphic.engine.lwjgl_engine.LwjglGraphicEngine;
import com.ngeneration.graphic.engine.report.ReportBuilder;
import com.ngeneration.graphic.engine.view.DrawArea;
import com.ngeneration.graphic.engine.view.DrawContext;
import com.ngeneration.graphic.engine.view.Window;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class LwjglDrawer extends Drawer<Long> {
    private Window<Long> window;
    private LwjglGraphicEngine engine = new LwjglGraphicEngine();
    private boolean cleanScreenBeforeDrawing = true;
    private boolean showGrid = true;
    private boolean showReport = false;
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
    private Instant lastReportInstant = null;

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
        for (DrawArea area : window.getAreas()) {
            render(area, null);
            for (DrawContext context : area.getContexts()) {
                for (DrawContext.Layer layer : context.getLayers()) {
                    synchronized (layer.getComponents()) {
                        for (RenderedComponent component : layer.getComponents()) {
                            report.append("AREA", area, 0);
                            report.append(context.getName(), context, 1);
                            report.append("layer#" + layer.getNumber(), layer, 2);
                            report.append("" + component);
                            render(component, area);
                        }
                    }
                }
            }
        }
        if (showGrid) {
            showGrid();
        }
        if (
                showReport
                &&
                (lastReportInstant == null
                        || Duration.between(lastReportInstant, Instant.now()).compareTo(Duration.ofSeconds(5)) >= 0)
                ) {
            System.out.println(report.build());
            lastReportInstant = Instant.now();
        }
    }

    private Set<RenderedComponent> gridLines = new HashSet<>();

    {
        int sectionNumber = 10;
        double width = 0.2;
        ColorEnum color = ColorEnum.DARK_GREEN;
        double opacity = 0.8;
        for (int i = 0; i <= sectionNumber; i++) {
            gridLines.add(new RenderedComponent(new Vector(0, i * (100 / sectionNumber) - 50),
                    new Vector(width, 100), 0, color, opacity, Shape.RECT));
        }
        for (int i = 0; i <= sectionNumber; i++) {
            gridLines.add(new RenderedComponent(new Vector(i * (100 / sectionNumber) - 50, 0),
                    new Vector(100, width), 0, color, opacity, Shape.RECT));
        }
    }

    private void showGrid() {
        for (RenderedComponent gridLine : gridLines) {
            doRender(gridLine);
        }
    }

    @Override
    protected RenderedComponent transform(RenderedComponent component, DrawArea holder) {
        if (holder == null) {
            return component;
        }
        Vector position
                = transformPosition(component, holder);
        Vector size
                = transformSize(component, holder);
        double rotation
                = transformRotation(component, holder);
        return new RenderedComponent(position, size, rotation,
                component.getColors(), component.getOpacity(), component.getShapes());
    }

    protected void doRender(RenderedComponent component) {
//        component.render();
//        System.out.println("Draw: " + component);
//        AssertUtils.notNull(component.getPosition());
//        AssertUtils.notNull(component.getSize());
        if (component.getPosition() != null
                && component.getSize() != null) {
            engine.render(component.getPosition().getX(),
                    component.getPosition().getY(),
                    component.getSize().getX(),
                    component.getSize().getY(),
                    component.getRotation(), component.getColors(), component.getShapes(), component.getOpacity());
        }
    }

    private Vector transformPosition(RenderedComponent component, DrawArea area) {
        Vector componentPosition = component.getPosition();
//        Vector componentSize = component.getSize();
        double areaRotation = area.getRotationRadian();
        Vector zoom = area.getZoomFactor();
        Vector shift = area.getShift();
        Vector.PolarCoordinateSystemVector polar
                = componentPosition//.coordinatewiseMultiplication(zoom).plus(shift)
                .toPolar();
        polar.setRadian(polar.getRadian() + areaRotation);
        Vector transformedPosition = polar.toFlatCartesianVector().coordinatewiseMultiplication(zoom).plus(shift);
//        Vector transformedPosition = polar.toFlatCartesianVector();
        return transformedPosition;
    }

    private Vector transformSize(RenderedComponent component, DrawArea area) {
        return component.getSize().coordinatewiseMultiplication(area.getZoomFactor());
    }

    private double transformRotation(RenderedComponent component, DrawArea area) {
        return component.getRotation() + area.getRotationRadian();
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
