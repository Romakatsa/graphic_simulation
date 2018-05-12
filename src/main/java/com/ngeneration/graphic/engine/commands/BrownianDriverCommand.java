package com.ngeneration.graphic.engine.commands;

import com.ngeneration.ai.BrownianDriver;

import java.util.function.Consumer;

public abstract class BrownianDriverCommand implements Consumer<Double>, Command {
    public final Command.Converter CONVERTER;
    protected final BrownianDriver driver;

    public BrownianDriverCommand(BrownianDriver driver) {
        this(driver, new Command.ExponentialConverter());
    }

    protected BrownianDriverCommand(BrownianDriver driver, Converter CONVERTER) {
        this.CONVERTER = CONVERTER;
        this.driver = driver;
    }

    public static class ChangeSpeed extends BrownianDriverCommand {
        public ChangeSpeed(BrownianDriver driver) {
            super(driver, new Command.ExponentialConverter());
        }

        public void accept(Double value) {
            value = this.CONVERTER.convert(value);
            driver.changeSpeedModule(value);
        }
    }

    public static class ChangeTwist extends BrownianDriverCommand {
        public ChangeTwist(BrownianDriver driver) {
            super(driver, new LinearConverter(2d / 100d));
        }

        public void accept(Double value) {
            value = this.CONVERTER.convert(value);
            driver.changeTwist(value);
        }
    }
}
