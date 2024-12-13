package by.it.group310901.sorochuk.lesson14;

import java.util.*;
import java.util.stream.Collectors;

public class PointsA {

    public static void main(String[] args) {

        // Список множеств, представляющий группы связанных точек
        List<Set<Point>> dsu = new ArrayList<>();
        int distance, dotsAmount; // Максимальное расстояние для объединения и количество точек

        try (Scanner scanner = new Scanner(System.in)) {

            // Чтение максимального расстояния и количества точек
            distance = scanner.nextInt();
            dotsAmount = scanner.nextInt();

            // Чтение координат точек
            for (int i = 0; i < dotsAmount; i++) {
                Point point = new Point(scanner.nextInt(), scanner.nextInt(), scanner.nextInt()); // Создание точки
                Set<Point> set = new HashSet<>(); // Создание множества для точки
                set.add(point); // Добавление точки в множество
                dsu.add(set); // Добавление множества в список групп
            }
        }

        // Основной цикл для объединения групп точек
        for (int i = 0; i < dsu.size(); i++) { // Проход по всем группам
            for (Set<Point> set : dsu) { // Сравнение текущей группы с другими
                boolean union = false; // Флаг объединения групп
                ok:
                if (dsu.get(i) != set) { // Проверяем только разные группы
                    for (Point p1 : dsu.get(i)) { // Для каждой точки из текущей группы
                        for (Point p2 : set) { // Проверяем каждую точку из другой группы
                            // Если расстояние между точками меньше или равно заданному
                            if (p1 != p2 && checkDistance(p1, p2, distance)) {
                                union = true; // Устанавливаем флаг объединения
                                break ok; // Прерываем вложенные циклы
                            }
                        }
                    }
                }

                if (union) { // Если группы нужно объединить
                    dsu.get(i).addAll(set); // Объединяем все точки в текущую группу
                    set.clear(); // Очищаем другую группу
                    i = 0; // Начинаем заново, так как список изменился
                }
            }
        }

        // Удаляем пустые группы
        dsu.removeIf(Set::isEmpty);

        // Формируем строку с размерами групп, отсортированными по убыванию
        String output = dsu.stream()
                .map(Set::size) // Преобразуем группы в их размеры
                .sorted((n, m) -> m - n) // Сортируем размеры по убыванию
                .map(String::valueOf) // Преобразуем размеры в строки
                .collect(Collectors.joining(" ")) // Объединяем в строку через пробел
                .trim(); // Удаляем лишние пробелы

        // Выводим результат
        System.out.println(output);
    }

    // Проверка расстояния между двумя точками
    private static boolean checkDistance(Point p1, Point p2, int distance) {
        // Вычисляем евклидово расстояние между точками в 3D
        return Math.hypot(Math.hypot(p1.getX() - p2.getX(), p1.getY() - p2.getY()), p1.getZ() - p2.getZ()) <= distance;
    }

    // Класс для представления точки в 3D
    private static class Point {

        private final int x; // Координата X
        private final int y; // Координата Y
        private final int z; // Координата Z

        // Конструктор для создания точки
        public Point(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        // Методы для получения координат
        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }
    }
}
