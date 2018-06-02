package com.ngeneration.graphic.engine.utils;

import com.ngeneration.graphic.engine.Vector;
import org.junit.Test;

import java.util.ArrayList;

import static com.ngeneration.graphic.engine.utils.MathUtils.loopValueInBounds;
import static org.junit.Assert.*;

public class MathUtilsTest {
    @Test
    public void testIntersection() {
        Vector p1 = new Vector(1, 2);
        Vector p2 = new Vector(-1, -1);
        Vector p3 = new Vector(-1, 2);
        Vector p4 = new Vector(1, -1);
        Vector intersection = MathUtils.intersection(p1, p2, p3, p4);
        System.out.println("intersection = " + intersection);

    }
    @Test
    public void testIntersection2() {
        Vector p1 = new Vector(0, 0);
        Vector p2 = new Vector(0, -100);
        Vector p3 = new Vector(-50, -10);
        Vector p4 = new Vector(50, -10);
        Vector intersection = MathUtils.intersection(p1, p2, p3, p4);
        System.out.println("intersection = " + intersection);

    }

    @Test
    public void testIntersection3() {
        Vector p1 = new Vector(10, 0);
        Vector p2 = new Vector(10, 10);
        Vector p3 = new Vector(-10, -10);
        Vector p4 = new Vector(0, -10);
        Vector intersection = MathUtils.intersection(p1, p2, p3, p4);
        System.out.println("intersection = " + intersection);

    }

    @Test
    public void loopValue() {
        assertEquals(0, MathUtils.loopValueInBounds(0, 0, 10));
        assertEquals(1, MathUtils.loopValueInBounds(1, 0, 10));
        assertEquals(10, MathUtils.loopValueInBounds(-1, 0, 10));
        assertEquals(0, MathUtils.loopValueInBounds(11, 0, 10));
        assertEquals(10, MathUtils.loopValueInBounds(10, 0, 10));
    }

    @Test
    public void magic() {
        double lowX = 0;
        ArrayList<Vector> points = new ArrayList<>();
        points.add(new Vector(-1, 0));
        points.add(new Vector(1, 5));
        int i = 0;



        double pointX = points.get(0).getX();
        double pointY = points.get(0).getY();
        Vector prevPoint = points.get(loopValueInBounds(i - 1, 0, points.size() - 1));
        Vector nextPoint = points.get(loopValueInBounds(i + 1, 0, points.size() - 1));
        double deltaX = Math.abs(nextPoint.getX() - pointX);
        double deltaY =  Math.abs(nextPoint.getY() - pointY);
        System.out.println(new Vector(lowX, pointY + Math.abs(pointX - lowX) / deltaX * deltaY));
    }
}