package by.it.group310901.sorochuk.lesson13;

import java.util.*;
import java.util.Map.Entry;

// Класс GraphC реализует алгоритм поиска сильных связных компонент в графе с использованием обхода в глубину (DFS).
public class GraphC {
    // Переменная для отслеживания времени в процессе обхода графа.
    static Integer currTime = 0;

    // Метод dfsTime выполняет обход в глубину, записывая время посещения каждой вершины.
    private static void dfsTime(String node, Map<String, List<String>> graph, Set<String> visited, Map<String, Integer> time) {
        visited.add(node); // Добавляем вершину в множество посещённых.
        // Если у вершины есть соседние вершины, продолжаем обход.
        if (graph.get(node) != null)
            for (String next_node : graph.get(node)) {
                // Если соседняя вершина не посещена, увеличиваем время и рекурсивно продолжаем обход.
                if (!visited.contains(next_node)) {
                    currTime++;
                    dfsTime(next_node, graph, visited, time);
                }
            }
        // Сохраняем время посещения вершины в map.
        time.put(node, currTime++);
    }

    // Метод dfs выполняет обход в глубину и записывает путь от текущей вершины до всех достижимых.
    private static void dfs(String node, Map<String, List<String>> graph, Set<String> visited, List<String> path) {
        visited.add(node); // Добавляем вершину в множество посещённых.
        path.add(node); // Добавляем вершину в текущий путь.
        // Если у вершины есть соседние вершины, продолжаем обход.
        if (graph.get(node) != null)
            for (String next_node : graph.get(node)) {
                // Если соседняя вершина не посещена, рекурсивно продолжаем обход.
                if (!visited.contains(next_node)) {
                    dfs(next_node, graph, visited, path);
                }
            }
    }

    // Класс MapEntryComparator реализует компаратор для сортировки элементов map по значению, затем по ключу.
    static class MapEntryComparator implements Comparator<Entry<String, Integer>> {
        @Override
        public int compare(Entry<String, Integer> entry1, Entry<String, Integer> entry2) {
            // Сравниваем по значению.
            int valueComparison = entry1.getValue().compareTo(entry2.getValue());
            if (valueComparison == 0) {
                // Если значения равны, сравниваем по ключу в обратном порядке (для правильного порядка в итоговой сортировке).
                return entry2.getKey().compareTo(entry1.getKey());
            }
            return valueComparison; // Возвращаем результат сравнения значений.
        }
    }

    public static void main(String[] args) {
        // Инициализация графа и его перевёрнутой версии.
        Map<String, List<String>> graph = new HashMap<>();
        Map<String, List<String>> graphReversed = new HashMap<>();
        Set<String> visited = new HashSet<>(); // Множество посещённых вершин.
        Map<String, Integer> time = new HashMap<>(); // Время посещения для каждой вершины.

        // Чтение входных данных.
        try (Scanner scanner = new Scanner(System.in)) {
            String input = scanner.nextLine(); // Считываем строку с рёбрами графа.
            String[] nodes = input.split("\\s*,\\s*"); // Разбиваем строку на рёбра.

            // Формируем граф и его перевёрнутую версию.
            for (String node : nodes) {
                String[] vertexes = node.split("\\s*->\\s*"); // Разбиваем строку на две вершины.
                // Добавляем рёбра в граф.
                if (graph.containsKey(vertexes[0])) {
                    graph.get(vertexes[0]).add(vertexes[1]);
                } else {
                    List<String> list = new ArrayList<>();
                    list.add(vertexes[1]);
                    graph.put(vertexes[0], list);
                }

                // Добавляем рёбра в перевёрнутый граф.
                if (graphReversed.containsKey(vertexes[1])) {
                    graphReversed.get(vertexes[1]).add(vertexes[0]);
                } else {
                    List<String> list = new ArrayList<>();
                    list.add(vertexes[0]);
                    graphReversed.put(vertexes[1], list);
                }
            }
        }

        // Обходим граф с учётом времени посещения вершин.
        for (String node : graph.keySet()) {
            if (!visited.contains(node)) {
                dfsTime(node, graph, visited, time);
            }
        }

        // Создаём список из записей time и сортируем его.
        List<Entry<String, Integer>> entryList = new ArrayList<>(time.entrySet());
        entryList.sort(new MapEntryComparator());
        String[] vertices = new String[entryList.size()];
        // Переворачиваем список, чтобы получить вершины в порядке убывания времени.
        for (int i = entryList.size() - 1; i > -1; i--) {
            vertices[entryList.size() - 1 - i] = entryList.get(i).getKey();
        }

        // Обходим перевёрнутый граф для поиска сильных связных компонент.
        visited = new HashSet<>();
        for (String node : vertices) {
            if (!visited.contains(node)) {
                List<String> path = new ArrayList<>();
                dfs(node, graphReversed, visited, path);

                // Сортируем путь в лексикографическом порядке.
                path.sort(CharSequence::compare);

                // Выводим вершины каждой сильной связной компоненты.
                for (String n : path) {
                    System.out.print(n);
                }
                System.out.println(); // Переход на новую строку.
            }
        }
    }
}
