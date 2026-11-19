package by.it.group510901.kaleeva.lesson10;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

// Реализация интерфейса Deque<E> на основе двусвязного списка.
public class MyLinkedList<E> implements Deque<E> {

    // Узел двусвязного списка: значение + ссылки на соседей.
    private class Node {
        private E value;
        private Node previous;
        private Node next;

        private Node(E value) {
            this.value = value;
        }
    }

    private Node first; // первый узел
    private Node last;  // последний узел
    private int size;   // количество элементов

    // Null-safe сравнение двух объектов.
    private boolean equals(Object first, Object second) {
        if (first == null) {
            return second == null;
        }
        return first.equals(second);
    }

    // Вставляет новый узел в начало списка.
    private void linkFirst(E element) {
        Node node = new Node(element);
        node.next = first;

        if (first == null) {
            last = node;
        } else {
            first.previous = node;
        }

        first = node;
        size++;
    }

    // Вставляет новый узел в конец списка.
    private void linkLast(E element) {
        Node node = new Node(element);
        node.previous = last;

        if (last == null) {
            first = node;
        } else {
            last.next = node;
        }

        last = node;
        size++;
    }

    // Отвязывает узел от списка (обрабатывает случаи head / tail / середины).
    private E unlink(Node node) {
        if (node.previous == null) {
            first = node.next;
        } else {
            node.previous.next = node.next;
        }

        if (node.next == null) {
            last = node.previous;
        } else {
            node.next.previous = node.previous;
        }

        size--;
        return node.value;
    }

