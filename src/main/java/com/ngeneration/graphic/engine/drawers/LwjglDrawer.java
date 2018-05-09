package com.ngeneration.graphic.engine.drawers;

import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;
import com.ngeneration.graphic.engine.enums.ColorEnum;
import com.ngeneration.graphic.engine.lwjgl_engine.GraphicEngine;
import com.ngeneration.graphic.engine.lwjgl_engine.HelloWorld;
import com.ngeneration.graphic.engine.lwjgl_engine.LwjglGraphicEngine;
import com.ngeneration.graphic.engine.utils.AssertUtils;
import com.ngeneration.graphic.engine.view.DrawArea;
import com.ngeneration.graphic.engine.view.DrawContext;
import com.ngeneration.graphic.engine.view.Window;

public class LwjglDrawer extends Drawer<Long> {
    private Window<Long> window;
    private LwjglGraphicEngine engine = new LwjglGraphicEngine();
    //    private Lock lock = new ReentrantLock();
//    private Condition shouldCreateWindow = lock.newCondition();
//    private Condition shouldDraw = lock.newCondition();
//    private Condition windowIsCreated = lock.newCondition();
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
            engine.init();
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
                        && engine.isAlive()) {
//                    engine.beforeFrame();
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
        for (DrawArea area : window.getAreas()) {
            for (DrawContext context : area.getContexts()) {
                for (DrawContext.Layer layer : context.getLayers()) {
                    synchronized (layer.getComponents()) {
                        for (RenderedComponent component : layer.getComponents()) {
                            render(component);
                        }
                    }
                }
            }
        }
    }

    public void render(RenderedComponent component) {
        if (isDrawable(component)) {
            doRender(component);
        }
    }

    protected void doRender(RenderedComponent component) {
//        component.render();
//        System.out.println("Draw: " + component);
//        AssertUtils.notNull(component.getPosition());
//        AssertUtils.notNull(component.getSize());
        if (component.getPosition() != null
                && component.getSize() != null) {
            engine.render(component.getPosition().getX(), component.getPosition().getY(),
                    component.getSize().getX(), component.getSize().getY(),
                    component.getRotation(), component.getColors(), component.getShapes());
        }
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
