import {Point} from "./tools/Point";
import {Track} from "./core/Track";
import {Car} from "./core/Car";
import {ColorPoint} from "./tools/ColorPoint";
import {Utils} from "./core/Utils";
import {Line} from "./tools/Line";
import {Global} from "./core/Global";
import {Player} from "./core/Player";
import {Key} from "./core/Key";
import {Bot} from "./core/Bot";

// var core = new Bezier([1,2,3,4]);
// =====================================================================================================================

let log: HTMLElement;
let vars: HTMLElement;
let input: HTMLInputElement;
let cnv: HTMLCanvasElement;
let ctx: CanvasRenderingContext2D;

let lastClickedTarget: HTMLElement;

//let Bezier: typeof BezierJs.Bezier;

const eventTrackOnLoad: CustomEvent = new CustomEvent("onLoad");

let lastTimeTick: number = 0;
let fps: number = 0;

// =====================================================================================================================

// Canvas
let scale: number = 1;                          // Масштаб
let offset: Point = new Point();                // Смещение видимой области (он логический! - масштабируется согласно scale)
let virtualMousePosition: Point = new Point();  // Положение мышки в виртуальной системе координат

// Линии
let isMouseDown: boolean = false;
let mouseDownPoint: Point = new Point();
const pointsOfLines: Point[] = [];
// Точки пересечения
const crossPointsSelf: ColorPoint[] = [];
let crossPointsWithCurve: ColorPoint[] = [];


// =====================================================================================================================
// запрос на получения трека
function getTrack(): void {

    Utils.debug("getTrack");

    const Http = new XMLHttpRequest();
    let url;
    if (window.location.hostname === "localhost") {
        url = 'http://localhost/getdata';
    } else {
        url = 'http://95.22.195.62/getdata';
    }
    Http.open("GET", url);
    Http.responseType = "json";
    Http.onload = () => {
        if (Http.readyState === Http.DONE) {
            if (Http.status === 200) {
                Global.track = new Track(0);
                Global.track.setTrack(Http.response.track);
                //Global.track = new Track(100);
                // обновляем у всех трассу
                for (let p of Global.players) {
                    p.car.track = Global.track;
                }
                for (let b of Global.bots) {
                    b.car.track = Global.track;
                }
                document.dispatchEvent(eventTrackOnLoad);
            }
        }
    };
    Http.send();
}

// =====================================================================================================================
// Полный поиск точек пересечения линий с новой кривой
function researchCrossPointsWithCurve(): void {

    // обнуляем старые точки
    crossPointsWithCurve = [];

    // проверка пересечений с кривой
    if (Global.track) {
        const tLen = Global.track.getLength();
        if (tLen < 2) return;

        // поиск ведем по левой и правой сторонам трека
        for (let tr = 1; tr < 3; ++tr) {
            // отсекаем НЕпарную точку
            const numOfPoints = pointsOfLines.length - pointsOfLines.length % 2;
            // идём по всем точкам линий
            for (let i = 0; i < numOfPoints - 1; i += 2) {
                for (let j = 0; j < tLen - 1; ++j) {
                    let p = Line.getCrossPoints(
                        Global.track.p[tr][j],
                        Global.track.p[tr][j + 1],
                        pointsOfLines[i],
                        pointsOfLines[i + 1]
                    );
                    if (p) {
                        const cp = new ColorPoint(p.x, p.y, '#' + Math.random().toString(16).slice(-6));
                        crossPointsWithCurve.push(cp);
                    }
                }
            }
        }
    }
}

// =====================================================================================================================
// отрисовываем все точки пересечения
function drawCrossPoints(): void {

    for (let i = 0; i < crossPointsSelf.length; ++i) {
        const p = crossPointsSelf[i];
        const tp = logicalToPhysical(p);
        ctx.beginPath();
        ctx.arc(tp.x, tp.y, 6, 0, 2 * Math.PI);
        ctx.fillStyle = p.color;
        ctx.fill();
        ctx.stroke();
    }

    for (let i = 0; i < crossPointsWithCurve.length; ++i) {
        const p = crossPointsWithCurve[i];
        const tp = logicalToPhysical(p);
        ctx.beginPath();
        ctx.arc(tp.x, tp.y, 10, 0, 2 * Math.PI);
        ctx.fillStyle = p.color;
        ctx.fill();
        ctx.stroke();
    }
}

