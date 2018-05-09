package com.fuzzygroup.view.input;

import com.ngeneration.graphic.engine.ThreeVector;

public class Mouse {
    private ThreeVector lastMousePosition;

    private double x;

    private double y;
    private boolean leftButtonDown;
    private boolean rightButtonDown;
    private boolean centerButtonDown;

    public boolean isButtonDown(int key) {
        switch (key) {
            case 0:
                return isLeftButtonDown();
            case 1:
                return isRightButtonDown();
            case 2:
                return isCenterButtonDown();
        }
        return false;
    }

    public ThreeVector getLastMousePosition() {
        return lastMousePosition;
    }

    public void setLastMousePosition(ThreeVector lastMousePosition) {
        this.lastMousePosition = lastMousePosition;
    }

    public boolean isLeftButtonDown() {
        return leftButtonDown;
    }

    public void setLeftButtonDown(boolean leftButtonDown) {
        this.leftButtonDown = leftButtonDown;
    }

    public boolean isRightButtonDown() {
        return rightButtonDown;
    }

    public void setRightButtonDown(boolean rightButtonDown) {
        this.rightButtonDown = rightButtonDown;
    }

    public boolean isCenterButtonDown() {
        return centerButtonDown;
    }

    public void setCenterButtonDown(boolean centerButtonDown) {
        this.centerButtonDown = centerButtonDown;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
