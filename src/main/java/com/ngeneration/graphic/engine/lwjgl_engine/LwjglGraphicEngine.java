package com.ngeneration.graphic.engine.lwjgl_engine;

import com.ngeneration.graphic.engine.Shape;
import com.ngeneration.graphic.engine.ThreeVector;
import com.ngeneration.graphic.engine.drawers.Drawer;
import com.ngeneration.graphic.engine.drawers.LwjglDrawer;
import com.ngeneration.graphic.engine.enums.ColorEnum;
import com.ngeneration.graphic.engine.input.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class LwjglGraphicEngine implements GraphicEngine<Long> {
    private static LwjglGraphicEngine instance;

    public static synchronized LwjglGraphicEngine getInstance() {
        if (instance == null) {
            instance = new LwjglGraphicEngine();
        }
        return instance;
    }

    private LwjglGraphicEngine() {
    }

    private boolean initialized = false;
    private boolean started = false;
    private boolean pause = false;

    private final Mouse mouse = new Mouse();
    private final Keyboard keyboard = new Keyboard();
    private final InputHandler inputHandler = new InputHandler(mouse, keyboard);

    private synchronized void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
//        drawTransparentContent();
//        glDisable(GL_BLEND);
//        drawNonTransparentContent();
    }

    @Override
    public synchronized Long createWindow(String title, int width, int height, ColorEnum background) {
        if (!initialized) {
            init();
            initialized = true;
            started = true;
        }
        // Create the window
        long windowId = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowId == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(windowId, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
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
                    vidmode.width() - pWidth.get(0) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(windowId);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(windowId);

        //==============================================
        //==============================================
        //==============================================

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        GL11.glClearColor((float) background.getRed(), (float) background.getGreen(), (float) background.getBlue(), (float) 0);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        enableInputHandling(width, height, windowId);
        return windowId;
    }

    @Override
    public void closeWindow(Long id) {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(id);
//        glfwDestroyWindow(id);
        glfwSetWindowShouldClose(id, true);
    }

    @Override
    public void beforeFrame() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
    }

    @Override
    public void render(double x, double y, double sx, double sy, double rotate, ColorEnum color, Shape shape, double opacity) {
        //TODO decouple shape drawing
        x = x / 50 - 0;
        y = y / 50 - 0;
        sx = sx / 50 * 1; // TODO this factor define window proportion
        sy = sy / 50 * 1;
        glPushMatrix();
        chooseColor(color, 1 - opacity);
        glTranslated(x, y, 0);
        glRotated((rotate - Math.PI / 2) * 180 / Math.PI, 0, 0, 1);
        x = 0;
        y = 0;
        glBegin(GL_QUADS);
        glVertex2f((float) (x + sx / 2), (float) (y + sy / 2));
        glVertex2f((float) (x + sx / 2), (float) (y - sy / 2));
        glVertex2f((float) (x - sx / 2), (float) (y - sy / 2));
        glVertex2f((float) (x - sx / 2), (float) (y + sy / 2));
        glEnd();
        glPopMatrix();
    }

    @Override
    public void afterFrame(Long id) {
        glfwSwapBuffers(id); // swap the color buffers

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();
    }

    @Override
    public void shutdown() {
        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private static void chooseColor(ColorEnum color, double opacity) {
        if (color == null) {
            return;
        }
        switch (color) {
            case WHITE:
                glColor4d(0.8f, 0.8f, 0.8f, opacity);
                break;
            case RED:
                glColor4d(1.0f, 0.0f, 0.0f, opacity);
                glColor4d(0.733f, 0.223f, 0.168f, opacity);
                break;
            case GREEN:
                glColor4d(0.478f, 0.737f, 0.345f, opacity);
                break;
            case BLUE:
                glColor4d(0.247f, 0.494f, 1.0f, opacity);
                break;
            case BLACK:
                glColor4d(0f, 0.0f, 0.0f, opacity);
                break;
            default:
                glColor4d(color.getRed(), color.getGreen(), color.getBlue(), opacity);
        }
    }

    // TODO refactor it, it's just copy from old version
    private void enableInputHandling(int width, int height, long windowId) {
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(windowId, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            else {
                keyboard.put(key, action);
            }
        });

        glfwSetMouseButtonCallback(windowId, GLFWMouseButtonCallback.create((window, button, action, mods) -> {
            System.out.println("smth");
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

//        glfwSetScrollCallback(windowId, GLFWScrollCallback.create((l, v, v1) -> {
//            double factor = 10;
//            if (v1 == 1) {
//                display.setScale(display.getScale().multiple(
//                        1 + ((factor - 1) * display.getDeltaTime())));
//            } else {
//                display.setScale(display.getScale().multiple(
//                        1 - 5 * display.getDeltaTime()
////                         1-((1-1d / (factor-1)) * display.getDeltaTime())
//                ));
//            }
//        }));
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
            // TODO refactor
            while (true) {
                try {
                    inputHandler.listenkeyboardEvent();
                    inputHandler.listenMouseEvent();
                    inputHandler.updateKeyboardEventsType();
                    inputHandler.updateMouseEventsType();
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

    public void addKeyboardEvent(KeyboardEvent event) {
        inputHandler.createKeyboardEvent(event);
    }

    public void addMouseEvent(MouseEvent event) {
        inputHandler.createMouseEvent(event);
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isPaused() {
        return pause;
    }

    @Override
    public Drawer<Long> getDrawer() {
        return new LwjglDrawer();
    }
}