// =====================================================================================================================
// отрисовываем все линии
function drawLines(): void {

    ctx.lineWidth = 1;
    ctx.strokeStyle = "black";

    let numOfPoints = pointsOfLines.length;
    for (let i = 0; i < numOfPoints; ++i) {
        if (i % 2 == 0) {
            ctx.beginPath();
        }
        let tp = logicalToPhysical(pointsOfLines[i]);
        ctx.lineTo(tp.x, tp.y);
        if (i % 2 == 1) {
            ctx.stroke();
        }
    }
}

// =====================================================================================================================
// отрисовка трека
function drawTrack(track: Track): void {

    if (!track) return;
    let p: Point[][] = track.p;
    // рисуем обочину
    ctx.lineWidth = Math.round(10 * scale);
    //ctx.strokeStyle = "#444444";
    if (Global.enableBlindMode == true) {
        return;
    }
    //     ctx.strokeStyle = "#9af9b4";
    // } else {
    ctx.setLineDash([]);    // solid line
    ctx.strokeStyle = "#444444";
    // }
    // for (let tr = 1; tr < 3; ++tr) {
    //     ctx.beginPath();
    //     // for (let i = 0; i < track.len; ++i) {
    //     //
    //     //     // var curve = new Bezier(150,40 , 80,30 , 105,150);
    //     //     //
    //     //     //
    //     //     // var draw = function() {
    //     //     //     drawSkeleton(curve);
    //     //     //     drawCurve(curve);
    //     //     // }
    //     //     let tp = logicalToPhysical(p[tr][i]);
    //     //     ctx.lineTo(tp.x, tp.y);
    //     // }
    //     for (let i = car.stage; i < track.len; ++i) {
    //         let tp = logicalToPhysical(p[tr][i]);
    //         ctx.lineTo(tp.x, tp.y);
    //         if (car.stage < i) {
    //
    //         }
    //         if (tp.x < 0 || tp.x > cnv.width || tp.y < 0 || tp.y > cnv.height) {
    //             ctx.stroke();
    //             ctx.beginPath();
    //
    //             break;
    //         }
    //     }
    //     ctx.stroke();
    // }

    ctx.beginPath();
    // ищем левую часть вышедшую за экран
    let cp: Point = physicalToLogical(new Point(cnv.width, cnv.height));
    let bPrevIn: boolean | undefined = undefined;
    for (let i = 0; i < track.getLength(); ++i) {
        let tp = p[1][i];
        if (tp.x < -offset.x || tp.x > cp.x || tp.y < -offset.y || tp.y > cp.y) {
            // мы за границей экрана
            if (bPrevIn == true) {
                tp = logicalToPhysical(tp);
                ctx.lineTo(tp.x, tp.y);
            }
            bPrevIn = false;
        } else {
            // мы внутри экрана
            if (bPrevIn == false) {
                // а были за границей
                tp = logicalToPhysical(p[1][i - 1]);
                ctx.moveTo(tp.x, tp.y);
            }
            tp = logicalToPhysical(p[1][i]);
            ctx.lineTo(tp.x, tp.y);
            bPrevIn = true;
        }
    }
    ctx.stroke();
    ctx.beginPath();
    bPrevIn = undefined;
    for (let i = 0; i < track.getLength(); ++i) {
        let tp = p[2][i];
        if (tp.x < -offset.x || tp.x > cp.x || tp.y < -offset.y || tp.y > cp.y) {
            // мы за границей экрана
            if (bPrevIn == true) {
                tp = logicalToPhysical(tp);
                ctx.lineTo(tp.x, tp.y);
            }
            bPrevIn = false;
        } else {
            // мы внутри экрана
            if (bPrevIn == false) {
                // а были за границей
                tp = logicalToPhysical(p[2][i - 1]);
                ctx.moveTo(tp.x, tp.y);
            }
            tp = logicalToPhysical(p[2][i]);
            ctx.lineTo(tp.x, tp.y);
            bPrevIn = true;
        }
    }
    ctx.stroke();

    // рисуем зебру
    ctx.lineWidth = 1;
    ctx.strokeStyle = "red";
    for (let i = 0; i < track.getLength(); ++i) {
        ctx.beginPath();
        let tp = logicalToPhysical(p[1][i]);
        ctx.lineTo(tp.x, tp.y);
        tp = logicalToPhysical(p[2][i]);
        ctx.lineTo(tp.x, tp.y);
        if (Global.players[0].car.stage - 2 <= i && i <= Global.players[0].car.stage + 2) {
            ctx.strokeStyle = "red";
        } else {
            ctx.strokeStyle = "black";
        }
        ctx.stroke();
    }
    // // рисуем начало трасс
    // ctx.beginPath();
    // let tp = logicalToPhysical(track.p[0][0]);
    // let pd = Tools.sub(p[1][0], p[2][0]);
    // let r = Math.sqrt(pd.x * pd.x + pd.y * pd.y) * scale / 2;
    // ctx.arc(tp.x, tp.y, r, 0, 2 * Math.PI);
    // ctx.strokeStyle = "black";
    // ctx.fillStyle = "#a1a1a1";
    // ctx.fill();
    // ctx.stroke();
    //
    // // рисуем конец трассы
    // ctx.beginPath();
    // tp = logicalToPhysical(track.p[0][track.len - 1]);
    // pd = Tools.sub(p[1][track.len - 1], p[2][track.len - 1]);
    // r = Math.sqrt(pd.x * pd.x + pd.y * pd.y) * scale / 2;
    // ctx.arc(tp.x, tp.y, r, 0, 2 * Math.PI);
    // ctx.strokeStyle = "red";
    // ctx.fillStyle = "#a1a1a1";
    // ctx.fill();
    // ctx.stroke();
}

