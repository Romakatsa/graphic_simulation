package com.fuzzygroup.view.lwjgl_engine;

import com.fuzzygroup.view.enums.ColorEnum;

import static org.lwjgl.opengl.GL11.*;

class Drawer {

    static void rect(double x, double y, double sx, double sy, double rotate,
                             ColorEnum color, double opacity) {
        x = x / 50 - 1;
        y = y / 50 - 1;
        sx = sx / 50 * 0.8;
        sy = sy / 50 * 1.2;
        glPushMatrix();
        chooseColor(color, 0f);
        glTranslated(x, y, 0);
        glRotated((rotate - Math.PI / 2) * 180 / Math.PI, 0, 0, 1);
        x = 0;
        y = 0;
        glBegin(GL_QUADS);
        glVertex2f((float) (x + sx / 2), (float) (y + sy / 2));
        glVertex2f((float) (x + sx / 2), (float) (y - sy / 2));
        glVertex2f((float) (x - sx / 2), (float) (y - sy / 2));
        glVertex2f((float) (x - sx / 2), (float) (y + sy / 2));
        glEnd();
        glPopMatrix();
    }

    static void rect2(double x, double y, double sx, double sy, double rotate,
                              ColorEnum color, double opacity) {
        rect(x, y, sx, sy, rotate, color, opacity);
        triangle2(x, y, sx / 2, sy / 2, rotate, ColorEnum.BLACK, opacity);
    }

    static void circle(double x, double y, double sx, double sy, double rotation,
                               ColorEnum color, double opacity) {
        glPushMatrix();
        {
            chooseColor(color, opacity);
            glTranslated(x, y, 0);
            if (sx < 200) {
                if (sx == 0) {
                    sx = 1;
                }
                if (sy == 0) {
                    sy = 1;
                }
                glEnable(GL_POINT_SMOOTH);
                glPointSize((float) sx);
                glBegin(GL_POINTS);
                glVertex2d(0, 0); // ������ �����
                glEnd();
            } else {
                glBegin(GL_POLYGON);
                {
                    int amountPoints = (int) (0.1 * sx);
                    for (int i = 0; i <= amountPoints; i++) {
                        glVertex2d(sx / 2 * Math.cos((float) i / amountPoints * 2 * Math.PI), sy / 2 * Math.sin((float) i / amountPoints * 2 * Math.PI));
                    }
                }
                glEnd();
            }
        }
        glPopMatrix();
    }

    static void star(double x, double y, double sx, double sy, double rotation,
                             ColorEnum color, double opacity) {
        glPushMatrix();
        {
            chooseColor(color, opacity);
            glTranslated(x, y, 0);
            glRotated(-rotation, 0, 0, 1);
            if (sx < 8) {
                if (sx == 0) {
                    sx = 1;
                }
                glEnable(GL_POINT_SMOOTH);
                glPointSize((float) sx);
                glBegin(GL_POINTS);
                glVertex2d(0, 0);
                glEnd();
            } else {
                glBegin(GL_POLYGON);
                {
                    int amountPoints = 18;
                    for (int i = 0; i <= amountPoints; i++) {
                        double factor = 1;
                        if (i % 2 == 0) {
                            factor = (0.5 + 0.0 * Math.random());
                        }
                        glVertex2d(factor * sx / 2 * Math.cos((float) i / amountPoints * 2 * Math.PI), factor * sy / 2 * Math.sin((float) i / amountPoints * 2 * Math.PI));
                    }
                }
                glEnd();
            }
        }
        glPopMatrix();
    }

