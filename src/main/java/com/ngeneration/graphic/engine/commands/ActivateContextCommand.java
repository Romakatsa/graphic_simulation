package com.ngeneration.graphic.engine.commands;

import com.ngeneration.graphic.engine.view.DrawContext;

public class ActivateContextCommand implements Runnable, Command {

    protected DrawContext context;

    public ActivateContextCommand(DrawContext context) {
        this.context = context;
    }

    public void run() {
        context.setActivated(true);
    }
}
