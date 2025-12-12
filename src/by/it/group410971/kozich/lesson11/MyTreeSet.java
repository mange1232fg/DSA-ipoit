package by.it.group410971.kozich.lesson11;

import java.util.*;

public class MyTreeSet<E> implements Set<E> {

    // Внутренний массив для хранения элементов
    private Object[] elements;
    // Размер множества
    private int size;
    // Начальная емкость массива
    private static final int DEFAULT_CAPACITY = 10;
    // Компаратор для сравнения элементов
    private final Comparator<? super E> comparator;

    // Конструктор без компаратора (использует natural ordering)
    public MyTreeSet() {
        this(null);
    }

    // Конструктор с компаратором
    @SuppressWarnings("unchecked")
    public MyTreeSet(Comparator<? super E> comparator) {
        this.elements = new Object[DEFAULT_CAPACITY];
        this.size = 0;
        this.comparator = comparator;

        // Проверяем, что элементы Comparable, если comparator == null
        if (comparator == null) {
            // Проверяем только при первом добавлении
        }
    }

    // Метод для увеличения емкости массива при необходимости
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = elements.length * 2;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            elements = Arrays.copyOf(elements, newCapacity);
        }
    }

    // Сравнение элементов
    @SuppressWarnings("unchecked")
    private int compare(E a, E b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        } else {
            // Используем natural ordering
            return ((Comparable<? super E>) a).compareTo(b);
        }
    }

    // Бинарный поиск элемента
    @SuppressWarnings("unchecked")
    private int binarySearch(Object o) {
        if (size == 0) {
            return -1;
        }

        E key;
        try {
            key = (E) o;
        } catch (ClassCastException e) {
            return -1;
        }

        int low = 0;
        int high = size - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            E midVal = (E) elements[mid];
            int cmp = compare(midVal, key);

            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return mid; // ключ найден
            }
        }

        // Возвращаем позицию, куда нужно вставить элемент (отрицательное число)
        return -(low + 1);
    }

    // Вставка элемента в отсортированную позицию
    private void insertAt(int index, E element) {
        ensureCapacity(size + 1);

        // Если index отрицательный, это позиция для вставки
        if (index < 0) {
            index = -index - 1;
        }

        // Сдвигаем элементы вправо
        if (index < size) {
            System.arraycopy(elements, index, elements, index + 1, size - index);
        }

        elements[index] = element;
        size++;
    }

    // Удаление элемента по индексу
    @SuppressWarnings("unchecked")
    private E removeAt(int index) {
        E removed = (E) elements[index];

        // Сдвигаем элементы влево
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elements, index + 1, elements, index, numMoved);
        }

        elements[--size] = null; // Помогаем сборщику мусора
        return removed;
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
        for (int i = 0; i < size; i++) {
            sb.append(elements[i]);
            if (i < size - 1) {
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
            elements[i] = null;
        }
        size = 0;
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

        // Проверяем natural ordering при первом добавлении
        if (size == 0 && comparator == null) {
            try {
                @SuppressWarnings("unchecked")
                Comparable<? super E> comparable = (Comparable<? super E>) element;
                // Проверка прошла успешно
            } catch (ClassCastException e) {
                throw new ClassCastException(
                        element.getClass().getName() +
                                " cannot be cast to Comparable"
                );
            }
        }

        int index = binarySearch(element);
        if (index >= 0) {
            return false; // Элемент уже существует
        }

        insertAt(index, element);
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object o) {
        if (o == null) {
            throw new NullPointerException("Null elements are not allowed");
        }

        int index = binarySearch(o);
        if (index < 0) {
            return false; // Элемент не найден
        }

        removeAt(index);
        return true;
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) {
            throw new NullPointerException("Null elements are not allowed");
        }

        return binarySearch(o) >= 0;
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
        if (c.isEmpty()) {
            return false;
        }

        boolean modified = false;
        // Собираем индексы элементов для удаления
        List<Integer> indicesToRemove = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            if (c.contains(elements[i])) {
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
        boolean modified = false;
        // Собираем индексы элементов для удаления (те, которые НЕ содержатся в c)
        List<Integer> indicesToRemove = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            if (!c.contains(elements[i])) {
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
    //////         Остальные методы интерфейса Set<E>                ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public Iterator<E> iterator() {
        return new TreeSetIterator();
    }

    // Итератор для TreeSet (в порядке возрастания)
    private class TreeSetIterator implements Iterator<E> {
        private int cursor = 0;
        private int lastRet = -1;

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @SuppressWarnings("unchecked")
        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            lastRet = cursor;
            return (E) elements[cursor++];
        }

        @Override
        public void remove() {
            if (lastRet < 0) {
                throw new IllegalStateException();
            }

            try {
                MyTreeSet.this.removeAt(lastRet);
                cursor = lastRet;
                lastRet = -1;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object[] toArray() {
        return Arrays.copyOf(elements, size);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            @SuppressWarnings("unchecked")
            T[] result = (T[]) Arrays.copyOf(elements, size, a.getClass());
            return result;
        }
        System.arraycopy(elements, 0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    /////////////////////////////////////////////////////////////////////////
    //////         Дополнительные методы для TreeSet                 ///////
    /////////////////////////////////////////////////////////////////////////

    // Получение первого (наименьшего) элемента
    @SuppressWarnings("unchecked")
    public E first() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return (E) elements[0];
    }

    // Получение последнего (наибольшего) элемента
    @SuppressWarnings("unchecked")
    public E last() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return (E) elements[size - 1];
    }

    // Получение наименьшего элемента, большего или равного заданному
    @SuppressWarnings("unchecked")
    public E ceiling(E e) {
        if (e == null) {
            throw new NullPointerException();
        }

        int index = binarySearch(e);
        if (index >= 0) {
            // Элемент найден
            return (E) elements[index];
        } else {
            // Возвращаем позицию для вставки
            int insertionPoint = -index - 1;
            if (insertionPoint < size) {
                return (E) elements[insertionPoint];
            }
        }
        return null;
    }

    // Получение наибольшего элемента, меньшего или равного заданному
    @SuppressWarnings("unchecked")
    public E floor(E e) {
        if (e == null) {
            throw new NullPointerException();
        }

        int index = binarySearch(e);
        if (index >= 0) {
            // Элемент найден
            return (E) elements[index];
        } else {
            // Возвращаем позицию для вставки
            int insertionPoint = -index - 1;
            if (insertionPoint > 0) {
                return (E) elements[insertionPoint - 1];
            }
        }
        return null;
    }
}