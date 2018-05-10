package com.ngeneration;

import com.ngeneration.ai.BrownianDriver;
import com.ngeneration.custom_rendered_components.Car;
import com.ngeneration.custom_rendered_components.Road;
import com.ngeneration.graphic.engine.ComponentsScheduler;
import com.ngeneration.graphic.engine.PlaceOnScreen;
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

import static com.ngeneration.ComponentsFactory.*;
import static com.ngeneration.ComponentsFactory.populate;

public class Simulation {

    private final Set<Window> windows = new HashSet<>();
    private final Set<RenderedComponent> renderedComponents = new HashSet<>();
    private final Command QUIT = new QuitCommand(this);

    private ComponentsScheduler<RenderedComponent> loop;
    private ComponentsScheduler<PhysicalRenderedComponent> physics;
    private ComponentsScheduler<Car> driver;

    private DrawContext player;
    private DrawContext secondaryRole;

    public void start() {
        System.out.println("Loading simulation. . .");

        System.out.println("Initialise graphic");
        GraphicEngine<Long> engine = new LwjglGraphicEngine();
        Window<Long> window = Window.<Long>create("Main window", 800, 800, engine);
        DrawArea backgroundArea = window.allocateFullScreenArea();
//        DrawArea backgroundAreaReflection = window.allocateFullScreenArea();
//        backgroundAreaReflection.setZoomFactor(-1);
//        backgroundAreaReflection.setShift(new Vector(10, 10));
        secondaryRole = new DrawContext("secondary");
        player = new DrawContext("player");

        System.out.println("Loading schedulers");
        loop = new ComponentsScheduler<>(new Loop());
        physics = new ComponentsScheduler<>(new PhysicalComponentStateUpdater());
        driver = new ComponentsScheduler<>(new BrownianDriver(1, 0.15));

        System.out.println("Loading objects");
        Car prototype = aCar()
                .withPosition(new Vector(50, 50))
                .withSize(new Vector(3, 7))
                .build();
        Road road = aDirectRoad().build();

        populate(prototype, 4,
                (curCar, iteration) -> curCar.setPosition(new Vector(10 * (iteration / 10), 10 * (iteration % 10))),
                (curCar, iteration) -> addSchedulers(curCar, driver, loop, physics),
                (curCar, iteration) -> player.put(10, curCar)
        );

        secondaryRole.put(5, road);

        System.out.println("Compose simulation");
        backgroundArea.addContext(secondaryRole);
        backgroundArea.addContext(player);
//        backgroundAreaReflection.addContext(player);
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
            Car car0 = aCar()
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