// =====================================================================================================================
// рисуем тачку
function drawCar(car: Car): void {
    // рисуем радиус поворота
    if (car.getWheelAngle() != 0) {
        ctx.beginPath();
        let tp = logicalToPhysical(car.ackerP);
        if (car.getWheelAngle() > 0) {
            ctx.arc(tp.x, tp.y,
                car.ackerR * scale,
                car.ackerA + Math.PI,
                car.ackerA + Math.PI * (1 + car.getWheelAngle())
            );
        } else {
            ctx.arc(tp.x, tp.y,
                car.ackerR * scale,
                car.ackerA + Math.PI * (1 + car.getWheelAngle()),
                car.ackerA - Math.PI
            );
        }
        ctx.setLineDash([6, 4]);
        ctx.strokeStyle = "#999999";
        ctx.stroke();
    }
    // рисуем спрайт тачки
    ctx.save();
    let carP = logicalToPhysical(car.getPosition());
    ctx.translate(carP.x, carP.y);
    ctx.rotate(car.getAngle());
    ctx.drawImage(
        car.image,
        -car.width / 2 * scale,
        -car.height / 2 * scale,
        car.width * scale,
        car.height * scale
    );
    ctx.restore();
}

// =====================================================================================================================
// рисуем лучи сенсоров
function drawSensors(car: Car) {
    ctx.beginPath();
    let p = logicalToPhysical(car.getPosition());
    for (let s of car.sensors) {
        if (s.intersection == null) return;
        ctx.moveTo(p.x, p.y);
        let tp = logicalToPhysical(s.intersection);
        ctx.lineTo(tp.x, tp.y);
    }
    ctx.setLineDash([6, 4]);
    //ctx.strokeStyle = "#bbbbbb";
    ctx.strokeStyle = "#444444";
    ctx.stroke();
}

// =====================================================================================================================
//рисуем сетку
function drawGrid(): void {

    ctx.strokeStyle = "#666666";
    for (let i = -2000; i < 2001; i += 5) {
        // вдоль оси Y
        ctx.beginPath();
        ctx.lineTo(
            (offset.x + i * 10),
            (-100000 + offset.y)
        );
        ctx.lineTo(
            (offset.x + i * 10),
            (100000 + offset.y)
        );
        ctx.stroke();
        // вдоль оси X
        ctx.beginPath();
        ctx.lineTo(
            (-100000 + offset.x),
            (offset.y + i * 10)
        );
        ctx.lineTo(
            (100000 + offset.x),
            (offset.y + i * 10)
        );
        ctx.stroke();
    }
}

