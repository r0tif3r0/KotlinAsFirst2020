@file:Suppress("UNUSED_PARAMETER")

package lesson8.task1

import kotlin.math.ceil


/**
 * Точка (гекс) на шестиугольной сетке.
 * Координаты заданы как в примере (первая цифра - y, вторая цифра - x)
 *
 *       60  61  62  63  64  65
 *     50  51  52  53  54  55  56
 *   40  41  42  43  44  45  46  47
 * 30  31  32  33  34  35  36  37  38
 *   21  22  23  24  25  26  27  28
 *     12  13  14  15  16  17  18
 *       03  04  05  06  07  08
 *
 * В примерах к задачам используются те же обозначения точек,
 * к примеру, 16 соответствует HexPoint(x = 6, y = 1), а 41 -- HexPoint(x = 1, y = 4).
 *
 * В задачах, работающих с шестиугольниками на сетке, считать, что они имеют
 * _плоскую_ ориентацию:
 *  __
 * /  \
 * \__/
 *
 * со сторонами, параллельными координатным осям сетки.
 *
 * Более подробно про шестиугольные системы координат можно почитать по следующей ссылке:
 *   https://www.redblobgames.com/grids/hexagons/
 */
data class HexPoint(val x: Int, val y: Int) {
    /**
     * Средняя (3 балла)
     *
     * Найти целочисленное расстояние между двумя гексами сетки.
     * Расстояние вычисляется как число единичных отрезков в пути между двумя гексами.
     * Например, путь межу гексами 16 и 41 (см. выше) может проходить через 25, 34, 43 и 42 и имеет длину 5.
     */
    fun distance(other: HexPoint): Int {
        if (y > other.y) {
            if ((y - other.y) / 2 == (other.x - x) && (y - other.y) % 2 == 0)
                return y - other.y
            return if (x >= other.x)
                (y - other.y) + (x - other.x)
            else {
                if (x + y <= other.x + other.y)
                    (other.x - x) else (y - other.y)
            }
        } else if ((other.y - y) / 2 == (x - other.x) && (other.y - y) % 2 == 0)
            return other.y - y
        return if (other.x >= x)
            (other.y - y) + (other.x - x)
        else {
            if (y + x >= other.y + other.x)
                x - other.x else (other.y - y)
        }
    }

    override fun toString(): String = "$y.$x"
}

/**
 * Правильный шестиугольник на гексагональной сетке.
 * Как окружность на плоскости, задаётся центральным гексом и радиусом.
 * Например, шестиугольник с центром в 33 и радиусом 1 состоит из гексов 42, 43, 34, 24, 23, 32.
 */
data class Hexagon(val center: HexPoint, val radius: Int) {

    /**
     * Средняя (3 балла)
     *
     * Рассчитать расстояние между двумя шестиугольниками.
     * Оно равно расстоянию между ближайшими точками этих шестиугольников,
     * или 0, если шестиугольники имеют общую точку.
     *
     * Например, расстояние между шестиугольником A с центром в 31 и радиусом 1
     * и другим шестиугольником B с центром в 26 и радиуоом 2 равно 2
     * (расстояние между точками 32 и 24)
     */
    fun distance(other: Hexagon): Int {
        return if (center.distance(other.center) <= radius + other.radius)
            0
        else center.distance(other.center) - radius - other.radius
    }

    /**
     * Тривиальная (1 балл)
     *
     * Вернуть true, если заданная точка находится внутри или на границе шестиугольника
     */


    fun contains(point: HexPoint): Boolean = center.distance(point) <= radius

