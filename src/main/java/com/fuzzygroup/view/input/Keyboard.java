package com.fuzzygroup.view.input;

import java.util.HashMap;
import java.util.Map;

public class Keyboard {
    private Map<Integer, Integer> keys = new HashMap<>();

    public void put(int key, int action) {
        keys.put(key, action);
    }

    public Integer get(int key) {
        boolean exists = keys.containsKey(key);
        if (exists) {
            return keys.get(key);
        } else {
            return 0;
        }
    }

    public boolean isKeyDown(int key) {
        boolean exists = keys.containsKey(key);
        if (exists) {
            return keys.get(key) != 0;
        } else {
            return false;
        }
    }
}
