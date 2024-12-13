package by.it.group310901.sorochuk.lesson13;

import java.util.*;

// Класс GraphB представляет граф с возможностью добавления рёбер и проверки на наличие цикла.
class GraphB {
    // Поле для хранения графа в виде списка смежности (где ключ — вершина, а значение — список её соседей).
    private Map<Character, List<Character>> gr;

    // Конструктор инициализирует пустой граф.
    public GraphB() {
        gr = new TreeMap<>();
    }

    // Метод addEdge добавляет ребро между двумя вершинами.
    public void addEdge(char first, char second) {
        // Если вершины first ещё нет в графе, создаём для неё список соседей.
        if (!gr.containsKey(first)) {
            gr.put(first, new ArrayList<>());
        }
        // Добавляем вершину second в список соседей для first.
        gr.get(first).add(second);
    }


    // deep Searching
    // Приватный метод DFS выполняет поиск в глубину для проверки на наличие цикла.
    // Аргументы: node - текущая вершина, glVisited - глобально посещённые вершины, local - локально посещённые вершины.
    private boolean DFS(char node, Set<Character> glVisited, Set<Character> local) {
        // Если текущая вершина уже посещена локально, это цикл.
        if (local.contains(node))
            return true;
        // Если текущая вершина уже посещена глобально, то цикл в этом пути отсутствует.
        if (glVisited.contains(node))
            return false;

        // Добавляем вершину в глобально и локально посещённые множества.
        glVisited.add(node);
        local.add(node);

        // Получаем список соседей текущей вершины (или пустой список, если их нет).
        List<Character> NearNodesList = gr.getOrDefault(node, new ArrayList<>());
        // Рекурсивно обходим соседей текущей вершины.
        for (char tempNode : NearNodesList) {
            if (DFS(tempNode, glVisited, local)) {
                return true; // Если найден цикл, возвращаем true.
            }
        }
        // Убираем вершину из локально посещённых (возвращаясь из рекурсии).
        local.remove(node);
        return false; // Цикл не найден.
    }

    // Метод CycleCheck проверяет граф на наличие циклов.
    public boolean CycleCheck() {
        Set<Character> glVisited = new HashSet<>(); // Глобально посещённые вершины.
        for (char node : gr.keySet()) { // Обходим все вершины графа.
            if (!glVisited.contains(node)) {
                Set<Character> Local = new HashSet<>(); // Локально посещённые вершины.
                if (DFS(node, glVisited, Local)) { // Если найден цикл, возвращаем true.
                    return true;
                }
            }
        }
        return false; // Если циклы не найдены, возвращаем false.
    }

    public static void main(String[] args) {
        // Создаём объект Scanner для чтения ввода.
        Scanner scanner = new Scanner(System.in);

        // Считываем строку, содержащую описание рёбер графа.
        String input = scanner.nextLine();

        // Создаём объект графа.
        GraphB graph = new GraphB();

        // Разбиваем строку на пары рёбер.
        String[] edges = input.split(", ");
        for (String edge : edges) {
            // Разделяем вершины каждой пары.
            String[] vertices = edge.split(" -> ");
            char A = vertices[0].charAt(0); // Первая вершина.
            char B = vertices[1].charAt(0); // Вторая вершина.
            graph.addEdge(A, B); // Добавляем ребро в граф.
        }

        // Проверяем граф на наличие циклов и выводим результат.
        if (graph.CycleCheck()) {
            System.out.print("yes"); // Цикл найден.
        } else {
            System.out.print("no"); // Цикл не найден.
        }

    }
}
