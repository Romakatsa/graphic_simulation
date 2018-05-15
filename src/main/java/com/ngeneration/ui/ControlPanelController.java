package com.ngeneration.ui;

import com.ngeneration.Simulation;
import com.ngeneration.TavrovSimulation;
import com.ngeneration.ai.BrownianDriver;
import com.ngeneration.graphic.engine.commands.BrownianDriverCommand;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class ControlPanelController extends Application {
    @FXML
    public Slider slider1;
    @FXML
    public Slider slider2;
    @FXML
    public Slider slider3;
    @FXML
    public Slider slider4;
    @FXML
    public Slider slider5;
    @FXML
    public CheckBox checkBox;
    @FXML
    public ChoiceBox<BrownianDriver> selectedDriver;

    private final ObservableList<BrownianDriver> drivers = FXCollections.observableArrayList();
    private final ObservableList<BrownianDriverCommand.ChangeSpeed> driverChangeSpeedCommand = FXCollections.observableArrayList();
    private final ObservableList<BrownianDriverCommand.ChangeTwist> driverTwistExtentCommand = FXCollections.observableArrayList();
    private Simulation simulation;

    public synchronized void addDriverChangeSpeedCommand(BrownianDriver driver,
                                                         BrownianDriverCommand.ChangeSpeed changeSpeed,
                                                         BrownianDriverCommand.ChangeTwist changeTwist) {
        this.drivers.add(driver);
        this.driverChangeSpeedCommand.add(changeSpeed);
        this.driverTwistExtentCommand.add(changeTwist);
        if (selectedDriver.getSelectionModel().isEmpty()) {
            selectedDriver.setItems(drivers);
            selectedDriver.valueProperty().addListener((o, oldValue, newValue) -> {
                if (newValue != null) {
                    slider1.setValue(changeSpeed.CONVERTER.reverse(newValue.getSpeedModule()));
                    slider2.setValue(changeTwist.CONVERTER.reverse(newValue.getTwistExtent()));
                }
            });
            Platform.runLater(
                    () -> selectedDriver.setValue(driver)
            );
//            selectedDriver.getSelectionModel().select(0);
        }
    }

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setAlwaysOnTop(true);

        selectedDriver.setConverter(new StringConverter<BrownianDriver>() {
            @Override
            public String toString(BrownianDriver object) {
                return "driver: " + object.getSpeedModule() + ", " + object.getTwistExtent();
            }

            @Override
            public BrownianDriver fromString(String string) {
                return null;
            }
        });
        selectedDriver.setItems(drivers);
    }

    @FXML
    public void dragSlider1(MouseEvent mouseEvent) {
        changeSpeed();
    }

    @FXML
    public void doneDragSlider1(MouseEvent dragEvent) {
        double sliderValue = slider1.getValue();
        changeSpeed();
    }

    private void changeSpeed() {
        double sliderValue = slider1.getValue();
        int driverNumber = selectedDriver.getItems().indexOf(selectedDriver.getValue());
        driverChangeSpeedCommand.get(driverNumber).accept(sliderValue);
    }

    @FXML
    public void dragSlider2(MouseEvent mouseEvent) {
        changeTwistExtent();
    }

    public void doneDragSlider2(MouseEvent mouseEvent) {
        changeTwistExtent();
    }

    private void changeTwistExtent() {
        double sliderValue = slider2.getValue();
        int driverNumber = selectedDriver.getItems().indexOf(selectedDriver.getValue());
        driverTwistExtentCommand.get(driverNumber).accept(sliderValue);
    }

    @FXML
    public void dragSlider3(MouseEvent mouseEvent) {
        double sliderValue = slider3.getValue();
    }

    @FXML
    public void doneDragSlider3(DragEvent dragEvent) {
        double sliderValue = slider3.getValue();
    }

    @FXML
    public void dragSlider4(MouseEvent mouseEvent) {
        double sliderValue = slider4.getValue();
    }

    @FXML
    public void doneDragSlider4(DragEvent dragEvent) {
        double sliderValue = slider4.getValue();
    }

    @FXML
    public void dragSlider5(MouseEvent mouseEvent) {
        double sliderValue = slider5.getValue();
    }

    @FXML
    public void doneDragSlider5(DragEvent dragEvent) {
        double sliderValue = slider5.getValue();
    }

    public void onActionCheckBox1(ActionEvent actionEvent) {

    }

    public void resetSimulation() {
        simulation.restart();
        driverTwistExtentCommand.clear();
        driverChangeSpeedCommand.clear();
//        for (BrownianDriver driver : drivers) {
//            drivers.remove(driver);
//        }
        drivers.clear();
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }
}
