package by.it.group510901.kaleeva.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyLinkedHashSet<E> implements Set<E> {

    private static final int DEFAULT_CAPACITY = 16;

    // Массив для хеш-таблицы (корзины с односвязными списками коллизий)
    private Node<E>[] table;
    private int size;

    private Node<E> head;
    private Node<E> tail;

    // Внутренний класс узла
    private static class Node<E> {
        E value;
        Node<E> next;

        Node<E> before;
        Node<E> after;

        Node(E value, Node<E> next) {
            this.value = value;
            this.next = next;
        }
    }

    // При создании заводим пустой массив корзин и пустой список порядка
    @SuppressWarnings("unchecked")
    public MyLinkedHashSet() {
        table = new Node[DEFAULT_CAPACITY];
        size = 0;
        head = null;
        tail = null;
    }

    private int hash(Object key) {
        if (key == null) return 0;
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
        head = null;
        tail = null;
    }

    @Override
    public boolean contains(Object o) {
        int index = hash(o);
        Node<E> current = table[index];

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

        // Проверяем, есть ли уже элемент
        while (current != null) {
            if (e == null ? current.value == null : e.equals(current.value)) {
                return false;
            }
            current = current.next;
        }

        // Создаем новый узел и добавляем в голову односвязного списка коллизий
        Node<E> newNode = new Node<>(e, table[index]);
        table[index] = newNode;

        // Теперь обновляем связи для сохранения порядка добавления (в конец списка)
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.after = newNode;
            newNode.before = tail;
            tail = newNode;
        }

        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int index = hash(o);
        Node<E> current = table[index];
        Node<E> prev = null;

        while (current != null) {
            if (o == null ? current.value == null : o.equals(current.value)) {
                // 1. Удаляем из хеш-таблицы (из односвязного списка коллизий)
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }

                // 2. Удаляем из связного списка порядка добавления
                if (current.before == null) {
                    head = current.after; // Удаляем голову
                } else {
                    current.before.after = current.after;
                }

                if (current.after == null) {
                    tail = current.before; // Удаляем хвост
                } else {
                    current.after.before = current.before;
                }

                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }
        return false;
    }

    // Групповые операции над коллекциями


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
        // Проходим по нашему множеству в порядке добавления
        Node<E> current = head;
        while (current != null) {
            // Запоминаем следующий узел, так как текущий может быть удален
            Node<E> nextNode = current.after;

            if (!c.contains(current.value)) {
                remove(current.value);
                modified = true;
            }
            current = nextNode;
        }
        return modified;
    }


    // Вывод коллекции

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        // Идем строго по указателям after от head до tail
        Node<E> current = head;
        while (current != null) {
            sb.append(current.value == this ? "(this Collection)" : current.value);
            if (current.after != null) {
                sb.append(", ");
            }
            current = current.after;
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

