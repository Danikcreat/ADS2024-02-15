package by.it.group310901.sorochuk.lesson12;

import java.util.*;
// Реализация красно-черного дерева как упорядоченной карты (SortedMap)
public class MyRbMap implements SortedMap<Integer, String> {
    // Корневой узел дерева
    Node Root;

    // Перечисление для представления цветов узлов
    enum Color {
        RED, BLACK
    }

    // Класс узла дерева
    class Node {
        Integer key; // Ключ узла
        String value; // Значение узла
        Node parent, left, right; // Родительский и дочерние узлы
        Color color; // Цвет узла

        // Конструктор для создания нового узла
        public Node(Color color, Integer key, String value) {
            this.color = color;
            this.key = key;
            this.value = value;
        }
    }

    // Метод для представления дерева в виде строки
    @Override
    public String toString() {
        if (Root == null)
            return "{}"; // Если дерево пустое
        StringBuilder sb = new StringBuilder().append("{");
        inOrderTraversal(Root, sb); // Обход дерева в симметричном порядке
        sb.replace(sb.length() - 2, sb.length(), ""); // Удаление последней запятой
        sb.append("}");
        return sb.toString();
    }

    // Вспомогательный метод для обхода дерева в симметричном порядке
    private void inOrderTraversal(Node node, StringBuilder sb) {
        if (node != null) {
            inOrderTraversal(node.left, sb);
            sb.append(node.key + "=" + node.value + ", ");
            inOrderTraversal(node.right, sb);
        }
    }

    // Метод для получения количества узлов в дереве
    @Override
    public int size() {
        return size(Root);
    }

    // Рекурсивный метод подсчета узлов
    int size(Node node) {
        if (node == null) {
            return 0; // Если узел пустой
        }
        return size(node.left) + size(node.right) + 1; // Левый + правый + текущий узел
    }

    // Метод проверки, пустое ли дерево
    @Override
    public boolean isEmpty() {
        return Root == null;
    }

    // Метод проверки наличия ключа в дереве
    @Override
    public boolean containsKey(Object key) {
        return SearchKey((Integer) key, Root) != null;
    }

    // Рекурсивный поиск узла по ключу
    Node SearchKey(Integer key, Node node) {
        if (node == null)
            return null; // Узел не найден
        int comparison = key.compareTo(node.key);
        if (comparison == 0)
            return node; // Узел найден

        // Рекурсивный поиск в левом или правом поддереве
        return SearchKey(key, comparison < 0 ? node.left : node.right);
    }

    // Метод проверки наличия значения в дереве
    @Override
    public boolean containsValue(Object value) {
        return containsValue(Root, value);
    }

    // Рекурсивный поиск значения в дереве
    boolean containsValue(Node node, Object value) {
        if (node == null) {
            return false; // Значение не найдено
        }
        if (value.equals(node.value)) {
            return true; // Значение найдено
        }
        return containsValue(node.left, value) || containsValue(node.right, value);
    }

    // Метод для получения значения по ключу
    @Override
    public String get(Object key) {
        Node node = SearchKey((Integer) key, Root);
        return (node != null) ? node.value : null;
    }

    // Метод для добавления нового узла в дерево
    @Override
    public String put(Integer key, String value) {
        if (Root == null) {
            // Если дерево пустое, создаем корень черного цвета
            Root = new Node(Color.BLACK, key, value);
        } else {
            Node newNode = new Node(Color.RED, key, value); // Новый узел красного цвета
            Node current = Root;
            while (true) {
                newNode.parent = current;
                if (key.compareTo(current.key) < 0) {
                    if (current.left == null) {
                        current.left = newNode;
                        break;
                    } else {
                        current = current.left;
                    }
                } else if (key.compareTo(current.key) > 0) {
                    if (current.right == null) {
                        current.right = newNode;
                        break;
                    } else {
                        current = current.right;
                    }
                } else {
                    // Если ключ уже существует, обновляем значение
                    String oldValue = current.value;
                    current.value = value;
                    return oldValue;
                }
            }

            balanceAfterInsert(newNode); // Балансировка после вставки
        }
        return null;
    }

