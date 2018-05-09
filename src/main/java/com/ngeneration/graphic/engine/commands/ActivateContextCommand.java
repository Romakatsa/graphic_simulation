package com.ngeneration.graphic.engine.commands;

import com.ngeneration.graphic.engine.view.DrawContext;

public class ActivateContextCommand extends Command {

    protected DrawContext context;

    public ActivateContextCommand(DrawContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        context.setActivated(true);
    }
}
