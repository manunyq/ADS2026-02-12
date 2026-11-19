package by.it.group510901.kaleeva.lesson10;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

// Реализация Queue<E> на основе минимальной двоичной кучи в массиве.
public class MyPriorityQueue<E> implements Queue<E> {

    private Object[] heap = new Object[10]; // хранилище кучи
    private int size;                       // количество элементов

    // Сравнивает два элемента(для минимальной кучи)
    @SuppressWarnings("unchecked")
    private int compare(Object first, Object second) {
        return ((Comparable<E>) first).compareTo((E) second);
    }

    // Удваивает массив, если он заполнен.
    private void ensureCapacity() {
        if (size < heap.length) {
            return;
        }
        Object[] expanded = new Object[heap.length * 2];
        for (int i = 0; i < size; i++) {
            expanded[i] = heap[i];
        }
        heap = expanded;
    }

    // Меняет местами два элемента массива.
    private void swap(int first, int second) {
        Object value = heap[first];
        heap[first] = heap[second];
        heap[second] = value;
    }

    // Поднимает элемент вверх, пока он меньше родителя.
    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (compare(heap[parent], heap[index]) <= 0) {
                return;
            }
            swap(parent, index);
            index = parent;
        }
    }

    // Опускает элемент вниз, пока он больше наименьшего из детей.
    private void siftDown(int index) {
        while (true) {
            int left = index * 2 + 1;
            int right = left + 1;
            int smallest = index;

            if (left < size && compare(heap[left], heap[smallest]) < 0) {
                smallest = left;
            }
            if (right < size && compare(heap[right], heap[smallest]) < 0) {
                smallest = right;
            }
            if (smallest == index) {
                return;
            }

            swap(index, smallest);
            index = smallest;
        }
    }

    // Возвращает порядок элементов как в массиве кучи.
    @Override
    public String toString() {
        String result = "[";
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                result += ", ";
            }
            result += heap[i];
        }
        return result + "]";
    }

    // Возвращает количество элементов в очереди.
    @Override
    public int size() {
        return size;
    }

    // Очищает очередь.
    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            heap[i] = null;
        }
        size = 0;
    }

    // Добавляет элемент: кладём в конец и "поднимаем" до нужного места.
    // null запрещён (бросает NullPointerException).
    @Override
    public boolean add(E element) {
        if (element == null) {
            throw new NullPointerException();
        }
        ensureCapacity();
        heap[size] = element;
        siftUp(size);
        size++;
        return true;
    }

    // Добавляем элемент
    @Override
    public boolean offer(E element) {
        return add(element);
    }

    // Возвращает минимальный элемент без удаления (или null, если пусто).
    @Override
    @SuppressWarnings("unchecked")
    public E peek() {
        if (size == 0) {
            return null;
        }
        return (E) heap[0];
    }

    // Возвращает минимальный элемент; бросает NoSuchElementException, если пусто.
    @Override
    public E element() {
        E result = peek();
        if (result == null) {
            throw new NoSuchElementException();
        }
        return result;
    }

    // Удаляет и возвращает минимальный элемент (или null, если пусто).
    // На место корня ставим последний элемент и "опускаем" его.
    @Override
    public E poll() {
        if (size == 0) {
            return null;
        }
        @SuppressWarnings("unchecked")
        E result = (E) heap[0];
        size--;
        heap[0] = heap[size];
        heap[size] = null;

        if (size > 0) {
            siftDown(0);
        }
        return result;
    }

    // Удаляет и возвращает минимальный элемент; бросает NoSuchElementException, если пусто.
    @Override
    public E remove() {
        E result = poll();
        if (result == null) {
            throw new NoSuchElementException();
        }
        return result;
    }

    // Проверяет, есть ли элемент в очереди (линейный поиск по массиву).
    @Override
    public boolean contains(Object object) {
        if (object == null) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (object.equals(heap[i])) {
                return true;
            }
        }
        return false;
    }

    // Удаляет первое вхождение объекта, затем восстанавливает кучу.
    @Override
    public boolean remove(Object object) {
        if (object == null) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (object.equals(heap[i])) {
                size--;
                heap[i] = heap[size];
                heap[size] = null;
                if (i < size) {
                    siftUp(i);
                    siftDown(i);
                }
                return true;
            }
        }
        return false;
    }

    // Пуста ли очередь.
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // true, если все элементы коллекции есть в очереди.
    @Override
    public boolean containsAll(Collection<?> collection) {
        for (Object object : collection) {
            if (!contains(object)) {
                return false;
            }
        }
        return true;
    }

    // Добавляет все элементы коллекции.
    @Override
    public boolean addAll(Collection<? extends E> collection) {
        boolean changed = false;
        for (E element : collection) {
            add(element);
            changed = true;
        }
        return changed;
    }

    // Удаляет все вхождения элементов коллекции.
    @Override
    public boolean removeAll(Collection<?> collection) {
        return filter(collection, false);
    }

    // Оставляет только элементы, которые есть в переданной коллекции.
    @Override
    public boolean retainAll(Collection<?> collection) {
        return filter(collection, true);
    }

    // Общий метод фильтрации: retain=true — оставить, retain=false — удалить.
    // После фильтрации пересобирает кучу (siftDown для всех нелистовых узлов).
    private boolean filter(Collection<?> collection, boolean retain) {
        int writeIndex = 0;

        for (int readIndex = 0; readIndex < size; readIndex++) {
            Object element = heap[readIndex];
            boolean inCollection = collection.contains(element);
            if (retain == inCollection) {
                heap[writeIndex++] = element;
            }
        }

        boolean changed = writeIndex != size;
        for (int i = writeIndex; i < size; i++) {
            heap[i] = null;
        }
        size = writeIndex;

        for (int i = size / 2 - 1; i >= 0; i--) {
            siftDown(i);
        }
        return changed;
    }

    // Итератор не поддержан.
    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    // Возвращает Object[] ровно нужного размера (порядок элементов — как в куче).
    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        for (int i = 0; i < size; i++) {
            result[i] = heap[i];
        }
        return result;
    }

    // Копирует элементы в переданный массив (или создаёт новый нужного размера).
    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] array) {
        if (array.length < size) {
            return (T[]) java.util.Arrays.copyOf(heap, size, array.getClass());
        }

        for (int i = 0; i < size; i++) {
            array[i] = (T) heap[i];
        }
        if (array.length > size) {
            array[size] = null;
        }
        return array;
    }
}
