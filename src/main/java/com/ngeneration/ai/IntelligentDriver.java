package com.ngeneration.ai;

import com.ngeneration.custom_rendered_components.Car;
import com.ngeneration.custom_rendered_components.Road;
import com.ngeneration.graphic.engine.Vector;
import com.ngeneration.graphic.engine.utils.MathUtils;

import java.util.List;

public class IntelligentDriver {
    private static final int DEFAULT_VIEW_LINE_AMOUNT = 5;
    private final int viewLineAmount;
    private double[] viewLinesObstacleDistance;
    private final Road road;

    public IntelligentDriver(Road road) {
        this.viewLineAmount = DEFAULT_VIEW_LINE_AMOUNT;
        viewLinesObstacleDistance = new double[viewLineAmount];
        this.road = road;
    }

    public class IntelligentDriverViewAnalyzer implements Driver {


        public IntelligentDriverViewAnalyzer() {
//            this.road = road;
        }

        @Override
        public void accept(Car car, Double deltaTime) {
            System.out.println("IntelligentDriverViewAnalyzer.accept");
            double viewFieldWidthRadian = 3.14;
            // uniform distribution
            for (int i = 0; i < viewLinesObstacleDistance.length; i++) {
                double distance = Double.NaN;
                Vector carPosition = car.getPosition();
                Vector viewLineEndPoint = carPosition.plus(new Vector.PolarCoordinateSystemVector(
                        car.getRotation() - viewFieldWidthRadian / 2 + i * (viewFieldWidthRadian / viewLineAmount),
                        10000).toFlatCartesianVector());

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
                                distance = curDistance;
                            }
                        }
                    }
                }


                viewLinesObstacleDistance[i] = distance;
            }
            for (int i = 0; i < viewLinesObstacleDistance.length; i++) {
                double distance = viewLinesObstacleDistance[i];
                System.out.println("[" + i + "] = " + distance);
            }
        }
    }

    public class IntelligentDriverController implements Driver {
        @Override
        public void accept(Car car, Double deltaTime) {

        }
    }

}