// =====================================================================================================================
// перерисовываем экран
function redrawCanvas(): void {
    if (Global.requestAnimationId) Global.requestAnimationId = requestAnimationFrame(redrawCanvas);

    cnv.width = cnv.clientWidth;
    cnv.height = cnv.clientHeight;
    ctx.clearRect(0, 0, cnv.width, cnv.height);

    let t = performance.now();
    let dt = (t - lastTimeTick) / 1000;
    fps = 1 / (dt);
    lastTimeTick = t;

    // если машину не трогали то только отрисовываем её старое положение, вернёмся на следующем тике, с нормальным dt
    let requestAnimation = 0;
    for (let p of Global.players) {
        let car = p.car;
        if (dt > 0.1 && car.speed == 0) dt = 0.01;  // TODO тут как то не красиво
        car.update(dt);
        if (car.isRequestAnimation() == true) {
            ++requestAnimation;
        }
    }

    // обновляем положение ботов
    if (Global.enableBots == true) {
        for (let b of Global.bots) {
            // едем вперед
            b.keys = 1 << Key.FORWARD;
            let dir = b.getDirection();
            if (dir != 0) {
                // поворачиваем
                b.keys |= 1 << (dir < 0 ? Key.LEFT : Key.RIGHT);
            }
            if (dt > 0.1 && b.car.speed == 0) dt = 0.01;  // TODO тут как то не красиво
            b.car.update(dt);
            if (b.car.isRequestAnimation() == true) {
                ++requestAnimation;
            }
        }
    } else {
        for (let b of Global.bots) {
            b.keys = 0;
        }
    }

    if (requestAnimation == 0) {
        cancelAnimationFrame(Global.requestAnimationId);
        Global.requestAnimationId = null;
        Utils.debug("anim-");
    }
    if (Global.isFollowMode === false || Global.followTarget === null) {
        // переносим центральную точку обзора в центр нового канваса.
        let shift = new Point((cnv.clientWidth - cnv.width) / (2 * scale), (cnv.clientHeight - cnv.height) / (2 * scale));
        offset = Point.sum(shift, offset);
    } else {
        // помещаем тачку в центр
        offset = Point.sub(new Point(cnv.width / (2 * scale), cnv.height / (2 * scale)), Global.followTarget.getPosition());
    }
    fillVars();
    drawTrack(Global.track);
    drawLines();
    drawCrossPoints();
    //drawGrid();
    for (let p of Global.players) {
        drawSensors(p.car);
        drawCar(p.car);
    }
    for (let b of Global.bots) {
        drawSensors(b.car);
        drawCar(b.car);
    }
}

// =====================================================================================================================
// меняем масштаб относительно положения мышки (scale и offset (offset - логический, скейлится согласно scale)
function rescaleCanvas(rate: number, p: Point): void {

    const temp = scale;
    const sc = scale * rate;
    if (0.95 < sc && sc < 1.05) {
        scale = 1;
    } else {
        //scale = sc < 0.01 ? 0.01 : sc > 100 ? 100 : sc;
        scale = Math.min(Math.max(sc, 0.001), 1000);
    }
    // таким образом избегаем лишнего дёргания в крайних положениях зума
    if (temp == scale) return;
    offset.x = p.x / scale - virtualMousePosition.x;
    offset.y = p.y / scale - virtualMousePosition.y;
}

// =====================================================================================================================
// конвертируем пиксели canvas в виртуальные координаты
function physicalToLogical(p: Point): Point {

    return new Point(p.x / scale - offset.x, p.y / scale - offset.y);
}

// =====================================================================================================================
// конвертируем виртуальные координаты в пиксели canvas
function logicalToPhysical(p: Point): Point {

    return new Point((p.x + offset.x) * scale, (p.y + offset.y) * scale);
}

