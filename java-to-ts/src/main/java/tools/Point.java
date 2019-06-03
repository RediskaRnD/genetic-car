package tools;

public class Point {

    public double x = 0;
    public double y = 0;

    // =====================================

    public Point() {
    }
    // =====================================

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    // =====================================

    public static double vm(Point p1, Point p2) {
        return p1.x * p2.y - p2.x * p1.y;
    }
    // =====================================

    public static Point sub(Point p1, Point p2) {
        return new Point(p1.x - p2.x, p1.y - p2.y);
    }
    // =====================================

    public static Point sum(Point p1, Point p2) {
        return new Point(p1.x + p2.x, p1.y + p2.y);
    }
    // =====================================

    public Point round() {
        this.x = (int) this.x;
        this.y = (int) this.y;
        return this;
    }
    // =====================================

    public static double angleByPoints(Point p1, Point p2) {
        final double dx = p2.x - p1.x;
        final double dy = p2.y - p1.y;
        final double a = Math.atan2(dy, dx);
        return a > 0 ? a : 2 * Math.PI + a;
    }

    // =====================================
    // угол растёт по часовой стрелке
    public static Point getPointByAngle(Point p, double length, double angle) {
        return new Point(p.x + length * Math.cos(angle), p.y + length * Math.sin(angle));
    }

    // =====================================
    @Override
    // toString - (x, y)
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
    // to String - (x, y) but rounded
    public String toString(int n) {
        n = (int) Math.pow(10, n);
        return "(" + Math.round(x * n) / n + ", " + Math.round(y * n) / n + ")";
    }
}
