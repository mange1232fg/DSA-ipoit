package by.it.group410971.kozich.lesson10;

import java.util.*;

public class MyPriorityQueue<E> implements Queue<E> {

    // Внутренний массив для хранения элементов кучи
    private Object[] heap;
    // Размер кучи (количество элементов)
    private int size;
    // Начальная емкость массива
    private static final int DEFAULT_CAPACITY = 10;
    // Компаратор для сравнения элементов
    private final Comparator<? super E> comparator;

    // Конструктор без компаратора (использует natural ordering)
    public MyPriorityQueue() {
        this(null);
    }

    // Конструктор с компаратором
    public MyPriorityQueue(Comparator<? super E> comparator) {
        this.heap = new Object[DEFAULT_CAPACITY];
        this.size = 0;
        this.comparator = comparator;
    }

    // Проверка, можно ли сравнивать элементы
    @SuppressWarnings("unchecked")
    private int compare(E a, E b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        } else {
            // Используем natural ordering
            return ((Comparable<? super E>) a).compareTo(b);
        }
    }

    // Метод для увеличения емкости массива при необходимости
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > heap.length) {
            int newCapacity = heap.length * 2;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            heap = Arrays.copyOf(heap, newCapacity);
        }
    }

    // Просеивание вверх (при добавлении элемента)
    private void siftUp(int k, E x) {
        if (comparator != null) {
            siftUpUsingComparator(k, x);
        } else {
            siftUpComparable(k, x);
        }
    }

    @SuppressWarnings("unchecked")
    private void siftUpComparable(int k, E x) {
        Comparable<? super E> key = (Comparable<? super E>) x;
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            Object e = heap[parent];
            if (key.compareTo((E) e) >= 0) {
                break;
            }
            heap[k] = e;
            k = parent;
        }
        heap[k] = key;
    }

    @SuppressWarnings("unchecked")
    private void siftUpUsingComparator(int k, E x) {
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            Object e = heap[parent];
            if (comparator.compare(x, (E) e) >= 0) {
                break;
            }
            heap[k] = e;
            k = parent;
        }
        heap[k] = x;
    }

    // Просеивание вниз (при удалении корня)
    private void siftDown(int k, E x) {
        if (comparator != null) {
            siftDownUsingComparator(k, x);
        } else {
            siftDownComparable(k, x);
        }
    }

    @SuppressWarnings("unchecked")
    private void siftDownComparable(int k, E x) {
        Comparable<? super E> key = (Comparable<? super E>) x;
        int half = size >>> 1; // Петля пока не лист
        while (k < half) {
            int child = (k << 1) + 1; // Левый потомок
            Object c = heap[child];
            int right = child + 1;
            if (right < size &&
                    ((Comparable<? super E>) c).compareTo((E) heap[right]) > 0) {
                child = right;
                c = heap[child];
            }
            if (key.compareTo((E) c) <= 0) {
                break;
            }
            heap[k] = c;
            k = child;
        }
        heap[k] = key;
    }

    @SuppressWarnings("unchecked")
    private void siftDownUsingComparator(int k, E x) {
        int half = size >>> 1;
        while (k < half) {
            int child = (k << 1) + 1;
            Object c = heap[child];
            int right = child + 1;
            if (right < size &&
                    comparator.compare((E) c, (E) heap[right]) > 0) {
                child = right;
                c = heap[child];
            }
            if (comparator.compare(x, (E) c) <= 0) {
                break;
            }
            heap[k] = c;
            k = child;
        }
        heap[k] = x;
    }

    // Построение кучи из массива
    private void heapify() {
        for (int i = (size >>> 1) - 1; i >= 0; i--) {
            siftDown(i, (E) heap[i]);
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

        Object[] copy = Arrays.copyOf(heap, size);
        Arrays.sort(copy, (a, b) -> {
            @SuppressWarnings("unchecked")
            int cmp = compare((E) a, (E) b);
            return cmp;
        });

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < copy.length; i++) {
            sb.append(copy[i]);
            if (i < copy.length - 1) {
                sb.append(", ");
            }
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
        for (int i = 0; i < size; i++) {
            heap[i] = null;
        }
        size = 0;
    }

    @Override
    public boolean add(E element) {
        return offer(element);
    }

    @Override
    public boolean remove(Object o) {
        int i = indexOf(o);
        if (i == -1) {
            return false;
        } else {
            removeAt(i);
            return true;
        }
    }

    @Override
    public E remove() {
        E x = poll();
        if (x == null) {
            throw new NoSuchElementException();
        }
        return x;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    public boolean offer(E element) {
        if (element == null) {
            throw new NullPointerException();
        }

        ensureCapacity(size + 1);
        heap[size] = element;
        siftUp(size, element);
        size++;
        return true;
    }

    @Override
    public E poll() {
        if (size == 0) {
            return null;
        }

        int s = --size;
        @SuppressWarnings("unchecked")
        E result = (E) heap[0];
        @SuppressWarnings("unchecked")
        E x = (E) heap[s];
        heap[s] = null;
        if (s != 0) {
            siftDown(0, x);
        }
        return result;
    }

    @Override
    public E peek() {
        return (size == 0) ? null : (E) heap[0];
    }

    @Override
    public E element() {
        E x = peek();
        if (x == null) {
            throw new NoSuchElementException();
        }
        return x;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
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
        if (c == null) {
            throw new NullPointerException();
        }
        if (c == this) {
            throw new IllegalArgumentException();
        }

        boolean modified = false;
        for (E element : c) {
            if (offer(element)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException();
        }

        boolean modified = false;
        // Собираем элементы для удаления
        List<Integer> indicesToRemove = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (c.contains(heap[i])) {
                indicesToRemove.add(i);
            }
        }

        // Удаляем с конца, чтобы индексы не сдвигались
        for (int i = indicesToRemove.size() - 1; i >= 0; i--) {
            int index = indicesToRemove.get(i);
            removeAt(index);
            modified = true;
        }

        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException();
        }

        boolean modified = false;
        // Собираем элементы для удаления (те, которые НЕ содержатся в c)
        List<Integer> indicesToRemove = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (!c.contains(heap[i])) {
                indicesToRemove.add(i);
            }
        }

        // Удаляем с конца, чтобы индексы не сдвигались
        for (int i = indicesToRemove.size() - 1; i >= 0; i--) {
            int index = indicesToRemove.get(i);
            removeAt(index);
            modified = true;
        }

        return modified;
    }

    /////////////////////////////////////////////////////////////////////////
    //////         Вспомогательные методы для работы с кучей         ///////
    /////////////////////////////////////////////////////////////////////////

    // Поиск индекса элемента
    private int indexOf(Object o) {
        if (o != null) {
            for (int i = 0; i < size; i++) {
                if (o.equals(heap[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    // Удаление элемента по индексу
    private void removeAt(int i) {
        int s = --size;
        if (s == i) { // удаление последнего элемента
            heap[i] = null;
        } else {
            @SuppressWarnings("unchecked")
            E moved = (E) heap[s];
            heap[s] = null;
            siftDown(i, moved);
            if (heap[i] == moved) {
                siftUp(i, moved);
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////
    //////         Остальные методы интерфейса Queue<E>              ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public Iterator<E> iterator() {
        return new PriorityQueueIterator();
    }

    // Итератор для приоритетной очереди
    private class PriorityQueueIterator implements Iterator<E> {
        // Копия кучи на момент создания итератора
        private final Object[] array;
        private int cursor = 0;
        private int lastRet = -1;

        PriorityQueueIterator() {
            this.array = Arrays.copyOf(heap, size);
            Arrays.sort(array, (a, b) -> {
                @SuppressWarnings("unchecked")
                int cmp = compare((E) a, (E) b);
                return cmp;
            });
        }

        @Override
        public boolean hasNext() {
            return cursor < array.length;
        }

        @SuppressWarnings("unchecked")
        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            lastRet = cursor;
            return (E) array[cursor++];
        }

        @Override
        public void remove() {
            if (lastRet < 0) {
                throw new IllegalStateException();
            }

            Object element = array[lastRet];
            MyPriorityQueue.this.remove(element);

            // Пересоздаем массив после удаления
            cursor = lastRet;
            lastRet = -1;
        }
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(heap, size);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            @SuppressWarnings("unchecked")
            T[] result = (T[]) Arrays.copyOf(heap, size, a.getClass());
            return result;
        }
        System.arraycopy(heap, 0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }
}