    static void triangle(double x, double y, double sx, double sy, double rotate,
                                 ColorEnum color, double opacity) {
        x = x / 50 - 1;
        y = y / 50 - 1;
        sx = sx / 50;
        sy = sy / 50;
        glPushMatrix();
        chooseColor(color, 0f);
        glTranslated(x, y, 0);
        glRotated((rotate - Math.PI / 2) * 180 / Math.PI, 0, 0, 1);
        x = 0;
        y = 0;
        //TRIANGLES
        glBegin(GL_POLYGON);
        glVertex2f((float) (x + 0), (float) (y + sy / 2));
        glVertex2f((float) (x + sx / 3.5), (float) (y));
        glVertex2f((float) (x + sx / 3), (float) (y - sy / 2));
        glVertex2f((float) (x + sx / 5), (float) (y - sy / 2 * 1.1));
        glVertex2f((float) (x - sx / 5), (float) (y - sy / 2 * 1.1));
        glVertex2f((float) (x - sx / 3), (float) (y - sy / 2));
        glVertex2f((float) (x - sx / 3.5), (float) (y));
        glEnd();
        glPopMatrix();
    }

    private static double normalise(double value) {
        return value / 50;
    }

    static void triangle2(double x, double y, double sx, double sy, double rotate,
                                  ColorEnum color, double opacity) {

        double sx1 = sx / 3;
        x = normalise(x) - 1;
        y = normalise(y) - 1;
        sx = normalise(sx);
        sy = normalise(sy) * 1.5;
        sx1 = normalise(sx1);
        glPushMatrix();
        chooseColor(color, 0f);
        glTranslated(x, y, 0);
        glRotated((rotate - Math.PI / 2) * 180 / Math.PI, 0, 0, 1);
//            glEnable( GL_LINE_SMOOTH );
//            glEnable( GL_POLYGON_SMOOTH );
//            glHint( GL_LINE_SMOOTH_HINT, GL_NICEST );
//            glHint( GL_POLYGON_SMOOTH_HINT, GL_NICEST );
        glBegin(GL_TRIANGLES);
        glVertex2f((float) (0), (float) (sy / 2));
        glVertex2f((float) (sx1 / 2), (float) (-sy / 2));
        glVertex2f((float) (-sx1 / 2), (float) (-sy / 2));
        glEnd();
        glPopMatrix();
    }

    static void arrow(double x, double y, double sx, double sy, double rotate,
                              ColorEnum color, double opacity) {
        double sx1 = sx / 3;
        x = normalise(x) - 1;
        y = normalise(y) - 1;
        sx = normalise(sx);
        sy = normalise(sy);

        sx1 = normalise(sx1);
        glPushMatrix();
        chooseColor(color, 0f);
        glTranslated(x, y, 0);
        glRotated((rotate - Math.PI / 2) * 180 / Math.PI, 0, 0, 1);
        glBegin(GL_TRIANGLES);
        glVertex2f((float) (0), (float) (sy / 2));
        glVertex2f((float) (sx1 / 2), (float) (-sy / 2));
        glVertex2f((float) (-sx1 / 2), (float) (-sy / 2));
        glEnd();
        glPopMatrix();

        double sy1 = sy / 2;
        glPushMatrix();
        chooseColor(color, 0f);
        glTranslated(x, y, 0);
        glRotated((rotate - Math.PI / 2) * 180 / Math.PI, 0, 0, 1);
        glBegin(GL_TRIANGLES);
        glVertex2f((float) (0), (float) (sy1));
        glVertex2f((float) (sx / 2), (float) (0));
        glVertex2f((float) (-sx / 2), (float) (0));
        glEnd();
        glPopMatrix();
    }


    private static void chooseColor(ColorEnum color, double opacity) {
        switch (color) {
            case WHITE:
                glColor4d(0.8f, 0.8f, 0.8f, opacity);
                break;
            case RED:
                glColor4d(1.0f, 0.0f, 0.0f, opacity);
                glColor4d(0.733f, 0.223f, 0.168f, opacity);
                break;
            case GREEN:
                glColor4d(0.478f, 0.737f, 0.345f, opacity);
                break;
            case BLUE:
                glColor4d(0.247f, 0.494f, 1.0f, opacity);
                break;
            case BLACK:
                glColor4d(0f, 0.0f, 0.0f, opacity);
                break;
            default:
                glColor4d(color.getRed(), color.getGreen(), color.getBlue(), opacity);
        }
    }
}
