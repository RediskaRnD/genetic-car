package core;

import tools.Point;

public class Track {
    private int length;             // длинна трека
    private double incMin;          // минимальная длина одного участка
    private double incMax;          // максимальная длина одного участка
    private double widthMin;        // минимальная ширина трека
    private double widthMax;        // максимальная ширина трека
    private double dWidth;          // максимальное измерение ширины за 1 шаг
    private double angle;           // максимальное отклонение траектории в градусах в какую либо сторону

    public Point[][] p = {};        // массив всех точек трека
    public double xMin = 0;         // минимальное значение X у центральной линии трека
    public double xMax = 0;         // максимальное значение X у центральной линии трека
    public double yMin = 0;         // минимальное значение Y у центральной линии трека
    public double yMax = 0;         // максимальное значение Y у центральной линии трека

    // =====================================
    public Track(int length) {
        this(length, 90, 150, 300, 50, 100, 200);
    }

    public Track(int length, double angle, double widthMin, double widthMax, double dWidth) {
        this(length, angle, widthMin, widthMax, dWidth, 100, 200);
    }

    public Track(int length, double angle, double widthMin, double widthMax, double dWidth, double incMin, double incMax) {
        this.angle = Math.min(angle, 360) * Math.PI / 180;
        this.widthMin = widthMin;
        this.widthMax = widthMax;
        this.dWidth = dWidth;
        this.incMin = incMin;
        this.incMax = incMax;
        Generate(Math.max(2, length));
        SetMinMax();
    }
    // =====================================

    private void SetMinMax() {
        for (int i = 0; i < length; ++i) {
            xMin = Math.min(xMin, p[0][i].x);
            yMin = Math.min(yMin, p[0][i].y);
            xMax = Math.max(xMax, p[0][i].x);
            yMax = Math.max(yMax, p[0][i].y);
        }
    }

    // =====================================
    // length
    public int getLength() {
        return length;
    }

    // =====================================
    // angle
    public double getAngle() {
        return angle * 180 / Math.PI;
    }

    // =====================================
    // incMin
    public double getIncMin() {
        return incMin;
    }

    // =====================================
    // incMax
    public double getIncMax() {
        return incMax;
    }

    // =====================================
    // widthMin
    public double getWidthMin() {
        return widthMin;
    }

    // =====================================
    // widthMax
    public double getWidthMax() {
        return widthMax;
    }

    // =====================================
    // dWidth
    public double getDWidth() {
        return dWidth;
    }
    // =====================================

    public Point[][] getTrack() {
        return this.p;
    }

    public void setTrack(Point[][] p) {
        this.p = p;
        this.length = p[0].length;
        SetMinMax();
    }

    // =====================================
    // TODO Надо сделать непересекающуюся дорогу. Иначе не работает sensor.
    private void Generate(int length) {
        this.p = new Point[3][length];
        this.length = length;
        // высчитываем максимальную ширину дороги, которая не приведёт к внутренним пересечениям
        //double maxWidthCalculated = incMin / Math.cos((360 - angle) * Math.PI / (4 * 180));//* Math.PI / 180
        double maxWidthCalculated = incMin / Math.cos((Math.PI * 2 - angle) / 4);
        widthMax = Math.min(widthMax, maxWidthCalculated);  // Math.min - это не ошибка
        widthMin = Math.min(widthMin, maxWidthCalculated);
        dWidth = Math.min(dWidth, widthMax - widthMin);

        double w = (widthMax + widthMin) / 2;
        Point p = new Point();
        double previousAngle = 0;
        double newAngle = Math.random() * 2 * Math.PI;
        double len;         // длина отрезка
        for (int i = 0; i < length; ++i) {
            // создаём центральную полосу трека
            this.p[0][i] = p;
            len = Math.random() * (incMax - incMin) + incMin;

            // создаём перпиндикулярное треку начало и конец
            if (i == length - 1) {
                newAngle = previousAngle;
            } else {
                newAngle += angle * (Math.random() - 0.5);
                if (i == 0) previousAngle = newAngle;
            }
            // ширина
            double dw = (Math.random() - 0.5) * dWidth;
            w += 2 * dw;
            w = Math.max(Math.min(w, widthMax), widthMin);

            this.p[1][i] = Point.getPointByAngle(p, w / 2, (newAngle + previousAngle - Math.PI) / 2).round();
            this.p[2][i] = Point.getPointByAngle(p, w / 2, (newAngle + previousAngle + Math.PI) / 2).round();

            p = Point.getPointByAngle(p, len, newAngle).round();
            previousAngle = newAngle;
        }
    }
}
