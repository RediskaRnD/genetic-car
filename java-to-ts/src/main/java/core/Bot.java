package core;

public class Bot extends Player {

    private int algorithm = 0;

    public Bot(String name, Car car, int algorithm) {
        super(name, car);
        this.algorithm = algorithm;
    }

    public void selectDirection() {
        switch (algorithm) {
            case 0:
                algorithm_b(2, 4);
                return;
            case 1:
                algorithm_b(2.2, 4.5);
                return;
            case 2:
                algorithm_b(2.4, 5);
                return;
            case 3:
                algorithm_b(2.6, 5.5);
                return;
            case 4:
                algorithm_b(2.8, 6);
                return;
//            case 0:
//                algorithm_a(1.8);
//                return;
//            case 1:
//                algorithm_a(1.9);
//                return;
//            case 2:
//                algorithm_a(2);
//                return;
//            case 3:
//                algorithm_a(2.1);
//                return;
//            case 4:
//                algorithm_a(2.2);
//                return;
            case 5:
                algorithm_b(1.5, 3);
                return;
            case 6:
                algorithm_b(1.6, 3.2);
                return;
            case 7:
                algorithm_b(1.7, 3.4);
                return;
            case 8:
                algorithm_b(1.8, 3.6);
                return;
            case 9:
                algorithm_b(1.9, 3.8);
                return;
        }
    }

    private void algorithm_a(double minDistance) {
        keys = 1 << Key.FORWARD.ordinal();
        double min = 1.0 / 0.0;     //Double.POSITIVE_INFINITY;
        double max = 0;
        int iMin = 0;
        int i = 0;
        for (Sensor s : car.sensors) {
            double dist = s.distance;
            if (max < dist) {
                max = dist;
            }
            if (min > dist) {
                min = dist;
                iMin = i;
            }
            ++i;
        }
        // если находимся слишком близко к обочине - рулим в другую сторону
        int len = car.sensors.length;
        if (min < car.width * minDistance) {
            if (iMin < len / 2) {
                keys |= 1 << Key.LEFT.ordinal();
            } else if (iMin >= len - len / 2) { // удаляем из расчетов средний сенсор при нечетном количестве сенсоров
                keys |= 1 << Key.RIGHT.ordinal();
            }
            return;
        }

        // ищем самую дальнюю точку и рулим в её сторону
        i = 0;
        int dir = 0;
        for (Sensor s : car.sensors) {
            if (s.distance == max) {
                if (i < len / 2) {
                    dir += 1;
                } else if (i >= len - len / 2) { // удаляем из расчетов средний сенсор при нечетном количестве сенсоров
                    dir -= 1;
                }
            }
            ++i;
        }
        if (dir < 0) {
            keys |= 1 << Key.LEFT.ordinal();
            return;
        }
        if (dir > 0) {
            keys |= 1 << Key.RIGHT.ordinal();
        }
    }

    private void algorithm_b(double distance1, double distance2) {
        keys = 1 << Key.FORWARD.ordinal();
        // если находимся слишком близко к обочине - рулим в другую сторону
        int dir = 0;
        int len = car.sensors.length;
        if (len > 1) {
            double d1 = car.sensors[0].distance;
            double d2 = car.sensors[len - 1].distance;
            if (d1 < d2 && d1 < car.width * distance1) {
                keys |= 1 << Key.LEFT.ordinal();
                return;
            } else if (d1 > d2 && d2 < car.width * distance1) {
                keys |= 1 << Key.RIGHT.ordinal();
                return;
            }
        }
        if (len > 3) {
            double d1 = car.sensors[1].distance;
            double d2 = car.sensors[len - 2].distance;
            if (d1 < d2 && d1 < car.width * distance2) {
                keys |= 1 << Key.LEFT.ordinal();
                return;
            } else if (d1 > d2 && d2 < car.width * distance2) {
                keys |= 1 << Key.RIGHT.ordinal();
                return;
            }
        }
        // ищем самую дальнюю точку и рулим в её сторону
        double max = 0;
        int i = 0;
        for (Sensor s : car.sensors) {
            double dist = s.distance;
            if (max < dist) {
                max = dist;
            }
            ++i;
        }
        i = 0;
        for (Sensor s : car.sensors) {
            if (s.distance == max) {
                if (i < len / 2) {
                    dir += 1;
                } else if (i >= len - len / 2) { // удаляем из расчетов средний сенсор при нечетном количестве сенсоров
                    dir -= 1;
                }
            }
            ++i;
        }
        if (dir < 0) {
            keys |= 1 << Key.LEFT.ordinal();
            return;
        }
        if (dir > 0) {
            keys |= 1 << Key.RIGHT.ordinal();
        }
    }
}
