package com.fuzzygroup.view.lwjgl_engine;

import com.fuzzygroup.view.*;
import com.fuzzygroup.view.enums.ColorEnum;
import com.fuzzygroup.view.input.*;
import com.ngeneration.graphic.engine.ThreeVector;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

//import com.fuzzygroup.view.input.keyboardEvent;
//import com.fuzzygroup.view.input.mouseEvent;
//import org.lwjgl.LWJGLException;
//import org.lwjgl.input.keyboard;
//import org.lwjgl.input.mouse;
//import org.lwjgl.opengl.Display;
//import org.lwjgl.opengl.DisplayMode;
//import org.lwjgl.opengl.GL11;
//import static org.lwjgl.opengl.GL11.*;

public class LwjglEngine extends AbstractVisualisationEngine {

    private Map<Long, Display> windows = new HashMap<>();

    private final Mouse mouse = new Mouse();
    private final Keyboard keyboard = new Keyboard();

    private double zoomDecreasingSpeed = 0;
    private int mouseDWheel;
    private volatile boolean keyboardEventsLocked = false;
    private final Object keyboardEventsMonitor = new Object();
    private boolean hideMouse = false;
    private long windowId;
    private List<MouseEvent> mouseEvents = new ArrayList<>();
    private List<KeyboardEvent> keyboardEvents = new ArrayList<>();

    @Override
    public void init() {

    }

    @Override
    public void createDisplay(Display display) {
        int width = display.getWidth();
        int height = display.getHeight();
        String title = display.getQualifier();
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        long windowId = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowId == NULL)
            throw new RuntimeException("Failed to create the GLFW window");
        this.windows.put(windowId, display);
        this.windowId = windowId;

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(windowId, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    windowId,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(windowId);
        // Enable v-sync
        glfwSwapInterval(1);
        // Make the window visible
        glfwShowWindow(windowId);
        glfwSetInputMode(windowId, GLFW_CURSOR, hideMouse ? GLFW_CURSOR_HIDDEN : GLFW_CURSOR_NORMAL);

        // ==================================================
        GL.createCapabilities();
        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        enableInputHandling(display, width, height, windowId);
    }

    private void enableInputHandling(Display display, int width, int height, long windowId) {
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(windowId, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            else {
                keyboard.put(key, action);
            }
        });

        glfwSetMouseButtonCallback(windowId, GLFWMouseButtonCallback.create((window, button, action, mods) -> {
            if (button == 0 && action > 0) {
                mouse.setLastMousePosition(new ThreeVector(mouse.getX(), mouse.getY(), 0));
            } else if (button == 0 && action == 0) {
                mouse.setLastMousePosition(null);
            }
            if (button == 0) {
                mouse.setLeftButtonDown(action > 0);
            }
            if (button == 1) {
                mouse.setRightButtonDown(action > 0);
            }
            if (button == 2) {
                mouse.setCenterButtonDown(action > 0);
            }
        }));

        glfwSetCursorPosCallback(windowId, GLFWCursorPosCallback.create((window, xpos, ypos) -> {
            mouse.setX(xpos / width * 100);
            mouse.setY(100 - ypos / height * 100);
        }));

