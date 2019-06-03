package core;

import tools.Point;
import tools.Line;

public class Sensor {
    public double maxDistance;              // расстояние на которое способен видить сенсор
    private Car car;                        // Машина на которой установлен сенсор
    private Track track;                    // Трек по которой едет машина
    public Point intersection;              // Точка пересечения луча с треком
    public double angle;                    // угол под которым установлен сенсор
    private double distance;                // расстояние до препятствия (вычисляется)

    // =====================================
    Sensor(Car car, Track track, double angle, double distance) {
        this.car = car;
        this.track = track;
        this.angle = angle;
        this.maxDistance = distance;
    }

    // =====================================
    // Точка пересечения луча с треком
    // пример работы алгоритма: stage = 2, track.len = 10;
    // 2, 3 (first, second)
    // 2, 1
    // 3, 4
    // 1, 0
    // 4, 5
    // 5, 6
    // 6, 7
    // 7, 8
    // 8, 9
    public Point getIntersection() {
        intersection = null;
        Point cp = Point.getPointByAngle(car.getPosition(), maxDistance, angle + car.getAngle());
        int lim = Math.max(Math.abs(track.len - 1 - car.stage), car.stage);
        for (int i = 0, j = 1; i < lim; j = -j, i = (j == 1 ? Math.abs(i) + 1 : -i)) {
            int first = car.stage + i;
            int second = car.stage + i + j;
            if (first < 0 || second < 0 || first > track.len - 1 || second > track.len - 1) continue;
            for (int k = 1; k < 3; k++) {
                intersection = Line.getCrossPoints(car.getPosition(), cp, track.p[k][first], track.p[k][second]);
                if (intersection != null) return intersection;
            }
        }
        return null;
    }

    // =====================================
    // расстояние от центра машины до препятствия
    public double getDistance() {
        if (intersection != null) {
            return Line.distance(car.getPosition(), intersection);
        }
        return 1.0 / 0.0;
    }
}
