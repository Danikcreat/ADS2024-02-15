package by.it.group310901.sorochuk.lesson13;

import java.util.*;

// Класс GraphA реализует граф с помощью отображения вершин в список соседей и поддерживает топологическую сортировку.
class GraphA {
    // Граф, представленный с помощью TreeMap, который сортирует ключи в обратном порядке.
    private Map<Character, List<Character>> gr;

    // Конструктор, инициализирует граф.
    public GraphA() {
        gr = new TreeMap<>(Collections.reverseOrder()); // Сортировка ключей графа в обратном порядке.
    }

    // Метод для добавления ребра в граф.
    public void addEdge(char first, char second) {
        // Если вершина ещё не существует в графе, добавляем её.
        if (!gr.containsKey(first)) {
            gr.put(first, new ArrayList<>());
        }
        // Добавляем вторую вершину в список соседей первой вершины.
        gr.get(first).add(second);
    }

    // Метод для выполнения топологической сортировки графа.
    public void topologicalSort(){
        // Сортируем соседей каждой вершины в обратном порядке.
        for (List<Character> temp : gr.values()) {
            Collections.sort(temp, Collections.reverseOrder());
        }

        // Стек для хранения результата сортировки.
        Stack<Character> stack = new Stack<>();
        // Множество для отслеживания посещённых вершин.
        Set<Character> visited = new HashSet<>();

        // Проходим по всем вершинам графа и вызываем DFS для каждой не посещённой вершины.
        for (char tmpNode : gr.keySet()) {
            if (!visited.contains(tmpNode)) {
                DFS(tmpNode, visited, stack);
            }
        }

        // Выводим результат топологической сортировки.
        while (!stack.isEmpty()) {
            System.out.print(stack.pop() + " ");
        }
    }

    // Метод для выполнения обхода в глубину (DFS) и добавления вершин в стек.
    private void DFS(char node, Set<Character> visited, Stack<Character> stack) {
        visited.add(node); // Помечаем вершину как посещённую.

        // Рекурсивно вызываем DFS для всех соседей текущей вершины.
        for (char tempnode : gr.getOrDefault(node, new ArrayList<>())) {
            if (!visited.contains(tempnode)) {
                DFS(tempnode, visited, stack);
            }
        }

        // После обхода всех соседей добавляем вершину в стек.
        stack.push(node);
    }

    // Главный метод для чтения входных данных и запуска топологической сортировки.
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Чтение входных данных (ребра графа).
        String input = scanner.nextLine();

        // Создание экземпляра графа.
        GraphA graph = new GraphA();

        // Разделение входных данных на рёбра и добавление их в граф.
        String[] edges = input.split(", ");
        for (String edge : edges) {
            String[] vert = edge.split(" -> ");
            char A = vert[0].charAt(0); // Первая вершина.
            char B = vert[1].charAt(0); // Вторая вершина.
            graph.addEdge(A, B); // Добавляем ребро в граф.
        }

        // Выполнение топологической сортировки.
        graph.topologicalSort();
    }
}
