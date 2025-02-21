package org.firstinspires.ftc.teamcode;

public class XyhVector {

    public double x;
    public double y;
    public double h;

    public XyhVector(double x, double y, double h)
    {
        this.x = x;
        this.y = y;
        this.h = h;
    }

    public XyhVector(XyhVector copyVector)
    {
        this.x = copyVector.x;
        this.y = copyVector.y;
        this.h = copyVector.h;
    }
}
