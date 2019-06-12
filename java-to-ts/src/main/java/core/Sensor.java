package core;

import tools.Point;
import tools.Line;

public class Sensor {
    private Car car;                        // Машина на которой установлен сенсор
    public double maxDistance;              // расстояние на которое способен видить сенсор
    public Point intersection;              // Точка пересечения луча с объектом(треком)
    public double distance;                 // Расстояние до объекта(треком)
    public double angle;                    // угол под которым установлен сенсор

    // =====================================
    Sensor(Car car, double angle, double maxDistance) {
        this.car = car;
        this.angle = angle;
        this.maxDistance = maxDistance;
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
    private Point updateIntersection() {
        intersection = null;
        Point cp = Point.getPointByAngle(car.getPosition(), maxDistance, angle + car.getAngle());
        int lim = Math.max(Math.abs(car.track.getLength() - 1 - car.stage), car.stage);
        for (int i = 0, j = 1; i < lim; j = -j, i = (j == 1 ? Math.abs(i) + 1 : -i)) {
            int first = car.stage + i;
            int second = car.stage + i + j;
            if (first < 0 || second < 0 || first > car.track.getLength() - 1 || second > car.track.getLength() - 1)
                continue;
            for (int k = 1; k < 3; ++k) {
                intersection = Line.getCrossPoints(car.getPosition(), cp, car.track.p[k][first], car.track.p[k][second]);
                if (intersection != null) return intersection;
            }
        }
        return null;
    }

    // =====================================
    // расстояние от центра машины до препятствия
    private double updateDistance() {
        distance = intersection != null ? Line.distance(car.getPosition(), intersection) : 1.0 / 0.0;
        return distance;
    }

    // =====================================
    // обновление показаний сенсора
    public void update() {
        updateIntersection();
        updateDistance();
    }
}
