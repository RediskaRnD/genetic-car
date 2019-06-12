package core;

import tools.Line;
import tools.Point;

public class Car {
    private final double maxWheelAngle = Math.PI / 180 * 42;    // максимальный угол поворота колеса
    private final int impactsToDeath = 3;       // количество столкновений на полной скорости которое не переживет машина
    public Track track;                         // трасса
    public Player driver;                       // водитель машины
    private double maxSpeed = 300;              // максимальная скорость
    private int width = 60;                     // длина машины
    public int height = 30;                     // ширина машины
    private double axl = 60;                    // ускорение тачки вперёд

    private double r;                           // расстояние от центра машины до её угловой точки
    private double a;                           // угол Point.angleByPoints(позиция машины, средняя точка машины);
    // видимо они не совпадают, поэтому TODO стоит перепроверить это

    // end readonly
    private Point[] cornerP = {new Point(), new Point(), new Point(), new Point()}; // координаты углов машины

    private Point ackerP = new Point();         // точка аккермана
    private double ackerA = 1.0 / 0.0;          // угол между задней осью и центром тачки относительно ackerP
    private double ackerR = 1.0 / 0.0;          // радиус аккермана для центра тачки

    private Point _position = new Point();      // текущая позиция машины
    private double _carAngle = 0;               // угол машины
    private double _wheelAngle = 0;             // угол колеса машины

    public boolean isReady = false;             // TODO нужен ли он?

    public Object image;                        // картинка машины

    public double speed = 0;                    // текущая скорость машины
    //public int keys = 0;                      // зажатые кнопки управления

    public double time = 0;                     // время заезда
    public double distance = 0;                 // пройденное расстояние
    public int stage = 0;                       // сегмент трэка на котором находится тачка
    public double fuel = 0;                     // оставшееся количество топлива
    public int crashes = 0;                     // сколько раз столкнулись со стеной
    public double durability = 1;               // остаток прочности
    public Sensor[] sensors;                    // сенсоры расстояния
    // =====================================

    public Car(int width, int height, double acceleration, double maxSpeed) {
        this.width = width;
        this.height = height;
        this.axl = acceleration;
        this.maxSpeed = maxSpeed;

        r = Math.sqrt(height * height + width * width) / 2;

        Point midPoint = new Point(width / 2d, height / 2d);
        a = Point.angleByPoints(_position, midPoint);

        initSensors(5, Math.PI * 2 / 3, 500);
    }

    public Car() {
        r = Math.sqrt(height * height + width * width) / 2;

        Point midPoint = new Point(width / 2d, height / 2d);
        a = Point.angleByPoints(_position, midPoint);

        initSensors(5, Math.PI * 2 / 3, 500);
    }

    // =====================================
    // quantity - количество датчиков
    // angle - угол который они охватывают
    private void initSensors(int quantity, double angle, double distance) {
        if (quantity == 1) {
            sensors = new Sensor[]{new Sensor(this, 0, distance)};
        } else {
            // равномерно распределяем лучи
            double segment = angle / (quantity - 1);
            angle /= 2;
            sensors = new Sensor[quantity];
            for (int i = 0; i < quantity; ++i) {
                sensors[i] = new Sensor(this, angle, distance);
                angle -= segment;
            }
        }
    }

    // =====================================
    // позиция машины
    public Point getPosition() {
        return _position;
    }

    private void setPosition(Point p) {
        // находим разницу между положением прошлым и настоящим
        Point dp = Point.sub(p, _position);
        _position = p;
        // добавляем разницу к крайним точкам тачки
        for (int i = 0; i < 4; ++i) {
            cornerP[i] = Point.sum(cornerP[i], dp);
        }
    }

    // =====================================
    // угол поворота колес относительно оси автомобиля
    public double getWheelAngle() {
        return _wheelAngle;
    }

