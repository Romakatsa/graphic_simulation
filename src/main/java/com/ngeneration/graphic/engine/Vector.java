package com.ngeneration.graphic.engine;


import com.ngeneration.graphic.engine.utils.MathUtils;

import static java.lang.Math.*;

public class Vector {
    private double x;
    private double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector plus(Vector term) {
        return new Vector(this.x + term.getX(),
                this.y + term.getY());
    }

    public Vector minus(Vector subtrahend) {
        return new Vector(x - subtrahend.x, y - subtrahend.y);
    }

    public Vector coordinatewiseMultiplication(Vector factor) {
        return new Vector(this.x * factor.getX(),
                this.y * factor.getY());
    }

    public Vector multiple(double factor) {
        return new Vector(x * factor, y * factor);
    }

    public Vector divide(double number) {
        return new Vector(x / number, y / number);
    }

    public Vector divide(Vector divider) {
        return new Vector(x / divider.x, y / divider.y);
    }

    public double module() {
        return abs(pow(x * x + y * y, 0.5));
    }

    public Vector clone() {
        return new Vector(x, y);
    }


    public Vector normalize() {
        return new Vector(x / this.module(), y / this.module());
    }

    public double angle() {
        double angle = 0;
        Vector norma = this.normalize();
        double innerAngle = atan(abs(norma.y / norma.x));
        if (x >= 0 && y >= 0) {
            angle = innerAngle;
        } else if (x >= 0 && y < 0) {
            angle = 2 * PI - innerAngle;
        } else if (x < 0 && y >= 0) {
            angle = PI / 2 - innerAngle + PI / 2;
        } else if (x < 0 && y < 0) {
            angle = innerAngle + PI;
        } else {
            System.err.println("Wrong angle value: " + innerAngle);
        }
        return angle;
    }

    public Vector projectOn(Vector vector) {
        double angle = this.angle();
        double angle2 = vector.angle();
        double deltaAngle = angle - angle2;
        if (abs(deltaAngle) <= PI / 2) {
            return new PolarCoordinateSystemVector(vector.angle(), abs(cos(deltaAngle) * this.module()))
                    .toFlatCartesianVector();
        } else {
            return new PolarCoordinateSystemVector(vector.angle() + PI, abs(cos(deltaAngle) * this.module()))
                    .toFlatCartesianVector();
        }
    }

    public Vector projectRestOn(Vector vector) {
        double angle = this.angle();
        double angle2 = vector.angle();
        double deltaAngle = (angle - angle2) % (2 * PI);
        double resultAngle = vector.angle() + PI / 2;
        if (deltaAngle > PI || (deltaAngle < 0 && deltaAngle > -PI)) {
            resultAngle *= -1;
        }
        return new PolarCoordinateSystemVector(resultAngle, abs(sin(deltaAngle) * this.module()))
                .toFlatCartesianVector();
    }

    public PolarCoordinateSystemVector toPolar() {
        double r;
        double phi;
        r = sqrt(x * x + y * y);
        if (x == 0) {
            if (y == 0) {
                phi = 0;
            } else if (y > 0) {
                phi = PI / 2;
            } else {
                phi = 3 * PI / 2;
            }
        } else if (x > 0) {
            if (y >= 0) {
                phi = atan(y / x);
            } else {
                phi = atan(y / x) + 2 * PI;
            }
        } else {
            phi = atan(y / x) + PI;
        }
        return new PolarCoordinateSystemVector(phi, r);
    }


    public static class PolarCoordinateSystemVector {

        private double radian;
        private double module;

        public PolarCoordinateSystemVector(double radian, double module) {
            this.radian = radian;
            this.module = module;
        }

        public Vector toFlatCartesianVector() {
            return new Vector(cos(radian) * module,
                    sin(radian) * module);
        }

        public Direction getDirection() {
            //todo
//            double phi = (PI * 2 + radian) % (2 * PI);
            return Direction.of(radian);
        }

        public double getRadian() {
            return radian;
        }

        public void setRadian(double radian) {
            this.radian = radian;
        }

        public double getModule() {
            return module;
        }

        public void setModule(double module) {
            this.module = module;
        }


    }

    @Override
    public String toString() {
        return toStringPrecisely(3);
    }

    public String toStringPrecisely(int amountNumbersAfterComma) {
        return "(" + MathUtils.round(x, amountNumbersAfterComma)
                + "; " + MathUtils.round(y, amountNumbersAfterComma) + ")";
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    private static final Vector zero = new Vector(0, 0);

    public static Vector zero() {
        return zero;
    }

    private static final Vector one = new Vector(1, 1);

    public static Vector one() {
        return one;
    }

    public static Vector diag(double x) {
        return new Vector(x, x);
    }
}
