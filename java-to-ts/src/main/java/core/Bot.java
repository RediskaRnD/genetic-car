package core;

public class Bot extends Player {

    private int algorithm = 0;

    public Bot(String name, Car car, int algorithm) {
        super(name, car);
        this.algorithm = algorithm;
    }

    public int getDirection() {
        switch (algorithm) {
            case 0:
                return algorithm_a(1.8);
            case 1:
                return algorithm_a(1.9);
            case 2:
                return algorithm_a(2);
            case 3:
                return algorithm_a(2.1);
            case 4:
                return algorithm_a(2.2);
            case 5:
                return algorithm_b(1.5, 3);
            case 6:
                return algorithm_b(1.6, 3.2);
            case 7:
                return algorithm_b(1.7, 3.4);
            case 8:
                return algorithm_b(1.8, 3.6);
            case 9:
                return algorithm_b(1.9, 3.8);
        }
        return 0;
    }

    private int algorithm_a(double minDistance) {
        double min = 1.0 / 0.0;     //Double.POSITIVE_INFINITY;
        double max = 0;
        int iMin = 0;
        int iMax = 0;
        int i = 0;
        for (Sensor s : car.sensors) {
            double dist = s.getDistance();
            if (max < dist) {
                max = dist;
                iMax = i;
            }
            if (min > dist) {
                min = dist;
                iMin = i;
            }
            ++i;
        }
        // если находимся слишком близко к обочине - рулим в другую сторону
        int dir = 0;
        int len = car.sensors.length;
        if (min < car.height * minDistance) {
            if (iMin < len / 2) {
                dir -= 1;
            } else if (iMin >= len - len / 2) { // удаляем из расчетов средний сенсор при нечетном количестве сенсоров
                dir += 1;
            }
            return dir;
        }

        // ищем самую дальнюю точку и рулим в её сторону
        i = 0;
        for (Sensor s : car.sensors) {
            if (s.getDistance() == max) {
                if (iMax < len / 2) {
                    dir += 1;
                } else if (iMax >= len - len / 2) { // удаляем из расчетов средний сенсор при нечетном количестве сенсоров
                    dir -= 1;
                }
            }
            ++i;
        }
        return dir;
    }

    private int algorithm_b(double distance1, double distance2) {
        double max = 0;
        int iMin = 0;
        int iMax = 0;
        int i = 0;
        for (Sensor s : car.sensors) {
            double dist = s.getDistance();
            if (max < dist) {
                max = dist;
                iMax = i;
            }
        }
        // если находимся слишком близко к обочине - рулим в другую сторону
        int dir = 0;
        int len = car.sensors.length;
        if (len > 1) {
            double d1 = car.sensors[0].getDistance();
            double d2 = car.sensors[len - 1].getDistance();
            if (d1 < d2 && d1 < car.height * distance1) {
                return -1;
            } else if (d1 > d2 && d2 < car.height * distance1) {
                return 1;
            }
        }
        if (len > 3) {
            double d1 = car.sensors[1].getDistance();
            double d2 = car.sensors[len - 2].getDistance();
            if (d1 < d2 && d1 < car.height * distance2) {
                return -1;
            } else if (d1 > d2 && d2 < car.height * distance2) {
                return 1;
            }
        }
        // ищем самую дальнюю точку и рулим в её сторону
        i = 0;
        for (Sensor s : car.sensors) {
            if (s.getDistance() == max) {
                if (iMax < len / 2) {
                    dir += 1;
                } else if (iMax >= len - len / 2) { // удаляем из расчетов средний сенсор при нечетном количестве сенсоров
                    dir -= 1;
                }
            }
            ++i;
        }
        return dir;
    }
}