    fun borderPoints(): List<HexPoint> {
        var list = mutableListOf<HexPoint>()
        if (radius == 0) {
            list.add(center)
            return list
        }
        var tmp = HexPoint(center.x + radius, center.y)
        list = (list + pathBetweenHexes(tmp, tmp.move(Direction.DOWN_LEFT, radius))).toMutableList()
        list.removeAt(list.size - 1)
        tmp = tmp.move(Direction.DOWN_LEFT, radius)
        list = (list + pathBetweenHexes(tmp, tmp.move(Direction.LEFT, radius))).toMutableList()
        list.removeAt(list.size - 1)
        tmp = tmp.move(Direction.LEFT, radius)
        list = (list + pathBetweenHexes(tmp, tmp.move(Direction.UP_LEFT, radius))).toMutableList()
        list.removeAt(list.size - 1)
        tmp = tmp.move(Direction.UP_LEFT, radius)
        list = (list + pathBetweenHexes(tmp, tmp.move(Direction.UP_RIGHT, radius))).toMutableList()
        list.removeAt(list.size - 1)
        tmp = tmp.move(Direction.UP_RIGHT, radius)
        list = (list + pathBetweenHexes(tmp, tmp.move(Direction.RIGHT, radius))).toMutableList()
        list.removeAt(list.size - 1)
        tmp = tmp.move(Direction.RIGHT, radius)
        list = (list + pathBetweenHexes(tmp, tmp.move(Direction.DOWN_RIGHT, radius))).toMutableList()
        list.removeAt(list.size - 1)
        return list
    }
}

/**
 * Прямолинейный отрезок между двумя гексами
 */
class HexSegment(val begin: HexPoint, val end: HexPoint) {
    /**
     * Простая (2 балла)
     *
     * Определить "правильность" отрезка.
     * "Правильным" считается только отрезок, проходящий параллельно одной из трёх осей шестиугольника.
     * Такими являются, например, отрезок 30-34 (горизонталь), 13-63 (прямая диагональ) или 51-24 (косая диагональ).
     * А, например, 13-26 не является "правильным" отрезком.
     */
    fun isValid(): Boolean =
        begin != end && (begin.x == end.x || begin.y == end.y || (begin.x + begin.y == end.x + end.y))

    /**
     * Средняя (3 балла)
     *
     * Вернуть направление отрезка (см. описание класса Direction ниже).
     * Для "правильного" отрезка выбирается одно из первых шести направлений,
     * для "неправильного" -- INCORRECT.
     */
    fun direction(): Direction = when {
        !isValid() -> Direction.INCORRECT
        begin.y == end.y -> {
            if (begin.x > end.x) Direction.LEFT
            else Direction.RIGHT
        }
        begin.x == end.x -> {
            if (begin.y > end.y) Direction.DOWN_LEFT
            else Direction.UP_RIGHT
        }
        else -> {
            if (begin.y > end.y) Direction.DOWN_RIGHT
            else Direction.UP_LEFT
        }
    }

    override fun equals(other: Any?) =
        other is HexSegment && (begin == other.begin && end == other.end || end == other.begin && begin == other.end)

    override fun hashCode() =
        begin.hashCode() + end.hashCode()
}

/**
 * Направление отрезка на гексагональной сетке.
 * Если отрезок "правильный", то он проходит вдоль одной из трёх осей шестугольника.
 * Если нет, его направление считается INCORRECT
 */
enum class Direction {
    RIGHT,      // слева направо, например 30 -> 34
    UP_RIGHT,   // вверх-вправо, например 32 -> 62
    UP_LEFT,    // вверх-влево, например 25 -> 61
    LEFT,       // справа налево, например 34 -> 30
    DOWN_LEFT,  // вниз-влево, например 62 -> 32
    DOWN_RIGHT, // вниз-вправо, например 61 -> 25
    INCORRECT;  // отрезок имеет изгиб, например 30 -> 55 (изгиб в точке 35)

    /**
     * Простая (2 балла)
     *
     * Вернуть направление, противоположное данному.
     * Для INCORRECT вернуть INCORRECT
     */
    fun opposite(): Direction = when (this) {
        LEFT -> RIGHT
        RIGHT -> LEFT
        UP_RIGHT -> DOWN_LEFT
        UP_LEFT -> DOWN_RIGHT
        DOWN_LEFT -> UP_RIGHT
        DOWN_RIGHT -> UP_LEFT
        else -> INCORRECT
    }