// =====================================================================================================================
// заполняем окно переменных
function fillVars(): void {

    /// MouseEvent.arguments.clientX  не работает так как хотелось бы!!!
    // let x = MouseEvent.arguments != null ? MouseEvent.arguments.clientX : 0;
    // let y = MouseEvent.arguments != null ? MouseEvent.arguments.clientY : 0;
    let str = `FPS .: ${Math.round(fps)}`;
    str += `\nScale: ${Math.round(scale * 1000) / 1000}`;
    //str += `\nOffset:${offset.toString(0)}`;
    // if (track != undefined) {
    //     str += `\nTrack[0]:.[${track.points[0][0].x}, ${track.points[0][0].y}]
    //     Track[N]:.[${track.points[0][track.distance - 1].x}, ${track.points[0][track.distance - 1].y}]
    //     TrackMin:.[${track.xMin}, ${track.yMin}]
    //     TrackMax:.[${track.xMax}, ${track.yMax}]`;
    // }

    // const e = <MouseEvent>window.event;
    // let x = 0, y = 0;
    // if (e && e.clientX) {
    //     x = e.clientX;
    //     y = e.clientY;
    // }
    // str +=
    //     `\nMouseDwn: [${mouseDownPoint.x}, ${mouseDownPoint.y}]
    //     Mouse: [${x}, ${y}]
    //     CnvWH: [${cnv.width}, ${cnv.height}]
    //     CnvCWH:[${cnv.clientWidth}, ${cnv.clientHeight}]
    //     VirtMP:[${Math.round(virtualMousePosition.x)}, ${Math.round(virtualMousePosition.y)}]`;

    for (let p of Global.players) {
        str += `\n${p.name}`;
        str += `\nSpeed: ${Math.round(p.car.speed)}`;
        str += `\nDist : ${Math.round(p.car.distance)}`;
        str += `\nStage: ${p.car.stage}`;
        str += `\nKeys : ${p.keys}\n`;
        // p.car.sensors.forEach((s, i) => {
        //     str += `S[${i}] : ${Math.round(s.getDistance())},`;
        // });
    }

    for (let b of Global.bots) {
        str += `\n${b.name}`;
        str += `\nSpeed: ${Math.round(b.car.speed)}`;
        str += `\nDist : ${Math.round(b.car.distance)}`;
        str += `\nStage: ${b.car.stage}`;
        str += `\nCrashes: ${b.car.crashes}`;
        str += `\nDurability: ${Math.round(b.car.durability * 1000)/10}`;
        //str += `\nKeys : ${b.keys}\n`;
        // b.car.sensors.forEach((s, i) => {
        //     str += `S[${i}] : ${Math.round(s.getDistance())},`;
        // });
    }

    //str += `\nCar.p: ${car.getPosition().toString(0)}`;
    // str += `\nAckP : ${car.ackerP.toString(0)}`;
    // str += `\AckR.: [${Math.round(car.ackerR)}]`;
    // str += `\AckA.: [${Math.round(car.ackerA * 180 / Math.PI)}]`;
    // str += `\Wheel: [${Math.round(car.getWheelAngle() * 180 / Math.PI)}]`;
    // str += `\Angle: [${Math.round(car.getAngle() * 180 / Math.PI)}]`;

    // let dt = performance.now() - lastTimeTick;
    // str += `\nTick: [${Math.round(dt)}]`;
    vars.innerText = str;
}

// =====================================================================================================================
// проверка нажатия кнопки игроком
// return redrawRequest:
// 0 - нажатий нет
// 1 - нажатие есть, перерисовываем картинку
// 2 - нажатие есть, запускаем анимацию
function checkKeyDown(e: KeyboardEvent, player: Player): number {
    switch (e.code) {
        case player.keyCodes[Key.FORWARD]:
            player.keys |= 1;
            return 2;
        case player.keyCodes[Key.BACK]:
            player.keys |= 2;
            return 2;
        case player.keyCodes[Key.LEFT]:
            player.keys |= 4;
            return 1;
        case player.keyCodes[Key.RIGHT]:
            player.keys |= 8;
            return 1;
    }
    return 0;
}

// =====================================================================================================================

function checkKeyUp(e: KeyboardEvent, player: Player) {
    switch (e.code) {
        case player.keyCodes[Key.FORWARD]:
            player.keys &= ~1;
            return 2;
        case player.keyCodes[Key.BACK]:
            player.keys &= ~2;
            return 2;
        case player.keyCodes[Key.LEFT]:
            player.keys &= ~4;
            return 1;
        case player.keyCodes[Key.RIGHT]:
            player.keys &= ~8;
            return 1;
    }
    return 0;
}

