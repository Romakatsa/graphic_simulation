package com.fuzzygroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ApplicationController {
    private List<Callable> onExitHooks = new ArrayList<>();

    public void addOnExitHook(Callable hook) {
        onExitHooks.add(hook);
    }

    public void onExitHooks() {
        for (Callable onExitHook : onExitHooks) {
            try {
                onExitHook.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
