package com.fuzzygroup;

import com.fuzzygroup.entities.Car;
import com.fuzzygroup.entities.Cell;
import com.fuzzygroup.view.*;
import com.fuzzygroup.view.enums.ActionType;
import com.fuzzygroup.view.input.MouseEvent;
import com.fuzzygroup.view.utils.Physics;
import com.ngeneration.graphic.engine.ThreeVector;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.glfw.GLFW.*;

public class EntityUtils {

    static ThreeVector mouseLastPosition;
    static Cell selectedCell;

    static void enableMapDrawing(Display display, List<Cell> cells) {
        display.addAction(new MouseEvent(() -> {
            if (selectedCell != null) {
                ThreeVector deltaVector = new ThreeVector(display.getMouseX(),
                        display.getMouseY(), 0)
                        .divide(display.getScale().getX()).minus(display.getShift())
                        .minus(selectedCell.getPosition());
//                if (deltaVector.module() > 3) {
                    selectedCell.setDirection(deltaVector
                            .toPolar()
                            .getDirection());
//                }
            }
            mouseLastPosition = new ThreeVector(display.getMouseX(), display.getMouseY(), 0)
                    .divide(display.getScale().getX())
                    .plus(display.getShift());
            cells.stream().filter(c -> {
//                    Physics.intersect(c, display.getMouseX(), display.getMouseY());
                return Physics.intersect(
                        c, (display.getMouseX()/ display.getScale().getX()- display.getShift().getX()) ,
                        (display.getMouseY()/ display.getScale().getY()  - display.getShift().getY())
                );
            }).findFirst().ifPresent(cell -> selectedCell = cell);
        }, ActionType.PRESSED, 1));
        display.addAction(new MouseEvent(() -> {
            selectedCell = null;
        }, ActionType.RELEASED, 1));
    }

    static void enableCarMovingEstimate(List<Cell> cells, List<Car> cars) {
        Thread carScoreCalculation = new Thread(() -> {
            while (true) {
                cars
//                        .stream().peek(Car::wrong)
                        .forEach(car -> cells.stream().filter(cell -> Physics.intersect(cell,
                                car.getPosition().getX(), car.getPosition().getY())).forEach(
                                cell -> {
                                    if (cell.getDirection() == Direction.of(car.getRotation())
                                            || cell.getDirection() == Direction.NONE) {
                                        car.correct();
                                    } else {
                                        car.wrong();
                                    }
                                }));
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        carScoreCalculation.setName("car-score-calculation");
        carScoreCalculation.setDaemon(true);
        carScoreCalculation.start();
    }

    static void updateStateEach(long millis, VisualRepresentationEntity entity1) {
        new Thread(() -> {
            while (true) {
                entity1.calculateNextInstant();
                try {
                    TimeUnit.MILLISECONDS.sleep(millis);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    static void enableControl(VisualRepresentationEntity entity,
                              double retardationSpeed, double maxAcceleration,
                              double rotationSpeed) {
        entity.addAction(() -> {
            entity.setRotation(entity.getRotation() + rotationSpeed);
        }, ActionType.PRESSED, GLFW_KEY_LEFT);
        entity.addAction(() -> {
            entity.setRotation(entity.getRotation() - rotationSpeed);
        }, ActionType.PRESSED, GLFW_KEY_RIGHT);
        entity.addAction(() -> {
            entity.setSpeed(entity.getSpeed().plus(new ThreeVector(
                    maxAcceleration, maxAcceleration, maxAcceleration)));
        }, ActionType.PRESSED, GLFW_KEY_UP);
        entity.addAction(() -> {
            entity.setAcceleration(new ThreeVector(-retardationSpeed, -retardationSpeed, -retardationSpeed));
//            entity.setSpeed(entity.getSpeed().plus(new ThreeVector(-0.1, 0, 0)));
        }, ActionType.PRESSED, GLFW_KEY_DOWN);
        ;
        entity.addAction(() -> {
            if (entity.getSpeed().module() == 0) {
                entity.setSpeed(new ThreeVector(25, 25, 25));
            } else {
                entity.setSpeed(new ThreeVector(0, 0, 0));
            }
        }, ActionType.RELEASED, GLFW_KEY_SPACE);
    }

    private static void stopVisualiseMomentWithDelayForSeconds(Display display,
                                                               int delaySeconds, int pauseDurationSeconds) {
        try {
            TimeUnit.SECONDS.sleep(delaySeconds);

            System.out.println("Stop at: " + Context.uptimeMillis());
            display.setVisualisationMoment(Context.uptimeMillis());
            TimeUnit.SECONDS.sleep(pauseDurationSeconds);

            System.out.println("Resume at: " + Context.uptimeMillis());
            display.visualiseLastMoment();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
