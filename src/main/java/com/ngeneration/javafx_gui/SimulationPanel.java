package com.ngeneration.javafx_gui;

import com.ngeneration.Simulation;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SimulationPanel extends Application {
    private static Map<String, ? extends SimulationPanelController> controllers = new HashMap<>();
    private static volatile boolean initComplete = false;
    private static final Object initCompleteMonitor = new Object();
    private static final BlockingQueue<PanelInformationHolder> queue = new LinkedBlockingQueue<>();


    public static <T extends SimulationPanelController>T createPanelAndGetController(
            Simulation simulation, PanelTemplate template) {
        return createPanelAndGetController(simulation, template.getTemplateFile(),
                template.getTitle(), template.getTitle());
    }

    public static <T extends SimulationPanelController>T createPanelAndGetController(Simulation simulation,
                                                                        String template,
                                                                        String panelName,
                                                                        String title) {
        PanelInformationHolder panel = new PanelInformationHolder(simulation, template, panelName, title);
        queue.add(panel);
        Thread javaFxThread = new Thread(Application::launch);
        javaFxThread.start();
        try {
            synchronized (SimulationPanel.initCompleteMonitor) {
                while ((!SimulationPanel.initComplete)) {
                    SimulationPanel.initCompleteMonitor.wait();
                }
            }
        } catch (InterruptedException e) {
            return null;
        }
        getController(panelName);
        T controller = (T) getController(panelName);
        controller.setSimulation(simulation);
        return controller;
    }

    private static SimulationPanelController getController(String panelName) {
        return controllers.get(panelName);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        PanelInformationHolder panelInfo = null;
        try {
            panelInfo = queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(panelInfo.getTemplate()));
//        Parent root = loader.load(getClass().getClassLoader().getResource("controlPanel.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        primaryStage.setTitle(panelInfo.getTitle());
        primaryStage.setScene(scene);
        primaryStage.show();
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        URL resource = new SimulationPanel().getClass().getClassLoader().getResource("controlPanel.fxml");
//        Pane p = fxmlLoader.load(resource.openStream());
        controllers.put(panelInfo.getName(), loader.getController());
//        primaryStage.setAlwaysOnTop(true);
//        primaryStage.setTitle("Hello World!");
//        Button btn = new Button();
//        btn.setText("Say 'Hello World'");
//        btn.setOnAction(event -> System.out.println("Hello World!"));
//
//        StackPane root = new StackPane();
//        root.getChildren().add(btn);
//        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
        initComplete = true;
        synchronized (initCompleteMonitor) {
            initCompleteMonitor.notifyAll();
        }
    }
}
