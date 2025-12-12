package by.it.group410971.kozich.lesson12;

import java.util.*;

public class MyAvlMap implements Map<Integer, String> {

    // Внутренний класс узла АВЛ-дерева
    private static class AvlNode {
        Integer key;
        String value;
        AvlNode left;
        AvlNode right;
        int height;

        AvlNode(Integer key, String value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
            this.height = 1;
        }
    }

    // Корень дерева
    private AvlNode root;
    // Количество элементов в дереве
    private int size;

    // Конструктор
    public MyAvlMap() {
        root = null;
        size = 0;
    }

    // Получение высоты узла
    private int height(AvlNode node) {
        return node == null ? 0 : node.height;
    }

    // Получение баланс-фактора узла
    private int balanceFactor(AvlNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    // Обновление высоты узла
    private void updateHeight(AvlNode node) {
        if (node != null) {
            node.height = Math.max(height(node.left), height(node.right)) + 1;
        }
    }

    // Правый поворот
    private AvlNode rotateRight(AvlNode y) {
        AvlNode x = y.left;
        AvlNode t2 = x.right;

        // Выполняем поворот
        x.right = y;
        y.left = t2;

        // Обновляем высоты
        updateHeight(y);
        updateHeight(x);

        return x;
    }

    // Левый поворот
    private AvlNode rotateLeft(AvlNode x) {
        AvlNode y = x.right;
        AvlNode t2 = y.left;

        // Выполняем поворот
        y.left = x;
        x.right = t2;

        // Обновляем высоты
        updateHeight(x);
        updateHeight(y);

        return y;
    }

    // Балансировка узла
    private AvlNode balance(AvlNode node) {
        if (node == null) {
            return null;
        }

        updateHeight(node);
        int balance = balanceFactor(node);

        // Левый левый случай
        if (balance > 1 && balanceFactor(node.left) >= 0) {
            return rotateRight(node);
        }

        // Левый правый случай
        if (balance > 1 && balanceFactor(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Правый правый случай
        if (balance < -1 && balanceFactor(node.right) <= 0) {
            return rotateLeft(node);
        }

        // Правый левый случай
        if (balance < -1 && balanceFactor(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    // Вспомогательный метод для поиска узла с минимальным ключом
    private AvlNode findMin(AvlNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    // Рекурсивное добавление узла
    private AvlNode putRecursive(AvlNode node, Integer key, String value) {
        if (node == null) {
            size++;
            return new AvlNode(key, value);
        }

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            node.left = putRecursive(node.left, key, value);
        } else if (cmp > 0) {
            node.right = putRecursive(node.right, key, value);
        } else {
            // Ключ уже существует, обновляем значение
            node.value = value;
            return node;
        }

        return balance(node);
    }

    // Рекурсивное удаление узла
    private AvlNode removeRecursive(AvlNode node, Integer key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            node.left = removeRecursive(node.left, key);
        } else if (cmp > 0) {
            node.right = removeRecursive(node.right, key);
        } else {
            // Узел найден
            size--;

            // Узел с одним или без детей
            if (node.left == null || node.right == null) {
                AvlNode temp = (node.left != null) ? node.left : node.right;

                // Нет детей
                if (temp == null) {
                    return null;
                } else {
                    // Один ребенок
                    node = temp;
                }
            } else {
                // Узел с двумя детьми
                AvlNode temp = findMin(node.right);
                node.key = temp.key;
                node.value = temp.value;
                node.right = removeRecursive(node.right, temp.key);
            }
        }

        return balance(node);
    }

    // Рекурсивный поиск узла
    private AvlNode getRecursive(AvlNode node, Integer key) {
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
    private void inOrderTraversal(AvlNode node, StringBuilder sb) {
        if (node != null) {
            inOrderTraversal(node.left, sb);
            if (sb.length() > 1) {
                sb.append(", ");
            }
            sb.append(node.key).append("=").append(node.value);
            inOrderTraversal(node.right, sb);
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

        AvlNode existing = getRecursive(root, key);
        String oldValue = (existing != null) ? existing.value : null;

        root = putRecursive(root, key, value);
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

        AvlNode existing = getRecursive(root, (Integer) key);
        if (existing == null) {
            return null;
        }

        String oldValue = existing.value;
        root = removeRecursive(root, (Integer) key);
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

        AvlNode node = getRecursive(root, (Integer) key);
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

    /////////////////////////////////////////////////////////////////////////
    //////         Остальные методы интерфейса Map<Integer, String>  ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public boolean containsValue(Object value) {
        return containsValueRecursive(root, value);
    }

    private boolean containsValueRecursive(AvlNode node, Object value) {
        if (node == null) {
            return false;
        }

        if (Objects.equals(node.value, value)) {
            return true;
        }

        return containsValueRecursive(node.left, value) ||
                containsValueRecursive(node.right, value);
    }

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

    private void collectKeys(AvlNode node, Set<Integer> keys) {
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

    private void collectValues(AvlNode node, List<String> values) {
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

    private void collectEntries(AvlNode node, Set<Entry<Integer, String>> entries) {
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