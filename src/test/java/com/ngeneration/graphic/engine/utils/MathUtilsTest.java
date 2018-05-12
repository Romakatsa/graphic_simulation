package com.ngeneration.graphic.engine.utils;

import com.ngeneration.graphic.engine.Vector;
import org.junit.Test;

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
}