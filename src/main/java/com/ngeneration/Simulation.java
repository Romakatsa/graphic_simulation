package com.ngeneration;

import com.ngeneration.ai.BrownianDriver;
import com.ngeneration.custom_rendered_components.Car;
import com.ngeneration.custom_rendered_components.Road;
import com.ngeneration.graphic.engine.ComponentsScheduler;
import com.ngeneration.graphic.engine.Corner;
import com.ngeneration.graphic.engine.ThreeVector;
import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.commands.Command;
import com.ngeneration.graphic.engine.commands.QuitCommand;
import com.ngeneration.graphic.engine.drawablecomponents.PhysicalRenderedComponent;
import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;
import com.ngeneration.graphic.engine.lwjgl_engine.GraphicEngine;
import com.ngeneration.graphic.engine.lwjgl_engine.LwjglGraphicEngine;
import com.ngeneration.graphic.engine.physics.PhysicalComponentStateUpdater;
import com.ngeneration.graphic.engine.schedulers.Loop;
import com.ngeneration.graphic.engine.view.DrawArea;
import com.ngeneration.graphic.engine.view.DrawContext;
import com.ngeneration.graphic.engine.view.Window;

import java.util.HashSet;
import java.util.Set;

public class Simulation {

    private final Set<Window> windows = new HashSet<>();
    private final Set<RenderedComponent> renderedComponents = new HashSet<>();
    private final Command QUIT = new QuitCommand(this);

    public void start() {
        System.out.println("Loading simulation. . .");

        System.out.println("Initialise graphic");
        GraphicEngine<Long> engine = new LwjglGraphicEngine();
        engine.init();
        Window<Long> window = Window.<Long>create("Main window", 800, 800, engine);
        DrawArea backgroundArea = window.allocateArea(Corner.TOP_LEFT, 1, 1);
        DrawContext secondaryRole = new DrawContext("player");
        DrawContext player = new DrawContext("player");

        System.out.println("Loading objects");
        Car car = ComponentsFactory.aCar()
                .withPosition(new Vector(50, 50))
                .withSize(new Vector(3, 7))
                .build();
        Road road = ComponentsFactory.aDirectRoad().build();

        System.out.println("Loading schedulers");
        ComponentsScheduler<PhysicalRenderedComponent> physics
                = new ComponentsScheduler<>(new PhysicalComponentStateUpdater());
        ComponentsScheduler<Car> driver = new ComponentsScheduler<>(
                new BrownianDriver(3, 0.05));
        ComponentsScheduler<RenderedComponent> loop = new ComponentsScheduler<>(
                new Loop());

        addSchedulers(car, driver, loop, physics);

        System.out.println("Create draw contexts");
        player.put(10, car);
        secondaryRole.put(5, road);

        System.out.println("Compose simulation");
        backgroundArea.addContext(secondaryRole);
        backgroundArea.addContext(player);
    }

    private <T extends RenderedComponent> void addSchedulers(T component, ComponentsScheduler<? super T>... schedulers) {
        for (int i = 0; i < schedulers.length; i++) {
            ComponentsScheduler<? super T> scheduler = schedulers[i];
            scheduler.add(component);
        }
    }

    private void addCars(ComponentsScheduler<PhysicalRenderedComponent> physics, ComponentsScheduler<Car> driver,
                         ComponentsScheduler<RenderedComponent> loop, DrawContext player, int number) {
        for (int i = 0; i < number; i++) {
            Car car0 = ComponentsFactory.aCar()
                    .withPosition(new Vector(50, 50))
                    .withSize(new Vector(1, 3))
                    .build();
            driver.add(car0);
            loop.add(car0);
            physics.add(car0);
            player.put(10, car0);
        }
    }


    private static ThreeVector toThreeVector(Vector vector) {
        if (vector != null) {
            return new ThreeVector(vector.getX(), vector.getY(), 0);
        }
        return new ThreeVector(0, 0, 0);
    }

    private void initGraphicEngine() {

    }


    public void finish() {
        for (Window window : windows) {
            window.close();
        }
        windows.clear();
    }


}
