package com.ngeneration.graphic.engine.commands;

public abstract class Command implements Runnable {//todo probably should extend Consumer and accept args array

    @Override
    public abstract void run();
}
