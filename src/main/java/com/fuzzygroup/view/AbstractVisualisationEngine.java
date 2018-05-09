package com.fuzzygroup.view;

import com.ngeneration.graphic.engine.ThreeVector;

public abstract class AbstractVisualisationEngine implements VisualisationEngine {
//    private final Logger logger = LoggerFactory.getLogger(Display.class);

    private boolean enableDrawing = true;


    public void drawProcess(VisualRepresentationState representation,
                            ThreeVector scale, ThreeVector shift, double canvasRotationRadian,
                            int pixelsPerDistanceUnit, double sidesRatio) {
//        if (isDrawable(representation, scale, shift, canvasRotationRadian)) {
        draw(representation, scale, shift, canvasRotationRadian, pixelsPerDistanceUnit, sidesRatio);
//        }
    }

    protected boolean isDrawable(VisualRepresentationState representation
//            ,
//                               ThreeVector scale, ThreeVector shift, double canvasRotationRadian
    ) {
        boolean drawable = true;
        String problemDescription = "";
        if (!enableDrawing) {
            drawable = false;
            problemDescription = "Drawing impossible: Drawing is disabled";
        } else if (representation == null) {
            drawable = false;
            problemDescription = "Drawing impossible: object is null";
        } else if (!representation.isVisible()) {
            drawable = false;
            problemDescription = "Drawing impossible: object is not visible";
        } else if (beyondVisibleArea(representation)) {
            drawable = false;
            problemDescription = "Skip drawing: object is beyond visible area";
        }
        if (drawable) {
//            logger.trace("{} is going to drawn", representation);
        } else {
//            logger.trace("{} isn't going to drawn. {}", representation, problemDescription);
        }
        return drawable;
    }

    public void setEnableDrawing(boolean enableDrawing) {
        this.enableDrawing = enableDrawing;
    }


}
