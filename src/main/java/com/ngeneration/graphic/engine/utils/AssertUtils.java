package com.ngeneration.graphic.engine.utils;

import java.util.Collection;

public final class AssertUtils {
    public static void notNull(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
    }

    public static void isNull(Object o) {
        if (o != null) {
            throw new IllegalStateException();
        }
    }

    public static void isEmpty(String string) {
        if (string == null || string.isEmpty()) {
            throw new IllegalStateException();
        }
    }

    public static void isEmpty(Collection collection) {
        if (collection == null || collection.size() == 0) {
            throw new IllegalStateException();
        }
    }
}
