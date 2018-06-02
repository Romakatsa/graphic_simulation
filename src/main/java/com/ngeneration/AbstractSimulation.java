package com.ngeneration;

import com.ngeneration.graphic.engine.LazyLogger;
import com.ngeneration.javafx_gui.SimulationPanel;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractSimulation implements Simulation {
    private static final LazyLogger logger = LazyLogger.getLogger(AbstractSimulation.class);
    private final String simulationName;
    private final Map<String, SimulationPanel> panels = new HashMap<>();
    private final Set<SimulationException> errors = new HashSet<>();
    private Instant startTime;

    public AbstractSimulation(String simulationName) {
        this.simulationName = simulationName;
    }

    private void init() {
        logger.info(String.format("Init simulation '%s'", simulationName));
    }

    @Override
    public final void start() {
        init();
        startTime = Instant.now();
        launch();
    }

    public abstract void launch();

    @Override
    public final void finish() {
        logger.info(String.format("Finish simulation '%s', uptime: %s",
                simulationName, Duration.between(startTime, Instant.now())));
        doFinish();
    }

    protected abstract void doFinish();

    @Override
    public void restart() {
        finish();
        start();
    }
}
