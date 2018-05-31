package org.shadowrun.models;

public class RectangleF {

    public double x;
    public double y;
    public double width;
    public double height;

    public RectangleF(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean contains(double x, double y) {
        return (x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height);
    }

}
