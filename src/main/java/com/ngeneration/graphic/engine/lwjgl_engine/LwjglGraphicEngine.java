package com.ngeneration.graphic.engine.lwjgl_engine;

import com.ngeneration.graphic.engine.Shape;
import com.ngeneration.graphic.engine.ThreeVector;
import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.drawers.Drawer;
import com.ngeneration.graphic.engine.drawers.LwjglDrawer;
import com.ngeneration.graphic.engine.enums.Color;
import com.ngeneration.graphic.engine.input.*;
import com.ngeneration.graphic.engine.view.DrawArea;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ngeneration.graphic.engine.utils.MathUtils.loopValueInBounds;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
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
    public synchronized Long createWindow(String title, int width, int height, Color background) {
        if (!initialized) {
            init();
            initialized = true;
            started = true;
        }
        // Create the window
        long windowId = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowId == NULL)
            throw new RuntimeException("Failed to createPanelAndGetController the GLFW window");

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
    public void render(double x, double y, double sx, double sy, double rotation, Color color, Shape shape,
                       double opacity, DrawArea holder) {
        //TODO decouple shape drawing
        x = x / 50 - 0;
        y = y / 50 - 0;
        sx = sx / 50 * 1; // TODO this factor define window proportion
        sy = sy / 50 * 1;
        double x1, x2;
        double y1, y2;
        x1 = GeometryUtils.min(-sx / 2, +sx / 2);
        x2 = GeometryUtils.max(-sx / 2, +sx / 2);
        y1 = GeometryUtils.min(-sy / 2, +sy / 2);
        y2 = GeometryUtils.max(-sy / 2, +sy / 2);
        Vector.Polar v1 = new Vector(x1, y1).toPolar();
        Vector.Polar v2 = new Vector(x1, y2).toPolar();
        Vector.Polar v3 = new Vector(x2, y2).toPolar();
        Vector.Polar v4 = new Vector(x2, y1).toPolar();
        double radianBias = Math.PI / 2;
        v1.setRadian(v1.getRadian() + rotation + radianBias);
        v2.setRadian(v2.getRadian() + rotation + radianBias);
        v3.setRadian(v3.getRadian() + rotation + radianBias);
        v4.setRadian(v4.getRadian() + rotation + radianBias);
        List<Vector> points = new ArrayList<>();
//        Vector shift = Vector.zero();
        Vector shift = new Vector(x, y);
        points.add(v1.toFlatCartesianVector().plus(shift));
        points.add(v2.toFlatCartesianVector().plus(shift));
        points.add(v3.toFlatCartesianVector().plus(shift));
        points.add(v4.toFlatCartesianVector().plus(shift));
//        x1 = v1.toFlatCartesianVector().getX();
//        y1 = v1.toFlatCartesianVector().getY();

        GeometryUtils.cutPolygon(holder, points);
        glPushMatrix();
        chooseColor(color, 1 - opacity);
//        glTranslated(x, y, 0);
//        glRotated((rotation - Math.PI / 2) * 180 / Math.PI, 0, 0, 1);
        x = 0;
        y = 0;
        glBegin(GL_POLYGON);
        for (Vector point : points) {
            glVertex2f((float) (x + point.getX()), (float) (y + point.getY()));
        }
//        glVertex2f((float) (x2), (float) (y2));
//        glVertex2f((float) (x2), (float) (y1));
//        glVertex2f((float) (x1), (float) (y1));
//        glVertex2f((float) (x1), (float) (y2));
        glEnd();
        glPopMatrix();
    }

    private static final class GeometryUtils {
        private static void cutPolygon(DrawArea holder, List<Vector> points) {
            if (holder != null) {
                Vector areaLowBound = Vector.zero().minus(holder.getSize().divide(2).divide(50));
                Vector areaHighBound = holder.getSize().divide(2).divide(50);
                Vector reverseZoom = Vector.one().divide(holder.getZoomFactor());
                double lowX = areaLowBound
                        .coordinatewiseMultiplication(reverseZoom).getX();
                double highX =
                        areaHighBound
                                .coordinatewiseMultiplication(reverseZoom).getX();
                double lowY =
                        areaLowBound
                                .coordinatewiseMultiplication(reverseZoom).getY();
                double highY =
                        areaHighBound
                                .coordinatewiseMultiplication(reverseZoom).getY();

                shiftArrayWithFirstPointIsWithinBound(points, lowX, highX, lowY, highY);
                for (int k = 0; k < 2; k++) {
                    for (int i = 0; i < points.size(); i++) {
                        boolean pointRemoved = cutPolygonPoint(points, lowX, i);
                        if (!pointRemoved) {
                            pointRemoved = cutPolygonPoint2(points, highX, i);
                        }
                        if (!pointRemoved) {
                            pointRemoved = cutPolygonPoint3(points, lowY, i);
                        }
                        if (!pointRemoved) {
                            cutPolygonPoint4(points, highY, i);
                        }
                    }
                }
            }
        }

        private static void shiftArrayWithFirstPointIsWithinBound(List<Vector> points, double lowX, double highX, double lowY, double highY) {
            for (int i = 0; i < points.size(); i++) {
                Vector point = points.get(i);
                if (point.getX() >= lowX
                        && point.getY() >= lowY
                        && point.getX() <= highX
                        && point.getY() <= highY) {
                    for (int j = 0; j < i; j++) {
                        Vector point2 = points.get(0);
                        points.add(point2);
                        points.remove(0);
                    }
                    break;
                }
            }
        }

        private static int countPointsWithinBounds(List<Vector> points, double lowX, double highX, double lowY, double highY) {
            int pointsWithinBounds = 0;
            for (int i = 0; i < points.size(); i++) {
                Vector point = points.get(i);
                if (point.getX() >= lowX
                        && point.getY() >= lowY
                        && point.getX() <= highX
                        && point.getY() <= highY) {
                    pointsWithinBounds++;
                }
            }
            return pointsWithinBounds;
        }

        @SuppressWarnings("all")
        private static boolean cutPolygonPoint(List<Vector> points, double lowX, int i) {
            double pointX = points.get(i).getX();
            double pointY = points.get(i).getY();
            double computationalError = 0.002;
            if (pointX < lowX - computationalError) {
                Vector prevPoint = points.get(loopValueInBounds(i - 1, 0, points.size() - 1));
                Vector nextPoint = points.get(loopValueInBounds(i + 1, 0, points.size() - 1));
                double deltaX;
                double deltaY;
                int addedPoints = 0;
                if (prevPoint.getX() >= lowX - computationalError) {
                    deltaX = Math.abs(prevPoint.getX() - pointX);
                    deltaY = (prevPoint.getY() - pointY);
                    double factor = Math.min(Math.abs(lowX - pointX) / deltaX, 1);
                    points.add(i + addedPoints + 1, new Vector(lowX, pointY + Math.min(Math.abs(lowX - pointX) / deltaX, 1) * deltaY));
                    addedPoints++;
                }
                if (nextPoint.getX() >= lowX - computationalError) {
                    deltaX = Math.abs(nextPoint.getX() - pointX);
                    deltaY = (nextPoint.getY() - pointY);
                    points.add(i + addedPoints + 1, new Vector(lowX, pointY + Math.min(Math.abs(lowX - pointX) / deltaX, 1) * deltaY));
                    addedPoints++;
                }
                points.remove(i);
                return true;
            }
            return false;
        }

        @SuppressWarnings("all")
        private static boolean cutPolygonPoint2(List<Vector> points, double highX, int i) {
            double pointX = points.get(i).getX();
            double pointY = points.get(i).getY();
            double computationalError = 0.002;
            if (pointX > highX + computationalError) {
                Vector prevPoint = points.get(loopValueInBounds(i - 1, 0, points.size() - 1));
                Vector nextPoint = points.get(loopValueInBounds(i + 1, 0, points.size() - 1));
                double deltaX;
                double deltaY;
                int addedPoints = 0;
                if (prevPoint.getX() <= highX + computationalError) {
                    deltaX = Math.abs(prevPoint.getX() - pointX);
                    deltaY = (prevPoint.getY() - pointY);
                    double factor = Math.min(Math.abs(highX - pointX) / deltaX, 1);
                    points.add(i + addedPoints + 1, new Vector(highX, pointY + Math.min(Math.abs(highX - pointX) / deltaX, 1) * deltaY));
                    addedPoints++;
                }
                if (nextPoint.getX() <= highX + computationalError) {
                    deltaX = Math.abs(nextPoint.getX() - pointX);
                    deltaY = (nextPoint.getY() - pointY);
                    points.add(i + addedPoints + 1, new Vector(highX, pointY + Math.min(Math.abs(highX - pointX) / deltaX, 1) * deltaY));
                    addedPoints++;
                }
                points.remove(i);
                return true;
            }
            return false;
        }

        //        @SuppressWarnings("all")
        private static boolean cutPolygonPoint3(List<Vector> points, double lowY, int i) {
            double pointX = points.get(i).getX();
            double pointY = points.get(i).getY();
            double computationalError = 0.002;
            if (pointY < lowY - computationalError) {
                Vector prevPoint = points.get(loopValueInBounds(i - 1, 0, points.size() - 1));
                Vector nextPoint = points.get(loopValueInBounds(i + 1, 0, points.size() - 1));
                double deltaX;
                double deltaY;
                int addedPoints = 0;
                if (prevPoint.getY() >= lowY - computationalError) {
                    deltaX = (prevPoint.getX() - pointX);
                    deltaY = Math.abs(prevPoint.getY() - pointY);
                    points.add(i + addedPoints + 1, new Vector(pointX + Math.min(Math.abs(lowY - pointY) / deltaY, 1) * deltaX, lowY));
                    addedPoints++;
                }
                if (nextPoint.getY() >= lowY - computationalError) {
                    deltaX = (nextPoint.getX() - pointX);
                    deltaY = Math.abs(nextPoint.getY() - pointY);
                    points.add(i + addedPoints + 1, new Vector(pointX + Math.min(Math.abs(lowY - pointY) / deltaY, 1) * deltaX, lowY));
                    addedPoints++;
                }
                points.remove(i);
                return true;
            }
            return false;
        }

        //        @SuppressWarnings("all")
        private static boolean cutPolygonPoint4(List<Vector> points, double highY, int i) {
            double pointX = points.get(i).getX();
            double pointY = points.get(i).getY();
            double computationalError = 0.002;
            if (pointY > highY + computationalError) {
                Vector prevPoint = points.get(loopValueInBounds(i - 1, 0, points.size() - 1));
                Vector nextPoint = points.get(loopValueInBounds(i + 1, 0, points.size() - 1));
                double deltaX;
                double deltaY;
                int addedPoints = 0;
                if (prevPoint.getY() <= highY + computationalError) {
                    deltaX = (prevPoint.getX() - pointX);
                    deltaY = Math.abs(prevPoint.getY() - pointY);
                    points.add(i + addedPoints + 1, new Vector(pointX + Math.min(Math.abs(highY - pointY) / deltaY, 1) * deltaX, highY));
                    addedPoints++;
                }
                if (nextPoint.getY() <= highY + computationalError) {
                    deltaX = (nextPoint.getX() - pointX);
                    deltaY = Math.abs(nextPoint.getY() - pointY);
                    points.add(i + addedPoints + 1, new Vector(pointX + Math.min(Math.abs(highY - pointY) / deltaY, 1) * deltaX, highY));
                    addedPoints++;
                }
                points.remove(i);
                return true;
            }
            return false;
        }

        private static double min(double... values) {
            if (values.length == 0) {
                return Double.NaN;
            }
            double min = values[0];
            for (double value : values) {
                min = min < value ? min : value;
            }
            return min;
        }

        private static double max(double... values) {
            if (values.length == 0) {
                return Double.NaN;
            }
            double max = values[0];
            for (double value : values) {
                max = max > value ? max : value;
            }
            return max;
        }

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

    private static void chooseColor(Color color, double opacity) {
        if (color == null) {
            return;
        }
        glColor4d(color.getRed(), color.getGreen(), color.getBlue(), opacity);
//        switch (color) {
//            case Color.WHITE:
//                glColor4d(0.8f, 0.8f, 0.8f, opacity);
//                break;
//            case RED:
//                glColor4d(1.0f, 0.0f, 0.0f, opacity);
//                glColor4d(0.733f, 0.223f, 0.168f, opacity);
//                break;
//            case GREEN:
//                glColor4d(0.478f, 0.737f, 0.345f, opacity);
//                break;
//            case BLUE:
//                glColor4d(0.247f, 0.494f, 1.0f, opacity);
//                break;
//            case BLACK:
//                glColor4d(0f, 0.0f, 0.0f, opacity);
//                break;
//            default:
//                glColor4d(color.getRed(), color.getGreen(), color.getBlue(), opacity);
//        }
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

//        glfwSetScrollCallback(windowId, GLFWScrollCallback.createPanelAndGetController((l, v, v1) -> {
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
