package by.it.group510901.kaleeva.lesson12;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;

public class MySplayMap implements NavigableMap<Integer, String> {

    // Узел дерева: добавлен родитель (parent) для быстрого поднятия (splay)
    private static class Node {
        Integer key;
        String value;
        Node left;
        Node right;
        Node parent;

        Node(Integer key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node root;
    private int size;

    // Левый поворот
    private void rotateLeft(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != null) y.left.parent = x;
        y.parent = x.parent;
        if (x.parent == null) root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        y.left = x;
        x.parent = y;
    }

    // Правый поворот
    private void rotateRight(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != null) y.right.parent = x;
        y.parent = x.parent;
        if (x.parent == null) root = y;
        else if (x == x.parent.right) x.parent.right = y;
        else x.parent.left = y;
        y.right = x;
        x.parent = y;
    }

    // Главный метод Splay-дерева: поднимает узел x в корень через серию поворотов (зиг, зиг-зиг, зиг-заг)
    private void splay(Node x) {
        while (x.parent != null) {
            Node p = x.parent;
            Node g = p.parent;
            if (g == null) { // Зиг: родитель - корень
                if (x == p.left) rotateRight(p);
                else rotateLeft(p);
            } else if (x == p.left && p == g.left) { // Зиг-зиг (левый-левый)
                rotateRight(g);
                rotateRight(p);
            } else if (x == p.right && p == g.right) { // Зиг-зиг (правый-правый)
                rotateLeft(g);
                rotateLeft(p);
            } else if (x == p.right && p == g.left) { // Зиг-заг (правый-левый)
                rotateLeft(p);
                rotateRight(g);
            } else { // Зиг-заг (левый-правый)
                rotateRight(p);
                rotateLeft(g);
            }
        }
    }

    // Поиск: если нашли, вызываем splay, чтобы поднять найденный узел в корень
    private Node find(Integer key) {
        Node n = root;
        while (n != null) {
            int cmp = key.compareTo(n.key);
            if (cmp < 0) n = n.left;
            else if (cmp > 0) n = n.right;
            else {
                splay(n);
                return n;
            }
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

    @Override
    public boolean containsKey(Object key) {
        if (key == null) return false;
        return find((Integer) key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return containsValueRec(root, value);
    }

    // Рекурсивный поиск значения
    private boolean containsValueRec(Node n, Object value) {
        if (n == null) return false;
        if (value == null ? n.value == null : value.equals(n.value)) return true;
        return containsValueRec(n.left, value) || containsValueRec(n.right, value);
    }

    @Override
    public String get(Object key) {
        if (key == null) return null;
        Node n = find((Integer) key);
        return n == null ? null : n.value;
    }

    // Вставка: спускаемся, вставляем и поднимаем новый узел в корень
    @Override
    public String put(Integer key, String value) {
        if (key == null) throw new NullPointerException();

        if (root == null) {
            root = new Node(key, value);
            size++;
            return null;
        }

        Node n = root;
        Node parent = null;

        while (n != null) {
            parent = n;
            int cmp = key.compareTo(n.key);
            if (cmp < 0) n = n.left;
            else if (cmp > 0) n = n.right;
            else {
                String old = n.value;
                n.value = value;
                splay(n);
                return old;
            }
        }

        Node node = new Node(key, value);
        node.parent = parent;
        if (key.compareTo(parent.key) < 0) parent.left = node;
        else parent.right = node;

        size++;
        splay(node);
        return null;
    }

    // Удаление: находим, поднимаем в корень, склеиваем левое и правое поддеревья
    @Override
    public String remove(Object key) {
        if (key == null) return null;

        Node n = find((Integer) key);
        if (n == null) return null;

        String old = n.value;
        splay(n);

        if (n.left == null) {
            root = n.right;
            if (root != null) root.parent = null;
        } else {
            Node leftTree = n.left;
            leftTree.parent = null;
            root = leftTree;

            // Ищем максимум в левом поддереве
            Node maxLeft = leftTree;
            while (maxLeft.right != null) maxLeft = maxLeft.right;
            splay(maxLeft);

            // Прикрепляем правое поддерево к максимуму левого
            maxLeft.right = n.right;
            if (n.right != null) n.right.parent = maxLeft;
        }

        size--;
        return old;
    }

    @Override
    public Integer firstKey() {
        if (root == null) throw new java.util.NoSuchElementException();
        Node n = root;
        while (n.left != null) n = n.left;
        return n.key;
    }

    @Override
    public Integer lastKey() {
        if (root == null) throw new java.util.NoSuchElementException();
        Node n = root;
        while (n.right != null) n = n.right;
        return n.key;
    }

    // Возвращает Map с ключами строго меньше toKey
    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        MySplayMap res = new MySplayMap();
        collectHead(root, toKey, res);
        return res;
    }

    private void collectHead(Node n, Integer toKey, MySplayMap res) {
        if (n == null) return;
        if (n.key.compareTo(toKey) < 0) {
            res.put(n.key, n.value);
            collectHead(n.left, toKey, res);
            collectHead(n.right, toKey, res);
        } else {
            collectHead(n.left, toKey, res);
        }
    }

    // Возвращает Map с ключами больше или равными fromKey
    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        MySplayMap res = new MySplayMap();
        collectTail(root, fromKey, res);
        return res;
    }

    private void collectTail(Node n, Integer fromKey, MySplayMap res) {
        if (n == null) return;
        if (n.key.compareTo(fromKey) >= 0) {
            res.put(n.key, n.value);
            collectTail(n.left, fromKey, res);
            collectTail(n.right, fromKey, res);
        } else {
            collectTail(n.right, fromKey, res);
        }
    }

    // Строго меньше заданного
    @Override
    public Integer lowerKey(Integer key) {
        Integer res = null;
        Node n = root;
        while (n != null) {
            if (key.compareTo(n.key) > 0) {
                res = n.key;
                n = n.right;
            } else {
                n = n.left;
            }
        }
        return res;
    }

    // Меньше или равно заданному
    @Override
    public Integer floorKey(Integer key) {
        Integer res = null;
        Node n = root;
        while (n != null) {
            int cmp = key.compareTo(n.key);
            if (cmp == 0) return n.key;
            if (cmp > 0) {
                res = n.key;
                n = n.right;
            } else {
                n = n.left;
            }
        }
        return res;
    }

    // Больше или равно заданному
    @Override
    public Integer ceilingKey(Integer key) {
        Integer res = null;
        Node n = root;
        while (n != null) {
            int cmp = key.compareTo(n.key);
            if (cmp == 0) return n.key;
            if (cmp < 0) {
                res = n.key;
                n = n.left;
            } else {
                n = n.right;
            }
        }
        return res;
    }

    // Строго больше заданного
    @Override
    public Integer higherKey(Integer key) {
        Integer res = null;
        Node n = root;
        while (n != null) {
            if (key.compareTo(n.key) < 0) {
                res = n.key;
                n = n.left;
            } else {
                n = n.right;
            }
        }
        return res;
    }

    @Override
    public Comparator<? super Integer> comparator() { return null; }

    // Печать дерева (симметричный обход)
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        inorder(root, sb);
        sb.append("}");
        return sb.toString();
    }

