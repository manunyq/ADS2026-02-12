package by.it.group510901.kaleeva.lesson12;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

public class MyRbMap implements SortedMap<Integer, String> {

    // Константы цветов (RED = true, BLACK = false)
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    // Корень дерева и размер
    private Node root;
    private int size;

    // Узел КЧ-дерева: добавлен родитель (parent) и цвет (color)
    private static class Node {
        Integer key;
        String value;
        Node left;
        Node right;
        Node parent;
        boolean color;

        Node(Integer key, String value, boolean color, Node parent) {
            this.key = key;
            this.value = value;
            this.color = color;
            this.parent = parent;
        }
    }

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

    // Правый поворот (симметричен левому)
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

    // Восстановление свойств КЧ-дерева после ВСТАВКИ (перекраска и повороты)
    private void fixInsert(Node n) {
        while (n != root && n.parent.color == RED) {
            Node g = n.parent.parent;
            if (n.parent == g.left) {
                Node u = g.right; // Дядя
                if (u != null && u.color == RED) {
                    n.parent.color = BLACK; // Случай 1: дядя красный
                    u.color = BLACK;
                    g.color = RED;
                    n = g;
                } else {
                    if (n == n.parent.right) { // Случай 2: дядя черный
                        n = n.parent;
                        rotateLeft(n);
                    }
                    n.parent.color = BLACK;    // Случай 3: дядя черный
                    g.color = RED;
                    rotateRight(g);
                }
            } else {
                Node u = g.left; // Дядя (симметрично)
                if (u != null && u.color == RED) {
                    n.parent.color = BLACK;
                    u.color = BLACK;
                    g.color = RED;
                    n = g;
                } else {
                    if (n == n.parent.left) {
                        n = n.parent;
                        rotateRight(n);
                    }
                    n.parent.color = BLACK;
                    g.color = RED;
                    rotateLeft(g);
                }
            }
        }
        root.color = BLACK; // Корень всегда черный
    }

    // Восстановление свойств КЧ-дерева после УДАЛЕНИЯ
    // Передаем parent, чтобы не ловить NullPointerException, когда x == null
    private void fixRemove(Node x, Node parent) {
        // Работаем, пока текущий узел черный (или null) и мы не дошли до корня
        while (x != root && (x == null || x.color == BLACK)) {
            if (parent == null) break;

            // Определяем, с какой стороны от родителя находится проблемный узел
            boolean isLeft = (x == parent.left);

            if (isLeft) {
                Node sib = parent.right; // Наш "брат"

                // СЛУЧАЙ 1: Брат красный. Перекрашиваем и крутим.
                if (sib != null && sib.color == RED) {
                    sib.color = BLACK;
                    parent.color = RED;
                    rotateLeft(parent);
                    sib = parent.right; // После поворота у нас новый брат
                }

                // СЛУЧАЙ 2: Брат черный, и оба его ребенка черные (или null).
                if ((sib == null) ||
                        ((sib.left == null || sib.left.color == BLACK) &&
                                (sib.right == null || sib.right.color == BLACK))) {
                    if (sib != null) sib.color = RED; // Красим брата в красный
                    x = parent;
                    parent = x.parent;
                } else {
                    // СЛУЧАЙ 3: Брат черный, правый ребенок черный, но левый — красный.
                    if (sib.right == null || sib.right.color == BLACK) {
                        if (sib.left != null) sib.left.color = BLACK;
                        sib.color = RED;
                        rotateRight(sib);
                        sib = parent.right; // Обновляем брата после поворота
                    }

                    // СЛУЧАЙ 4: Брат черный, и правый ребенок красный (Прямая линия).
                    sib.color = parent.color;
                    parent.color = BLACK;
                    if (sib.right != null) sib.right.color = BLACK;
                    rotateLeft(parent);
                    x = root; // Ставим x на корень, чтобы цикл завершился
                    parent = null;
                }
            } else {
                // ТО ЖЕ САМОЕ, НО ЗЕРКАЛЬНО (если x — правый ребенок)
                Node sib = parent.left;

                if (sib != null && sib.color == RED) {
                    sib.color = BLACK;
                    parent.color = RED;
                    rotateRight(parent);
                    sib = parent.left;
                }

                if ((sib == null) ||
                        ((sib.left == null || sib.left.color == BLACK) &&
                                (sib.right == null || sib.right.color == BLACK))) {
                    if (sib != null) sib.color = RED;
                    x = parent;
                    parent = x.parent;
                } else {
                    if (sib.left == null || sib.left.color == BLACK) {
                        if (sib.right != null) sib.right.color = BLACK;
                        sib.color = RED;
                        rotateLeft(sib);
                        sib = parent.left;
                    }

                    sib.color = parent.color;
                    parent.color = BLACK;
                    if (sib.left != null) sib.left.color = BLACK;
                    rotateRight(parent);
                    x = root;
                    parent = null;
                }
            }
        }
        // Если x не null, он должен стать черным (избавляемся от "двойной черноты")
        if (x != null) x.color = BLACK;
    }

