package by.it.group310901.sorochuk.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

// Объявление собственного класса MyLinkedHashSet, который работает с дженериками (<E>).
public class MyLinkedHashSet<E> implements Set<E> {
    // Вложенный класс Node для представления элементов множества.
    // Каждый узел хранит данные (data), ссылки на следующие узлы в хэш-таблице (next),
    // а также указатели для поддержки порядка добавления (prev и follow).
    class Node<E> {
        E data;            // Данные узла
        Node<E> next;      // Ссылка на следующий узел в хэш-таблице
        Node<E> prev, follow; // Ссылки для порядка добавления
        Node(E e) {
            data = e; // Инициализация узла данными
        }
    }

    static final int START_SIZE = 20; // Начальный размер массива для хранения элементов
    int _size = 0; // Текущее количество элементов в коллекции
    Node<E>[] _items; // Массив хэш-таблицы для хранения узлов

    Node<E> _head, _tail; // Указатели на первый и последний элементы для сохранения порядка добавления

    // Конструктор без параметров (создает коллекцию с размером массива по умолчанию)
    MyLinkedHashSet() {
        this(START_SIZE);
    }

    // Конструктор с заданным размером массива
    MyLinkedHashSet(int size) {
        _items = new Node[size]; // Инициализация массива
    }

    // Переопределение метода toString() для представления множества в строковом формате.
    @Override
    public String toString() {
        StringBuilder line = new StringBuilder("[");
        Node<E> curr = _head; // Стартуем с первого элемента
        while (curr != null) {
            line.append(curr.data); // Добавляем данные текущего узла
            if (curr.follow != null)
                line.append(", "); // Разделяем элементы запятой
            curr = curr.follow; // Переходим к следующему узлу
        }
        line.append("]");
        return line.toString();
    }

    // Возвращает количество элементов в коллекции
    @Override
    public int size() {
        return _size;
    }

    // Проверяет, пустое ли множество
    @Override
    public boolean isEmpty() {
        return _size == 0;
    }

    // Проверяет, содержится ли элемент в коллекции
    @Override
    public boolean contains(Object o) {
        for (Node<E> item : _items) {
            Node<E> curr = item;
            while (curr != null) {
                if (o.equals(curr.data)) { // Если нашли элемент, возвращаем true
                    return true;
                }
                curr = curr.next;
            }
        }
        return false; // Элемент не найден
    }

    // Метод итератора пока не реализован
    @Override
    public Iterator<E> iterator() {
        return null;
    }

    // Хэш-функция для вычисления индекса массива
    int getHash(Object o) {
        return (o.hashCode() & 0x7FFFFFFF) % _items.length;
    }

    // Добавление нового узла в связный список (для поддержки порядка добавления)
    void addNode(Node<E> newNode) {
        if (_head == null) // Если список пуст
            _head = newNode;
        else {
            _tail.follow = newNode; // Привязываем новый элемент к концу
            newNode.prev = _tail;
        }
        _tail = newNode; // Новый узел становится последним
    }

    // Удаление узла из связного списка (обновление связей prev и follow)
    void removeNode(Node<E> newNode) {
        if (newNode.follow != null) {
            newNode.follow.prev = newNode.prev;
        } else {
            _tail = newNode.prev; // Если это последний узел
        }
        if (newNode.prev != null) {
            newNode.prev.follow = newNode.follow;
        } else {
            _head = newNode.follow; // Если это первый узел
        }
    }

    // Увеличение размера массива и перераспределение элементов
    void resize() {
        Node<E>[] newItems = new Node[_items.length * 2];
        for (Node<E> curr : _items) {
            while (curr != null) {
                Node<E> next = curr.next;
                int newInd = (curr.data.hashCode() & 0x7FFFFFFF) % newItems.length;
                curr.next = newItems[newInd];
                newItems[newInd] = curr;
                curr = next;
            }
        }
        _items = newItems;
    }

    // Удаление элемента из множества
    @Override
    public boolean remove(Object o) {
        int ind = getHash(o);
        Node<E> curr = _items[ind];
        Node<E> prev = null;
        while (curr != null) {
            if (o.equals(curr.data)) {
                if (prev == null) {
                    _items[ind] = curr.next;
                } else {
                    prev.next = curr.next;
                }
                _size--;
                removeNode(curr); // Удаление из связного списка
                return true;
            }
            prev = curr;
            curr = curr.next;
        }
        return false;
    }

    // Удаление всех элементов
    @Override
    public void clear() {
        for (int i = 0; i < _items.length; i++) {
            _items[i] = null;
        }
        _size = 0;
        _head = null;
        _tail = null;
    }

    // Добавление элемента в множество
    @Override
    public boolean add(Object o) {
        Node<E> newNode = new Node<E>((E) o);
        int ind = getHash(o);
        Node<E> curr = _items[ind];
        while (curr != null) {
            if (curr.data.equals(o)) {
                return false; // Элемент уже существует
            }
            curr = curr.next;
        }
        newNode.next = _items[ind];
        _items[ind] = newNode;
        addNode(newNode); // Добавление в связный список
        if (++_size > _items.length * 0.7) // Если превышен коэффициент загрузки
            resize();
        return true;
    }

    // Другие методы (addAll, removeAll, retainAll, containsAll) реализованы по стандартам интерфейса Set.

    @Override
    public boolean addAll(Collection c) {
        boolean isModified = false;
        for(Object item : c) {
            if(add(item)){
                isModified = true;
            }
        }
        return isModified;
    }

    @Override
    public boolean removeAll(Collection c) {
        boolean isModified = false;
        for(Object item : c) {
            if(remove(item)){
                isModified = true;
            }
        }
        return isModified;
    }

    @Override
    public boolean retainAll(Collection c) {
        if (c.isEmpty()){
            clear();
            return true;
        }
        boolean isModified = false;
        Node<E> curr = _head;
        while (curr != null) {
            Node<E> next = curr.follow;
            if (!c.contains(curr.data)) {
                remove(curr.data);
                isModified = true;
            }
            curr = next;
        }
        return isModified;
    }

    @Override
    public boolean containsAll(Collection c) {
        for (Object item : c){
            if(!contains(item))
                return false;
        }
        return true;
    }
    //------------------------------



    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public Object[] toArray(Object[] a) {
        return new Object[0];
    }
}
