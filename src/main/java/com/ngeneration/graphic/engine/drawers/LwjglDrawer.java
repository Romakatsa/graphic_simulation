package com.ngeneration.graphic.engine.drawers;

import com.ngeneration.graphic.engine.LazyLogger;
import com.ngeneration.graphic.engine.Shape;
import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;
import com.ngeneration.graphic.engine.enums.Color;
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
    private static final LazyLogger logger = LazyLogger.getLogger(LwjglDrawer.class);
    private static final boolean TRACE_FPS = false;

    private Window<Long> window;
    private LwjglGraphicEngine engine = LwjglGraphicEngine.getInstance();
    private boolean cleanScreenBeforeDrawing = true;
    private boolean showGrid = true;
    private boolean showReport = false;
    //    private Lock lock = new ReentrantLock();
    //    private Condition windowIsCreated = lock.newCondition();
    //    private Condition shouldDraw = lock.newCondition();
    //    private Condition shouldCreateWindow = lock.newCondition();

    private final Object shouldCreateWindowMonitor = new Object();
    private final Object shouldDrawMonitor = new Object();
    private final Object windowIsCreatedMonitor = new Object();
    private boolean shouldCreateWindow = false;
    private boolean shouldDraw = false;
    private boolean windowIsCreated = false;
    private Instant lastReportInstant = null;
    private boolean saveProportions;

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
                    Instant startInstant = Instant.now();
                    if (cleanScreenBeforeDrawing) {
                        engine.beforeFrame();
                    }
                    if (!engine.isPaused()) {
                        renderFrame();
                    }
                    engine.afterFrame(window.getId());
                    Duration iterationDuration = Duration.between(startInstant, Instant.now());
                    if (TRACE_FPS || iterationDuration.compareTo(Duration.ofMillis(18)) > 0) {
                        logger.trace(iterationDuration.toString() + ". Should be lower than 0.017S");
                    }
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
        ReportBuilder.TreeReport reportBuilder = new ReportBuilder.TreeReport();

//        for (DrawArea area : window.getAreas()) {
//            render(area, null);
//        }
        for (DrawArea area : window.getAreas()) {
            area.render(this, null);
            for (DrawContext context : area.getContexts()) {
                for (DrawContext.Layer layer : context.getLayers()) {
                    synchronized (layer.getComponents()) {
                        for (RenderedComponent component : layer.getComponents()) {
                            updateReport(reportBuilder, area, context, layer, component);
                            if (area.withinAreaBounds(component.getPosition())) {
                                component.render(this, area);
                            }
//                            render(component, area);
                        }
                    }
                }
            }
        }
        showGrid();
        report(reportBuilder);
    }

    private void report(ReportBuilder.TreeReport report) {
        if (
                showReport
                        &&
                        (lastReportInstant == null
                                || Duration.between(lastReportInstant, Instant.now()).compareTo(Duration.ofSeconds(5)) >= 0)
                ) {
            report.print();
            lastReportInstant = Instant.now();
        }
    }

    private void updateReport(ReportBuilder.TreeReport report, DrawArea area, DrawContext context, DrawContext.Layer layer, RenderedComponent component) {
        report.append("AREA", area, 0);
        report.append(context.getName(), context, 1);
        report.append("layer#" + layer.getNumber(), layer, 2);
        report.append("" + component);
    }

    private Set<RenderedComponent> gridLines = new HashSet<>();

    {
        int sectionNumber = 10;
        double width = 0.2;
        Color color = Color.DARK_GREEN;
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
        if (showGrid) {
            for (RenderedComponent gridLine : gridLines) {
                doRender(gridLine, null);
            }
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

    protected void doRender(RenderedComponent component, DrawArea holder) {
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
                    component.getRotation(), component.getColors(), component.getShapes(), component.getOpacity(),
                    holder);
        } else {
            logger.error("Object isn't drawable. Position: " + component.getPosition() +
                    ", size: " + component.getSize());

        }
    }

    private Vector transformPosition(RenderedComponent component, DrawArea area) {
        Vector componentPosition = component.getPosition();
//        Vector componentSize = component.getSize();
        double areaRotation = area.getRotationRadian();//+ area.getRotation();
        Vector zoom = area.getZoomFactor();
        if (saveProportions) {
            zoom = zoom.coordinatewiseMultiplication(area.getSize().divide(100));
        }
        Vector shift = area.getShift().plus(area.getPosition());
        Vector.Polar polar
                = componentPosition//.coordinatewiseMultiplication(zoom).plus(shift)
                .toPolar();
        polar.setRadian(polar.getRadian() + areaRotation);
        Vector transformedPosition = polar.toFlatCartesianVector().coordinatewiseMultiplication(zoom).plus(shift);
//        Vector transformedPosition = polar.toFlatCartesianVector();
        return transformedPosition;
    }

    private Vector transformSize(RenderedComponent component, DrawArea area) {
        saveProportions = false;
        Vector size = component.getSize().coordinatewiseMultiplication(area.getZoomFactor());
        if (saveProportions) {
            size = size.coordinatewiseMultiplication(area.getSize().divide(100));
        }
        return size;
    }

    private double transformRotation(RenderedComponent component, DrawArea area) {
        return component.getRotation() + area.getRotationRadian();
    }

    protected boolean isDrawable(RenderedComponent component, DrawArea holder) {
        if (holder == null) {
            return true;//todo collapse
        }
        return holder.isVisible();
//        double componentPositionX = component.getPosition().getX();
//        double componentPositionY = component.getPosition().getY();
//        Vector areaLowBound = Vector.zero().minus(holder.getSize().divide(2));
//        Vector areaHighBound = holder.getSize().divide(2);
//        Vector reverseZoom = Vector.one().divide(holder.getZoomFactor());
//        return componentPositionX >
//                areaLowBound
//                        .coordinatewiseMultiplication(reverseZoom).getX()
//                &&
//                componentPositionX <
//                        areaHighBound
//                                .coordinatewiseMultiplication(reverseZoom).getX()
//                &&
//                componentPositionY >
//                        areaLowBound
//                                .coordinatewiseMultiplication(reverseZoom).getY()
//                &&
//                componentPositionY <
//                        areaHighBound
//                                .coordinatewiseMultiplication(reverseZoom).getY()
//                ;
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
