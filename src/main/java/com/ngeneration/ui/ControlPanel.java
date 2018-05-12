package com.ngeneration.ui;

import com.ngeneration.Simulation;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ControlPanel extends Application {
    private static ControlPanelController controller;
    private static volatile boolean initComplete = false;
    private static final Object initCompleteMonitor = new Object();

    public static ControlPanelController create(Simulation simulation) {
        Thread javaFxThread = new Thread(Application::launch);
        javaFxThread.start();
        try {
            synchronized (initCompleteMonitor) {
                while ((!initComplete)) {
                    initCompleteMonitor.wait();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        controller.setSimulation(simulation);
        return controller;
    }

    @Override
    public void init() throws Exception {
        super.init();
//        selectedDriver.setSelectionModel(new SingleSelectionModel<BrownianDriverCommand>() {
//            private List<BrownianDriverCommand> cars = new ArrayList<>(driverChangeSpeedCommand);
//
//            @Override
//            protected BrownianDriverCommand getModelItem(int index) {
//                return cars.get(index);
//            }
//
//            @Override
//            protected int getItemCount() {
//                return cars.size();
//            }
//        });
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("controlPanel.fxml"));
//        Parent root = loader.load(getClass().getClassLoader().getResource("controlPanel.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        primaryStage.setTitle("FXML Welcome");
        primaryStage.setScene(scene);
        primaryStage.show();
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        URL resource = new ControlPanel().getClass().getClassLoader().getResource("controlPanel.fxml");
//        Pane p = fxmlLoader.load(resource.openStream());
        controller = loader.getController();
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
