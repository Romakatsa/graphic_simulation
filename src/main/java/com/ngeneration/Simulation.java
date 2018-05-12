package com.ngeneration;

import com.ngeneration.ai.BrownianDriver;
import com.ngeneration.ai.IntelligentDriver;
import com.ngeneration.custom_rendered_components.Car;
import com.ngeneration.custom_rendered_components.Road;
import com.ngeneration.graphic.engine.ComponentsScheduler;
import com.ngeneration.graphic.engine.ThreeVector;
import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.commands.BrownianDriverCommand;
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
import com.ngeneration.ui.ControlPanel;
import com.ngeneration.ui.ControlPanelController;

import java.util.HashSet;
import java.util.Set;

import static com.ngeneration.ComponentsFactory.*;

public class Simulation {

    private final Set<Window> windows = new HashSet<>();
    private final Set<RenderedComponent> renderedComponents = new HashSet<>();
    private final Command QUIT = new QuitCommand(this);

    private ComponentsScheduler<RenderedComponent> loop;
    private ComponentsScheduler<PhysicalRenderedComponent> physics;
    private ComponentsScheduler<Car> driver;

    private DrawContext player;
    private DrawContext secondaryRole;
    private ControlPanelController controlPanelController;

    public void init() {
        System.out.println("Init Simulation. . .");
        System.out.println("Loading control panel");
        controlPanelController = ControlPanel.create(this);
    }

    public void start() {
        System.out.println("Prepare Simulation. . .");

        System.out.println("Initialise graphic");
        GraphicEngine<Long> engine = new LwjglGraphicEngine();
        Window<Long> window = Window.<Long>create("Simulation", 800, 800, engine);
        windows.add(window);
        DrawArea backgroundArea = window.allocateFullScreenArea();
//        DrawArea backgroundAreaReflection = window.allocateFullScreenArea();
//        DrawArea backgroundAreaReflection2 = window.allocateFullScreenArea();
//        DrawArea backgroundAreaReflection3 = window.allocateFullScreenArea();
//        backgroundAreaReflection.setRotationRadian(3.14/4);
//        backgroundArea.setZoomFactor(0.6);
//        backgroundArea.setRotationRadian(3.14 / 4);
//        backgroundArea.setShift(Vector.diag(0));
//        backgroundAreaReflection.setShift(Vector.diag(0));
//        backgroundAreaReflection2.setZoom(new Vector(1, -1));
//        backgroundAreaReflection3.setZoom(new Vector(-1, 1));
//        backgroundAreaReflection.setShift(new Vector(10, 10));
        secondaryRole = new DrawContext("secondary");
        player = new DrawContext("player");

        System.out.println("Loading schedulers");
        loop = new ComponentsScheduler<>(new Loop());
        physics = new ComponentsScheduler<>(new PhysicalComponentStateUpdater());
//        driver = new ComponentsScheduler<>(createDriver(11.2, 1.05));

        System.out.println("Loading objects");
        Car bigCar = aCar()
                .withSize(new Vector(7, 30))
                .build();
        Car miniCar = aCar()
                .withSize(new Vector(3, 7))
                .build();
        Road road = aDirectRoad().build();

//        populate(miniCar, 0,
//                new UniformPopulator(),
//                (curCar, iteration) -> curCar.setName(curCar.getName() + iteration),
//                (curCar, iteration) -> addSchedulers(curCar, driver, loop, physics),
//                (curCar, iteration) -> player.put(10, curCar)
//        );
        populate(miniCar, 1,
                new UniformPopulator(),
//                (curCar, iteration) -> curCar.setPosition(new Vector(10 * (iteration / 10), 10 * (iteration % 10))),
                (curCar, iteration) -> curCar.setName(curCar.getName() + iteration),
                (curCar, iteration) -> {
                    IntelligentDriver intelligentDriver = new IntelligentDriver(road);
                    addSchedulers(curCar, loop, physics,
                            new ComponentsScheduler<>(createDriver(0.0, 0.00)),
                            new ComponentsScheduler<>(intelligentDriver.new IntelligentDriverViewAnalyzer(), 2000),
                            new ComponentsScheduler<>(intelligentDriver.new IntelligentDriverController(), 2000)
                            );
                },
                (curCar, iteration) -> player.put(10, curCar)
        );

        secondaryRole.put(5, road);

        System.out.println("Compose simulation");
        backgroundArea.addContext(secondaryRole);
        backgroundArea.addContext(player);
//        backgroundAreaReflection.addContext(player);
//        backgroundAreaReflection2.addContext(player);
//        backgroundAreaReflection3.addContext(player);

        // Control panel
        // Simulation
        System.out.println("Start simulation");


    }

    private BrownianDriver createDriver(double speed, double twistExtent) {
        BrownianDriver driver = new BrownianDriver(speed, twistExtent);
        controlPanelController.addDriverChangeSpeedCommand(driver,
                new BrownianDriverCommand.ChangeSpeed(driver),
                new BrownianDriverCommand.ChangeTwist(driver));
        return driver;
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

    public void reset() {
        finish();
        start();
    }


}
