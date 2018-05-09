package com.fuzzygroup.view.input;

import com.fuzzygroup.view.enums.ActionType;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;

public class InputHandler {
    private List<MouseEvent> mouseEvents = new CopyOnWriteArrayList<>();
    private List<KeyboardEvent> keyboardEvents = new CopyOnWriteArrayList<>();

    private double zoomDecreasingSpeed = 0;
    private int mouseDWheel;
    private volatile boolean keyboardEventsLocked = false;
    private final Object keyboardEventsMonitor = new Object();

    private final Mouse mouse;
    private final Keyboard keyboard;

    public InputHandler(Mouse mouse, Keyboard keyboard) {
        this.mouse = mouse;
        this.keyboard = keyboard;
    }

    public void updateMouseEventsType() {
        for (MouseEvent event : mouseEvents) {
            boolean keysPressed = true;
            for (int key : event.getKeys()) {
//                    System.out.println("key = " + key);
                if (!mouse.isButtonDown(key)) {
                    keysPressed = false;
                }
            }
            if (Arrays.stream(event.getKeys()).noneMatch(k -> k == GLFW_KEY_LEFT_SHIFT)
                    && keyboard.get(GLFW_KEY_LEFT_SHIFT) != 0) {
                keysPressed = false;
            }
            if (Arrays.stream(event.getKeys()).noneMatch(k -> k == GLFW_KEY_LEFT_CONTROL)
                    && keyboard.get(GLFW_KEY_LEFT_CONTROL) != 0) {
                keysPressed = false;
            }
            if ((event.getCurrentActionType() & ActionType.NO_ACTION) > 0 && keysPressed) {
                event.setCurrentActionType(ActionType.CLICKED);
            } else if ((event.getCurrentActionType() & ActionType.CLICKED) > 0 && keysPressed) {
                event.setCurrentActionType(ActionType.PRESSED);
            }
            //every 200 millis
//            if ((event.getCurrentActionType() & ActionType.PRESSED) > 0
//                    && (event.getCurrentActionType() & ActionType.PRESSED_EVERY_200_MILLISECONDS) == 0
//                    && keysPressed) {
//                event.setCurrentActionType(
//                        event.getCurrentActionType() | ActionType.PRESSED_200_MILLISECONDS);
//            } else if ((event.getCurrentActionType() & ActionType.PRESSED_EVERY_200_MILLISECONDS) > 0 && keysPressed) {
//                event.setCurrentActionType(ActionType.PRESSED);
//            }
            //other
            if ((event.getCurrentActionType() & ActionType.RELEASED) > 0 && keysPressed) {
                event.setCurrentActionType(ActionType.CLICKED);
            }
            if ((event.getCurrentActionType() & ActionType.RELEASED) > 0 && !keysPressed) {
                event.setCurrentActionType(ActionType.NO_ACTION);
            }
            if ((event.getCurrentActionType() & ActionType.PRESSED) > 0 && !keysPressed) {
                event.setCurrentActionType(ActionType.RELEASED);
            }
            if ((event.getCurrentActionType() & ActionType.PRESSED) > 0 && keysPressed
                    && System.currentTimeMillis() - event.getLastActionTime() >= 10) {
                event.setCurrentActionType(ActionType.PRESSED_50_MILLISECONDS);
            }
            if ((event.getCurrentActionType() & ActionType.PRESSED) > 0 && keysPressed
                    && System.currentTimeMillis() - event.getLastActionTime() >= 200) {
                event.setCurrentActionType(ActionType.PRESSED_200_MILLISECONDS);
                event.setCurrentActionType(ActionType.PRESSED_EVERY_200_MILLISECONDS);
            }
            if ((event.getCurrentActionType() & ActionType.PRESSED) > 0 && keysPressed
                    && System.currentTimeMillis() - event.getLastActionTime() >= 1000) {
                event.setCurrentActionType(ActionType.PRESSED_ONE_SECOND);
            }
            if ((event.getCurrentActionType() & ActionType.PRESSED) > 0 && keysPressed
                    && System.currentTimeMillis() - event.getLastActionTime() >= 2000) {
                event.setCurrentActionType(ActionType.PRESSED_TWO_SECONDS);
            }
        }
    }

    public void createMouseEvent(MouseEvent event) {
        mouseEvents.add(event);
    }

