package by.it.group410971.kozich.lesson11;

import java.util.*;

public class MyLinkedHashSet<E> implements Set<E> {

    // Внутренний класс узла для двусвязного списка (для сохранения порядка)
    private static class LinkedNode<E> {
        final E item;
        final int hash;
        LinkedNode<E> next;      // следующий в хеш-цепочке
        LinkedNode<E> before;    // предыдущий в порядке добавления
        LinkedNode<E> after;     // следующий в порядке добавления

        LinkedNode(int hash, E item, LinkedNode<E> next) {
            this.hash = hash;
            this.item = item;
            this.next = next;
            this.before = null;
            this.after = null;
        }
    }

    // Массив бакетов (корзин)
    private LinkedNode<E>[] table;
    // Голова и хвост двусвязного списка (для порядка добавления)
    private LinkedNode<E> head;
    private LinkedNode<E> tail;
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
    public MyLinkedHashSet() {
        table = (LinkedNode<E>[]) new LinkedNode[DEFAULT_INITIAL_CAPACITY];
        head = null;
        tail = null;
        size = 0;
        threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
    }

    // Вычисление хеш-кода с дополнительным "размешиванием"
    private int hash(Object key) {
        if (key == null) {
            throw new NullPointerException("Null elements are not allowed");
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
        LinkedNode<E>[] oldTable = table;
        int oldCapacity = oldTable.length;
        int newCapacity = oldCapacity << 1; // Удваиваем емкость
        LinkedNode<E>[] newTable = (LinkedNode<E>[]) new LinkedNode[newCapacity];

        // Перераспределяем элементы
        for (int i = 0; i < oldCapacity; i++) {
            LinkedNode<E> e = oldTable[i];
            if (e != null) {
                oldTable[i] = null; // Помогаем сборщику мусора
                do {
                    LinkedNode<E> next = e.next;
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

    // Добавление узла в конец двусвязного списка
    private void linkNodeLast(LinkedNode<E> node) {
        if (tail == null) {
            head = node;
        } else {
            tail.after = node;
            node.before = tail;
        }
        tail = node;
    }

    // Удаление узла из двусвязного списка
    private void unlinkNode(LinkedNode<E> node) {
        LinkedNode<E> before = node.before;
        LinkedNode<E> after = node.after;

        if (before == null) {
            head = after;
        } else {
            before.after = after;
            node.before = null;
        }

        if (after == null) {
            tail = before;
        } else {
            after.before = before;
            node.after = null;
        }
    }

    /////////////////////////////////////////////////////////////////////////
    //////               Обязательные к реализации методы             ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        LinkedNode<E> current = head;
        boolean first = true;

        while (current != null) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(current.item);
            first = false;
            current = current.after;
        }

        sb.append("]");
        return sb.toString();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        if (size > 0) {
            // Очищаем все ссылки для помощи сборщику мусора
            Arrays.fill(table, null);
            head = null;
            tail = null;
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
        LinkedNode<E> e = table[index];
        while (e != null) {
            if (e.hash == hash && Objects.equals(e.item, element)) {
                return false; // Элемент уже есть
            }
            e = e.next;
        }

        // Добавляем новый элемент
        LinkedNode<E> newNode = new LinkedNode<>(hash, element, table[index]);
        table[index] = newNode;

        // Добавляем в двусвязный список для сохранения порядка
        linkNodeLast(newNode);
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

        LinkedNode<E> prev = null;
        LinkedNode<E> e = table[index];

        while (e != null) {
            if (e.hash == hash && Objects.equals(e.item, o)) {
                // Найден элемент для удаления
                if (prev == null) {
                    table[index] = e.next;
                } else {
                    prev.next = e.next;
                }

                // Удаляем из двусвязного списка
                unlinkNode(e);
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

        LinkedNode<E> e = table[index];
        while (e != null) {
            if (e.hash == hash && Objects.equals(e.item, o)) {
                return true;
            }
            e = e.next;
        }

        return false;
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
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object element : c) {
            if (remove(element)) {
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

    /////////////////////////////////////////////////////////////////////////
    //////         Остальные методы интерфейса Set<E>                ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public Iterator<E> iterator() {
        return new LinkedHashSetIterator();
    }

    // Итератор для LinkedHashSet (в порядке добавления)
    private class LinkedHashSetIterator implements Iterator<E> {
        private LinkedNode<E> next;    // следующий элемент для возврата
        private LinkedNode<E> current; // текущий элемент
        private int expectedModCount;  // для fail-fast поведения

        LinkedHashSetIterator() {
            next = head;
            current = null;
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
            next = next.after;
            return current.item;
        }

        @Override
        public void remove() {
            if (current == null) {
                throw new IllegalStateException();
            }

            E item = current.item;
            LinkedNode<E> nodeToRemove = current;
            current = null;

            // Находим и удаляем узел из хеш-таблицы
            int hash = hash(item);
            int index = indexFor(hash, table.length);

            LinkedNode<E> prev = null;
            LinkedNode<E> e = table[index];

            while (e != null) {
                if (e == nodeToRemove) {
                    if (prev == null) {
                        table[index] = e.next;
                    } else {
                        prev.next = e.next;
                    }

                    // Удаляем из двусвязного списка
                    unlinkNode(nodeToRemove);
                    size--;
                    break;
                }
                prev = e;
                e = e.next;
            }
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
}