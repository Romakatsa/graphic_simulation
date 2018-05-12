package com.ngeneration.graphic.engine.drawablecomponents;

import com.ngeneration.graphic.engine.ComponentsScheduler;
import com.ngeneration.graphic.engine.LazyLogger;
import com.ngeneration.graphic.engine.Shape;
import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.drawers.Console1DDrawer;
import com.ngeneration.graphic.engine.drawers.Drawer;
import com.ngeneration.graphic.engine.drawers.LwjglDrawer;
import com.ngeneration.graphic.engine.enums.ColorEnum;
import com.ngeneration.graphic.engine.view.DrawContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class RenderedComponent implements Cloneable {
    private static final LazyLogger logger = LazyLogger.getLogger(RenderedComponent.class);

    protected String name;
    protected Vector position;
    protected Vector size;
    protected Shape shapes;
    protected ColorEnum colors;
    protected double rotation;
    protected boolean visible = true;
    protected double opacity;

    protected Set<DrawContext> contexts = new HashSet<>();

//    protected Drawer drawer = new LwjglDrawer();//todo: inject?

    protected Supplier<Boolean> isComponentAlreadyNeed = () -> true; //TODO elaborate on
    protected List<ComponentsScheduler> schedulers = new ArrayList<>();

    public RenderedComponent(Vector position, Vector size, double rotation, ColorEnum colors, double opacity, Shape shapes) {
        this.position = position;
        this.size = size;
        this.shapes = shapes;
        this.colors = colors;
        this.rotation = rotation;
        this.opacity = opacity;
    }


//     TODO good idea, but need more time for design
//    public void render() {
//        if (visible) {
//            drawer.render(this);
//        }
//    }

    public static <T> RenderedComponent map(T mappedEntity) {
        logger.warn("behaviour doesn't implemented");
//        TODO choose:
//        throw new OperationNotSupportedException();
//        return new RenderedComponent();
//        return null;
        return null;
    }

    public void onCreation() {
        //TODO chain of responsibility + observer
        // TODO add schedulers
    }

    public void destroy() {
        contexts.forEach(c -> c.remove(this));
        schedulers.forEach(s -> s.remove(this)); // TODO handle warning. Investigate here
    }

    /*
        This method only for internal use.
        Investigate possibility of using this method only within package.
        Probably should use Jigsaw?
     */
    public void registerContext(DrawContext context) {
        contexts.add(context);
    }

    public void unregisterFromContext(DrawContext context) {
        contexts.remove(context);
    }

    /*
        This method only for internal use.
        Investigate possibility of using this method only within package.
        Probably should use Jigsaw?
     */
    public void registerScheduler(ComponentsScheduler scheduler) {
        schedulers.add(scheduler);
    }

    public void unregisterScheduler(ComponentsScheduler<? extends RenderedComponent> scheduler) {
        schedulers.remove(scheduler);
    }

    @Override
    public RenderedComponent clone() throws CloneNotSupportedException {
        return (RenderedComponent) super.clone();
    }

    public Vector getPosition() {
        return position;
    }

    public Vector getSize() {
        return size;
    }

    public void setRotation(double radian) {
        this.rotation = radian;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public void setSize(Vector size) {
        this.size = size;
    }

    public Shape getShapes() {
        return shapes;
    }

    public void setShapes(Shape shapes) {
        this.shapes = shapes;
    }

    public ColorEnum getColors() {
        return colors;
    }

    public void setColors(ColorEnum colors) {
        this.colors = colors;
    }

    public double getRotation() {
        return rotation;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public double getOpacity() {
        return opacity;
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