    /**
     * Средняя (3 балла)
     *
     * Вернуть направление, повёрнутое относительно
     * заданного на 60 градусов против часовой стрелки.
     *
     * Например, для RIGHT это UP_RIGHT, для UP_LEFT это LEFT, для LEFT это DOWN_LEFT.
     * Для направления INCORRECT бросить исключение IllegalArgumentException.
     * При решении этой задачи попробуйте обойтись без перечисления всех семи вариантов.
     */
    fun next(): Direction = when (this) {
        INCORRECT -> throw IllegalArgumentException()
        LEFT -> DOWN_LEFT
        RIGHT -> UP_RIGHT
        DOWN_LEFT -> DOWN_RIGHT
        DOWN_RIGHT -> RIGHT
        UP_LEFT -> LEFT
        UP_RIGHT -> UP_LEFT
    }

    /**
     * Простая (2 балла)
     *
     * Вернуть true, если данное направление совпадает с other или противоположно ему.
     * INCORRECT не параллельно никакому направлению, в том числе другому INCORRECT.
     */
    fun isParallel(other: Direction): Boolean = (this != INCORRECT && (this == other || this == other.opposite()))
}

/**
 * Средняя (3 балла)
 *
 * Сдвинуть точку в направлении direction на расстояние distance.
 * Бросить IllegalArgumentException(), если задано направление INCORRECT.
 * Для расстояния 0 и направления не INCORRECT вернуть ту же точку.
 * Для отрицательного расстояния сдвинуть точку в противоположном направлении на -distance.
 *
 * Примеры:
 * 30, direction = RIGHT, distance = 3 --> 33
 * 35, direction = UP_LEFT, distance = 2 --> 53
 * 45, direction = DOWN_LEFT, distance = 4 --> 05
 */
fun HexPoint.move(direction: Direction, distance: Int): HexPoint = when (direction) {
    Direction.INCORRECT -> throw IllegalArgumentException()
    Direction.RIGHT -> HexPoint(x + distance, y)
    Direction.LEFT -> HexPoint(x - distance, y)
    Direction.UP_LEFT -> HexPoint(x - distance, y + distance)
    Direction.UP_RIGHT -> HexPoint(x, y + distance)
    Direction.DOWN_RIGHT -> HexPoint(x + distance, y - distance)
    Direction.DOWN_LEFT -> HexPoint(x, y - distance)
}

/**
 * Сложная (5 баллов)
 *
 * Найти кратчайший путь между двумя заданными гексами, представленный в виде списка всех гексов,
 * которые входят в этот путь.
 * Начальный и конечный гекс также входят в данный список.
 * Если кратчайших путей существует несколько, вернуть любой из них.
 *
 * Пример (для координатной сетки из примера в начале файла):
 *   pathBetweenHexes(HexPoint(y = 2, x = 2), HexPoint(y = 5, x = 3)) ->
 *     listOf(
 *       HexPoint(y = 2, x = 2),
 *       HexPoint(y = 2, x = 3),
 *       HexPoint(y = 3, x = 3),
 *       HexPoint(y = 4, x = 3),
 *       HexPoint(y = 5, x = 3)
 *     )
 */
fun onlyPath(from: HexPoint, to: HexPoint): List<HexPoint> {
    val list = mutableListOf<HexPoint>()
    if ((to.y - from.y) / 2 == (from.x - to.x) && (to.y - from.y) % 2 == 0) {
        var k = 0
        for (i in 0 until to.y - from.y + 1) {
            if (i % 2 == 0) {
                list.add(HexPoint(from.x - k, from.y + i))
                k++
            } else list.add(HexPoint(from.x - k, from.y + i))
        }
    } else {
        when {
            to.x >= from.x -> {
                for (i in 0 until to.x - from.x + 1)
                    list.add(HexPoint(from.x + i, from.y))
                for (i in 0 until to.y - from.y)
                    list.add(HexPoint(to.x, from.y + i + 1))
            }
            to.x + to.y <= from.x + from.y -> {
                for (i in 0 until from.x - to.x - (to.y - from.y) + 1)
                    list.add(HexPoint(from.x - i, from.y))
                for (i in 0 until to.y - from.y)
                    list.add(HexPoint(to.x + (to.y - from.y) - i - 1, from.y + i + 1))
            }
            else -> {
                for (i in 0 until from.x - to.x + 1)
                    list.add(HexPoint(from.x - i, from.y + i))
                for (i in 0 until to.y - from.y - (from.x - to.x))
                    list.add(HexPoint(to.x, from.y + (from.x - to.x) + i + 1))
            }
        }
    }
    return list
}

