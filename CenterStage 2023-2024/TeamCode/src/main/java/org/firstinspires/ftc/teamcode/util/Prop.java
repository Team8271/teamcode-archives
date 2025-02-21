package org.firstinspires.ftc.teamcode.util;

public class Prop {
    int storedX = 0;
    int storedY = 0;
    double storedArea = 0;
    Hardwaremap.fieldSides storedColor;

    public Prop(int x, int y, double area, Hardwaremap.fieldSides color) {
        storedX = x;
        storedY = y;
        storedArea = area;
        storedColor = color;
    }

    public int getX() {
        return storedX;
    }
    public int getY() {
        return storedY;
    }
    public double getArea() {
        return storedArea;
    }
    public Hardwaremap.fieldSides getColor() {return storedColor;}
}
