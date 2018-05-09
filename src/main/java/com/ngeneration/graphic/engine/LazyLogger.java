package com.ngeneration.graphic.engine;

public class LazyLogger {
    private final Level currentLevel = Level.TRACE;

    public static LazyLogger getLogger(Class<?> clazz) {
        return new LazyLogger(clazz);
    }

    private LazyLogger(Class<?> clazz) {
        // i'm too lazy for this
    }

    public void trace(String msg) {
        log(msg, Level.TRACE);
    }

    public void debug(String msg) {
        log(msg, Level.DEBUG);
    }

    public void info(String msg) {
        log(msg, Level.INFO);
    }

    public void warn(String msg) {
        log(msg, Level.WARN);
    }

    public void error(String msg) {
        log(msg, Level.ERROR);
    }


    private void log(String msg, Level level) {
        if (isVisible(level)) {
            doLog(msg, level);
        }
    }

    private void doLog(String msg, Level level) {
        System.out.format("[%5s] %s%n", level, msg);
    }

    private boolean isVisible(Level msgLevel) {
        return msgLevel.isUpper(currentLevel);
    }

    enum Level {
        TRACE(1),
        DEBUG(2),
        INFO(3),
        WARN(4),
        ERROR(5),
        OFF(6);
        private final int levelNumber;

        Level(int levelNumber) {
            this.levelNumber = levelNumber;
        }

        private boolean isUpper(Level minLevel) {
            return this.levelNumber >= minLevel.getLevelNumber();
        }

        public int getLevelNumber() {
            return levelNumber;
        }
    }
}