    // Балансировка дерева после вставки нового узла
    void balanceAfterInsert(Node node) {
        while (node != Root && node.color == Color.RED && node.parent.color == Color.RED) {
            Node parent = node.parent;
            Node grandparent = parent.parent;

            if (parent == grandparent.left) {
                Node uncle = grandparent.right;
                if (uncle != null && uncle.color == Color.RED) {
                    parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    grandparent.color = Color.RED;
                    node = grandparent;
                } else {
                    if (node == parent.right) {
                        node = parent;
                        RotateLeft(node);
                    }
                    parent.color = Color.BLACK;
                    grandparent.color = Color.RED;
                    RotateRight(grandparent);
                }
            } else {
                Node uncle = grandparent.left;
                if (uncle != null && uncle.color == Color.RED) {
                    parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    grandparent.color = Color.RED;
                    node = grandparent;
                } else {
                    if (node == parent.left) {
                        node = parent;
                        RotateRight(node);
                    }
                    parent.color = Color.BLACK;
                    grandparent.color = Color.RED;
                    RotateLeft(grandparent);
                }
            }
        }

        Root.color = Color.BLACK; // Корень всегда черного цвета
    }

    Node getSuccessor(Node node) {
        Node successor = null;
        Node current = node.right;

        while (current != null) {
            successor = current;
            current = current.left;
        }

        return successor;
    }

    void deleteNode(Node node) {
        Node replacement;
        if (node.left != null && node.right != null) {
            Node successor = getSuccessor(node);
            node.key = successor.key;
            node.value = successor.value;
            node = successor;
        }

        replacement = (node.left != null) ? node.left : node.right;

        if (replacement != null) {
            replacement.parent = node.parent;
            if (node.parent == null) {
                Root = replacement;
            } else if (node == node.parent.left) {
                node.parent.left = replacement;
            } else {
                node.parent.right = replacement;
            }

            if (node.color == Color.BLACK) {
                balanceDeletion(replacement);
            }
        } else if (node.parent == null) {
            Root = null;
        } else {
            if (node.color == Color.BLACK) {
                balanceDeletion(node);
            }

            if (node.parent != null) {
                if (node == node.parent.left) {
                    node.parent.left = null;
                } else if (node == node.parent.right) {
                    node.parent.right = null;
                }
                node.parent = null;
            }
        }
    }


    Color getColor(Node node) {
        return (node == null) ? Color.BLACK : node.color;
    }

    void setColor(Node node, Color color) {
        if (node != null) {
            node.color = color;
        }
    }

    void RotateLeft(Node node)
    {
        Node right = node.right;
        node.right = right.left;
        if (right.left != null)
            right.left.parent = node;
        right.parent = node.parent;
        if (node.parent == null)
            Root = right;
        else if (node == node.parent.left)
            node.parent.left = right;
        else
            node.parent.right = right;
        right.left = node;
        node.parent = right;
    }

    void RotateRight(Node node)
    {
        Node left = node.left;
        node.left = left.right;
        if (left.right != null)
            left.right.parent = node;
        left.parent = node.parent;
        if (node.parent == null)
            Root = left;
        else if (node == node.parent.right)
            node.parent.right = left;
        else
            node.parent.left = left;
        left.right = node;
        node.parent = left;
    }