    // Обычный поиск по ключу
    private Node find(Integer key) {
        Node n = root;
        while (n != null) {
            int cmp = key.compareTo(n.key);
            if (cmp < 0) n = n.left;
            else if (cmp > 0) n = n.right;
            else return n;
        }
        return null;
    }

    // Пересаживает поддерево v на место поддерева u (используется при удалении)
    private void transplant(Node u, Node v) {
        if (u.parent == null) root = v;
        else if (u == u.parent.left) u.parent.left = v;
        else u.parent.right = v;
        if (v != null) v.parent = u.parent;
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

    // Рекурсивный поиск значения по всему дереву
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

    // Вставка нового узла
    @Override
    public String put(Integer key, String value) {
        if (key == null) throw new NullPointerException();

        Node existing = find(key);
        if (existing != null) { // Если ключ есть, просто обновляем значение
            String old = existing.value;
            existing.value = value;
            return old;
        }

        Node n = new Node(key, value, RED, null); // Новый узел всегда красный
        Node parent = null;
        Node cur = root;

        // Ищем место для вставки
        while (cur != null) {
            parent = cur;
            if (key.compareTo(cur.key) < 0) cur = cur.left;
            else cur = cur.right;
        }

        n.parent = parent;
        if (parent == null) root = n;
        else if (key.compareTo(parent.key) < 0) parent.left = n;
        else parent.right = n;

        size++;
        fixInsert(n); // Восстанавливаем свойства
        return null;
    }

    // Удаление узла (самый сложный метод)
    @Override
    public String remove(Object key) {
        if (key == null) return null;

        Node z = find((Integer) key);
        if (z == null) return null;

        String old = z.value;
        Node y = z;
        Node x;
        Node xParent;
        boolean yOriginalColor = y.color;

        // Случай 1 и 2: у узла не больше одного ребенка
        if (z.left == null) {
            x = z.right;
            xParent = z.parent;
            transplant(z, z.right);
        } else if (z.right == null) {
            x = z.left;
            xParent = z.parent;
            transplant(z, z.left);
        } else {
            // Случай 3: два ребенка. Ищем минимум в правом поддереве
            y = z.right;
            while (y.left != null) y = y.left;
            yOriginalColor = y.color;
            x = y.right;

            if (y.parent == z) {
                xParent = y;
                if (x != null) x.parent = y;
            } else {
                xParent = y.parent;
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            transplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color;
        }

        // Если удалили черный узел, баланс нарушен
        if (yOriginalColor == BLACK) {
            fixRemove(x, xParent);
        }

        size--;
        return old;
    }

    // Возвращает минимальный ключ
    @Override
    public Integer firstKey() {
        if (root == null) throw new NoSuchElementException();
        Node n = root;
        while (n.left != null) n = n.left;
        return n.key;
    }

    // Возвращает максимальный ключ
    @Override
    public Integer lastKey() {
        if (root == null) throw new NoSuchElementException();
        Node n = root;
        while (n.right != null) n = n.right;
        return n.key;
    }

    // Возвращает Map со всеми ключами строго меньше toKey
    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        MyRbMap res = new MyRbMap();
        collectHead(root, toKey, res);
        return res;
    }

    private void collectHead(Node n, Integer toKey, MyRbMap res) {
        if (n == null) return;
        if (n.key.compareTo(toKey) < 0) {
            res.put(n.key, n.value);
            collectHead(n.left, toKey, res);
            collectHead(n.right, toKey, res);
        } else {
            collectHead(n.left, toKey, res);
        }
    }

    // Возвращает Map со всеми ключами больше или равными fromKey
    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        MyRbMap res = new MyRbMap();
        collectTail(root, fromKey, res);
        return res;
    }

    private void collectTail(Node n, Integer fromKey, MyRbMap res) {
        if (n == null) return;
        if (n.key.compareTo(fromKey) >= 0) {
            res.put(n.key, n.value);
            collectTail(n.left, fromKey, res);
            collectTail(n.right, fromKey, res);
        } else {
            collectTail(n.right, fromKey, res);
        }
    }

    @Override
    public Comparator<? super Integer> comparator() { return null; }

    @Override
    public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        throw new UnsupportedOperationException();
    }

    // Печать дерева (симметричный обход дает порядок возрастания)
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

    // Заглушки
    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) { throw new UnsupportedOperationException(); }
    @Override
    public Set<Integer> keySet() { throw new UnsupportedOperationException(); }
    @Override
    public Collection<String> values() { throw new UnsupportedOperationException(); }
    @Override
    public Set<Map.Entry<Integer, String>> entrySet() { throw new UnsupportedOperationException(); }
}