        glfwSetScrollCallback(windowId, GLFWScrollCallback.create((l, v, v1) -> {
            double factor = 10;
            if (v1 == 1) {
                display.setScale(display.getScale().multiple(
                        1 + ((factor - 1) * display.getDeltaTime())));
            } else {
                display.setScale(display.getScale().multiple(
                        1 - 5 * display.getDeltaTime()
//                         1-((1-1d / (factor-1)) * display.getDeltaTime())
                ));
            }
        }));
        Thread inputListener = new Thread(() -> {
            while (true) {
                glfwPollEvents();
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        inputListener.setName("input-listener-daemon");
        inputListener.setDaemon(true);
        inputListener.start();
        Thread listenActions = new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    System.err.println("Interrupt input handling");
                    break;
                }
            }
        });
        listenActions.setName("listen-input");
        listenActions.setDaemon(true);
        listenActions.start();
    }

    @Override
    public double getMouseX() {
        return mouse.getX();
    }

    @Override
    public double getMouseY() {
        return mouse.getY();
    }

    @Override
    public void shutdown() {
        // Free the window callbacks and destroy the window
        windows.forEach((w, d) -> glfwFreeCallbacks(w));
        windows.forEach((w, d) -> glfwDestroyWindow(w));
//        glfwFreeCallbacks(windowId);
//        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    @Override
    public InputHandler createInputHandler() {
        return new InputHandler(mouse, keyboard);
    }

    @Override
    public void nextFrame() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        windows.get(windowId).getInputHandler().updateMouseEventsType();
        windows.get(windowId).getInputHandler().listenMouseEvent();
        windows.get(windowId).getInputHandler().updateKeyboardEventsType();
        windows.get(windowId).getInputHandler().listenkeyboardEvent();
        if (mouse.getLastMousePosition() != null) {
            Display display = windows.get(windowId);
            display.setShift(display.getShift().plus(new ThreeVector(
                    mouse.getX() - mouse.getLastMousePosition().getX(),
                    mouse.getY() - mouse.getLastMousePosition().getY(), 0)));
            mouse.setLastMousePosition(new ThreeVector(mouse.getX(), mouse.getY(), 0));
        }
    }

    @Override
    public void afterFrame() {
        windows.forEach((w, d) -> glfwSwapBuffers(w));
        glfwSwapBuffers(windowId);
        glfwPollEvents();
    }

    @Override
    public void draw(VisualRepresentationState representation,
                     ThreeVector scale, ThreeVector shift, double canvasRotationRadian, int pixelsPerDistanceUnit,
                     double sidesRatio) {
        // todo x3 memory consumption
        VisualRepresentationState transformed
                = transform(representation,
                scale, shift, canvasRotationRadian, pixelsPerDistanceUnit, sidesRatio);

//        boolean beyondVisibleArea = beyondVisibleArea(transformed);
        if (isDrawable(transformed)) {
            draw(transformed);
        }
    }

    private VisualRepresentationState transform(VisualRepresentationState original,
                                                ThreeVector scale, ThreeVector shift,
                                                double canvasRotationRadian,
                                                int pixelsPerDistanceUnit, double sidesRatio) {
        // position
        ThreeVector transformedPosition = shift(original.getPosition(), shift);
        transformedPosition = scale(transformedPosition, scale);
//        transformedPosition = scale(transformedPosition, pixelsPerDistanceUnit);
        transformedPosition = rotate(transformedPosition, canvasRotationRadian);
        // size
        ThreeVector transformedSize = scale(original.getSize(), scale);

        VisualRepresentationState transformed = original.cloneState();
        transformed.setPosition(transformedPosition);
        transformed.setSize(transformedSize);

        return transformed;
    }


    private void draw(VisualRepresentationState representation) {
        double x = representation.getPosition().getX();
        double y = representation.getPosition().getY();
        double sx = representation.getSize().getX();
        double sy = representation.getSize().getY();
        double rotation = representation.getRotation();
        ColorEnum color = representation.getColors();
        double opacity = representation.getOpacity();
        draw(representation.getShape(), x, y, sx, sy, rotation, color, opacity);
    }

    private void draw(Shape shape, double x, double y, double sx, double sy, double rotation,
                      ColorEnum color, double opacity) {
        glDisable(GL_TEXTURE_2D);
//        x = representation.getPosition().getX();
//        y = representation.getPosition().getY();
//        sx = representation.getSize().getX();
//        sy = representation.getSize().getY();
//        rotation = representation.getRotation();
//        color = representation.getColors();
//        opacity = representation.getOpacity();
        switch (shape) {
            case RECT:
                Drawer.rect(x, y, sx, sy, rotation, color, opacity);
                break;
            case RECT_2:
                Drawer.rect2(x, y, sx, sy, rotation, color, opacity);
                break;
            case TRIANGLE:
                Drawer.triangle(x, y, sx, sy, rotation, color, opacity);
                break;
            case NARROW_TRIANGLE:
//                Drawer.triangle(x, y, sx/3, sy*1.5, rotation, color, opacity);
                Drawer.arrow(x, y, sx, sy, rotation, color, opacity);
                break;
            case CIRCLE:
                Drawer.circle(x, y, sx, sy, rotation, color, opacity);
                break;
            case STAR:
                Drawer.star(x, y, sx, sy, rotation, color, opacity);
                break;
            case FON:
                break;
            default:
                break;
        }
    }

    private ThreeVector shift(ThreeVector position, ThreeVector shift) {
        return position.plus(shift);
    }

    private ThreeVector scale(ThreeVector position, ThreeVector scale) {
        return position.coordinatewiseMultiplication(scale);
    }

    private ThreeVector scale(ThreeVector position, double factor) {
        return position.multiple(factor);
    }

    private ThreeVector rotate(ThreeVector position, double radian) {
        ThreeVector.PolarCoordinateSystemVector polarVector = position.toPolar();
        polarVector.setRadian(polarVector.getRadian() + radian);
        return polarVector.toFlatCartesianVector();
    }

    @Override
    public boolean beyondVisibleArea(VisualRepresentationState state) {
        double x = state.getPosition().getX();
        double y = state.getPosition().getY();
        ThreeVector bound1 = state.getPosition().plus(state.getSize().multiple(0.5));
        double rightX = bound1.getX();
        double topY = bound1.getY();
        ThreeVector bound2 = state.getPosition().minus(state.getSize().multiple(0.5));
        double leftX = bound2.getX();
        double bottomY = bound2.getY();
        return !(pointBelongsVisibleArea(x, bottomY)
                || pointBelongsVisibleArea(x, topY)
                || pointBelongsVisibleArea(leftX, y)
                || pointBelongsVisibleArea(rightX, y));
    }

    private boolean pointBelongsVisibleArea(double x, double y) {
        return (y >= 0 && y <= 100) && (x >= 0 && x <= 100);
    }

