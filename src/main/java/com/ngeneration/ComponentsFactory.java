package com.ngeneration;

import com.ngeneration.ai.BrownianDriver;
import com.ngeneration.custom_rendered_components.Car;
import com.ngeneration.custom_rendered_components.Road;
import com.ngeneration.graphic.engine.ComponentsScheduler;
import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;
import com.ngeneration.graphic.engine.enums.Color;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class ComponentsFactory {

    public static Car.Builder aCar() {
        return new Car.Builder()
                .withColors(Color.DARK_BLUE)
                .withSize(Vector.one())
                .withPosition(Vector.one())
                .withSpeed(Vector.one())
                .withAcceleration(Vector.zero())
                .withDriver(new BrownianDriver(1, 1));
    }

    public static Road.BoundsBuilder aVerticalRoadBound(double x, double y, double y2) {
        return new Road.BoundsBuilder()
                .firstBoundPoint(new Vector(x, y))
                .nextBoundPoint(new Vector(x, y2));
    }

    public static Road.BoundsBuilder aHorizontalRoadBound(double y, double x, double x2) {
        return new Road.BoundsBuilder()
                .firstBoundPoint(new Vector(x, y))
                .nextBoundPoint(new Vector(x2, y));
    }

    public static Road.BoundsBuilder aDirectRoad() {
        return new Road.BoundsBuilder()
                .firstBoundPoint(new Vector(-50, 10))
                .nextBoundPoint(new Vector(50, 10))
                .firstBoundPoint(new Vector(-50, -10))
                .nextBoundPoint(new Vector(50, -10))
                ;
    }

    public static Road.Builder aRoad() {
        return new Road.Builder();
    }

    public static Set<RenderedComponent> populate(RenderedComponent component, int number) {
        HashSet<RenderedComponent> components = new HashSet<>();
        try {
            for (int i = 0; i < number; i++) {
                components.add(component.clone());
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return components;
    }

    public static <T extends RenderedComponent> Set<T> populate(T component, int number,
                                                                BiConsumer<T, Integer>... transformation) {
        Set<T> components = new HashSet<>();
        try {
            for (int i = 0; i < number; i++) {
                T clone = (T) component.clone();
                for (BiConsumer<T, Integer> transform : transformation) {
                    transform.accept(clone, i);
                    System.out.println("11112");
                }
                components.add(clone);
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return components;
    }

    public static class PopulationBuilder<T extends RenderedComponent> {
        private List<T> population = new ArrayList<>();
        private Set<BiConsumer<T, Integer>> handlers = new HashSet<>();
        private T specimen;

        public PopulationBuilder(T specimen) {
            this.specimen = specimen;
        }

        public PopulationBuilder<T> withName(BiConsumer<T, Integer> nameGenerator) {
            for (int i = 0; i < population.size(); i++) {
                nameGenerator.accept(population.get(i), i);
            }
            return this;
        }

        public PopulationBuilder<T> withPosition(BiConsumer<T, Integer> positionGenerator) {
            for (int i = 0; i < population.size(); i++) {
                positionGenerator.accept(population.get(i), i);
            }
            return this;
        }

        public List<T> populate(int number) {
            population = new ArrayList<>(number);
            try {
                for (int i = 0; i < number; i++) {
                    population.add((T) specimen.clone());
                }
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            for (BiConsumer<T, Integer> handler : handlers) {
                for (int i = 0; i < number; i++) {
                    handler.accept(population.get(i), i);
                }
            }
            return population;
        }
    }

    public static class UniformPopulator<T extends RenderedComponent> implements BiConsumer<T, Integer> {
        private final int amount;

        public UniformPopulator(int amount) {
            this.amount = amount;
        }

        @Override
        public void accept(T component, Integer iteration) {
            int side = (int) (Math.sqrt(amount));
            double step = (100d / Math.sqrt(amount));
            System.out.println("side = " + side);
            component.setPosition(new Vector(step * (iteration / side), step * (iteration % side)));
        }
    }

    public static class FixedPositionPopulator<T extends RenderedComponent> implements BiConsumer<T, Integer> {
        private final double x;
        private final double y;

        public FixedPositionPopulator(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void accept(T component, Integer iteration) {
            component.setPosition(new Vector(x, y));
        }
    }

    public static class SimpleNameGenerator<T extends RenderedComponent> implements BiConsumer<T, Integer> {
        @Override
        public void accept(T component, Integer iteration) {
            String basicName;
            String prototypeName = component.getName();
            if (prototypeName == null || prototypeName.isEmpty()) {
                basicName = component.getClass().getSimpleName();
            } else {
                basicName = prototypeName;
            }
            component.setName(basicName + iteration);
        }
    }

    public static class BindingWithScheduler<T extends RenderedComponent> implements BiConsumer<T, Integer> {
        private ComponentsScheduler<? super T>[] schedulers;

        public BindingWithScheduler(ComponentsScheduler<? super T>... schedulers) {
            this.schedulers = schedulers;
        }

        @Override
        public void accept(T component, Integer iteration) {
            for (ComponentsScheduler<? super T> scheduler : schedulers) {
                scheduler.add(component);
            }
        }
    }
}
