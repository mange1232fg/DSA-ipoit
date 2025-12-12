package by.it.group410971.kozich.lesson11;

import java.util.*;

public class MyHashSet<E> implements Set<E> {

    // Внутренний класс узла для односвязного списка
    private static class Node<E> {
        final E item;
        final int hash;
        Node<E> next;

        Node(int hash, E item, Node<E> next) {
            this.hash = hash;
            this.item = item;
            this.next = next;
        }
    }

    // Массив бакетов (корзин)
    private Node<E>[] table;
    // Количество элементов в множестве
    private int size;
    // Начальная емкость таблицы
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    // Коэффициент загрузки
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    // Порог, при котором происходит рехэширование
    private int threshold;

    // Конструктор
    @SuppressWarnings("unchecked")
    public MyHashSet() {
        table = (Node<E>[]) new Node[DEFAULT_INITIAL_CAPACITY];
        size = 0;
        threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
    }

    // Вычисление хеш-кода с дополнительным "размешиванием"
    private int hash(Object key) {
        if (key == null) {
            return 0;
        }
        int h = key.hashCode();
        return h ^ (h >>> 16); // Размешивание для лучшего распределения
    }

    // Получение индекса бакета по хеш-коду
    private int indexFor(int hash, int length) {
        return hash & (length - 1);
    }

    // Рехэширование таблицы при достижении порога
    @SuppressWarnings("unchecked")
    private void resize() {
        Node<E>[] oldTable = table;
        int oldCapacity = oldTable.length;
        int newCapacity = oldCapacity << 1; // Удваиваем емкость
        Node<E>[] newTable = (Node<E>[]) new Node[newCapacity];

        // Перераспределяем элементы
        for (int i = 0; i < oldCapacity; i++) {
            Node<E> e = oldTable[i];
            if (e != null) {
                oldTable[i] = null; // Помогаем сборщику мусора
                do {
                    Node<E> next = e.next;
                    int newIndex = indexFor(e.hash, newCapacity);
                    e.next = newTable[newIndex];
                    newTable[newIndex] = e;
                    e = next;
                } while (e != null);
            }
        }

        table = newTable;
        threshold = (int)(newCapacity * DEFAULT_LOAD_FACTOR);
    }

    /////////////////////////////////////////////////////////////////////////
    //////               Обязательные к реализации методы             ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        if (size > 0) {
            Arrays.fill(table, null);
            size = 0;
        }
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean add(E element) {
        if (element == null) {
            throw new NullPointerException("Null elements are not allowed");
        }

        int hash = hash(element);
        int index = indexFor(hash, table.length);

        // Проверяем, нет ли уже такого элемента
        Node<E> e = table[index];
        while (e != null) {
            if (e.hash == hash && Objects.equals(e.item, element)) {
                return false; // Элемент уже есть
            }
            e = e.next;
        }

        // Добавляем новый элемент в начало списка
        table[index] = new Node<>(hash, element, table[index]);
        size++;

        // Проверяем, не нужно ли рехэшировать
        if (size >= threshold) {
            resize();
        }

        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) {
            throw new NullPointerException("Null elements are not allowed");
        }

        int hash = hash(o);
        int index = indexFor(hash, table.length);

        Node<E> prev = null;
        Node<E> e = table[index];

        while (e != null) {
            if (e.hash == hash && Objects.equals(e.item, o)) {
                // Найден элемент для удаления
                if (prev == null) {
                    table[index] = e.next;
                } else {
                    prev.next = e.next;
                }
                size--;
                return true;
            }
            prev = e;
            e = e.next;
        }

        return false; // Элемент не найден
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) {
            throw new NullPointerException("Null elements are not allowed");
        }

        int hash = hash(o);
        int index = indexFor(hash, table.length);

        Node<E> e = table[index];
        while (e != null) {
            if (e.hash == hash && Objects.equals(e.item, o)) {
                return true;
            }
            e = e.next;
        }

        return false;
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        boolean first = true;

        // Обходим все бакеты
        for (Node<E> bucket : table) {
            Node<E> current = bucket;
            while (current != null) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(current.item);
                first = false;
                current = current.next;
            }
        }

        sb.append("]");
        return sb.toString();
    }

    /////////////////////////////////////////////////////////////////////////
    //////         Остальные методы интерфейса Set<E>                ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public Iterator<E> iterator() {
        return new HashSetIterator();
    }

    // Итератор для HashSet
    private class HashSetIterator implements Iterator<E> {
        private Node<E> next;        // следующий элемент для возврата
        private Node<E> current;     // текущий элемент
        private int index;           // текущий индекс в table

        HashSetIterator() {
            current = null;
            index = 0;
            // Находим первый непустой бакет
            while (index < table.length && table[index] == null) {
                index++;
            }
            next = (index < table.length) ? table[index] : null;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            current = next;

            // Ищем следующий элемент
            next = next.next;
            if (next == null) {
                index++;
                while (index < table.length && table[index] == null) {
                    index++;
                }
                if (index < table.length) {
                    next = table[index];
                }
            }

            return current.item;
        }

        @Override
        public void remove() {
            if (current == null) {
                throw new IllegalStateException();
            }

            E item = current.item;
            MyHashSet.this.remove(item);
            current = null;
        }
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (E element : this) {
            result[i++] = element;
        }
        return result;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object element : c) {
            if (!contains(element)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E element : c) {
            if (add(element)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object element : c) {
            if (remove(element)) {
                modified = true;
            }
        }
        return modified;
    }
}