// =====================================================================================================================
// инициализация игроков
function initPlayers() {
    Global.players.push(new Player("Редиска", new Car()));
    let p = Global.players[0];
    p.setKeys("KeyW", "KeyS", "KeyA", "KeyD");
    p.car.track = Global.track;
    p.car.image = new Image();
    p.car.image.src = "images\\SimpleDarkBlueCarTopView.svg";
    p.car.image.onload = () => {
        Utils.debug(Global.players[0].name + " ready!");
        Global.players[0].car.restart();
        if (Global.requestAnimationId === null) redrawCanvas();
    };

    // Global.players.push(new Player("RDX", new Car()));
    // p = Global.players[1];
    // p.setKeys("ArrowUp", "ArrowDown", "ArrowLeft", "ArrowRight");
    // p.car.track = Global.track;
    // p.car.image = new Image();
    // p.car.image.src = "images\\SimpleRedCarTopView.svg";
    // p.car.image.onload = () => {
    //     Utils.debug(Global.players[1].name + " ready!");
    //     Global.players[1].car.restart();
    //     if (Global.requestAnimationId === null) redrawCanvas();
    // };
}

// =====================================================================================================================

function createBots() {

    let algorithm = 0;
    Global.bots.push(new Bot("Вася 0", new Car(), algorithm));
    let b = Global.bots[algorithm];
    b.car.track = Global.track;
    b.car.image = new Image();
    b.car.image.src = "images\\SimplePinkCarTopView.svg";
    b.car.image.onload = () => {
        Utils.debug(Global.bots[0].name + " ready!");
        Global.bots[0].car.restart();
        if (Global.requestAnimationId === null) redrawCanvas();
    };

    algorithm = 1;
    Global.bots.push(new Bot("Михалыч 1", new Car(), algorithm));
    b = Global.bots[algorithm];
    b.car.track = Global.track;
    b.car.image = new Image();
    b.car.image.src = "images\\SimpleBrightGreenCarTopView.svg";
    b.car.image.onload = () => {
        Utils.debug(Global.bots[1].name + " ready!");
        Global.bots[1].car.restart();
        if (Global.requestAnimationId === null) redrawCanvas();
    };
    algorithm = 2;
    Global.bots.push(new Bot("Колян 2", new Car(), algorithm));
    b = Global.bots[algorithm];
    b.car.track = Global.track;
    b.car.image = new Image();
    b.car.image.src = "images\\SimpleOrangeCarTopView.svg";
    b.car.image.onload = () => {
        Utils.debug(Global.bots[2].name + " ready!");
        Global.bots[2].car.restart();
        if (Global.requestAnimationId === null) redrawCanvas();
    };
    algorithm = 3;
    Global.bots.push(new Bot("Сидоров 3", new Car(), algorithm));
    b = Global.bots[algorithm];
    b.car.track = Global.track;
    b.car.image = new Image();
    b.car.image.src = "images\\SimpleTurquoiseCarTopView.svg";
    b.car.image.onload = () => {
        Utils.debug(Global.bots[3].name + " ready!");
        Global.bots[3].car.restart();
        if (Global.requestAnimationId === null) redrawCanvas();
    };
    algorithm = 4;
    Global.bots.push(new Bot("Караул 4", new Car(), algorithm));
    b = Global.bots[algorithm];
    b.car.track = Global.track;
    b.car.image = new Image();
    b.car.image.src = "images\\SimplePurpleCarTopView.svg";
    b.car.image.onload = () => {
        Utils.debug(Global.bots[4].name + " ready!");
        Global.bots[4].car.restart();
        if (Global.requestAnimationId === null) redrawCanvas();
    };

    algorithm = 5;
    Global.bots.push(new Bot("Bot " + algorithm, new Car(), algorithm));
    b = Global.bots[algorithm];
    b.car.track = Global.track;
    b.car.image = new Image();
    b.car.image.src = "images\\SimplePinkCarTopView.svg";
    b.car.image.onload = () => {
        Utils.debug(Global.bots[5].name + " ready!");
        Global.bots[5].car.restart();
        if (Global.requestAnimationId === null) redrawCanvas();
    };
    algorithm = 6;
    Global.bots.push(new Bot("Bot " + algorithm, new Car(), algorithm));
    b = Global.bots[algorithm];
    b.car.track = Global.track;
    b.car.image = new Image();
    b.car.image.src = "images\\SimpleBrightGreenCarTopView.svg";
    b.car.image.onload = () => {
        Utils.debug(Global.bots[6].name + " ready!");
        Global.bots[6].car.restart();
        if (Global.requestAnimationId === null) redrawCanvas();
    };
    algorithm = 7;
    Global.bots.push(new Bot("Bot " + algorithm, new Car(), algorithm));
    b = Global.bots[algorithm];
    b.car.track = Global.track;
    b.car.image = new Image();
    b.car.image.src = "images\\SimpleOrangeCarTopView.svg";
    b.car.image.onload = () => {
        Utils.debug(Global.bots[7].name + " ready!");
        Global.bots[7].car.restart();
        if (Global.requestAnimationId === null) redrawCanvas();
    };
    algorithm = 8;
    Global.bots.push(new Bot("Bot " + algorithm, new Car(), algorithm));
    b = Global.bots[algorithm];
    b.car.track = Global.track;
    b.car.image = new Image();
    b.car.image.src = "images\\SimpleTurquoiseCarTopView.svg";
    b.car.image.onload = () => {
        Utils.debug(Global.bots[8].name + " ready!");
        Global.bots[8].car.restart();
        if (Global.requestAnimationId === null) redrawCanvas();
    };
    algorithm = 9;
    Global.bots.push(new Bot("Bot " + algorithm, new Car(), algorithm));
    b = Global.bots[algorithm];
    b.car.track = Global.track;
    b.car.image = new Image();
    b.car.image.src = "images\\SimplePurpleCarTopView.svg";
    b.car.image.onload = () => {
        Utils.debug(Global.bots[9].name + " ready!");
        Global.bots[9].car.restart();
        if (Global.requestAnimationId === null) redrawCanvas();
    };
}