//    public void drawCursor(int cursorType, float cursorWidth, float cursorHeight, float em, float DEFAULT_EM, ThreeVector shift) {
//
//        switch (cursorType) {
//            case 1:
//                draw(Shape.TRIANGLE,
//                        mouse.getX() / em + shift.getX() + cursorWidth / em / 3,
//                        100 / em * DEFAULT_EM - mouse.getY() / em + shift.getY() + cursorHeight / em / 3,
//                        cursorHeight / em, cursorWidth / em, 115, ColorEnum.BLUE, 1);
//                break;
//            case 2:
//                draw(Shape.TRIANGLE,
//                        mouse.getX() / em + shift.getX(),
//                        100 / em * DEFAULT_EM - mouse.getY() / em + shift.getY(),
//                        cursorHeight / em, cursorWidth / em, 0, ColorEnum.BLUE, 1);
//                draw(Shape.TRIANGLE,
//                        mouse.getX() / em + shift.getX(),
//                        100 / em * DEFAULT_EM - mouse.getY() / em + shift.getY(),
//                        cursorHeight / em, cursorWidth / em, 90, ColorEnum.BLUE, 1);
//                draw(Shape.TRIANGLE,
//                        mouse.getX() / em + shift.getX(),
//                        100 / em * DEFAULT_EM - mouse.getY() / em + shift.getY(),
//                        cursorHeight / em, cursorWidth / em, 180, ColorEnum.BLUE, 1);
//                draw(Shape.TRIANGLE,
//                        mouse.getX() / em + shift.getX(),
//                        100 / em * DEFAULT_EM - mouse.getY() / em + shift.getY(),
//                        cursorHeight / em, cursorWidth / em, -90, ColorEnum.BLUE, 1);
//                break;
//            case 3:
//                double animationCycleMilliseconds = 1000;
//                double cursorAnimationPhase = animationCycleMilliseconds - System.currentTimeMillis() % animationCycleMilliseconds;
//                double maxCursorShift = 3;
//                double cursorShiftX = 1.2 + (maxCursorShift - 1.2) * Math.abs(1 - cursorAnimationPhase / animationCycleMilliseconds * 2);
//                double cursorShiftY = 1.6 + (maxCursorShift - 1.4) * Math.abs(1 - cursorAnimationPhase / animationCycleMilliseconds * 2);
//                double trWidth = cursorWidth / em / 3;
//                double trHeight = cursorHeight / em / 3;
//                draw(Shape.TRIANGLE,
//                        mouse.getX() / em + shift.getX() - cursorWidth / em / cursorShiftX,
//                        100 / em * DEFAULT_EM - mouse.getY() / em + shift.getY() + 0,
//                        trHeight, trWidth, 0, ColorEnum.BLUE, 1);
//                draw(Shape.TRIANGLE,
//                        mouse.getX() / em + shift.getX() - 0,
//                        100 / em * DEFAULT_EM - mouse.getY() / em + shift.getY() + cursorHeight / em / cursorShiftY,
//                        trHeight, trWidth, 90, ColorEnum.BLUE, 1);
//                draw(Shape.TRIANGLE,
//                        mouse.getX() / em + shift.getX() + cursorWidth / em / cursorShiftX,
//                        100 / em * DEFAULT_EM - mouse.getY() / em + shift.getY() + 0,
//                        trHeight, trWidth, 180, ColorEnum.BLUE, 1);
//                draw(Shape.TRIANGLE,
//                        mouse.getX() / em + shift.getX() - 0,
//                        100 / em * DEFAULT_EM - mouse.getY() / em + shift.getY() - cursorHeight / em / cursorShiftY,
//                        trHeight, trWidth, -90, ColorEnum.BLUE, 1);
//                break;
//        }
//    }

}
