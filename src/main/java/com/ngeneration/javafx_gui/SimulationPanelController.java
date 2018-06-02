package com.ngeneration.javafx_gui;

import com.ngeneration.Simulation;
import javafx.application.Application;

public abstract class SimulationPanelController extends Application {
    protected Simulation simulation;

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }
}
