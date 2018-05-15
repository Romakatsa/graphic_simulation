package com.ngeneration;

import com.ngeneration.ai.BrownianDriver;
import com.ngeneration.ai.IntelligentDriver;
import com.ngeneration.custom_rendered_components.Car;
import com.ngeneration.custom_rendered_components.Road;
import com.ngeneration.graphic.engine.ComponentsScheduler;
import com.ngeneration.graphic.engine.PlaceOnScreen;
import com.ngeneration.graphic.engine.ThreeVector;
import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.commands.BrownianDriverCommand;
import com.ngeneration.graphic.engine.commands.Command;
import com.ngeneration.graphic.engine.commands.QuitCommand;
import com.ngeneration.graphic.engine.drawablecomponents.PhysicalRenderedComponent;
import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;
import com.ngeneration.graphic.engine.input.ActionType;
import com.ngeneration.graphic.engine.input.KeyboardEvent;
import com.ngeneration.graphic.engine.lwjgl_engine.GraphicEngine;
import com.ngeneration.graphic.engine.lwjgl_engine.LwjglGraphicEngine;
import com.ngeneration.graphic.engine.physics.PhysicalComponentStateUpdater;
import com.ngeneration.graphic.engine.schedulers.Loop;
import com.ngeneration.graphic.engine.view.DrawArea;
import com.ngeneration.graphic.engine.view.DrawContext;
import com.ngeneration.graphic.engine.view.Window;
import com.ngeneration.ui.ControlPanel;
import com.ngeneration.ui.ControlPanelController;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.ngeneration.ComponentsFactory.*;
import static org.lwjgl.glfw.GLFW.*;

public class TavrovSimulation implements Simulation{

    private final Set<Window<Long>> windows = new HashSet<>();
    private final Set<RenderedComponent> renderedComponents = new HashSet<>();
    private final Command QUIT = new QuitCommand(this);

    private ComponentsScheduler<RenderedComponent> loop;
    private ComponentsScheduler<PhysicalRenderedComponent> physics;
    private ComponentsScheduler<Car> driver;

    private DrawContext aiViewContext;
    private DrawContext player;
    private DrawContext secondaryRole;
    private ControlPanelController controlPanelController;
    private DrawArea backgroundArea;
    private Window<Long> mainWindow;
    private DrawArea mapArea;

    public void init() {
        System.out.println("Init TavrovSimulation. . .");
        System.out.println("Loading control panel");
        controlPanelController = ControlPanel.create(this);
    }


    public void start() {
        System.out.println("Prepare TavrovSimulation. . .");
        initGraphic();
//        playWithReflection(window);
//        playWithAreaRotation(backgroundArea);
        initSchedulers();

        Car bigCar = aCar()
                .withSize(new Vector(2, 5))
                .build();
        Car miniCar = aCar()
                .withSize(new Vector(3, 7))
                .build();
        Road road = aRoad()
                .withWidth(30)
                .firstPoint(-50, 0)
                .withWidth(10)
                .nextPoint(-40, 0)
                .withWidth(5)
                .nextPoint(0, 40)
                .withWidth(45)
                .nextPoint(50, 0)
                .build();
        Set<Car> miniCarPopulation = populate(miniCar, 1,
                new FixedPositionPopulator<>(-50, 0),
                new SimpleNameGenerator<>(),
                new BindingWithScheduler<>(loop, physics),
                (curCar, iteration) -> {
                    ComponentsScheduler<DrawArea> scheduler = new ComponentsScheduler<>((area, value)
                            -> {
                        area.setRotationRadian(-curCar.getRotation() + Math.PI / 2);
//                        area.setShift(curCar.getPosition()//.coordinatewiseMultiplication(area.getZoomFactor())
//                                .multiple(-1));
                    }, 20);
                    scheduler.add(mapArea);
                },
                (curCar, iteration) -> {
                    IntelligentDriver intelligentDriver = new IntelligentDriver(road, aiViewContext);
                    addSchedulers(curCar,
                            new ComponentsScheduler<>(createDriver(0.0, 0.00)),
                            new ComponentsScheduler<>(intelligentDriver.new IntelligentDriverViewAnalyzer(), 20),
                            new ComponentsScheduler<>(intelligentDriver.new IntelligentDriverController(), 50)
                    );
                }
        );
        Set<Car> bigCarPopulation = populate(bigCar, 10,
                new UniformPopulator<>(),
                new SimpleNameGenerator<>(),
                new BindingWithScheduler<>(loop, physics),
                (curCar, iteration) -> addSchedulers(curCar, driver)
        );
        player.put(10, bigCarPopulation);
        player.put(10, miniCarPopulation);
        secondaryRole.put(5, road);

        addInputCommands();
        // TavrovSimulation
        System.out.println("Start simulation plot");
        simulationPlot();
    }

