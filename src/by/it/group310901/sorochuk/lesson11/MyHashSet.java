package by.it.group310901.sorochuk.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyHashSet<E> implements Set<E> {
    // Узел для хранения данных и ссылки на следующий элемент
    class Node<E> {
        E data;
        Node<E> next;

        Node(E data) {
            this.data = data;
        }
    }

    static final int DEFAULT_CAPACITY = 32; // Начальный размер хэш-таблицы
    private Node<E>[] items; // Массив для хранения цепочек элементов
    private int count; // Количество элементов в множестве

    // Конструктор по умолчанию
    public MyHashSet() {
        this(DEFAULT_CAPACITY);
    }

    // Конструктор с указанием начальной ёмкости
    public MyHashSet(int capacity) {
        items = new Node[capacity];
    }

    // Вычисление индекса в массиве на основе хэша
    private int getHash(Object value) {
        return (value.hashCode() & 0x7FFFFFFF) % items.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Node<E> current : items) {
            while (current != null) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(current.data);
                first = false;
                current = current.next;
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public boolean isEmpty() {
        return count == 0;
    }

    @Override
    public boolean contains(Object o) {
        Node<E> current = items[getHash(o)];
        while (current != null) {
            if (current.data.equals(o)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public boolean add(E e) {
        int index = getHash(e);
        Node<E> current = items[index];

        // Проверяем, есть ли уже элемент
        while (current != null) {
            if (current.data.equals(e)) {
                return false; // Дубликаты не добавляются
            }
            current = current.next;
        }

        // Добавляем элемент в начало цепочки
        Node<E> newNode = new Node<>(e);
        newNode.next = items[index];
        items[index] = newNode;
        count++;

        // Увеличиваем размер хэш-таблицы, если необходимо
        if (count > items.length * 0.75) {
            resize();
        }
        return true;
    }

    // Увеличение размера массива и перераспределение элементов
    private void resize() {
        Node<E>[] newItems = new Node[items.length * 2];
        for (Node<E> current : items) {
            while (current != null) {
                Node<E> next = current.next;
                int newIndex = (current.data.hashCode() & 0x7FFFFFFF) % newItems.length;
                current.next = newItems[newIndex];
                newItems[newIndex] = current;
                current = next;
            }
        }
        items = newItems;
    }

    @Override
    public boolean remove(Object o) {
        int index = getHash(o);
        Node<E> current = items[index];
        Node<E> previous = null;

        // Ищем элемент в цепочке
        while (current != null) {
            if (current.data.equals(o)) {
                if (previous == null) {
                    items[index] = current.next;
                } else {
                    previous.next = current.next;
                }
                count--;
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
    }

    @Override
    public void clear() {
        for (int i = 0; i < items.length; i++) {
            items[i] = null;
        }
        count = 0;
    }


    ////////////////////////////////////////////////////////////////////////

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

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }
}
