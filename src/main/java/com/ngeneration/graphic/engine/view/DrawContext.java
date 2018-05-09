package com.ngeneration.graphic.engine.view;

import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;
import com.ngeneration.graphic.engine.exceptions.ComponentAlreadyAddedException;
import com.ngeneration.graphic.engine.exceptions.ComponentNotFoundException;

import java.util.*;

public class DrawContext {
    private static final int DEFAULT_LAYERS_AMOUNT = 100;

    private final String name;
    private boolean isActivated = true;
    private final List<Layer> layers;
    // TODO probably DrawContext should know about DrawArea and Window holders. Add as needed

    public DrawContext(String name) {
        this(name, true);
    }

    public DrawContext(String name, boolean isActivated) {
        this(name, isActivated, DEFAULT_LAYERS_AMOUNT);
    }

    public DrawContext(String name, boolean isActivated, int layersAmount) {
        this.name = name;
        this.isActivated = isActivated;
        layers = allocateLayerNumber(layersAmount);
    }

    public List<Layer> getLayers() {
        return layers;
    }

    /**
     * Added component mustn't be already added in this context, arise ComponentAlreadyAddedException otherwise
     *
     * @param components component array to be appended to this context
     * @throws ComponentAlreadyAddedException if such component already was added in current context
     */

    public void put(int layer, RenderedComponent... components) throws ComponentAlreadyAddedException {
        for (RenderedComponent component : components) {
            checkThatNotAddedYet(component);
            component.registerContext(this);
            getLayer(layer).putUp(component);
        }
    }

    public void putDown(int layer, RenderedComponent... newComponents) throws ComponentAlreadyAddedException {
        for (RenderedComponent newComponent : newComponents) {
            checkThatNotAddedYet(newComponent);
            newComponent.registerContext(this);
            getLayer(layer).putDown(newComponent);
        }
    }

    public void putAfter(int layer, RenderedComponent afterMe, RenderedComponent... newComponents)
            throws ComponentAlreadyAddedException, ComponentNotFoundException {
        for (RenderedComponent newComponent : newComponents) {
            checkThatNotAddedYet(newComponent);
            checkThatExists(afterMe);
            newComponent.registerContext(this);
            getLayer(layer).putAfter(afterMe, newComponent);
        }
    }

    public void putBefore(int layer, RenderedComponent beforeMe, RenderedComponent... newComponents)
            throws ComponentAlreadyAddedException, ComponentNotFoundException {
        for (RenderedComponent newComponent : newComponents) {
            checkThatNotAddedYet(newComponent);
            checkThatExists(beforeMe);
            newComponent.registerContext(this);
            getLayer(layer).putBefore(beforeMe, newComponent);
        }
    }

    public boolean remove(RenderedComponent component) {
        // "anyMatch()" instead of "foreach()" because should try remove in each layer until once successfully,
        // Otherwise computation complexity O(n*m), where n - layers amount, m - objects amount.
        // Should try another approach?
        // HashMap instead of list (but no such ordering capabilities, crucial disadvantage)
        return layers.parallelStream().anyMatch(layer -> layer.remove(component));
    }


    public boolean contains(RenderedComponent component) {
        return layers.parallelStream().anyMatch(layer -> layer.contains(component));
    }

    private void checkThatExists(RenderedComponent component) throws ComponentNotFoundException {
        boolean contains = layers.parallelStream().anyMatch(layer -> layer.contains(component));
        if (!contains) {
            throw new ComponentNotFoundException(component);
        }
    }

    private void checkThatNotAddedYet(RenderedComponent component) throws ComponentAlreadyAddedException {
        boolean contains = layers.parallelStream().anyMatch(layer -> layer.contains(component));
        if (contains) {
            throw new ComponentAlreadyAddedException(component);
        }
    }

    private Layer getLayer(int layer) {
        return layers.get(layer);
    }

    public void close() {
        layers.forEach(Layer::close);
    }

    public String getName() {
        return name;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    private List<Layer> allocateLayerNumber(int amount) {
        List<Layer> layers = new ArrayList<>(amount);
        for (int i = 0; i < 100; i++) {
            layers.add(new Layer());
        }
        return layers;
    }

    public class Layer {
        private final List<RenderedComponent> components = Collections.synchronizedList(new ArrayList<>());

        public List<RenderedComponent> getComponents() {
            return components;
        }

        private boolean putDown(RenderedComponent component) {
            components.add(0, component);
            return true;
        }

        private boolean putUp(RenderedComponent component) {
            return components.add(component);
        }

        private boolean putBefore(RenderedComponent beforeMe, RenderedComponent newComponent) {
            int index = components.indexOf(beforeMe);
            components.add(index, newComponent);
            return true;
        }

        private boolean putAfter(RenderedComponent afterMe, RenderedComponent newComponent) {
            int index = components.indexOf(afterMe);
            components.add(index + 1, newComponent);
            return true;
        }

        private boolean remove(RenderedComponent component) {
            return components.remove(component);
        }

        private boolean contains(RenderedComponent component) {
            return components.contains(component);
        }

        private void close() {
            components.forEach(component -> component.unregisterFromContext(DrawContext.this));
            components.clear();
        }
    }
}
