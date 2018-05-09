package com.ngeneration.graphic.engine;

import com.ngeneration.graphic.engine.utils.MathUtils;

import java.io.Serializable;

import static java.lang.Math.*;

public class ThreeVector implements Serializable {

    private double x;
    private double y;
    private double z;

    public ThreeVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public ThreeVector plus(ThreeVector term) {
        return new ThreeVector(this.x + term.getX(),
                this.y + term.getY(),
                this.z + term.getZ());
    }

    public ThreeVector minus(ThreeVector subtrahend) {
        return new ThreeVector(x - subtrahend.x, y - subtrahend.y, z - subtrahend.z);
    }

    public ThreeVector coordinatewiseMultiplication(ThreeVector factor) {
        return new ThreeVector(this.x * factor.getX(),
                this.y * factor.getY(),
                this.z * factor.getZ());
    }

    public ThreeVector multiple(double factor) {
        return new ThreeVector(x * factor, y * factor, z * factor);
    }

    public ThreeVector divide(double number) {
        return new ThreeVector(x / number, y / number, z / number);
    }

    public ThreeVector divide(ThreeVector divider) {
        return new ThreeVector(x / divider.x, y / divider.y, z / divider.z);
    }

    public double module() {
        return abs(pow(x * x + y * y + z * z, 0.5));
    }

    public ThreeVector clone() {
        return new ThreeVector(x, y, z);
    }


    public ThreeVector normalize() {
        return new ThreeVector(x / this.module(), y / this.module(), z / this.module());
    }

    public double angle() {
        double angle = 0;
        ThreeVector norma = this.normalize();
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

    public ThreeVector projectOn(ThreeVector vector) {
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

    public ThreeVector projectRestOn(ThreeVector vector) {
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

        public ThreeVector toFlatCartesianVector() {
            return new ThreeVector(cos(radian) * module,
                    sin(radian) * module,
                    0);
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
        return "vector(" + MathUtils.round(x, amountNumbersAfterComma)
                + "; " + MathUtils.round(y, amountNumbersAfterComma)
                + "; " + MathUtils.round(z, amountNumbersAfterComma) + ")";
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
