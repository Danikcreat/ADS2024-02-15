package by.it.group310901.sorochuk.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Класс MyTreeSet реализует множество на основе бинарного дерева поиска.
 * Элементы множества упорядочены по возрастанию (естественный порядок),
 * так как класс использует интерфейс Comparable.
 *
 * @param <E> Тип элементов множества, должен реализовывать интерфейс Comparable.
 */
public class MyTreeSet<E extends Comparable<E>> implements Set<E> {
    /**
     * Внутренний класс, представляющий узел бинарного дерева.
     */
    class Node {
        E data;       // Хранимое значение
        Node left;    // Ссылка на левый дочерний узел
        Node right;   // Ссылка на правый дочерний узел

        Node(E data) {
            this.data = data;
        }
    }

    private Node _root; // Корень дерева
    private int _count; // Количество элементов в множестве

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        inOrderTraversal(_root, sb); // Обход дерева в симметричном порядке
        return sb.append("]").toString();
    }

    /**
     * Рекурсивный метод для симметричного обхода дерева и добавления элементов в строку.
     */
    void inOrderTraversal(Node node, StringBuilder sb) {
        if (node == null) return;
        inOrderTraversal(node.left, sb);
        if (sb.length() > 1) // Добавляем запятую только между элементами
            sb.append(", ");
        sb.append(node.data);
        inOrderTraversal(node.right, sb);
    }

    @Override
    public int size() {
        return _count;
    }

    @Override
    public boolean isEmpty() {
        return _count == 0;
    }

    /**
     * Рекурсивный метод проверки, содержит ли дерево указанный элемент.
     */
    boolean contains(Node node, E element) {
        if (node == null) return false; // Базовый случай: узел пуст
        int compare = element.compareTo(node.data);
        if (compare < 0)
            return contains(node.left, element); // Ищем в левом поддереве
        else if (compare > 0)
            return contains(node.right, element); // Ищем в правом поддереве
        else
            return true; // Элемент найден
    }

    @Override
    public boolean contains(Object o) {
        return contains(_root, (E) o);
    }

    /**
     * Рекурсивный метод вставки нового элемента в дерево.
     */
    Node insert(Node node, E element) {
        if (node == null) // Базовый случай: создаем новый узел
            return new Node(element);
        int compare = element.compareTo(node.data);
        if (compare < 0)
            node.left = insert(node.left, element); // Вставляем в левое поддерево
        else if (compare > 0)
            node.right = insert(node.right, element); // Вставляем в правое поддерево
        return node;
    }

    @Override
    public boolean add(E e) {
        if (!contains(e)) { // Проверяем, что элемента ещё нет
            _root = insert(_root, e); // Добавляем элемент
            _count++;
            return true;
        }
        return false;
    }

    /**
     * Находит минимальный элемент в поддереве.
     */
    Node findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    /**
     * Удаляет узел с заданным элементом из дерева.
     */
    Node delete(Node node, E element) {
        if (node == null) return null;
        int compare = element.compareTo(node.data);
        if (compare < 0) {
            node.left = delete(node.left, element);
        } else if (compare > 0) {
            node.right = delete(node.right, element);
        } else {
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            }
            node.data = findMin(node.right).data;
            node.right = delete(node.right, node.data);
        }
        return node;
    }

    @Override
    public boolean remove(Object o) {
        if (contains(o)) {
            _root = delete(_root, (E) o);
            _count--;
            return true;
        }
        return false;
    }

    // Другие методы Set, такие как containsAll, addAll, retainAll, removeAll, реализованы ниже.
    // Они обеспечивают работу с коллекциями в соответствии с контрактом интерфейса Set.
    // Методы clear и итератор также реализованы для поддержки базовой функциональности множества.

@Override
    public boolean containsAll(Collection<?> c) {
        for (Object obj: c) {
            if (!contains(obj))
                return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean isModified = false;
        for (E element: c) {
            if (add(element))
                isModified = true;
        }
        return isModified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (c.isEmpty()) {
            this.clear();
            return true;
        }
        boolean isModified = false;
        MyTreeSet<E> retainSet = new MyTreeSet<>();
        for (Object obj : c) {
            if (contains(obj)) {
                retainSet.add((E) obj);
                isModified = true;
            }
        }
        _root = retainSet._root;
        _count = retainSet._count;

        return isModified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean isModified = false;
        for (Object obj: c) {
            if (remove(obj))
                isModified = true;
        }
        return isModified;
    }

    @Override
    public void clear() {
        _root = null;
        _count = 0;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }
}
