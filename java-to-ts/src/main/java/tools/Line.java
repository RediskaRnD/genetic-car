package tools;

public class Line {

    public Point p1;
    public Point p2;

    public Line(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    // =====================================
    //поиск точки пересечения
    public static Point crossPoint(double a1, double b1, double c1, double a2, double b2, double c2) {
        double d = a1 * b2 - b1 * a2;
        double dx = -c1 * b2 + b1 * c2;
        double dy = -a1 * c2 + c1 * a2;

        return new Point(dx / d, dy / d);
    }

    // =====================================
    //коэффициенты уравнения прямой вида: Ax + By + C = 0
    public static double[] Equation(Line l) {
        double a = l.p2.y - l.p1.y;
        double b = l.p1.x - l.p2.x;
        double c = -l.p1.x * (l.p2.y - l.p1.y) + l.p1.y * (l.p2.x - l.p1.x);
        return new double[]{a, b, c};
    }

    public static double[] Equation(Point p1, Point p2) {
        double a = p2.y - p1.y;
        double b = p1.x - p2.x;
        double c = -p1.x * (p2.y - p1.y) + p1.y * (p2.x - p1.x);
        return new double[]{a, b, c};
    }

    // =====================================
    // TODO почему-то не работает оверлоад в JS
    // поиск точки пересечения отрезков
//    public static Point getCrossPoints(Line l1, Line l2) {
//        if (isCrossing(l1, l2) == true) {
//            double[] abc = Equation(l1);
//            double[] abc2 = Equation(l2);
//            return crossPoint(abc[0], abc[1], abc[2], abc2[0], abc2[1], abc2[2]);
//        }
//        return null;
//    }

    public static Point getCrossPoints(Point p1, Point p2, Point p3, Point p4) {
        if (isCrossing(p1, p2, p3, p4) == true) {
            double[] abc = Equation(p1, p2);
            double[] abc2 = Equation(p3, p4);
            return crossPoint(abc[0], abc[1], abc[2], abc2[0], abc2[1], abc2[2]);
        }
        return null;
    }

    // =====================================
    // Пересекаются ли отрезки?
    public static boolean isCrossing(Line l1, Line l2) {
        double v1 = Point.vm(Point.sub(l2.p2, l2.p1), Point.sub(l1.p1, l2.p1));
        double v2 = Point.vm(Point.sub(l2.p2, l2.p1), Point.sub(l1.p2, l2.p1));
        double v3 = Point.vm(Point.sub(l1.p2, l1.p1), Point.sub(l2.p1, l1.p1));
        double v4 = Point.vm(Point.sub(l1.p2, l1.p1), Point.sub(l2.p2, l1.p1));
        return (v1 * v2 < 0) && (v3 * v4 < 0);
    }

    public static boolean isCrossing(Point p1, Point p2, Point p3, Point p4) {
        double v1 = Point.vm(Point.sub(p4, p3), Point.sub(p1, p3));
        double v2 = Point.vm(Point.sub(p4, p3), Point.sub(p2, p3));
        double v3 = Point.vm(Point.sub(p2, p1), Point.sub(p3, p1));
        double v4 = Point.vm(Point.sub(p2, p1), Point.sub(p4, p1));
        return (v1 * v2 < 0) && (v3 * v4 < 0);
    }

    // =====================================
    // Длинна отрезка
    //A = √(X²+Y²) = √ ((X2-X1)²+(Y2-Y1)²)
    public static double distance(Point p1, Point p2) {
        return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }
    // =====================================
}
