package com.ngeneration.graphic.engine.commands;

import com.ngeneration.TavrovSimulation;

public class QuitCommand implements Runnable, Command {

    protected TavrovSimulation simulation;

    public QuitCommand(TavrovSimulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public void run() {
        simulation.finish();
    }
}