// =====================================================================================================================
// поиск пересечений последней линии с остальными
function findIntersectionsWithTrack() {
    // проверка пересечений с кривой
    if (Global.track) {
        for (let tr = 1; tr < 3; ++tr) {
            for (let i = 0; i < Global.track.getLength() - 1; ++i) {
                let p = Line.getCrossPoints(
                    Global.track.p[tr][i],
                    Global.track.p[tr][i + 1],
                    pointsOfLines[pointsOfLines.length - 2],
                    pointsOfLines[pointsOfLines.length - 1]
                );
                if (p) {
                    const cp = new ColorPoint(p.x, p.y, '#' + Math.random().toString(16).slice(-6));
                    crossPointsWithCurve.push(cp);
                }
            }
        }
    }
}

// поиск новых пересечений с себе подобными
function findSelfIntersections() {
    const length = pointsOfLines.length;
    if (length > 3) {
        for (let i = 0; i < length - 3; i += 2) {
            let p = Line.getCrossPoints(
                pointsOfLines[i],
                pointsOfLines[i + 1],
                pointsOfLines[length - 2],
                pointsOfLines[length - 1]
            );
            if (p) {
                const cp = new ColorPoint(p.x, p.y, '#' + Math.random().toString(16).slice(-6));
                crossPointsSelf.push(cp);
            }
        }
    }
}

