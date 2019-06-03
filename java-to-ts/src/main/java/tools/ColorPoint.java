package tools;

public class ColorPoint extends Point {

    public String color = "black";

    public ColorPoint() {
    }

    public ColorPoint(double x, double y, String color) {
        super(x, y);
        this.color = color;
    }
}
