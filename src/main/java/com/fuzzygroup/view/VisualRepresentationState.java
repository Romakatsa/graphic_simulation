package com.fuzzygroup.view;

import com.fuzzygroup.view.enums.ColorEnum;
import com.ngeneration.graphic.engine.ThreeVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class VisualRepresentationState implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(Display.class);

    transient protected Shape shapes;
    transient protected ColorEnum colors;
    transient protected ThreeVector position;
    transient protected ThreeVector speed;
    transient protected ThreeVector acceleration;
    transient protected ThreeVector size;
    transient protected double rotation;
    transient protected boolean visible = true;
    transient protected double opacity;


    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation % (2 * Math.PI);
    }

    public VisualRepresentationState(ThreeVector position, ThreeVector speed, ThreeVector acceleration, ThreeVector size,
                                     Shape shapes, ColorEnum colors,
                                     double rotation, double opacity, boolean visible) {
        this.shapes = shapes;
        this.colors = colors;
        this.position = position;
        this.speed = speed;
        this.acceleration = acceleration;
        this.size = size;
        this.visible = visible;
        this.rotation = rotation;
        this.opacity = opacity;
    }

    public void enableReporting() {
        this.addStateReporter(1000,
                state ->
                {
                    logger.debug("Automatic report: " + state.getSpeed().module() + ": "
                            + state.getSpeed().module() * Math.cos(state.getRotation())
                            + " x " + state.getSpeed().module() * Math.sin(state.getRotation()));
                }
        );
    }

    private static List<ScheduledTask> consumers = new CopyOnWriteArrayList<>();

    static {
        Runnable schedulerRunnable = () -> {
            int fps = 1;
            while (true) {
                long beginIterationNano = System.nanoTime();
                consumers.stream().filter(t -> Context.uptimeMillis() - t.lastExecution >= t.period)
                        .forEach(t -> {
                            t.lastExecution = Context.uptimeMillis();
                            t.action.accept(t.entity);
                        });
                try {
                    TimeUnit.MILLISECONDS.sleep(1000 / fps - (System.nanoTime() - beginIterationNano) / 1000_000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        };
        Thread scheduler = new Thread(schedulerRunnable, "scheduled_tasks_executor");
        scheduler.setDaemon(true);
        scheduler.start();
    }

    public <T extends VisualRepresentationState> void addStateReporter(int period, Consumer<T> action) {
        consumers.add(new ScheduledTask(this, period, action));
    }

    private static class ScheduledTask<T extends VisualRepresentationState> {
        private T entity;
        private Consumer<T> action;
        private int period;
        public int lastExecution;

        public ScheduledTask(T state, int period,
                             Consumer<T> action) {
            this.entity = state;
            this.action = action;
            this.period = period;
        }
    }


    public VisualRepresentationState cloneState() {
        return new VisualRepresentationState(position, speed, acceleration, size, shapes, colors, rotation, opacity, visible);
    }

    @Override
    public String toString() {
        return String.format("VisualRepresentation {%-30s %-30s %-30s %b}", size, position, speed, visible);
    }

    public Shape getShape() {
        return shapes;
    }

    public ColorEnum getColors() {
        return colors;
    }

    public ThreeVector getPosition() {
        return position;
    }

    public ThreeVector getSpeed() {
        return speed;
    }

    public ThreeVector getAcceleration() {
        return acceleration;
    }

    public boolean isVisible() {
        return visible;
    }

    public ThreeVector getSize() {
        return size;
    }

    public void setShapes(Shape shapes) {
        this.shapes = shapes;
    }

    public void setColors(ColorEnum colors) {
        this.colors = colors;
    }

    public void setPosition(ThreeVector position) {
        this.position = position;
    }

    public void setSpeed(ThreeVector speed) {
        this.speed = speed;
    }

    public void setAcceleration(ThreeVector acceleration) {
        this.acceleration = acceleration;
    }

    public void setSize(ThreeVector size) {
        this.size = size;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public double getOpacity() {
        return opacity;
    }
}