    void balanceDeletion(Node node) {
        while (node != Root && getColor(node) == Color.BLACK) {
            if (node == node.parent.left) {
                Node sibling = node.parent.right;
                if (getColor(sibling) == Color.RED) {
                    setColor(sibling, Color.BLACK);
                    setColor(node.parent, Color.RED);
                    RotateLeft(node.parent);
                    sibling = node.parent.right;
                }
                if (getColor(sibling.left) == Color.BLACK && getColor(sibling.right) == Color.BLACK) {
                    setColor(sibling, Color.RED);
                    node = node.parent;
                } else {
                    if (getColor(sibling.right) == Color.BLACK) {
                        setColor(sibling.left, Color.BLACK);
                        setColor(sibling, Color.RED);
                        RotateRight(sibling);
                        sibling = node.parent.right;
                    }
                    setColor(sibling, getColor(node.parent));
                    setColor(node.parent, Color.BLACK);
                    setColor(sibling.right, Color.BLACK);
                    RotateLeft(node.parent);
                    node = Root;
                }
            } else {
                Node sibling = node.parent.left;
                if (getColor(sibling) == Color.RED) {
                    setColor(sibling, Color.BLACK);
                    setColor(node.parent, Color.RED);
                    RotateRight(node.parent);
                    sibling = node.parent.left;
                }
                if (getColor(sibling.right) == Color.BLACK && getColor(sibling.left) == Color.BLACK) {
                    setColor(sibling, Color.RED);
                    node = node.parent;
                } else {
                    if (getColor(sibling.left) == Color.BLACK) {
                        setColor(sibling.right, Color.BLACK);
                        setColor(sibling, Color.RED);
                        RotateLeft(sibling);
                        sibling = node.parent.left;
                    }
                    setColor(sibling, getColor(node.parent));
                    setColor(node.parent, Color.BLACK);
                    setColor(sibling.left, Color.BLACK);
                    RotateRight(node.parent);
                    node = Root;
                }
            }
        }

        setColor(node, Color.BLACK);
    }

    @Override
    public String remove(Object key) {
        Node node = SearchKey((Integer) key, Root);
        if (node != null) {
            String oldValue = node.value;
            deleteNode(node);
            return oldValue;
        }
        return null;
    }

    @Override
    public void clear() {
        Root = clear(Root);
    }

    Node clear(Node node) {
        if (node == null)
            return null;
        node.left = clear(node.left);
        node.right = clear(node.right);
        return null;
    }

    @Override
    public Integer firstKey() {
        Node first = firstNode(Root);
        return (first != null) ? first.key : null;
    }

    Node firstNode(Node node) {
        while (node != null && node.left != null) {
            node = node.left;
        }
        return node;
    }

    @Override
    public Integer lastKey() {
        Node last = lastNode(Root);
        return (last != null) ? last.key : null;
    }

    Node lastNode(Node node) {
        while (node != null && node.right != null) {
            node = node.right;
        }
        return node;
    }

    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        MyRbMap subMap = new MyRbMap();
        headMap(Root, toKey, subMap);
        return subMap;
    }

    void headMap(Node node, Integer toKey, MyRbMap subMap) {
        if (node == null) {
            return;
        }

        if (node.key.compareTo(toKey) < 0) {
            subMap.put(node.key, node.value);
            headMap(node.right, toKey, subMap);
        }

        headMap(node.left, toKey, subMap);
    }


    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        MyRbMap subMap = new MyRbMap();
        tailMap(Root, fromKey, subMap);
        return subMap;
    }

    void tailMap(Node node, Integer fromKey, MyRbMap subMap) {
        if (node == null) {
            return;
        }

        if (node.key.compareTo(fromKey) >= 0) {
            subMap.put(node.key, node.value);
            tailMap(node.left, fromKey, subMap);
        }

        tailMap(node.right, fromKey, subMap);
    }


    ///////////////////////

    @Override
    public Set<Integer> keySet() {
        return null;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) {

    }


    @Override
    public Collection<String> values() {
        return null;
    }

    @Override
    public Set<Entry<Integer, String>> entrySet() {
        return null;
    }

    @Override
    public Comparator<? super Integer> comparator() {
        return null;
    }

    @Override
    public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        return null;
    }
}
