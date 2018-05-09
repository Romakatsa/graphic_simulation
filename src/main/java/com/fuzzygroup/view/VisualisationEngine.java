package com.fuzzygroup.view;

import com.fuzzygroup.view.input.InputHandler;
import com.ngeneration.graphic.engine.ThreeVector;

interface VisualisationEngine {
    void init();

    void shutdown();

    void nextFrame();

    void afterFrame();

    void drawProcess(VisualRepresentationState representation,
                     ThreeVector scale, ThreeVector shift, double canvasRotationRadian, int pixelsPerDistanceUnit, double sidesRatio);

    void draw(VisualRepresentationState representation,
              ThreeVector scale, ThreeVector shift, double canvasRotationRadian, int pixelsPerDistanceUnit, double sidesRatio);

    boolean beyondVisibleArea(VisualRepresentationState representation);

//    void createDisplay(Display display);

    InputHandler createInputHandler();

    double getMouseX();

    double getMouseY();
}