    private void inorder(Node n, StringBuilder sb) {
        if (n == null) return;
        inorder(n.left, sb);
        if (sb.length() > 1) sb.append(", ");
        sb.append(n.key).append("=").append(n.value);
        inorder(n.right, sb);
    }

    // Заглушки NavigableMap
    @Override
    public Map.Entry<Integer, String> lowerEntry(Integer key) { throw new UnsupportedOperationException(); }
    @Override
    public Map.Entry<Integer, String> floorEntry(Integer key) { throw new UnsupportedOperationException(); }
    @Override
    public Map.Entry<Integer, String> ceilingEntry(Integer key) { throw new UnsupportedOperationException(); }
    @Override
    public Map.Entry<Integer, String> higherEntry(Integer key) { throw new UnsupportedOperationException(); }
    @Override
    public Map.Entry<Integer, String> firstEntry() { throw new UnsupportedOperationException(); }
    @Override
    public Map.Entry<Integer, String> lastEntry() { throw new UnsupportedOperationException(); }
    @Override
    public Map.Entry<Integer, String> pollFirstEntry() { throw new UnsupportedOperationException(); }
    @Override
    public Map.Entry<Integer, String> pollLastEntry() { throw new UnsupportedOperationException(); }
    @Override
    public NavigableMap<Integer, String> descendingMap() { throw new UnsupportedOperationException(); }
    @Override
    public NavigableSet<Integer> navigableKeySet() { throw new UnsupportedOperationException(); }
    @Override
    public NavigableSet<Integer> descendingKeySet() { throw new UnsupportedOperationException(); }
    @Override
    public NavigableMap<Integer, String> subMap(Integer fromKey, boolean fromInclusive, Integer toKey, boolean toInclusive) { throw new UnsupportedOperationException(); }
    @Override
    public NavigableMap<Integer, String> headMap(Integer toKey, boolean inclusive) { throw new UnsupportedOperationException(); }
    @Override
    public NavigableMap<Integer, String> tailMap(Integer fromKey, boolean inclusive) { throw new UnsupportedOperationException(); }
    @Override
    public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) { throw new UnsupportedOperationException(); }
    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) { throw new UnsupportedOperationException(); }
    @Override
    public Set<Integer> keySet() { throw new UnsupportedOperationException(); }
    @Override
    public Collection<String> values() { throw new UnsupportedOperationException(); }
    @Override
    public Set<Map.Entry<Integer, String>> entrySet() { throw new UnsupportedOperationException(); }
}