fun pathBetweenHexes(from: HexPoint, to: HexPoint): List<HexPoint> {
    return if (from.y < to.y) {
        onlyPath(from, to)
    } else onlyPath(to, from).reversed()
}

/**
 * Очень сложная (20 баллов)
 *
 * Дано три точки (гекса). Построить правильный шестиугольник, проходящий через них
 * (все три точки должны лежать НА ГРАНИЦЕ, а не ВНУТРИ, шестиугольника).
 * Все стороны шестиугольника должны являться "правильными" отрезками.
 * Вернуть null, если такой шестиугольник построить невозможно.
 * Если шестиугольников существует более одного, выбрать имеющий минимальный радиус.
 *
 * Пример: через точки 13, 32 и 44 проходит правильный шестиугольник с центром в 24 и радиусом 2.
 * Для точек 13, 32 и 45 такого шестиугольника не существует.
 * Для точек 32, 33 и 35 следует вернуть шестиугольник радиусом 3 (с центром в 62 или 05).
 *
 * Если все три точки совпадают, вернуть шестиугольник нулевого радиуса с центром в данной точке.
 */

fun hexagonByThreePoints(a: HexPoint, b: HexPoint, c: HexPoint): Hexagon? {
    val d = maxOf(a.distance(b), a.distance(c), b.distance(c))
    if (HexSegment(a, b).isValid() && HexSegment(b, c).isValid()) {
        val list = Hexagon(a, d).borderPoints()
        for (i in list.indices) {
            if (list[i].distance(a) == list[i].distance(b) && list[i].distance(b) == list[i].distance(c))
                return Hexagon(list[i], d)
        }
    }
    val list = Hexagon(a, ceil(d / 2.0).toInt()).borderPoints()
    for (i in list.indices) {
        if (list[i].distance(a) == list[i].distance(b) && list[i].distance(b) == list[i].distance(c))
            return Hexagon(list[i], ceil(d / 2.0).toInt())
    }

    return null
}

/**
 * Очень сложная (20 баллов)
 *
 * Дано множество точек (гексов). Найти правильный шестиугольник минимального радиуса,
 * содержащий все эти точки (безразлично, внутри или на границе).
 * Если множество пустое, бросить IllegalArgumentException.
 * Если множество содержит один гекс, вернуть шестиугольник нулевого радиуса с центром в данной точке.
 *
 * Пример: 13, 32, 45, 18 -- шестиугольник радиусом 3 (с центром, например, в 15)
 */
fun minContainingHexagon(vararg points: HexPoint): Hexagon {
    if (points.isEmpty()) throw IllegalArgumentException()
    var d = 0
    var p1 = HexPoint(0, 0)
    var p2 = HexPoint(0, 0)
    for (i in points.indices)
        for (j in i + 1 until points.size) {
            if (points[i].distance(points[j]) > d) {
                d = points[i].distance(points[j])
                p1 = points[i]
                p2 = points[j]
            }
        }
    if (HexSegment(p1, p2).isValid()) {
        val list = Hexagon(p1, d).borderPoints()
        var k = 0
        for (i in list.indices) {
            for (j in points.indices) {
                if (list[i].distance(points[j]) <= d)
                    k++
            }
            if (k == points.size) return Hexagon(list[i], d)
        }
    }
    val list = Hexagon(p1, ceil(d / 2.0).toInt()).borderPoints()
    var ind = 0
    for (i in list.indices) {
        var k = 0
        for (j in points.indices) {
            if (list[i].distance(points[j]) <= ceil(d / 2.0).toInt()) {
                k++
            }
        }
        if (k == points.size) {
            ind = i
            break
        }
    }
    return Hexagon(list[ind], ceil(d / 2.0).toInt())
}



