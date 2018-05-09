package com.fuzzygroup.entities;

import com.fuzzygroup.view.Shape;
import com.fuzzygroup.view.StateHistory;
import com.fuzzygroup.view.enums.ColorEnum;
import com.ngeneration.graphic.engine.ThreeVector;

public class CrazyCar extends Car {
    public CrazyCar(ThreeVector position, ThreeVector speed, ThreeVector acceleration, ThreeVector size,
                    Shape shapes, ColorEnum colors, double rotation, double opacity, boolean visible, StateHistory history) {
        super(position, speed, acceleration, size, shapes, colors, rotation, opacity, visible, history);
    }

    public CrazyCar(ThreeVector position, ThreeVector speed, ThreeVector acceleration, ThreeVector size,
                    Shape shapes, ColorEnum colors, boolean visible) {
        super(position, speed, acceleration, size, shapes, colors, 0, 0, visible, new StateHistory());
    }


    @Override
    public void recomputeState() {
//        setSpeed(getSpeed().plus(getAcceleration().multiple(getDisplay().getDeltaTime()));
//        ThreeVector step = getSpeed().multiple(getDisplay().getDeltaTime());
//        ThreeVector.PolarCoordinateSystemVector polarStep = step.toPolar();
//        polarStep.setRadian(getRotation() + Math.PI/2);
//        setPosition(getPosition().plus(
//                polarStep.toFlatCartesianVector()
//        ));
        double a = 5;
        rotation += Math.PI * a * getDisplay().getDeltaTime() * (Math.random() > 0.5?-1:1);
        speed = speed.plus(acceleration.multiple(getDisplay().getDeltaTime()));
        if (speed.getX() < 0) speed = new ThreeVector(0, 0, 0);
        ThreeVector step = speed.multiple(getDisplay().getDeltaTime());
        ThreeVector.PolarCoordinateSystemVector polarStep = step.toPolar();
        polarStep.setRadian(rotation);
        position = position.plus(polarStep.toFlatCartesianVector());
        if (position.getY() > 100) {
            position = new ThreeVector(position.getX(), 0, 0);
        }
        if (position.getY() < 0) {
            position = new ThreeVector(position.getX(), 100, 0);
        }
        if (position.getX() > 100) {
            position = new ThreeVector(0, position.getY(), 0);
        }
        if (position.getX() < 0) {
            position = new ThreeVector(100, position.getY(), 0);
        }
//        position = position.plus(speed.multiple(getDisplay().getDeltaTime()));
        acceleration = new ThreeVector(0, 0, 0);

    }

    private long score;
    private boolean isWrong;

    public void wrong() {
        isWrong = true;
        setColors(ColorEnum.RED);
    }

    public void correct() {
        isWrong = false;
        setColors(ColorEnum.GREEN);
    }

    public long getScore() {
        return score;
    }
}