// Init
window.onload = () => {

    log = <HTMLElement>document.getElementById("log");
    vars = <HTMLElement>document.getElementById("vars");
    input = <HTMLInputElement>document.getElementById("input");
    cnv = <HTMLCanvasElement>document.getElementById("canvas");
    ctx = <CanvasRenderingContext2D>cnv.getContext("2d");

    lastClickedTarget = cnv;

    window.addEventListener("resize", () => {
        if (Global.requestAnimationId === null) redrawCanvas();
    });

    // =====================================

    document.addEventListener("onLoad", () => {

        Utils.debug("onLoad");
        // ставим тачку в центр
        offset.x = cnv.width / 2.0;
        offset.y = cnv.height / 2.0;
        researchCrossPointsWithCurve();
        // если загрузились все файлы - начинаем подготовку к старту.
        if (Global.track != null) {
            if (Global.bots.length === 0) {
                createBots();
            }
            if (Global.players.length === 0) {
                initPlayers();
            }
            if (Global.followTarget === null && Global.isFollowMode === true) {
                if (Global.bots.length) {
                    Global.followTarget = Global.bots[0].car;
                    Global.isFollowMode = true;
                } else if (Global.players.length) {
                    Global.followTarget = Global.players[0].car;
                    Global.isFollowMode = true;
                }
            }
        }
    });
    // =====================================

    cnv.addEventListener("mousedown", e => {

        isMouseDown = true;

        // запоминаем где зажали мышку
        // @ts-ignore
        lastClickedTarget = e.target;
        let x = e.clientX;
        let y = e.clientY;

        mouseDownPoint = new Point(x, y);

        // проверяем что за кнопка нажата
        if (e.buttons & 1) {    // Left button
            let numOfPoints = pointsOfLines.push(new Point(x / scale - offset.x, y / scale - offset.y));
            if (numOfPoints % 2 == 0) {
                findIntersectionsWithTrack();
                findSelfIntersections();
            }
            if (Global.requestAnimationId === null) redrawCanvas();
            return;
        }
        if (e.buttons & 4) {    // Middle button
            // переключаем режим следования камеры за машиной
            Global.isFollowMode = !Global.isFollowMode;
            if (Global.requestAnimationId === null) redrawCanvas();
        }
    });
    // =====================================

    cnv.addEventListener("mouseup", () => {

        isMouseDown = false;
    });
    // =====================================

    cnv.addEventListener("mousemove", e => {

        virtualMousePosition = physicalToLogical(new Point(e.clientX, e.clientY));
        if (isMouseDown == false) {
            //fillVars();
            return;
        }
        // Right button
        if (e.buttons & 2) {
            Global.isFollowMode = false;
            offset.x += (e.clientX - mouseDownPoint.x) / scale;
            offset.y += (e.clientY - mouseDownPoint.y) / scale;
            mouseDownPoint.x = e.clientX;
            mouseDownPoint.y = e.clientY;
            if (Global.requestAnimationId === null) redrawCanvas();
        }
        // Middle button
        if (e.buttons & 4) {
        }
    });
    // =====================================

    cnv.addEventListener("wheel", e => {

        // e.preventDefault();
        // e.stopPropagation();
        rescaleCanvas(e.deltaY > 0 ? 0.9 : 10 / 9, new Point(e.clientX, e.clientY));
        if (Global.requestAnimationId === null) redrawCanvas();
    }, {passive: true});
    // =====================================

    cnv.addEventListener('contextmenu', e => {

        e.preventDefault();
        e.stopPropagation();
    });
    // =====================================

    document.addEventListener("keydown", e => {

        let redrawRequest = 0;
        switch (e.target) {
            case cnv:
                for (let p of Global.players) {
                    redrawRequest |= checkKeyDown(e, p);
                }
                // рестарт той же трассы
                if (e.code === "KeyR") {
                    redrawRequest |= 1;
                    for (let p of Global.players) {
                        p.car.restart();
                    }
                    for (let b of Global.bots) {
                        b.car.restart();
                    }
                    break;
                }
                // запуск/остановка ботов
                if (e.code === "KeyZ") {
                    redrawRequest = 2;
                    Global.enableBots = !Global.enableBots;
                    break;
                }
                if (e.code === "Space") {
                    if (e.ctrlKey === true) {
                        getTrack();
                    }
                    if (e.shiftKey === true) {
                        Global.enableBlindMode = !Global.enableBlindMode;
                        redrawRequest |= 1;
                    }
                    break;
                }
                // переключение камеры
                let k = +e.key;
                if (k === NaN) return;
                k = k ? k - 1 : 9;
                if (k < Global.bots.length) {
                    Global.isFollowMode = true;
                    Global.followTarget = Global.bots[k].car;
                    Utils.debug(`Follow ${Global.bots[k].name}`);
                }
                redrawRequest |= 1;
                break;
            case input:
                if (e.code === "Enter") {
                    e.preventDefault();
                    Utils.debug(input.value);
                    input.value = "";
                }
                break;
            default:
                return;
        }
        switch (redrawRequest) {
            case 1:
                // просто перерисовываем канвас
                if (Global.requestAnimationId === null) redrawCanvas();
                break;
            case 2:
            case 3:
                // запускаем анимацию
                if (Global.requestAnimationId === null) {
                    Utils.debug("anim+");
                    Global.requestAnimationId = requestAnimationFrame(redrawCanvas);
                }
                break;
        }
    });
    // =====================================

    document.addEventListener("keyup", e => {
        if (e.target === cnv) {
            for (let p of Global.players) checkKeyUp(e, p);
        }
    });
    // =====================================

    Utils.debug("Begin");
    cnv.width = cnv.clientWidth;
    cnv.height = cnv.clientHeight;

    cnv.tabIndex = 0;   // если убрать tabIndex то не будет работать event cnv.keyDown
    getTrack();

    cnv.focus();
};
// =====================================================================================================================