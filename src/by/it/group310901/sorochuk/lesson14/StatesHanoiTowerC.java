package by.it.group310901.sorochuk.lesson14;

import java.util.Scanner;

public class StatesHanoiTowerC {
    public static void main(String[] args) {

        int N;

        // Читаем количество дисков для задачи Ханойской башни
        try (Scanner scanner = new Scanner(System.in)) {
            N = scanner.nextInt();
        }

        // Общее количество шагов для перемещения N дисков в задаче Ханойской башни
        int max_size = (1 << N) - 1; // 2^N - 1 шагов (по формуле для Ханойской башни)
        int[] steps_heights = new int[N]; // Хранение первых встреченных шагов для каждой высоты башни

        // Инициализируем массив `steps_heights` значениями -1
        for (int i = 0; i < N; i++) {
            steps_heights[i] = -1;
        }

        DSU dsu = new DSU(max_size); // Создаем структуру DSU для объединения шагов
        int[] heights = new int[3]; // Высоты башен A, B и C
        heights[0] = N; // Изначально все диски находятся на первой башне

        for (int i = 0; i < max_size; i++) {
            int step = i + 1; // Текущий шаг
            int[] delta; // Массив изменений высот башен для текущего шага

            // Если шаг нечетный, это простое перемещение диска
            if (step % 2 != 0) {
                delta = carryingOver(N, step, 1); // Перемещение диска 1
            } else {
                // Если шаг четный, определяем, какой диск перемещается
                int count = step;
                int countDisk = 0;

                // Определяем номер перемещаемого диска
                while (count % 2 == 0) {
                    countDisk++;
                    count /= 2;
                }

                delta = carryingOver(N, step, countDisk + 1); // Перемещение указанного диска
            }

            // Применяем изменения высот башен
            for (int j = 0; j < 3; j++) {
                heights[j] += delta[j];
            }

            // Находим максимальную высоту башни
            int max = max(heights);

            dsu.make_set(i); // Создаем новое множество для текущего шага

            // Если высота еще не встречалась, запоминаем шаг
            if (steps_heights[max - 1] == -1) {
                steps_heights[max - 1] = i;
            } else {
                // Если высота уже встречалась, объединяем множества
                dsu.union_sets(steps_heights[max - 1], i);
            }
        }

        // Определяем размеры множества для каждой высоты
        int[] sizes = new int[N];
        for (int i = 0; i < N; i++) {
            if (steps_heights[i] != -1) {
                sizes[i] = dsu.size(steps_heights[i]);
            }
        }

        StringBuilder sb = new StringBuilder();

        // Сортируем и формируем строку результатов
        for (int i = 0; i < N; i++) {
            int max = i;
            for (int j = i + 1; j < N; j++) {
                if (sizes[max] < sizes[j]) {
                    max = j;
                }
            }

            if (sizes[max] == 0) {
                break;
            }

            int temp = sizes[max];
            sizes[max] = sizes[i];
            sizes[i] = temp;
            sb.insert(0, sizes[i] + " ");
        }

        sb.deleteCharAt(sb.length() - 1); // Удаляем последний пробел
        System.out.println(sb); // Выводим результат
    }

    // Функция возвращает максимальное значение среди трех высот башен
    private static int max(int[] heights) {
        return Math.max(Math.max(heights[0], heights[1]), heights[2]);
    }

    // Функция вычисляет изменения высот башен при перемещении диска
    static int[] carryingOver(int N, int step, int k) {
        int t, axisY, axisZ;

        // Определяем оси для четного и нечетного N
        if (N % 2 == 0) {
            axisY = 1;
            axisZ = 2;
        } else {
            axisY = 2;
            axisZ = 1;
        }

        int[] result = new int[3];
        t = (step / (1 << (k - 1)) - 1) / 2;
        int from = 0, to = 0;

        // Определяем башни "откуда" и "куда" идет диск
        if (k % 2 != 0)
            switch (t % 3) {
                case 0 -> to = axisY;
                case 1 -> { from = axisY; to = axisZ; }
                case 2 -> from = axisZ;
            }
        else
            switch (t % 3) {
                case 0 -> to = axisZ;
                case 1 -> { from = axisZ; to = axisY; }
                case 2 -> from = axisY;
            }

        result[from] = -1; // Уменьшаем высоту башни "откуда"
        result[to] = 1;   // Увеличиваем высоту башни "куда"

        return result;
    }

    // Реализация структуры данных DSU
    private static class DSU {
        private final int[] parent;
        private final int[] size;

        public DSU(int size) {
            parent = new int[size];
            this.size = new int[size];
        }

        public void make_set(int v) {
            parent[v] = v;
            size[v] = 1;
        }

        public int size(int v) {
            return size[find_set(v)];
        }

        public int find_set(int v) {
            if (v == parent[v])
                return v;
            return parent[v] = find_set(parent[v]);
        }

        public void union_sets(int a, int b) {
            a = find_set(a);
            b = find_set(b);
            if (a != b) {
                if (size[a] < size[b]) {
                    int temp = a;
                    a = b;
                    b = temp;
                }
                parent[b] = a;
                size[a] += size[b];
            }
        }
    }
}
