package com.fuzzygroup.view;

import com.fuzzygroup.exceptions.DisplayException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public enum Context {
    SINGLETON; // todo multi?
    public static final long STARTUP_TIMESTAMP = Instant.now().toEpochMilli();
    private Map<String, Display> contexts = new HashMap<>();


    // todo synchronize
    public synchronized Display getByName(String qualifier) throws DisplayException {
        if (qualifier == null || qualifier.equals("")) {
            throw new IllegalArgumentException("Display qualifier cannot be empty");
        }
        Display display = contexts.get(qualifier);
        if (display == null) {
            throw new DisplayException("Display don't find");
        }
        return display;
    }

    // todo synchronize
    public synchronized void register(Display display) {
        if (display == null || display.getQualifier().equals("")) {
            throw new IllegalArgumentException("Display qualifier shouldn't be empty");
        }
        boolean alreadyContain = contexts.containsKey(display.getQualifier());
        if (alreadyContain) {
            throw new IllegalArgumentException("Display with such qualifier already exists");
        }
        contexts.put(display.getQualifier(), display);
    }

    public static int uptimeMillis() {
        // todo
        return (int) (Instant.now().toEpochMilli() - STARTUP_TIMESTAMP);
    }
}
