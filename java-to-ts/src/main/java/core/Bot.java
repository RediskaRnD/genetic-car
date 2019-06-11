package core;

public class Bot extends Player {

    public Bot(String name) {
        super(name);
    }

    public int getDirection() {
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
        if (min < car.height * 2) {
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
}