    public void updateKeyboardEventsType() {
        keyboardEventsLocked = true;
        for (KeyboardEvent event : keyboardEvents) {
            boolean keysPressed = true;
            for (int key : event.getKeys()) {
//                    System.out.println("key = " + key);
                if (!keyboard.isKeyDown(key)) {
                    keysPressed = false;
                }
            }
            if (Arrays.stream(event.getKeys()).noneMatch(k -> k == GLFW_KEY_LEFT_SHIFT)
                    && keyboard.isKeyDown(GLFW_KEY_LEFT_SHIFT)) {
                keysPressed = false;
            }
            if (Arrays.stream(event.getKeys()).noneMatch(k -> k == GLFW_KEY_LEFT_CONTROL)
                    && keyboard.isKeyDown(GLFW_KEY_LEFT_CONTROL)) {
                keysPressed = false;
            }
            if ((event.getCurrentActionType() & ActionType.NO_ACTION) > 0 && keysPressed) {
                event.setCurrentActionType(ActionType.CLICKED);
            } else if ((event.getCurrentActionType() & ActionType.CLICKED) > 0 && keysPressed) {
                event.setCurrentActionType(ActionType.PRESSED);
            }
            //other
            if ((event.getCurrentActionType() & ActionType.RELEASED) > 0 && keysPressed) {
                event.setCurrentActionType(ActionType.CLICKED);
            }
            if ((event.getCurrentActionType() & ActionType.RELEASED) > 0 && !keysPressed) {
                event.setCurrentActionType(ActionType.NO_ACTION);
            }
            if ((event.getCurrentActionType() & ActionType.PRESSED) > 0 && !keysPressed) {
                event.setCurrentActionType(ActionType.RELEASED);
                event.resetLastIntervalTrigerMilliseconds();
            }
            if ((event.getCurrentActionType() & ActionType.PRESSED) > 0 && keysPressed
                    && System.currentTimeMillis() - event.getLastActionTime() >= 10) {
                event.setCurrentActionType(ActionType.PRESSED_50_MILLISECONDS);
            }
            if ((event.getCurrentActionType() & ActionType.PRESSED) > 0 && keysPressed
                    && System.currentTimeMillis() - event.getLastActionTime() >= 200) {
                event.setCurrentActionType(ActionType.PRESSED_200_MILLISECONDS);
            }
            if ((event.getCurrentActionType() & ActionType.PRESSED) > 0 && keysPressed
                    && System.currentTimeMillis() - event.getLastActionTime() >= 1000) {
                event.setCurrentActionType(ActionType.PRESSED_ONE_SECOND);
            }
            if ((event.getCurrentActionType() & ActionType.PRESSED) > 0 && keysPressed
                    && System.currentTimeMillis() - event.getLastActionTime() >= 2000) {
                event.setCurrentActionType(ActionType.PRESSED_TWO_SECONDS);
            }
            //every 200 millis
//            if(System.currentTimeMillis() - event.getLastIntervalTrigerMilliseconds()> 100) {
//                System.out.println("");
//            }
//            if(keysPressed) {
//                System.out.println(System.currentTimeMillis() - event.getLastIntervalTrigerMilliseconds());
//            }
            if (
//                    (event.getCurrentActionType() & ActionType.PRESSED) > 0
//                    && (event.getCurrentActionType() & ActionType.PRESSED_EVERY_200_MILLISECONDS) == 0
                    System.currentTimeMillis() - event.getLastIntervalTrigerMilliseconds() >= 100
                            && keysPressed
                    ) {
                event.setCurrentActionType(
                        event.getCurrentActionType() | ActionType.PRESSED_EVERY_200_MILLISECONDS);
                event.resetLastIntervalTrigerMilliseconds();
            } else if ((event.getCurrentActionType() & ActionType.PRESSED_EVERY_200_MILLISECONDS) > 0) {
                event.setCurrentActionType(ActionType.PRESSED);
            }
//            if (event.getCurrentActionType() != 1) {
//                System.out.println(event.getCurrentActionType());
//            }
//            if ((event.getCurrentActionType() & ActionType.PRESSED_EVERY_200_MILLISECONDS) > 0) {
//                System.out.println("PRESSED_EVERY_200_MILLISECONDS");
//            }
        }
//        if (keyboardEventsTryToGetAccess) {
//        System.out.println("keyboardEventsMonitor");
        synchronized (keyboardEventsMonitor) {
            keyboardEventsMonitor.notifyAll();
            keyboardEventsLocked = false;
        }
//        }
    }

    public void createKeyboardEvent(KeyboardEvent event) {
        while (keyboardEventsLocked) {
            synchronized (keyboardEventsMonitor) {
                while (keyboardEventsLocked) {
                    try {
                        keyboardEventsMonitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        keyboardEvents.add(event);
    }

    public void listenkeyboardEvent() {
        for (KeyboardEvent event : keyboardEvents) {
            if (Arrays.stream(event.getKeys()).allMatch(keyboard::isKeyDown)) {
            }
            if (//Arrays.stream(event.getKeys()).allMatch(keyboard::isKeyDown)
                // &&
                    (event.getTriggerActionType() & event.getCurrentActionType()) > 0) {
//                System.out.println(event);
                event.run();
            }
        }
    }

    public void listenMouseEvent() {
        for (MouseEvent event : mouseEvents) {
            if ((
//                    Arrays.stream(event.getKeys()).allMatch(mouse::isButtonDown)
//                    &&
                    event.getTriggerActionType() == event.getCurrentActionType())
//                || (Arrays.stream(event.getKeys()).allMatch(mouse::isButtonDown)
//                    && event.getTriggerActionType() == event.getCurrentActionType())
                    ) {
                event.run();
            }
//            mouseDWheel = mouse.getDWheel();
            if (
                    Arrays.stream(event.getKeys()).allMatch(k -> k == 3)
                            &&
                            (mouseDWheel != 0 || zoomDecreasingSpeed != 0)
//                    && event.getTriggerActionType() == event.getCurrentActionType()
                    ) {
                event.run();
            }
        }
    }
}
