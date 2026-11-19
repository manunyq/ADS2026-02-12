package by.it.group510901.kaleeva.lesson12;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class MyAvlMap implements Map<Integer, String> {

    private Node root;
    private int size;

    private static class Node {
        Integer key;
        String value;
        int height;
        Node left;
        Node right;

        // Конструктор нового узла (при создании высота равна 1)
        Node(Integer key, String value) {
            this.key = key;
            this.value = value;
            this.height = 1;
        }
    }

    private int height(Node n) {
        return n == null ? 0 : n.height;
    }

    // Пересчитывает высоту узла на основе высот его левого и правого ребенка
    private void updateHeight(Node n) {
        n.height = 1 + Math.max(height(n.left), height(n.right));
    }

    // Считает баланс узла (разница высот: левая минус правая)
    private int balance(Node n) {
        return n == null ? 0 : height(n.left) - height(n.right);
    }

    // Малый правый поворот (используется для балансировки)
    private Node rotateRight(Node y) {
        Node x = y.left;
        Node t = x.right;
        x.right = y;
        y.left = t;
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    // Малый левый поворот (используется для балансировки)
    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node t = y.left;
        y.left = x;
        x.right = t;
        updateHeight(x);
        updateHeight(y);
        return y;
    }

    // Проверяет 4 случая и вызывает нужные повороты
    private Node rebalance(Node n) {
        updateHeight(n);
        int b = balance(n);

        if (b > 1 && balance(n.left) >= 0) return rotateRight(n); // Случай LL
        if (b > 1 && balance(n.left) < 0) {                       // Случай LR
            n.left = rotateLeft(n.left);
            return rotateRight(n);
        }
        if (b < -1 && balance(n.right) <= 0) return rotateLeft(n); // Случай RR
        if (b < -1 && balance(n.right) > 0) {                      // Случай RL
            n.right = rotateRight(n.right);
            return rotateLeft(n);
        }
        return n; // Если баланс в норме, возвращаем узел как есть
    }

    // Идем влево/вправо по ключу, обновляем значение или создаем новый узел
    private Node put(Node n, Integer key, String value) {
        if (n == null) return new Node(key, value);

        if (key < n.key) {
            n.left = put(n.left, key, value);
        } else if (key > n.key) {
            n.right = put(n.right, key, value);
        } else {
            n.value = value; // Ключ уже есть, просто обновляем значение
            return n;
        }
        return rebalance(n); // После вставки балансируем дерево
    }

    // Ищет узел с минимальным ключом в поддереве (нужен при удалении)
    private Node min(Node n) {
        while (n.left != null) n = n.left;
        return n;
    }

    // Находим узел и удаляем его (3 случая)
    private Node remove(Node n, Integer key) {
        if (n == null) return null;

        if (key < n.key) {
            n.left = remove(n.left, key);
        } else if (key > n.key) {
            n.right = remove(n.right, key);
        } else {
            if (n.left == null) return n.right;
            if (n.right == null) return n.left;
            Node m = min(n.right);              // Есть оба: ищем минимум справа
            n.key = m.key;                      // Заменяем ключ и значение
            n.value = m.value;
            n.right = remove(n.right, m.key);   // Удаляем дубликат
        }
        return rebalance(n);
    }

    // Обычный поиск узла по ключу
    private Node find(Node n, Integer key) {
        while (n != null) {
            if (key < n.key) n = n.left;
            else if (key > n.key) n = n.right;
            else return n;
        }
        return null;
    }

    @Override
    public int size() { return size; }

    @Override
    public boolean isEmpty() { return size == 0; }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    // Проверяет, есть ли ключ в дереве
    @Override
    public boolean containsKey(Object key) {
        return find(root, (Integer) key) != null;
    }

    // Возвращает значение по ключу (или null, если нет)
    @Override
    public String get(Object key) {
        Node n = find(root, (Integer) key);
        return n == null ? null : n.value;
    }

    // Добавляет элемент: если ключ новый — увеличивает size, возвращает старое значение
    @Override
    public String put(Integer key, String value) {
        if (key == null) throw new NullPointerException("null key not supported");

        Node existing = find(root, key);
        String old = existing == null ? null : existing.value;

        if (existing == null) size++; // Если ключа не было, размер увеличивается
        root = put(root, key, value);

        return old;
    }

    // Удаляет элемент: находит, удаляет, уменьшает size, возвращает старое значение
    @Override
    public String remove(Object key) {
        Node existing = find(root, (Integer) key);
        if (existing == null) return null;

        String old = existing.value;
        root = remove(root, (Integer) key);
        size--;
        return old;
    }

    // Выводит дерево в формате {ключ=значение, ключ=значение}
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        inorder(root, sb);
        sb.append("}");
        return sb.toString();
    }

    // Симметричный обход для вывода в порядке возрастания ключей
    private void inorder(Node n, StringBuilder sb) {
        if (n == null) return;
        inorder(n.left, sb);
        if (sb.length() > 1) sb.append(", ");
        sb.append(n.key).append("=").append(n.value);
        inorder(n.right, sb);
    }

    // Методы-заглушки, которые требует интерфейс Map, но они не нужны в задании
    @Override
    public boolean containsValue(Object value) { throw new UnsupportedOperationException(); }
    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) { throw new UnsupportedOperationException(); }
    @Override
    public Set<Integer> keySet() { throw new UnsupportedOperationException(); }
    @Override
    public Collection<String> values() { throw new UnsupportedOperationException(); }
    @Override
    public Set<Map.Entry<Integer, String>> entrySet() { throw new UnsupportedOperationException(); }
}