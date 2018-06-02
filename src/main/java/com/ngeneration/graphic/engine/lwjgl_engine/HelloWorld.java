package com.ngeneration.graphic.engine.lwjgl_engine;

import com.ngeneration.ComponentsFactory;
import com.ngeneration.ai.BrownianDriver;
import com.ngeneration.custom_rendered_components.Car;
import com.ngeneration.graphic.engine.PlaceOnScreen;
import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.view.DrawArea;
import com.ngeneration.graphic.engine.view.DrawContext;
import com.ngeneration.graphic.engine.view.Window;
import org.lwjgl.Version;

import java.util.concurrent.TimeUnit;

public class HelloWorld {

//    private GraphicEngine<Long> engine = new LwjglGraphicEngine();
//
//    public void start() {
//        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
//
//        engine.init();
//        Window<Long> window = Window.<Long>createPanelAndGetController("Main window", 900, 500, engine);
////        Window window2 = new Window("Main window", 200, 200, engine);
////        window2.createPanelAndGetController();
//
//        DrawArea backgroundArea = window.allocateArea(Corner.TOP_LEFT_CORNER, 1, 1);
//        DrawContext mainContext = new DrawContext("main", true, 10);
//        backgroundArea.addContext(mainContext);
//
//
//        Car car = ComponentsFactory.aCar()
//                .withSize(Vector.diag(50))
//                .withPosition(Vector.diag(5))
//                .withSpeed(new Vector(5, 0))
//                .withDriver(new BrownianDriver())
//                .build();
//        mainContext.put(5, car);
//
////        window.createPanelAndGetController();
//
//
//        try {
//            TimeUnit.SECONDS.sleep(3);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
////        engine.closeWindow(window);
//
//    }
//
//    public static void main(String[] args) {
//        new HelloWorld().start();
//    }


}