    // Возвращает узел по индексу; бросает IndexOutOfBoundsException, если индекс вне диапазона.
    private Node getNode(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", size: " + size);
        }
        Node node = first;
        for (int i = 0; i < index; i++) {
            node = node.next;
        }
        return node;
    }

    // Возвращает строку вида [a, b, c] в порядке от первого к последнему.
    @Override
    public String toString() {
        String result = "[";
        Node node = first;
        while (node != null) {
            if (node != first) {
                result += ", ";
            }
            result += node.value;
            node = node.next;
        }
        return result + "]";
    }

    // Добавляет элемент в конец (делегирует в addLast).
    @Override
    public boolean add(E element) {
        addLast(element);
        return true;
    }

    // Удаляет элемент по индексу и возвращает его значение.
    public E remove(int index) {
        return unlink(getNode(index));
    }

    // Удаляет первое вхождение объекта (по equals); true, если удалили.
    @Override
    public boolean remove(Object object) {
        Node node = first;
        while (node != null) {
            if (equals(object, node.value)) {
                unlink(node);
                return true;
            }
            node = node.next;
        }
        return false;
    }

    // Возвращает количество элементов в списке.
    @Override
    public int size() {
        return size;
    }

    // Добавляет элемент в начало.
    @Override
    public void addFirst(E element) {
        linkFirst(element);
    }

    // Добавляет элемент в конец.
    @Override
    public void addLast(E element) {
        linkLast(element);
    }

    // Возвращает первый элемент без удаления (делегирует в getFirst).
    @Override
    public E element() {
        return getFirst();
    }

    // Возвращает первый элемент; бросает NoSuchElementException, если пусто.
    @Override
    public E getFirst() {
        if (first == null) {
            throw new NoSuchElementException();
        }
        return first.value;
    }

    // Возвращает последний элемент; бросает NoSuchElementException, если пусто.
    @Override
    public E getLast() {
        if (last == null) {
            throw new NoSuchElementException();
        }
        return last.value;
    }

    // Возвращает первый элемент или null, если пусто.
    @Override
    public E peek() {
        return first == null ? null : first.value;
    }

    // То же, что peek() — посмотреть первый элемент.
    @Override
    public E peekFirst() {
        return peek();
    }

    // Возвращает последний элемент или null, если пусто.
    @Override
    public E peekLast() {
        return last == null ? null : last.value;
    }

    // Удаляет и возвращает первый элемент (делегирует в pollFirst).
    @Override
    public E poll() {
        return pollFirst();
    }

    // Удаляет и возвращает первый элемент; null, если пусто.
    @Override
    public E pollFirst() {
        return first == null ? null : unlink(first);
    }

    // Удаляет и возвращает последний элемент; null, если пусто.
    @Override
    public E pollLast() {
        return last == null ? null : unlink(last);
    }

    // Удаляет и возвращает первый элемент (делегирует в removeFirst).
    @Override
    public E remove() {
        return removeFirst();
    }

    // Удаляет и возвращает первый элемент; бросает NoSuchElementException, если пусто.
    @Override
    public E removeFirst() {
        if (first == null) {
            throw new NoSuchElementException();
        }
        return unlink(first);
    }

    // Удаляет и возвращает последний элемент; бросает NoSuchElementException, если пусто.
    @Override
    public E removeLast() {
        if (last == null) {
            throw new NoSuchElementException();
        }
        return unlink(last);
    }

    // Добавляет элемент в конец (делегирует в add).
    @Override
    public boolean offer(E element) {
        return add(element);
    }

    // Добавляет элемент в начало; всегда true.
    @Override
    public boolean offerFirst(E element) {
        addFirst(element);
        return true;
    }

    // Добавляет элемент в конец; всегда true.
    @Override
    public boolean offerLast(E element) {
        addLast(element);
        return true;
    }

    // Семантика стека: положить элемент в начало.
    @Override
    public void push(E element) {
        addFirst(element);
    }

    // Семантика стека: снять первый элемент (с исключением, если пусто).
    @Override
    public E pop() {
        return removeFirst();
    }

    // Удаляет первое вхождение элемента.
    @Override
    public boolean removeFirstOccurrence(Object object) {
        return remove(object);
    }

    // Удаляет последнее вхождение элемента (идём от хвоста к голове).
    @Override
    public boolean removeLastOccurrence(Object object) {
        Node node = last;
        while (node != null) {
            if (equals(object, node.value)) {
                unlink(node);
                return true;
            }
            node = node.previous;
        }
        return false;
    }

    // Проверяет, есть ли элемент в списке.
    @Override
    public boolean contains(Object object) {
        for (Node node = first; node != null; node = node.next) {
            if (equals(object, node.value)) {
                return true;
            }
        }
        return false;
    }

    // Пуст ли список.
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // Очищает список.
    @Override
    public void clear() {
        first = null;
        last = null;
        size = 0;
    }

    // Прямой итератор: от первого узла к последнему.
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Node node = first;

            @Override
            public boolean hasNext() {
                return node != null;
            }

            @Override
            public E next() {
                if (node == null) {
                    throw new NoSuchElementException();
                }
                E value = node.value;
                node = node.next;
                return value;
            }
        };
    }

    // Обратный итератор: от последнего узла к первому.
    @Override
    public Iterator<E> descendingIterator() {
        return new Iterator<E>() {
            private Node node = last;

            @Override
            public boolean hasNext() {
                return node != null;
            }

            @Override
            public E next() {
                if (node == null) {
                    throw new NoSuchElementException();
                }
                E value = node.value;
                node = node.previous;
                return value;
            }
        };
    }

    // Возвращает Object[] ровно нужного размера в порядке от первого к последнему.
    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        int index = 0;
        for (Node node = first; node != null; node = node.next) {
            result[index++] = node.value;
        }
        return result;
    }

    // Копирует элементы в переданный массив (или создаёт новый нужного размера).
    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] array) {
        if (array.length < size) {
            return (T[]) java.util.Arrays.copyOf(toArray(), size, array.getClass());
        }

        int index = 0;
        for (Node node = first; node != null; node = node.next) {
            array[index++] = (T) node.value;
        }
        if (array.length > size) {
            array[size] = null;
        }
        return array;
    }

    // true, если все элементы коллекции есть в списке.
    @Override
    public boolean containsAll(Collection<?> collection) {
        for (Object object : collection) {
            if (!contains(object)) {
                return false;
            }
        }
        return true;
    }

    // Добавляет все элементы коллекции в конец.
    @Override
    public boolean addAll(Collection<? extends E> collection) {
        boolean changed = false;
        for (E element : collection) {
            addLast(element);
            changed = true;
        }
        return changed;
    }

    // Удаляет все вхождения всех элементов коллекции.
    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean changed = false;
        for (Object object : collection) {
            while (remove(object)) {
                changed = true;
            }
        }
        return changed;
    }

    // Оставляет только элементы, которые есть в переданной коллекции.
    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean changed = false;
        Node node = first;
        while (node != null) {
            Node next = node.next;
            if (!collection.contains(node.value)) {
                unlink(node);
                changed = true;
            }
            node = next;
        }
        return changed;
    }
}