    private void setWheelAngle(double value) {
        _wheelAngle = Math.min(Math.max(value, -maxWheelAngle), maxWheelAngle);
        // если угол поворота колеса меньше 1 градуса то округляем его до 0
        if (Math.abs(this._wheelAngle) < Math.PI / 179) {
            _wheelAngle = 0;
        } else {
            // ищем точку аккермана
            double ackerL = width / Math.tan(_wheelAngle);
            ackerR = Math.sqrt(width * width / 4. + ackerL * ackerL);
            ackerA = _carAngle + (Math.asin(width / (2 * this.ackerR)) + Math.PI / 2) * (_wheelAngle > 0 ? 1 : -1);
            ackerP = Point.getPointByAngle(_position, ackerR, ackerA);
        }
    }

    // =====================================
    // угол машины относительно оси Х
    public double getAngle() {
        return this._carAngle;
    }

    public void setAngle(double value) {
        _carAngle = value;
        // Находим крайние точки тачки.
        cornerP[0] = (Point.getPointByAngle(_position, r, _carAngle + a));
        cornerP[1] = (Point.getPointByAngle(_position, r, _carAngle - a));
        cornerP[2] = (Point.getPointByAngle(_position, r, _carAngle + a + Math.PI));
        cornerP[3] = (Point.getPointByAngle(_position, r, _carAngle - a + Math.PI));
    }

    // =====================================
    // обновление всех параметров машины
    public void update(double dt) {
        updateSpeed(dt);
        updateWheelAngle(dt);
        updatePosition(dt);
        updateSensors();
        // проверка на выезд за трассу
        if (checkCollisions() == true) {
            ++crashes;
            updateDurability();
            recoil(dt);
        }
        updateStageProgress();
    }

    // =====================================
    // расчитываем оставшуюся прочность машины
    private void updateDurability() {

        durability -= speed < 10 ? 0 : speed / maxSpeed / impactsToDeath;
        if (durability < 0.005) durability = 0;
    }

    // =====================================
    // обновление позиции машины
    private void updatePosition(double dt) {
        double s = dt * this.speed;    // пройденный путь за dt
        distance += s;
        if (_wheelAngle != 0) {
            // находим угол на который сместились за dt
            // на какой угол повернулась машина относительно точки аккермана.
            double dAngle = s / ackerR * (_wheelAngle > 0 ? 1 : -1);
            // поворачиваем корпус тачки
            setAngle(_carAngle + dAngle);
            // находим новую позицию тачки
            setPosition(Point.getPointByAngle(ackerP, ackerR, (ackerA + dAngle + Math.PI)));
        } else {
            setPosition(Point.getPointByAngle(_position, speed * dt, _carAngle));
        }
    }

    // =====================================
    // пересчет угла поворота колеса
    // TODO ввести зависимость угла поворота от скорости и времени dt
    private void updateWheelAngle(double dt) {
        // поворачиваем машину вокруг точки аккермана
        if ((driver.keys & (1 << Key.LEFT.ordinal())) > 0) {
            setWheelAngle(_wheelAngle - 2 * Math.PI / 180);
        } else {
            if (_wheelAngle < 0) {
                setWheelAngle(_wheelAngle + 3 * Math.PI / 180);
            }
        }
        // поворачиваем машину вокруг точки аккермана
        if ((driver.keys & (1 << Key.RIGHT.ordinal())) > 0) {
            setWheelAngle(_wheelAngle + 2 * Math.PI / 180);
        } else {
            if (_wheelAngle > 0) {
                setWheelAngle(_wheelAngle - 3 * Math.PI / 180);
            }
        }
    }

