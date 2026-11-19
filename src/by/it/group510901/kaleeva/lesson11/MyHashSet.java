package by.it.group510901.kaleeva.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyHashSet<E> implements Set<E> {

    private static final int DEFAULT_CAPACITY = 16;

    private Node<E>[] table;

    private int size;

    // Внутренний класс для узла односвязного списка
    private static class Node<E> {
        E value;
        Node<E> next;

        Node(E value, Node<E> next) {
            this.value = value;
            this.next = next;
        }
    }

    @SuppressWarnings("unchecked")
    public MyHashSet() {
        // Создаем массив узлов
        table = new Node[DEFAULT_CAPACITY];
        size = 0;
    }

    // Превращает любой объект в номер ячейки; null всегда попадает в нулевую ячейку.
    private int hash(Object key) {
        if (key == null) {
            return 0;
        }
        // Берем модуль для предотвращения отрицательных индексов
        return Math.abs(key.hashCode()) % table.length;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        @SuppressWarnings("unchecked")
        Node<E>[] newTable = new Node[DEFAULT_CAPACITY];
        table = newTable;
        size = 0;
    }

    @Override
    public boolean contains(Object o) {
        int index = hash(o);
        Node<E> current = table[index];

        // Проходим по односвязному списку в поисках элемента
        while (current != null) {
            if (o == null ? current.value == null : o.equals(current.value)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public boolean add(E e) {
        int index = hash(e);
        Node<E> current = table[index];

        // Проверяем, есть ли уже такой элемент, чтобы не добавлять дубликаты
        while (current != null) {
            if (e == null ? current.value == null : e.equals(current.value)) {
                return false;
            }
            current = current.next;
        }

        // Добавляем новый элемент в НАЧАЛО списка
        table[index] = new Node<>(e, table[index]);
        size++;
        return true;
    }
    // Удаление: находим элемент в цепочке, запоминая предыдущий, и "выдёргиваем" его из списка.
    @Override
    public boolean remove(Object o) {
        int index = hash(o);
        Node<E> current = table[index];
        Node<E> prev = null;

        while (current != null) {
            if (o == null ? current.value == null : o.equals(current.value)) {
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;

        // Проходим по всем корзинам (ячейкам массива)
        for (Node<E> node : table) {
            Node<E> current = node;
            // Проходим по связному списку внутри каждой корзины
            while (current != null) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(current.value == this ? "(this Collection)" : current.value);
                first = false;
                current = current.next;
            }
        }

        sb.append("]");
        return sb.toString();
    }

    // Методы интерфейса Set, не требуемые в задании. Оставлены как "заглушки"

    @Override
    public Iterator<E> iterator() { throw new UnsupportedOperationException("Метод не реализован"); }

    @Override
    public Object[] toArray() { throw new UnsupportedOperationException("Метод не реализован"); }

    @Override
    public <T> T[] toArray(T[] a) { throw new UnsupportedOperationException("Метод не реализован"); }

    @Override
    public boolean containsAll(Collection<?> c) { throw new UnsupportedOperationException("Метод не реализован"); }

    @Override
    public boolean addAll(Collection<? extends E> c) { throw new UnsupportedOperationException("Метод не реализован"); }

    @Override
    public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException("Метод не реализован"); }

    @Override
    public boolean removeAll(Collection<?> c) { throw new UnsupportedOperationException("Метод не реализован"); }
}

