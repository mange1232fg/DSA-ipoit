package by.it.group410971.kozich.lesson12;

import java.util.*;

public class MyRbMap implements SortedMap<Integer, String> {

    // Константы для цвета узлов
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    // Внутренний класс узла красно-черного дерева
    private static class RbNode {
        Integer key;
        String value;
        RbNode left;
        RbNode right;
        boolean color; // цвет ссылки от родителя к этому узлу

        RbNode(Integer key, String value, boolean color) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
            this.color = color;
        }
    }

    // Корень дерева
    private RbNode root;
    // Количество элементов в дереве
    private int size;

    // Конструктор
    public MyRbMap() {
        root = null;
        size = 0;
    }

    // Проверка цвета узла
    private boolean isRed(RbNode node) {
        return node != null && node.color == RED;
    }

    // Левый поворот
    private RbNode rotateLeft(RbNode h) {
        RbNode x = h.right;
        h.right = x.left;
        x.left = h;
        x.color = h.color;
        h.color = RED;
        return x;
    }

    // Правый поворот
    private RbNode rotateRight(RbNode h) {
        RbNode x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = h.color;
        h.color = RED;
        return x;
    }

    // Переключение цвета (flip colors)
    private void flipColors(RbNode h) {
        h.color = !h.color;
        h.left.color = !h.left.color;
        h.right.color = !h.right.color;
    }

    // Балансировка после вставки
    private RbNode balance(RbNode h) {
        if (isRed(h.right) && !isRed(h.left)) {
            h = rotateLeft(h);
        }
        if (isRed(h.left) && isRed(h.left.left)) {
            h = rotateRight(h);
        }
        if (isRed(h.left) && isRed(h.right)) {
            flipColors(h);
        }
        return h;
    }

    // Вспомогательный метод для перемещения красной ссылки влево
    private RbNode moveRedLeft(RbNode h) {
        flipColors(h);
        if (isRed(h.right.left)) {
            h.right = rotateRight(h.right);
            h = rotateLeft(h);
            flipColors(h);
        }
        return h;
    }

    // Вспомогательный метод для перемещения красной ссылки вправо
    private RbNode moveRedRight(RbNode h) {
        flipColors(h);
        if (isRed(h.left.left)) {
            h = rotateRight(h);
            flipColors(h);
        }
        return h;
    }

    // Восстановление свойств красно-черного дерева после удаления
    private RbNode fixUp(RbNode h) {
        if (isRed(h.right)) {
            h = rotateLeft(h);
        }
        if (isRed(h.left) && isRed(h.left.left)) {
            h = rotateRight(h);
        }
        if (isRed(h.left) && isRed(h.right)) {
            flipColors(h);
        }
        return h;
    }

    // Поиск минимального узла в поддереве
    private RbNode min(RbNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    // Удаление минимального узла из поддерева
    private RbNode deleteMin(RbNode h) {
        if (h.left == null) {
            return null;
        }

        if (!isRed(h.left) && !isRed(h.left.left)) {
            h = moveRedLeft(h);
        }

        h.left = deleteMin(h.left);
        return fixUp(h);
    }

    // Рекурсивное добавление узла
    private RbNode putRecursive(RbNode node, Integer key, String value) {
        if (node == null) {
            size++;
            return new RbNode(key, value, RED);
        }

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            node.left = putRecursive(node.left, key, value);
        } else if (cmp > 0) {
            node.right = putRecursive(node.right, key, value);
        } else {
            // Ключ уже существует, обновляем значение
            node.value = value;
        }

        return balance(node);
    }

    // Рекурсивное удаление узла
    private RbNode removeRecursive(RbNode h, Integer key) {
        if (key.compareTo(h.key) < 0) {
            if (!isRed(h.left) && !isRed(h.left.left)) {
                h = moveRedLeft(h);
            }
            h.left = removeRecursive(h.left, key);
        } else {
            if (isRed(h.left)) {
                h = rotateRight(h);
            }
            if (key.compareTo(h.key) == 0 && h.right == null) {
                size--;
                return null;
            }
            if (!isRed(h.right) && !isRed(h.right.left)) {
                h = moveRedRight(h);
            }
            if (key.compareTo(h.key) == 0) {
                size--;
                RbNode minNode = min(h.right);
                h.key = minNode.key;
                h.value = minNode.value;
                h.right = deleteMin(h.right);
            } else {
                h.right = removeRecursive(h.right, key);
            }
        }
        return fixUp(h);
    }

    // Рекурсивный поиск узла
    private RbNode getRecursive(RbNode node, Integer key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            return getRecursive(node.left, key);
        } else if (cmp > 0) {
            return getRecursive(node.right, key);
        } else {
            return node;
        }
    }

    // Обход дерева в порядке возрастания ключей (in-order)
    private void inOrderTraversal(RbNode node, StringBuilder sb) {
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
    private boolean containsValueRecursive(RbNode node, String value) {
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
    private void collectKeysLessThan(RbNode node, Integer toKey, SortedMap<Integer, String> result) {
        if (node != null) {
            collectKeysLessThan(node.left, toKey, result);
            if (node.key.compareTo(toKey) < 0) {
                result.put(node.key, node.value);
            }
            collectKeysLessThan(node.right, toKey, result);
        }
    }

    // Сбор ключей от указанного значения (для tailMap)
    private void collectKeysGreaterOrEqual(RbNode node, Integer fromKey, SortedMap<Integer, String> result) {
        if (node != null) {
            collectKeysGreaterOrEqual(node.left, fromKey, result);
            if (node.key.compareTo(fromKey) >= 0) {
                result.put(node.key, node.value);
            }
            collectKeysGreaterOrEqual(node.right, fromKey, result);
        }
    }

    // Поиск первого (минимального) ключа
    private Integer findFirstKey(RbNode node) {
        if (node == null) {
            return null;
        }
        while (node.left != null) {
            node = node.left;
        }
        return node.key;
    }

    // Поиск последнего (максимального) ключа
    private Integer findLastKey(RbNode node) {
        if (node == null) {
            return null;
        }
        while (node.right != null) {
            node = node.right;
        }
        return node.key;
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

        RbNode existing = getRecursive(root, key);
        String oldValue = (existing != null) ? existing.value : null;

        root = putRecursive(root, key, value);
        if (root != null) {
            root.color = BLACK; // Корень всегда черный
        }
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

        RbNode existing = getRecursive(root, (Integer) key);
        if (existing == null) {
            return null;
        }

        String oldValue = existing.value;

        if (!isRed(root.left) && !isRed(root.right)) {
            if (root != null) {
                root.color = RED;
            }
        }

        root = removeRecursive(root, (Integer) key);
        if (root != null) {
            root.color = BLACK;
        }

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

        RbNode node = getRecursive(root, (Integer) key);
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

        return getRecursive(root, (Integer) key) != null;
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
        if (toKey == null) {
            throw new NullPointerException("toKey cannot be null");
        }

        MyRbMap result = new MyRbMap();
        collectKeysLessThan(root, toKey, result);
        return result;
    }

    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        if (fromKey == null) {
            throw new NullPointerException("fromKey cannot be null");
        }

        MyRbMap result = new MyRbMap();
        collectKeysGreaterOrEqual(root, fromKey, result);
        return result;
    }

    @Override
    public Integer firstKey() {
        if (isEmpty()) {
            throw new NoSuchElementException("Map is empty");
        }
        return findFirstKey(root);
    }

    @Override
    public Integer lastKey() {
        if (isEmpty()) {
            throw new NoSuchElementException("Map is empty");
        }
        return findLastKey(root);
    }

    /////////////////////////////////////////////////////////////////////////
    //////         Остальные методы интерфейса SortedMap             ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public Comparator<? super Integer> comparator() {
        return null; // Используется естественный порядок
    }

    @Override
    public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        if (fromKey == null || toKey == null) {
            throw new NullPointerException("Keys cannot be null");
        }
        if (fromKey.compareTo(toKey) > 0) {
            throw new IllegalArgumentException("fromKey > toKey");
        }

        MyRbMap result = new MyRbMap();
        // Можно оптимизировать, но для простоты используем headMap и tailMap
        SortedMap<Integer, String> tail = tailMap(fromKey);
        for (Entry<Integer, String> entry : tail.entrySet()) {
            if (entry.getKey().compareTo(toKey) < 0) {
                result.put(entry.getKey(), entry.getValue());
            } else {
                break;
            }
        }
        return result;
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

    @Override
    public Set<Integer> keySet() {
        Set<Integer> keys = new TreeSet<>();
        collectKeys(root, keys);
        return keys;
    }

    private void collectKeys(RbNode node, Set<Integer> keys) {
        if (node != null) {
            collectKeys(node.left, keys);
            keys.add(node.key);
            collectKeys(node.right, keys);
        }
    }

    @Override
    public Collection<String> values() {
        List<String> values = new ArrayList<>();
        collectValues(root, values);
        return values;
    }

    private void collectValues(RbNode node, List<String> values) {
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

    private void collectEntries(RbNode node, Set<Entry<Integer, String>> entries) {
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