    private void initGraphic() {
        GraphicEngine<Long> engine = LwjglGraphicEngine.getInstance();
        mainWindow = Window.<Long>create("TavrovSimulation", 800, 800, engine);
        windows.add(mainWindow);
        backgroundArea = mainWindow.allocateFullScreenArea();
        mapArea = mainWindow.allocateArea(PlaceOnScreen.BOTTOM_LEFT_CORNER, 0.3, 0.3);
        secondaryRole = new DrawContext("secondary");
        player = new DrawContext("player");
        aiViewContext = new DrawContext("ai");


        System.out.println("Compose simulation");
        backgroundArea.addContext(secondaryRole);
        backgroundArea.addContext(player);
        backgroundArea.addContext(aiViewContext);
        mapArea.addContext(secondaryRole);
        mapArea.addContext(player);

        mapArea.setSize(Vector.diag(40));
        mapArea.setZoom(Vector.diag(0.5));
    }

    private void playWithAreaRotation(DrawArea backgroundArea) {
        backgroundArea.setZoomFactor(0.6);
        ComponentsScheduler<RenderedComponent> areaRotation = new ComponentsScheduler<>((component, aDouble)
                -> backgroundArea.setRotationRadian(backgroundArea.getRotationRadian() + 0.001), 10);
        areaRotation.add(backgroundArea);
//        backgroundArea.setRotationRadian(3.14 / 4);
//        backgroundArea.setShift(Vector.diag(0));
    }

    private void playWithReflection(Window<Long> window) {
        DrawArea backgroundAreaReflection = window.allocateFullScreenArea();
        DrawArea backgroundAreaReflection2 = window.allocateFullScreenArea();
        DrawArea backgroundAreaReflection3 = window.allocateFullScreenArea();
        backgroundAreaReflection.setZoom(new Vector(-1, -1));
        backgroundAreaReflection2.setZoom(new Vector(1, -1));
        backgroundAreaReflection3.setZoom(new Vector(-1, 1));
        backgroundAreaReflection.addContext(secondaryRole);
        backgroundAreaReflection.addContext(player);
        backgroundAreaReflection2.addContext(secondaryRole);
        backgroundAreaReflection2.addContext(player);
        backgroundAreaReflection3.addContext(secondaryRole);
        backgroundAreaReflection3.addContext(player);
        backgroundAreaReflection.addContext(player);
        backgroundAreaReflection2.addContext(player);
        backgroundAreaReflection3.addContext(player);
    }

    private void initSchedulers() {
        loop = new ComponentsScheduler<>(new Loop());
        physics = new ComponentsScheduler<>(new PhysicalComponentStateUpdater());
        driver = new ComponentsScheduler<>(createDriver(11.2, 1.05));
    }

    private void addInputCommands() {
        double updatesPerSecond = 100;
        mainWindow.addKeyboardEvent(new KeyboardEvent(()
                -> backgroundArea.setShift(
                new Vector(backgroundArea.getShift().getX() + 0.1 / updatesPerSecond,
                        backgroundArea.getShift().getY())),
                ActionType.CLICKED, GLFW_KEY_LEFT));
        mainWindow.addKeyboardEvent(new KeyboardEvent(()
                -> backgroundArea.setShift(
                new Vector(backgroundArea.getShift().getX() - 0.1 / updatesPerSecond,
                        backgroundArea.getShift().getY())),
                ActionType.CLICKED, GLFW_KEY_RIGHT));
        mainWindow.addKeyboardEvent(new KeyboardEvent(()
                -> backgroundArea.setShift(
                new Vector(backgroundArea.getShift().getX(),
                        backgroundArea.getShift().getY() - 0.1 / updatesPerSecond)),
                ActionType.CLICKED, GLFW_KEY_UP));
        mainWindow.addKeyboardEvent(new KeyboardEvent(()
                -> backgroundArea.setShift(
                new Vector(backgroundArea.getShift().getX(),
                        backgroundArea.getShift().getY() + 0.1 / updatesPerSecond)),
                ActionType.CLICKED, GLFW_KEY_DOWN));
        mainWindow.addKeyboardEvent(new KeyboardEvent(()
                -> {
            //TODO show report
        },
                ActionType.CLICKED, GLFW_KEY_I));
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


    private void simulationPlot() {

    }

    public void finish() {
        for (Window<? extends Long> window : windows) {
            window.close();
        }
        windows.clear();
    }

    public void restart() {
        finish();
        start();
    }


    private static ThreeVector toThreeVector(Vector vector) {
        if (vector != null) {
            return new ThreeVector(vector.getX(), vector.getY(), 0);
        }
        return new ThreeVector(0, 0, 0);
    }

}