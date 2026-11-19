package by.it.group510901.kaleeva.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyTreeSet<E> implements Set<E> {

    private static final int DEFAULT_CAPACITY = 10;

    // Внутренний массив для хранения элементов
    private Object[] elements;

    private int size;

    public MyTreeSet() {
        elements = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    // Вспомогательные методы

    // Бинарный поиск элемента.
    // Возвращает индекс, если элемент найден.
    // Если не найден, возвращает отриц число.
    @SuppressWarnings("unchecked")
    private int binarySearch(Object key) {
        int low = 0;
        int high = size - 1;

        Comparable<? super E> k = (Comparable<? super E>) key;

        while (low <= high) {
            int mid = (low + high) >>> 1; // Беззнаковый сдвиг (деление на 2)
            E midVal = (E) elements[mid];

            int cmp = k.compareTo(midVal);

            if (cmp < 0) {
                high = mid - 1;
            } else if (cmp > 0) {
                low = mid + 1;
            } else {
                return mid; // Элемент найден
            }
        }
        return -(low + 1); // Элемент не найден, возвращаем позицию для вставки
    }

    // Если массив заполнен — создаём вдвое больший
    private void ensureCapacity() {
        if (size == elements.length) {
            Object[] newElements = new Object[elements.length * 2];
            // Вручную перекладываем в него все элементы.
            for (int i = 0; i < size; i++) {
                newElements[i] = elements[i];
            }
            elements = newElements;
        }
    }

    // Обязательные методы по заданию

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
        elements = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    @Override
    public boolean contains(Object o) {
        if (o == null || size == 0) return false;
        return binarySearch(o) >= 0;
    }

    @Override
    public boolean add(E e) {
        if (e == null) {
            throw new NullPointerException("Null elements are not supported in TreeSet");
        }

        if (size == 0) {
            elements[0] = e;
            size++;
            return true;
        }

        int index = binarySearch(e);
        if (index >= 0) {
            return false; // Элемент уже существует
        }

        int insertIndex = -index - 1;
        ensureCapacity();

        // Сдвигаем элементы вправо, освобождая место для нового элемента
        for (int i = size; i > insertIndex; i--) {
            elements[i] = elements[i - 1];
        }

        elements[insertIndex] = e;
        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null || size == 0) return false;

        int index = binarySearch(o);
        if (index < 0) {
            return false; // Элемент не найден
        }

        // Сдвигаем элементы влево, затирая удаляемый элемент
        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }

        elements[size - 1] = null; // Очищаем последнюю ссылку для сборщика мусора
        size--;
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c) {
            if (!contains(e)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c) {
            if (add(e)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object e : c) {
            if (remove(e)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        // Проходим по массиву с конца в начало, чтобы безопасно удалять элементы
        // без сбоя индексов для оставшихся
        for (int i = size - 1; i >= 0; i--) {
            if (!c.contains(elements[i])) {
                remove(elements[i]);
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < size; i++) {
            sb.append(elements[i] == this ? "(this Collection)" : elements[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }

        sb.append("]");
        return sb.toString();
    }

    // Неиспользуемые методы интерфейса Set

    @Override
    public Iterator<E> iterator() { throw new UnsupportedOperationException(); }

    @Override
    public Object[] toArray() { throw new UnsupportedOperationException(); }

    @Override
    public <T> T[] toArray(T[] a) { throw new UnsupportedOperationException(); }
}

