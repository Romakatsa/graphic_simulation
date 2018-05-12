package com.ngeneration.graphic.engine;

import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class ComponentsScheduler<T extends RenderedComponent> { // todo the same interface as other schedulers. Or redesign this
    private static final int DEFAULT_INTERVAL_MILLIS = 10;
    private BiConsumer<T, Double> action;
    private Set<T> components = new HashSet<>();
    private Thread thread; // todo manage thread amount. Should use static field ExecutorService?
    private boolean pause;
    private long intervalMillis;

    public ComponentsScheduler(BiConsumer<T, Double> action) {
        this(action, DEFAULT_INTERVAL_MILLIS);
    }

    public ComponentsScheduler(BiConsumer<T, Double> action, long intervalMillis) {
        this.action = action;
        thread = new Thread(this::run, "scheduler-" + this + "-thread");
        thread.start();
        this.intervalMillis = intervalMillis;
    }

    private void run() {
        double timesPerSecond = 1000d / intervalMillis;
        while (!thread.isInterrupted()) {
            long startIterationMillis = System.nanoTime() / 1_000_000;
            action( timesPerSecond / 1000d);
            try {
                sleep(timesPerSecond, startIterationMillis);
            } catch (InterruptedException e) {
                if (thread.isInterrupted()) {
                    System.err.println("Unnecessary line here");
                }
                thread.interrupt();
            }
        }
    }

    private void action(double deltaTime) {
        if (action != null) {
            synchronized (components) {
                for (T component : components) {
                    action.accept(component, deltaTime);
                }
            }
        }
    }

    private void sleep(double timesPerSecond, long startIterationMillis) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(
                (long) (1_000d / timesPerSecond
                                        - (System.nanoTime() / 1_000_000 - startIterationMillis)));
    }

    public boolean isPause() {
        return pause;
    }

    public void pause() {
        this.pause = true;
    }

    public void resume() {
        this.pause = false;
    }

    public synchronized void add(T component) {
        // TODO should be lock here, next two actions should be simultaneously.
        // For the sake of this reason track this method calling frequency
        component.registerScheduler(this);
        components.add(component);
    }

    public void remove(T component) {
        components.remove(component);
    }

    public BiConsumer<T, Double> getUpdater() {
        return action;
    }
}
