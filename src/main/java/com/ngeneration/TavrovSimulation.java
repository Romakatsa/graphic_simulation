package com.ngeneration;

import com.ngeneration.ai.BrownianDriver;
import com.ngeneration.ai.IntelligentDriver;
import com.ngeneration.custom_rendered_components.Car;
import com.ngeneration.custom_rendered_components.Road;
import com.ngeneration.graphic.engine.*;
import com.ngeneration.graphic.engine.commands.BrownianDriverCommand;
import com.ngeneration.graphic.engine.commands.Command;
import com.ngeneration.graphic.engine.commands.QuitCommand;
import com.ngeneration.graphic.engine.drawablecomponents.PhysicalRenderedComponent;
import com.ngeneration.graphic.engine.drawablecomponents.RenderedComponent;
import com.ngeneration.graphic.engine.enums.Color;
import com.ngeneration.graphic.engine.input.ActionType;
import com.ngeneration.graphic.engine.input.KeyboardEvent;
import com.ngeneration.graphic.engine.lwjgl_engine.GraphicEngine;
import com.ngeneration.graphic.engine.lwjgl_engine.LwjglGraphicEngine;
import com.ngeneration.graphic.engine.physics.PhysicalComponentStateUpdater;
import com.ngeneration.graphic.engine.schedulers.Loop;
import com.ngeneration.graphic.engine.view.DrawArea;
import com.ngeneration.graphic.engine.view.DrawContext;
import com.ngeneration.graphic.engine.view.Window;
import com.ngeneration.javafx_gui.CarControlPanelController;
import com.ngeneration.javafx_gui.PanelTemplate;
import com.ngeneration.javafx_gui.SimulationPanel;

import java.util.HashSet;
import java.util.Set;

import static com.ngeneration.ComponentsFactory.*;
import static org.lwjgl.glfw.GLFW.*;

public class TavrovSimulation extends AbstractSimulation {

    private final Set<Window<Long>> windows = new HashSet<>();
    private final Set<RenderedComponent> renderedComponents = new HashSet<>();
    private final Command QUIT = new QuitCommand(this);

    private ComponentsScheduler<RenderedComponent> loop;
    private ComponentsScheduler<PhysicalRenderedComponent> physics;
    private ComponentsScheduler<Car> driver;

    private DrawContext aiViewContext;
    private DrawContext player;
    private DrawContext secondaryRole;
    private CarControlPanelController carControlPanelController;
    private DrawArea backgroundArea;
    private Window<Long> mainWindow;
    private DrawArea mapArea;

    public TavrovSimulation() {
        super("Tavrov simulation");
    }

    public void launch() {
        System.out.println("Prepare Tavrov simulation. . .");
        initGraphic();
//        playWithReflection(window);
//        playWithAreaRotation(backgroundArea);
        initPanels();
        initSchedulers();

        Car smallCar = aCar()
                .withSize(new Vector(2, 5))
                .withRotation(Math.PI / 4)
                .build();
        Car bigCar = aCar()
                .withSize(new Vector(3, 7))
                .withName("Super car")
                .withSpeed(Vector.diag(4))
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
        Set<Car> bigCarPopulation = populate(bigCar, 1,
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
                    IntelligentDriver intelligentDriver = new IntelligentDriver(road, aiViewContext, true);
                    addSchedulers(curCar,
//                            new ComponentsScheduler<>(createDriver(0.0, 0.00)),
                            new ComponentsScheduler<>(intelligentDriver.new IntelligentDriverViewAnalyzer(), 20),
                            new ComponentsScheduler<>(intelligentDriver.new IntelligentDriverController(), 50)
                    );
                }
        );
        Set<Car> smallCarPopulation = populate(smallCar, 1000,
                new UniformPopulator<>(1000),
                new SimpleNameGenerator<>(),
                new BindingWithScheduler<>(loop, physics),
                (curCar, iteration) -> addSchedulers(curCar, driver),
                (car, i) -> {
                    car.setColors(new Color(Math.random(), Math.random(), Math.random()));
                }
        );
        player.put(11, bigCarPopulation);
        player.put(10, smallCarPopulation);

//        RenderedComponent c1 = new RenderedComponent(new Vector(-22, 0), Vector.diag(10), Math.PI / 1.05,
//                Color.DARK_RED, 0, Shape.RECT_2);
//        c1.setName("QUA");
//        player.put(1, c1);
//        ComponentsScheduler<RenderedComponent> scheduler = new ComponentsScheduler<>((c, d) -> {
//            c.setRotation(c.getRotation() + 0.005);
//        });
//        scheduler.add(c1);

//        player.put(1, new RenderedComponent(new Vector(-24.5, 15), Vector.diag(10), Math.PI/6,
//                Color.DARK_BLUE, 0, Shape.RECT));
//        secondaryRole.put(5, road);

        addInputCommands();
        // TavrovSimulation
        System.out.println("Start simulation plot");
        simulationPlot();
    }

    private void initGraphic() {
        GraphicEngine<Long> engine = LwjglGraphicEngine.getInstance();
        mainWindow = Window.<Long>create("TavrovSimulation", 800, 800, engine);
        windows.add(mainWindow);
        backgroundArea = mainWindow.allocateArea(PlaceOnScreen.CENTER, 0.5, 0.5);
        mapArea = mainWindow.allocateArea(PlaceOnScreen.BOTTOM_LEFT_CORNER, 0.1, 0.1);
        secondaryRole = new DrawContext("secondary");
        player = new DrawContext("player");
        aiViewContext = new DrawContext("ai");


        System.out.println("Compose simulation");
        backgroundArea.addContext(secondaryRole);
        backgroundArea.addContext(player);
        backgroundArea.addContext(aiViewContext);
        mapArea.addContext(secondaryRole);
        mapArea.addContext(player);

        mapArea.setSize(Vector.diag(50));
        mapArea.setZoom(Vector.diag(2));
        mapArea.setVisible(false);
    }

    private void initPanels() {
//        carControlPanelController = SimulationPanel.createPanelAndGetController(
//                this, PanelTemplate.CAR_CONTROLLER.getTemplateFile(),
//                "Car controller", "Car controller");
        carControlPanelController = SimulationPanel.createPanelAndGetController(
                this, PanelTemplate.CAR_CONTROLLER);
    }

    private void initSchedulers() {
        loop = new ComponentsScheduler<>(new Loop(50));
        physics = new ComponentsScheduler<>(new PhysicalComponentStateUpdater());
        driver = new ComponentsScheduler<>(createDriver(10.2, 0.1));
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
        carControlPanelController.addDriverChangeSpeedCommand(driver,
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

    public void doFinish() {
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
