package by.it.group310901.sorochuk.lesson12;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

// Класс MyAvlMap реализует интерфейс Map, позволяя хранить пары "ключ-значение"
public class MyAvlMap implements Map<Integer, String> {

    // Вложенный класс, представляющий узел AVL-дерева
    class AVLNode {
        int key; // Ключ узла
        String value; // Значение, связанное с ключом
        int Height; // Высота узла
        AVLNode Left, Right; // Ссылки на левого и правого потомков

        // Конструктор для создания нового узла
        AVLNode(int key, String value) {
            this.key = key;
            this.value = value;
            this.Height = 1; // Новые узлы всегда создаются с высотой 1
        }
    }

    AVLNode Root; // Корень AVL-дерева
    StringBuilder result; // Вспомогательная переменная для хранения результатов операций

    // Метод для получения размера дерева
    @Override
    public int size() {
        return size(Root); // Рекурсивный вызов подсчета количества узлов
    }

    private int size(AVLNode node) {
        if (node == null) {
            return 0; // Если узел пустой, размер равен 0
        }
        return 1 + size(node.Left) + size(node.Right); // Рекурсивно считаем количество узлов в поддеревьях
    }

    // Проверяет, пусто ли дерево
    @Override
    public boolean isEmpty() {
        return Root == null; // Если корень дерева пуст, то дерево пусто
    }

    // Метод для представления карты в строковом виде
    @Override
    public String toString() {
        if (Root == null)
            return "{}"; // Если дерево пустое, возвращается пустой объект
        StringBuilder sb = new StringBuilder().append('{');
        InOrderTraversal(Root, sb); // Выполняем симметричный обход дерева
        sb.replace(sb.length() - 2, sb.length(), "}"); // Убираем последнюю запятую
        return sb.toString();
    }

    // Симметричный обход дерева (in-order traversal)
    private void InOrderTraversal(AVLNode node, StringBuilder sb) {
        if (node != null) {
            InOrderTraversal(node.Left, sb); // Рекурсивный вызов для левого поддерева
            sb.append(node.key + "=" + node.value + ", "); // Добавляем текущий узел
            InOrderTraversal(node.Right, sb); // Рекурсивный вызов для правого поддерева
        }
    }

    // Проверяет, содержит ли дерево указанный ключ
    @Override
    public boolean containsKey(Object key) {
        return Search((Integer) key, Root) != null; // Поиск узла с данным ключом
    }

    // Поиск узла по ключу
    AVLNode Search(Integer key, AVLNode node) {
        if (node == null)
            return null; // Если узел пустой, ключ не найден
        int comparison = key.compareTo(node.key); // Сравниваем ключ с текущим узлом
        if (comparison == 0)
            return node; // Если ключи равны, возвращаем текущий узел

        // Рекурсивный поиск в левом или правом поддереве
        return Search(key, comparison < 0 ? node.Left : node.Right);
    }

    // Получает значение по ключу
    @Override
    public String get(Object key) {
        AVLNode result = Search((Integer) key, Root); // Ищем узел
        return result == null ? null : result.value; // Возвращаем значение или null
    }

    // Добавляет пару "ключ-значение" в карту
    @Override
    public String put(Integer key, String value) {
        result = new StringBuilder(); // Инициализация строки результата
        Root = put(Root, key, value); // Рекурсивный вызов добавления в дерево
        return result.isEmpty() ? null : result.toString(); // Возвращаем результат
    }

    AVLNode put(AVLNode node, Integer key, String value) {
        if (node == null) {
            return new AVLNode(key, value); // Если узел пустой, создаем новый узел
        }
        int comparison = key.compareTo(node.key); // Сравниваем ключи
        if (comparison < 0)
            node.Left = put(node.Left, key, value); // Рекурсивное добавление в левое поддерево
        else if (comparison > 0)
            node.Right = put(node.Right, key, value); // Рекурсивное добавление в правое поддерево
        else {
            // Если ключи равны, обновляем значение, если оно изменилось
            if (!node.value.equalsIgnoreCase(value)) {
                node.value = value;
                result.append("generate" + key); // Фиксация изменения
            }
        }
        return Balance(node); // Балансируем дерево
    }

    // Получение высоты узла
    int Height(AVLNode node) {
        return node == null ? 0 : node.Height;
    }
    
    // Баланс-фактор для узла
    int BalanceFactor(AVLNode node) {
        return node == null ? 0 : Height(node.Left) - Height(node.Right);
    }

    AVLNode RotateRight(AVLNode node)
    {
        AVLNode newRoot = node.Left;
        node.Left = newRoot.Right;
        newRoot.Right = node;
        node.Height = 1 + Math.max(Height(node.Left), Height(node.Right));
        newRoot.Height = 1 + Math.max(Height(newRoot.Left), Height(newRoot.Right));
        return newRoot;
    }

    AVLNode RotateLeft(AVLNode node)
    {
        AVLNode newRoot = node.Right;
        node.Right = newRoot.Left;
        newRoot.Left = node;
        node.Height = 1 + Math.max(Height(node.Left), Height(node.Right));
        newRoot.Height = 1 + Math.max(Height(newRoot.Left), Height(newRoot.Right));
        return newRoot;
    }

    AVLNode Balance(AVLNode node)
    {
        if (node == null)
            return node;

        node.Height = 1 + Math.max(Height(node.Left), Height(node.Right));
        int balanceFactor = BalanceFactor(node);

        if (balanceFactor > 1)
        {
            if (BalanceFactor(node.Left) < 0)
                node.Left = RotateLeft(node.Left);
            return RotateRight(node);
        }

        if (balanceFactor < -1)
        {
            if (BalanceFactor(node.Right) > 0)
                node.Right = RotateRight(node.Right);
            return RotateLeft(node);
        }

        return node;
    }

    @Override
    public String remove(Object key) {
        result = new StringBuilder();
        Root = remove(Root, (Integer) key);
        return result.isEmpty() ? null : result.toString();
    }

    AVLNode remove(AVLNode node, Integer key)
    {
        if (node == null)
            return node;
        int comparison = key.compareTo(node.key);
        if (comparison < 0)
            node.Left = remove(node.Left, key);
        else if (comparison > 0)
            node.Right = remove(node.Right, key);
        else
        {
            result.append("generate" + key);
            if (node.Left == null)
                return node.Right;
            if (node.Right == null)
                return node.Left;

            AVLNode minNode = minValueNode(node.Right);
            node.value = minNode.value;
            node.Right = RemoveMinNode(node.Right);
        }

        return Balance(node);
    }

    AVLNode RemoveMinNode(AVLNode node)
    {
        if (node.Left == null)
            return node.Right;

        node.Left = RemoveMinNode(node.Left);
        return Balance(node);
    }

    AVLNode minValueNode(AVLNode node) {
        return node.Left == null ? node : minValueNode(node.Left);
    }
    @Override
    public void clear() {
        Root = clear(Root);
    }

    AVLNode clear(AVLNode node) {
        if (node == null)
            return null;
        node.Left = clear(node.Left);
        node.Right = clear(node.Right);
        return null;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }


    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) {

    }

    @Override
    public Set<Integer> keySet() {
        return null;
    }

    @Override
    public Collection<String> values() {
        return null;
    }

    @Override
    public Set<Entry<Integer, String>> entrySet() {
        return null;
    }
}