    // =====================================
    // пересчет скорости
    private void updateSpeed(double dt) {
        double v = speed;

        if (fuel > 0 && durability > 0) {
            // ускоряемся вперед
            if ((driver.keys & (1 << Key.FORWARD.ordinal())) > 0) {
                if (v < 0) {
                    // тормозим
                    calcSpeed(axl * 2, dt, v, 0);
                } else {
                    // разгоняемся вперед
                    calcSpeed(axl, dt, v, maxSpeed);
                }
            } else {
                //машина медленно останавливается без газа
                if (v > 0) {
                    calcSpeed(-axl * 0.5, dt, 0, v);
                }
            }
            // ускоряемся назад
            if ((driver.keys & (1 << Key.BACK.ordinal())) > 0) {
                if (v > 0) {
                    // тормозим
                    calcSpeed(-axl * 2, dt, 0, v);
                } else {
                    // разгоняемся назад
                    calcSpeed(-axl, dt, -maxSpeed / 5, v);
                }
            } else {
                //машина медленно останавливается без газа
                if (v < 0) {
                    calcSpeed(axl * 0.5, dt, v, 0);
                }
            }
        } else {
            //машина медленно останавливается без топлива
            if (v > 0) {
                calcSpeed(-axl * 0.5, dt, 0, v);
            } else if (v < 0) {
                calcSpeed(axl * 0.5, dt, v, 0);
            }
        }
    }

    // =====================================
    // пересчитываем скорость машины
    private void calcSpeed(double axl, double dt, double vMin, double vMax) {
        double v = speed + axl * dt;
        speed = Math.max(Math.min(v, vMax), vMin);
    }

    // =====================================
    // обновляем показания сенсоров
    private void updateSensors() {
        for (Sensor s : sensors) s.update();
    }

    // =====================================
    // требуется ли анимация машины
    public boolean isRequestAnimation() {
        // машина не двигается и колёса стоят ровно, останавливаем анимацию
        return (driver.keys != 0) || (speed != 0) || (_wheelAngle != 0);
    }

    // =====================================
    // проверка на выезд за трассу
    private boolean checkCollisions() {
        // считаем что +/-2 зебры - это зона проверки столкновений
        int iMin = Math.max(stage - 2, 0);
        int iMax = Math.min(stage + 2, track.getLength() - 1);
        // проверяем обе стороны
        for (int i = 1; i < 3; ++i) {
            for (int j = iMin; j < iMax; ++j) {
                if (Line.isCrossing(track.p[i][j], track.p[i][j + 1], cornerP[0], cornerP[1]) == true) return true;
                if (Line.isCrossing(track.p[i][j], track.p[i][j + 1], cornerP[1], cornerP[2]) == true) return true;
                if (Line.isCrossing(track.p[i][j], track.p[i][j + 1], cornerP[2], cornerP[3]) == true) return true;
                if (Line.isCrossing(track.p[i][j], track.p[i][j + 1], cornerP[3], cornerP[0]) == true) return true;
            }
        }
        // надо будет потом найти место столкновения
        return false;
    }

    // =====================================
    // проверяем между какими зебрами находимся
    private void updateStageProgress() {
        int st = stage;
        // проверка следующей зебры
        if (stage < track.getLength() - 1) {
            st = stage + 1;
            // проверяем диагонали на пересечение зебры
            if (checkStagePass(st) == true) return;
        }
        // проверка на случай если мы едем задом наперёд
        if (stage > 0) {
            st = stage - 1;
            // проверяем диагонали на пересечение зебры
            if (checkStagePass(st) == true) return;
        }
    }

    // =====================================
    // проверка на пересечение сегмента дороги с диагоналями машины
    private boolean checkStagePass(int stage) { // TODO какой тут stage имеется в виду?
        boolean a = Line.isCrossing(track.p[1][stage], track.p[2][stage], cornerP[0], cornerP[2]);
        boolean b = Line.isCrossing(track.p[1][stage], track.p[2][stage], cornerP[1], cornerP[3]);
        if (a || b) {
            this.stage = stage;
            return true;
        }
        return false;
    }

    // =====================================
    // отскок машины от препятствия
    private void recoil(double dt) {
        setPosition(Point.getPointByAngle(_position, speed * dt * 2, _carAngle - Math.PI));
        speed = -speed / 3;
    }

    // =====================================
    // подготовка к старту
    public void restart() {
        //Utils.debug("Restart");
        Point p = new Point();
        setPosition(p);
        setAngle(Point.angleByPoints(track.p[0][0], track.p[0][1]));
        setWheelAngle(0);
        speed = 0;
        time = 0;
        distance = 0;
        stage = 0;
        crashes = 0;
        fuel = 1;
        durability = 1;
        isReady = true;
    }
}
