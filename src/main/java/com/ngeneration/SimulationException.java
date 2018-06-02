package com.ngeneration;

import java.time.Instant;

public class SimulationException extends Exception {
    private final Instant time;

    public SimulationException(Exception cause, Instant time) {
        super(cause);
        this.time = time;
    }


}
