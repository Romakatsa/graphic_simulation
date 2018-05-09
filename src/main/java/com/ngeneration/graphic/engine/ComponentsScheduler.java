package com.ngeneration.graphic.engine;

import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ComponentsScheduler<T extends RenderedComponent> { // todo the same interface as other schedulers. Or redesign this
    private Consumer<T> action;
    private Set<T> components = new HashSet<>();
    private Thread thread; // todo manage thread amount. Should use static field ExecutorService?
    private boolean pause;

    public ComponentsScheduler(Consumer<T> action) {
        this.action = action;
        thread = new Thread(this::run, "scheduler-" + this + "-thread");
        thread.start();
    }

    private void run() {
        int timesPerSecond = 30;//todo hardcode
        while (!thread.isInterrupted()) {
            long startIterationMillis = System.nanoTime() / 1_000_000;
            action();
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

    private void action() {
        if (action != null) {
            synchronized (components) {
                for (T component : components) {
                    action.accept(component);
                }
            }
        }
    }

    private void sleep(int timesPerSecond, long startIterationMillis) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(
                1_000 / timesPerSecond
                        - (System.nanoTime() / 1_000_000 - startIterationMillis));
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
}
