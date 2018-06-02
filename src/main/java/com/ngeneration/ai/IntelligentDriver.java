package com.ngeneration.ai;

import com.ngeneration.custom_rendered_components.Car;
import com.ngeneration.custom_rendered_components.Road;
import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.drawablecomponents.Line;
import com.ngeneration.graphic.engine.enums.Color;
import com.ngeneration.graphic.engine.utils.MathUtils;
import com.ngeneration.graphic.engine.view.DrawContext;

import java.util.List;

public class IntelligentDriver {
    private static final int DEFAULT_VIEW_LINE_AMOUNT = 7;
    private final int viewLineAmount;
    private final DrawContext context; // TODO Roma. If you want draw smth just put it here and inherit your object from RenderedComponent
    private final boolean drawLine;
    private double[] viewLinesObstacleDistance;// TODO Roma look here. It's output of intelligent driver viewer
    private Line[] lines;
    private final Road road;

    public IntelligentDriver(Road road, DrawContext context, boolean drawLine) {
        this.viewLineAmount = DEFAULT_VIEW_LINE_AMOUNT;
        viewLinesObstacleDistance = new double[viewLineAmount];
        lines = initLines();
        this.road = road;
        this.context = context;
        this.drawLine = drawLine;
        context.put(50, lines);
    }

    private Line[] initLines() {
        Line[] viewLines = new Line[viewLineAmount];
        for (int i = 0; i < viewLines.length; i++) {
            viewLines[i] = new Line(Vector.zero(), Vector.zero(), 0.3, Color.RED, 0);
            viewLines[i].setVisible(drawLine);
        }
        return viewLines;
    }

    public class IntelligentDriverViewAnalyzer implements Driver {


        public IntelligentDriverViewAnalyzer() {
//            this.road = road;
        }

        @Override
        public void accept(Car car, Double deltaTime) {
            double viewFieldWidthRadian = 3.14;
            // uniform distribution
            for (int i = 0; i < viewLinesObstacleDistance.length; i++) {
                double distance = Double.NaN;
                Vector carPosition = car.getPosition();
                Vector viewLineEndPoint = carPosition.plus(new Vector.PolarCoordinateSystemVector(
                        car.getRotation() - viewFieldWidthRadian / 2 + i * (viewFieldWidthRadian / (viewLineAmount - 1)),
                        10000).toFlatCartesianVector());

                Vector minDistanceIntersectionVector = null;
                for (List<Vector> bound : road.getBounds()) {
                    for (int j = 1; j < bound.size() && bound.size() >= 2; j++) {
                        Vector roadPoint1 = bound.get(j - 1);
                        Vector roadPoint2 = bound.get(j);
                        Vector intersectionVector = MathUtils.intersection(
                                carPosition, viewLineEndPoint, roadPoint1, roadPoint2);
                        if (intersectionVector != null) {
                            double curDistance = carPosition
                                    .minus(intersectionVector)
                                    .module();
                            if (distance <= 0 || Double.isNaN(distance) || Double.isInfinite(distance)
                                    || curDistance < distance) {
                                minDistanceIntersectionVector = intersectionVector;
                                distance = curDistance;
                            }
                        }
                    }
                }

                viewLinesObstacleDistance[i] = distance;
                if (minDistanceIntersectionVector != null) {
                    Line line = lines[i];
                    line.changePosition(carPosition, minDistanceIntersectionVector);
                } else {
                    Line line = lines[i];
                    line.changePosition(carPosition, carPosition);
                }
            }
//            for (int i = 0; i < viewLinesObstacleDistance.length; i++) {
//                double distance = viewLinesObstacleDistance[i];
//                System.out.println("[" + i + "] = " + distance);
//            }
        }
    }

    public class IntelligentDriverController implements Driver {
        @Override
        public void accept(Car car, Double deltaTime) {

        }
    }

}
