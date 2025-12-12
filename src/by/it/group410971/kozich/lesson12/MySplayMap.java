package by.it.group410971.kozich.lesson12;

import java.util.*;

public class MySplayMap implements NavigableMap<Integer, String> {

    // Внутренний класс узла splay-дерева
    private static class SplayNode {
        Integer key;
        String value;
        SplayNode left;
        SplayNode right;
        SplayNode parent;

        SplayNode(Integer key, String value, SplayNode parent) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
            this.parent = parent;
        }
    }

    // Корень дерева
    private SplayNode root;
    // Количество элементов в дереве
    private int size;

    // Конструктор
    public MySplayMap() {
        root = null;
        size = 0;
    }

    // Zig (правый поворот)
    private void zig(SplayNode x) {
        SplayNode p = x.parent;
        if (p == null) return;

        SplayNode g = p.parent;

        if (x == p.left) {
            // Правый поворот
            p.left = x.right;
            if (x.right != null) {
                x.right.parent = p;
            }
            x.right = p;
            p.parent = x;
        } else {
            // Левый поворот
            p.right = x.left;
            if (x.left != null) {
                x.left.parent = p;
            }
            x.left = p;
            p.parent = x;
        }

        x.parent = g;
        if (g != null) {
            if (g.left == p) {
                g.left = x;
            } else {
                g.right = x;
            }
        } else {
            root = x;
        }
    }

    // Zig-zig (два поворота в одном направлении)
    private void zigZig(SplayNode x) {
        zig(x.parent);
        zig(x);
    }

    // Zig-zag (повороты в разных направлениях)
    private void zigZag(SplayNode x) {
        zig(x);
        zig(x);
    }

    // Splay операция - поднимает узел x в корень
    private void splay(SplayNode x) {
        while (x.parent != null) {
            SplayNode p = x.parent;
            SplayNode g = p.parent;

            if (g == null) {
                // Zig
                zig(x);
            } else if ((x == p.left && p == g.left) || (x == p.right && p == g.right)) {
                // Zig-zig
                zigZig(x);
            } else {
                // Zig-zag
                zigZag(x);
            }
        }
        root = x;
    }

    // Вставка узла
    private SplayNode insert(Integer key, String value) {
        if (root == null) {
            root = new SplayNode(key, value, null);
            size++;
            return root;
        }

        SplayNode current = root;
        SplayNode parent = null;

        while (current != null) {
            parent = current;
            int cmp = key.compareTo(current.key);

            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                // Ключ уже существует
                current.value = value;
                splay(current);
                return current;
            }
        }

        // Создаем новый узел
        SplayNode newNode = new SplayNode(key, value, parent);
        int cmp = key.compareTo(parent.key);
        if (cmp < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }

        size++;
        splay(newNode);
        return newNode;
    }

    // Поиск узла по ключу
    private SplayNode find(Integer key) {
        SplayNode current = root;
        SplayNode lastVisited = null;

        while (current != null) {
            lastVisited = current;
            int cmp = key.compareTo(current.key);

            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                // Найден узел
                splay(current);
                return current;
            }
        }

        // Узел не найден, splay последний посещенный узел
        if (lastVisited != null) {
            splay(lastVisited);
        }
        return null;
    }

    // Поиск узла без splay (для внутренних операций)
    private SplayNode findWithoutSplay(Integer key) {
        SplayNode current = root;

        while (current != null) {
            int cmp = key.compareTo(current.key);

            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                return current;
            }
        }

        return null;
    }

    // Поиск минимального узла в поддереве
    private SplayNode findMin(SplayNode node) {
        if (node == null) return null;
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    // Поиск максимального узла в поддереве
    private SplayNode findMax(SplayNode node) {
        if (node == null) return null;
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }

    // Удаление узла
    private void delete(Integer key) {
        SplayNode node = find(key);
        if (node == null) return;

        // Если у узла нет левого ребенка
        if (node.left == null) {
            root = node.right;
            if (root != null) {
                root.parent = null;
            }
        } else {
            // Находим максимум в левом поддереве
            SplayNode maxLeft = findMax(node.left);

            // Отделяем максимум от его родителя
            if (maxLeft.parent != node) {
                if (maxLeft.left != null) {
                    maxLeft.left.parent = maxLeft.parent;
                }
                maxLeft.parent.right = maxLeft.left;
                maxLeft.left = node.left;
                node.left.parent = maxLeft;
            }

            maxLeft.right = node.right;
            if (node.right != null) {
                node.right.parent = maxLeft;
            }

            root = maxLeft;
            root.parent = null;
        }

        size--;
        if (root != null) {
            splay(root);
        }
    }

    // Поиск наибольшего ключа, меньшего заданного
    private SplayNode findLower(Integer key) {
        SplayNode current = root;
        SplayNode result = null;

        while (current != null) {
            int cmp = key.compareTo(current.key);

            if (cmp <= 0) {
                current = current.left;
            } else {
                result = current;
                current = current.right;
            }
        }

        if (result != null) {
            splay(result);
        }
        return result;
    }

    // Поиск наибольшего ключа, меньшего или равного заданному
    private SplayNode findFloor(Integer key) {
        SplayNode current = root;
        SplayNode result = null;

        while (current != null) {
            int cmp = key.compareTo(current.key);

            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                result = current;
                current = current.right;
            } else {
                result = current;
                break;
            }
        }

        if (result != null) {
            splay(result);
        }
        return result;
    }

    // Поиск наименьшего ключа, большего или равного заданному
    private SplayNode findCeiling(Integer key) {
        SplayNode current = root;
        SplayNode result = null;

        while (current != null) {
            int cmp = key.compareTo(current.key);

            if (cmp < 0) {
                result = current;
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                result = current;
                break;
            }
        }

        if (result != null) {
            splay(result);
        }
        return result;
    }

    // Поиск наименьшего ключа, большего заданного
    private SplayNode findHigher(Integer key) {
        SplayNode current = root;
        SplayNode result = null;

        while (current != null) {
            int cmp = key.compareTo(current.key);

            if (cmp < 0) {
                result = current;
                current = current.left;
            } else {
                current = current.right;
            }
        }

        if (result != null) {
            splay(result);
        }
        return result;
    }

    // Обход дерева в порядке возрастания ключей (in-order)
    private void inOrderTraversal(SplayNode node, StringBuilder sb) {
        if (node != null) {
            inOrderTraversal(node.left, sb);
            if (sb.length() > 1) {
                sb.append(", ");
            }
            sb.append(node.key).append("=").append(node.value);
            inOrderTraversal(node.right, sb);
        }
    }

    // Обход дерева для поиска значения
    private boolean containsValueRecursive(SplayNode node, String value) {
        if (node == null) {
            return false;
        }

        if (Objects.equals(node.value, value)) {
            return true;
        }

        return containsValueRecursive(node.left, value) ||
                containsValueRecursive(node.right, value);
    }

    // Сбор ключей до указанного значения (для headMap)
    private void collectKeysLessThan(SplayNode node, Integer toKey, NavigableMap<Integer, String> result) {
        if (node != null) {
            collectKeysLessThan(node.left, toKey, result);
            if (node.key.compareTo(toKey) < 0) {
                result.put(node.key, node.value);
            }
            collectKeysLessThan(node.right, toKey, result);
        }
    }

    // Сбор ключей от указанного значения (для tailMap)
    private void collectKeysGreaterOrEqual(SplayNode node, Integer fromKey, NavigableMap<Integer, String> result) {
        if (node != null) {
            collectKeysGreaterOrEqual(node.left, fromKey, result);
            if (node.key.compareTo(fromKey) >= 0) {
                result.put(node.key, node.value);
            }
            collectKeysGreaterOrEqual(node.right, fromKey, result);
        }
    }

    /////////////////////////////////////////////////////////////////////////
    //////               Обязательные к реализации методы             ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        if (size == 0) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder("{");
        inOrderTraversal(root, sb);
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String put(Integer key, String value) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        SplayNode existing = findWithoutSplay(key);
        String oldValue = (existing != null) ? existing.value : null;

        insert(key, value);
        return oldValue;
    }

    @Override
    public String remove(Object key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        if (!(key instanceof Integer)) {
            return null;
        }

        SplayNode existing = findWithoutSplay((Integer) key);
        if (existing == null) {
            return null;
        }

        String oldValue = existing.value;
        delete((Integer) key);
        return oldValue;
    }

    @Override
    public String get(Object key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        if (!(key instanceof Integer)) {
            return null;
        }

        SplayNode node = find((Integer) key);
        return (node != null) ? node.value : null;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        if (!(key instanceof Integer)) {
            return false;
        }

        return find((Integer) key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) {
            throw new NullPointerException("Value cannot be null");
        }

        if (!(value instanceof String)) {
            return false;
        }

        return containsValueRecursive(root, (String) value);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        return headMap(toKey, false);
    }

    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        return tailMap(fromKey, true);
    }

    @Override
    public Integer firstKey() {
        if (isEmpty()) {
            throw new NoSuchElementException("Map is empty");
        }
        SplayNode min = findMin(root);
        if (min != null) {
            splay(min);
        }
        return min != null ? min.key : null;
    }

    @Override
    public Integer lastKey() {
        if (isEmpty()) {
            throw new NoSuchElementException("Map is empty");
        }
        SplayNode max = findMax(root);
        if (max != null) {
            splay(max);
        }
        return max != null ? max.key : null;
    }

    @Override
    public Integer lowerKey(Integer key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        SplayNode node = findLower(key);
        return node != null ? node.key : null;
    }

    @Override
    public Integer floorKey(Integer key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        SplayNode node = findFloor(key);
        return node != null ? node.key : null;
    }

    @Override
    public Integer ceilingKey(Integer key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        SplayNode node = findCeiling(key);
        return node != null ? node.key : null;
    }

    @Override
    public Integer higherKey(Integer key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        SplayNode node = findHigher(key);
        return node != null ? node.key : null;
    }

    /////////////////////////////////////////////////////////////////////////
    //////         Остальные методы интерфейса NavigableMap          ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public Entry<Integer, String> lowerEntry(Integer key) {
        Integer lowerKey = lowerKey(key);
        if (lowerKey == null) return null;
        String value = get(lowerKey);
        return new SimpleEntry(lowerKey, value);
    }

    @Override
    public Entry<Integer, String> floorEntry(Integer key) {
        Integer floorKey = floorKey(key);
        if (floorKey == null) return null;
        String value = get(floorKey);
        return new SimpleEntry(floorKey, value);
    }

    @Override
    public Entry<Integer, String> ceilingEntry(Integer key) {
        Integer ceilingKey = ceilingKey(key);
        if (ceilingKey == null) return null;
        String value = get(ceilingKey);
        return new SimpleEntry(ceilingKey, value);
    }

    @Override
    public Entry<Integer, String> higherEntry(Integer key) {
        Integer higherKey = higherKey(key);
        if (higherKey == null) return null;
        String value = get(higherKey);
        return new SimpleEntry(higherKey, value);
    }

    @Override
    public Entry<Integer, String> firstEntry() {
        Integer firstKey = firstKey();
        if (firstKey == null) return null;
        String value = get(firstKey);
        return new SimpleEntry(firstKey, value);
    }

    @Override
    public Entry<Integer, String> lastEntry() {
        Integer lastKey = lastKey();
        if (lastKey == null) return null;
        String value = get(lastKey);
        return new SimpleEntry(lastKey, value);
    }

    @Override
    public Entry<Integer, String> pollFirstEntry() {
        if (isEmpty()) return null;
        Entry<Integer, String> first = firstEntry();
        if (first != null) {
            remove(first.getKey());
        }
        return first;
    }

    @Override
    public Entry<Integer, String> pollLastEntry() {
        if (isEmpty()) return null;
        Entry<Integer, String> last = lastEntry();
        if (last != null) {
            remove(last.getKey());
        }
        return last;
    }

    @Override
    public NavigableMap<Integer, String> descendingMap() {
        MySplayMap result = new MySplayMap();
        addDescending(root, result);
        return result;
    }

    private void addDescending(SplayNode node, MySplayMap result) {
        if (node != null) {
            addDescending(node.right, result);
            result.put(node.key, node.value);
            addDescending(node.left, result);
        }
    }

    @Override
    public NavigableSet<Integer> navigableKeySet() {
        TreeSet<Integer> keys = new TreeSet<>();
        collectKeys(root, keys);
        return keys;
    }

    @Override
    public NavigableSet<Integer> descendingKeySet() {
        TreeSet<Integer> keys = new TreeSet<>(Collections.reverseOrder());
        collectKeys(root, keys);
        return keys;
    }

    @Override
    public NavigableMap<Integer, String> subMap(Integer fromKey, boolean fromInclusive, Integer toKey, boolean toInclusive) {
        if (fromKey == null || toKey == null) {
            throw new NullPointerException("Keys cannot be null");
        }
        if (fromKey.compareTo(toKey) > 0) {
            throw new IllegalArgumentException("fromKey > toKey");
        }

        MySplayMap result = new MySplayMap();
        collectSubMap(root, fromKey, fromInclusive, toKey, toInclusive, result);
        return result;
    }

    private void collectSubMap(SplayNode node, Integer fromKey, boolean fromInclusive,
                               Integer toKey, boolean toInclusive, MySplayMap result) {
        if (node != null) {
            collectSubMap(node.left, fromKey, fromInclusive, toKey, toInclusive, result);

            boolean fromOk = fromInclusive ?
                    node.key.compareTo(fromKey) >= 0 : node.key.compareTo(fromKey) > 0;
            boolean toOk = toInclusive ?
                    node.key.compareTo(toKey) <= 0 : node.key.compareTo(toKey) < 0;

            if (fromOk && toOk) {
                result.put(node.key, node.value);
            }

            collectSubMap(node.right, fromKey, fromInclusive, toKey, toInclusive, result);
        }
    }

    @Override
    public NavigableMap<Integer, String> headMap(Integer toKey, boolean inclusive) {
        if (toKey == null) {
            throw new NullPointerException("toKey cannot be null");
        }

        MySplayMap result = new MySplayMap();
        collectHeadMap(root, toKey, inclusive, result);
        return result;
    }

    private void collectHeadMap(SplayNode node, Integer toKey, boolean inclusive, MySplayMap result) {
        if (node != null) {
            collectHeadMap(node.left, toKey, inclusive, result);

            boolean ok = inclusive ?
                    node.key.compareTo(toKey) <= 0 : node.key.compareTo(toKey) < 0;

            if (ok) {
                result.put(node.key, node.value);
            }

            collectHeadMap(node.right, toKey, inclusive, result);
        }
    }

    @Override
    public NavigableMap<Integer, String> tailMap(Integer fromKey, boolean inclusive) {
        if (fromKey == null) {
            throw new NullPointerException("fromKey cannot be null");
        }

        MySplayMap result = new MySplayMap();
        collectTailMap(root, fromKey, inclusive, result);
        return result;
    }

    private void collectTailMap(SplayNode node, Integer fromKey, boolean inclusive, MySplayMap result) {
        if (node != null) {
            collectTailMap(node.left, fromKey, inclusive, result);

            boolean ok = inclusive ?
                    node.key.compareTo(fromKey) >= 0 : node.key.compareTo(fromKey) > 0;

            if (ok) {
                result.put(node.key, node.value);
            }

            collectTailMap(node.right, fromKey, inclusive, result);
        }
    }

    @Override
    public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        return subMap(fromKey, true, toKey, false);
    }

    @Override
    public Comparator<? super Integer> comparator() {
        return null; // Используется естественный порядок
    }

    /////////////////////////////////////////////////////////////////////////
    //////         Остальные методы интерфейса Map                   ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) {
        for (Entry<? extends Integer, ? extends String> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    private void collectKeys(SplayNode node, Set<Integer> keys) {
        if (node != null) {
            collectKeys(node.left, keys);
            keys.add(node.key);
            collectKeys(node.right, keys);
        }
    }

    @Override
    public Set<Integer> keySet() {
        return navigableKeySet();
    }

    @Override
    public Collection<String> values() {
        List<String> values = new ArrayList<>();
        collectValues(root, values);
        return values;
    }

    private void collectValues(SplayNode node, List<String> values) {
        if (node != null) {
            collectValues(node.left, values);
            values.add(node.value);
            collectValues(node.right, values);
        }
    }

    @Override
    public Set<Entry<Integer, String>> entrySet() {
        Set<Entry<Integer, String>> entries = new TreeSet<>(Map.Entry.comparingByKey());
        collectEntries(root, entries);
        return entries;
    }

    private void collectEntries(SplayNode node, Set<Entry<Integer, String>> entries) {
        if (node != null) {
            collectEntries(node.left, entries);
            entries.add(new SimpleEntry(node.key, node.value));
            collectEntries(node.right, entries);
        }
    }

    // Простая реализация Entry
    private static class SimpleEntry implements Entry<Integer, String> {
        private final Integer key;
        private String value;

        SimpleEntry(Integer key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public Integer getKey() {
            return key;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String setValue(String value) {
            String oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Entry<?, ?> e)) {
                return false;
            }
            return Objects.equals(key, e.getKey()) &&
                    Objects.equals(value, e.getValue());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }
}