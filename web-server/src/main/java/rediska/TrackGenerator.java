package rediska;

import org.springframework.stereotype.Service;
import tools.Point;

@Service
public class TrackGenerator {

    private int length = 100;   // длинна трека
    private int incMin = 100;   //
    private int incMax = 200;
    private double widthMin = 150;
    private double widthMax = 300;
    private double dWidth = 50;
    private double angle = Math.PI / 2;

    private Point[][] track;

    // =====================================
    // angle
    public double getAngle() {

        return angle * 180 / Math.PI;
    }

    public void setAngle(double angle) {

        this.angle = angle * Math.PI / 180;
    }

    // =====================================
    // len
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    // =====================================
    // incMin
    public int getIncMin() {
        return incMin;
    }

    public void setIncMin(int incMin) {
        this.incMin = incMin;
    }

    // =====================================
    // incMax
    public int getIncMax() {
        return incMax;
    }

    public void setIncMax(int incMax) {
        this.incMax = incMax;
    }

    // =====================================
    // widthMin
    public double getWidthMin() {
        return widthMin;
    }

    public void setWidthMin(double widthMin) {
        this.widthMin = widthMin;
    }

    // =====================================
    // widthMax
    public double getWidthMax() {
        return widthMax;
    }

    public void setWidthMax(double widthMax) {
        this.widthMax = widthMax;
    }

    // =====================================
    // dWidth
    public double getDWidth() {
        return dWidth;
    }

    public void setDWidth(double dWidth) {
        this.dWidth = dWidth;
    }

    // =====================================

    public Point[][] getTrack() {

        Generate();
        return track;
    }
    // =====================================

    private void Generate() {

        Point p = new Point();
        double previousAngle = 0;
        double newAngle = Math.random() * 2 * Math.PI;
        double len;         // длина отрезка

        if (length < 2) return;
        track = new Point[3][length];
        // высчитываем максимальную ширину дороги, которая не приведёт к внутренним пересечениям
        double maxWidthCalculated = incMin / Math.cos((360 - angle) * Math.PI / (4 * 180));
        widthMax = Math.min(widthMax, maxWidthCalculated);  // Math.min - это не ошибка
        widthMin = Math.min(widthMin, maxWidthCalculated);
        dWidth = Math.min(dWidth, widthMax - widthMin);

        double w = (widthMax + widthMin) / 2;

        for (int i = 0; i < length; i++) {
            // создаём центральную полосу трека
            track[0][i] = p;
            newAngle += angle * (Math.random() - 0.5);
            len = Math.random() * (incMax - incMin) + incMin;

            // создаём перпиндикулярное треку начало и конец
            if (i == 0) previousAngle = newAngle;
            if (i == length - 1) newAngle = previousAngle;

            // ширина
            double dw = (Math.random() - 0.5) * dWidth;
            w += 2 * dw;
            w = Math.min(w, widthMax);
            w = Math.max(w, widthMin);

            track[1][i] = Point.getPointByAngle(p, w / 2, (newAngle + previousAngle - Math.PI) / 2).round();
            track[2][i] = Point.getPointByAngle(p, w / 2, (newAngle + previousAngle + Math.PI) / 2).round();

            p = Point.getPointByAngle(p, len, newAngle).round();
            previousAngle = newAngle;
        }
    }
}


