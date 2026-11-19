package by.it.group510901.kaleeva.lesson10;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

// Реализация интерфейса Deque<E> на основе кольцевого массива.
public class MyArrayDeque<E> implements Deque<E> {

    private Object[] elements = new Object[10]; // хранилище
    private int head;                            // индекс первого элемента
    private int size;                            // количество элементов

    // Переводит логическую позицию в физический индекс массива.
    private int getIndex(int position) {
        return (head + position) % elements.length;
    }

    // перестройка кольцевого массива в линейный.
    private void ensureCapacity() {
        if (size < elements.length) {
            return;
        }
        //Удваивает массив, если он заполнен.
        Object[] expanded = new Object[elements.length * 2];
        for (int i = 0; i < size; i++) {
            expanded[i] = elements[getIndex(i)];
        }
        elements = expanded;
        head = 0;
    }

    // Возвращает элемент по логическому индексу (с учетом смещения head).
    @SuppressWarnings("unchecked")
    private E getElement(int position) {
        return (E) elements[getIndex(position)];
    }

    // Null-safe сравнение двух объектов
    private boolean equals(Object first, Object second) {
        if (first == null) {
            return second == null;
        }
        return first.equals(second);
    }

    //Метод собирает строку из элементов, элементы выводятся в правильном порядке (от головы к хвосту)
    @Override
    public String toString() {
        String result = "[";
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                result += ", ";
            }
            result += elements[getIndex(i)];
        }
        return result + "]";
    }

    // Возвращает количество элементов
    @Override
    public int size() {
        return size;
    }

    // Добавляет элемент в конец
    @Override
    public boolean add(E element) {
        addLast(element);
        return true;
    }

    // Добавляет элемент в начало (сдвигает head назад по кругу).
    @Override
    public void addFirst(E element) {
        ensureCapacity();
        head = (head - 1 + elements.length) % elements.length;
        elements[head] = element;
        size++;
    }

    // Добавляет элемент в конец.
    @Override
    public void addLast(E element) {
        ensureCapacity();
        elements[getIndex(size)] = element;
        size++;
    }

    // Возвращает первый элемент без удаления.
    @Override
    public E element() {
        return getFirst();
    }

    // Возвращает первый элемент; NoSuchElementException, если пусто.
    @Override
    public E getFirst() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return getElement(0);
    }

    // Возвращает последний элемент; NoSuchElementException, если пусто.
    @Override
    public E getLast() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return getElement(size - 1);
    }

    // Возвращает первый элемент или null, если пусто.
    @Override
    public E peek() {
        return size == 0 ? null : getElement(0);
    }

    // Возвращаем первый элемент.
    @Override
    public E peekFirst() {
        return peek();
    }

    // Возвращает последний элемент или null, если пусто.
    @Override
    public E peekLast() {
        return size == 0 ? null : getElement(size - 1);
    }

    // Удаляет и возвращает первый элемент.
    @Override
    public E poll() {
        return pollFirst();
    }

    // Удаляет и возвращает первый элемент; null, если пусто.
    @Override
    public E pollFirst() {
        if (size == 0) {
            return null;
        }
        E result = getElement(0);
        elements[head] = null;
        head = (head + 1) % elements.length;
        size--;
        return result;
    }

    // Удаляет и возвращает последний элемент; null, если пусто.
    @Override
    public E pollLast() {
        if (size == 0) {
            return null;
        }
        int lastIndex = getIndex(size - 1);
        E result = getElement(size - 1);
        elements[lastIndex] = null;
        size--;
        return result;
    }

    // Удаляет и возвращает первый элемент.
    @Override
    public E remove() {
        return removeFirst();
    }

    // Удаляет и возвращает первый элемент; NoSuchElementException, если пусто.
    @Override
    public E removeFirst() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return pollFirst();
    }

    // Удаляет и возвращает последний элемент; NoSuchElementException, если пусто.
    @Override
    public E removeLast() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return pollLast();
    }

    // Добавляет элемент.
    @Override
    public boolean offer(E element) {
        return add(element);
    }

    // Добавляет элемент в начало; всегда будет true.
    @Override
    public boolean offerFirst(E element) {
        addFirst(element);
        return true;
    }

    // Добавляет элемент в конец; всегда будет true.
    @Override
    public boolean offerLast(E element) {
        addLast(element);
        return true;
    }

    // Кладем в начало.
    @Override
    public void push(E element) {
        addFirst(element);
    }

    // Удаляет и возвращает первый элемент в очереди.
    @Override
    public E pop() {
        return removeFirst();
    }

    // Удаляет первое вхождение элемента.
    @Override
    public boolean removeFirstOccurrence(Object object) {
        return removeObject(object, false);
    }

    // Удаляет последнее вхождение элемента.
    @Override
    public boolean removeLastOccurrence(Object object) {
        return removeObject(object, true);
    }

    // Ищет элемент (с начала или с конца), сдвигает элементы, уменьшает size.
    private boolean removeObject(Object object, boolean fromEnd) {
        int found = -1;
        int start = fromEnd ? size - 1 : 0;
        int finish = fromEnd ? -1 : size;
        int step = fromEnd ? -1 : 1;

        for (int i = start; i != finish; i += step) {
            if (equals(object, getElement(i))) {
                found = i;
                break;
            }
        }

        if (found < 0) {
            return false;
        }

        for (int i = found; i < size - 1; i++) {
            elements[getIndex(i)] = elements[getIndex(i + 1)];
        }
        elements[getIndex(size - 1)] = null;
        size--;
        return true;
    }

    // Удаляет первое вхождение объекта (из Collection).
    @Override
    public boolean remove(Object object) {
        return removeFirstOccurrence(object);
    }

    // Проверяет, есть ли элемент в деке.
    @Override
    public boolean contains(Object object) {
        for (int i = 0; i < size; i++) {
            if (equals(object, getElement(i))) {
                return true;
            }
        }
        return false;
    }

    // Пуст ли дек.
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // Очищает дек: обнуляет ячейки, head = 0, size = 0.
    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[getIndex(i)] = null;
        }
        head = 0;
        size = 0;
    }

    // Прямой итератор: от первого элемента к последнему.
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int position;

            @Override
            public boolean hasNext() {
                return position < size;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return getElement(position++);
            }
        };
    }

    // Обратный итератор: от последнего элемента к первому.
    @Override
    public Iterator<E> descendingIterator() {
        return new Iterator<E>() {
            private int position = size - 1;

            @Override
            public boolean hasNext() {
                return position >= 0;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return getElement(position--);
            }
        };
    }

    // Возвращает массив Object[], содержащий все элементы дека.
    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        for (int i = 0; i < size; i++) {
            result[i] = getElement(i);
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

        for (int i = 0; i < size; i++) {
            array[i] = (T) getElement(i);
        }
        if (array.length > size) {
            array[size] = null;
        }
        return array;
    }

    // true, если все элементы коллекции есть в деке.
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
        for (int i = size - 1; i >= 0; i--) {
            if (!collection.contains(getElement(i))) {
                removeObject(getElement(i), false);
                changed = true;
            }
        }
        return changed;
    }
}
