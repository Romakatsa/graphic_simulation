package com.ngeneration.graphic.engine.commands;

import com.ngeneration.Simulation;

public class QuitCommand implements Runnable, Command {

    protected Simulation simulation;

    public QuitCommand(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public void run() {
        simulation.finish();
    }
}
