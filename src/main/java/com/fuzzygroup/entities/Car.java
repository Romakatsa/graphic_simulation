package com.fuzzygroup.entities;

import com.fuzzygroup.view.*;
import com.fuzzygroup.view.enums.ColorEnum;
import com.ngeneration.graphic.engine.ThreeVector;

public class Car extends VisualRepresentationEntity {
    private boolean fix;

    public Car(ThreeVector position, ThreeVector speed, ThreeVector acceleration, ThreeVector size,
               Shape shapes, ColorEnum colors, double rotation, double opacity, boolean visible, StateHistory history) {
        super(position, speed, acceleration, size, shapes, colors, rotation, opacity, visible, history);
    }

    public Car(ThreeVector position, ThreeVector speed, ThreeVector acceleration, ThreeVector size,
               Shape shapes, ColorEnum colors, boolean visible) {
        super(position, speed, acceleration, size, shapes, colors, 0, 0, visible, new StateHistory());
    }

    @Override
    public void recomputeState() {
        speed = speed.plus(acceleration.multiple(getDisplay().getDeltaTime()));
        if (speed.getX() < 0) speed = new ThreeVector(0, 0, 0);
        ThreeVector step = speed.multiple(getDisplay().getDeltaTime());
        ThreeVector.PolarCoordinateSystemVector polarStep = step.toPolar();
        polarStep.setRadian(rotation);
        position = position.plus(polarStep.toFlatCartesianVector());
        double highBound = 500;
        if (inLoopRoom) {
            if (position.getY() > highBound) {
                position = new ThreeVector(position.getX(), 0, 0);
            }
            if (position.getY() < 0) {
                position = new ThreeVector(position.getX(), highBound, 0);
            }
            if (position.getX() > highBound) {
                position = new ThreeVector(0, position.getY(), 0);
            }
            if (position.getX() < 0) {
                position = new ThreeVector(highBound, position.getY(), 0);
            }
        }
//        position = position.plus(speed.multiple(getDisplay().getDeltaTime()));
        acceleration = new ThreeVector(0, 0, 0);

        if (!isWrong) {
            score++;
        }
        if (fix) {
            getDisplay().setShift(
                    this.getPosition().minus(new ThreeVector(50, 50, 0)
                            .divide(getDisplay().getScale())).multiple(-1));
        }
    }

    private long score;
    private boolean isWrong;
    private boolean inLoopRoom = true;

    public void setInLoopRoom(boolean inLoopRoom) {
        this.inLoopRoom = inLoopRoom;
    }

    private ColorEnum normalColor = ColorEnum.DARK_GREEN;
    private ColorEnum warnColor = ColorEnum.RED;

    public void wrong() {
        isWrong = true;
        setColors(warnColor);
    }

    public void correct() {
        isWrong = false;
        setColors(normalColor);
    }

    public long getScore() {
        return score;
    }

    public void fixInCenter(boolean fix) {
        this.fix = fix;
    }
}
