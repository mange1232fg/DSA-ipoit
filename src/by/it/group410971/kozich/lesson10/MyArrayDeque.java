package by.it.group410971.kozich.lesson10;

import java.util.*;

public class MyArrayDeque<E> implements Deque<E> {

    // Внутренний массив для хранения элементов
    private Object[] elements;
    // Индекс первого элемента
    private int head;
    // Индекс следующего за последним элементом
    private int tail;
    // Размер дека
    private int size;
    // Начальная емкость массива (должна быть степенью двойки для оптимизации)
    private static final int DEFAULT_CAPACITY = 8;

    // Конструктор
    public MyArrayDeque() {
        elements = new Object[DEFAULT_CAPACITY];
        head = 0;
        tail = 0;
        size = 0;
    }

    // Метод для увеличения емкости массива при необходимости
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = elements.length * 2;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            resize(newCapacity);
        }
    }

    // Перераспределение элементов при увеличении емкости
    private void resize(int newCapacity) {
        Object[] newElements = new Object[newCapacity];

        // Копируем элементы в новый массив
        if (head < tail) {
            // Элементы расположены непрерывно
            System.arraycopy(elements, head, newElements, 0, size);
        } else if (size > 0) {
            // Элементы "обернулись" вокруг конца массива
            int firstPart = elements.length - head;
            System.arraycopy(elements, head, newElements, 0, firstPart);
            System.arraycopy(elements, 0, newElements, firstPart, tail);
        }

        elements = newElements;
        head = 0;
        tail = size;
    }

    // Метод для получения индекса с учетом кругового буфера
    private int wrapIndex(int index) {
        return (index & (elements.length - 1));
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
            int index = wrapIndex(head + i);
            sb.append(elements[index]);
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
    public boolean add(E element) {
        addLast(element);
        return true;
    }

    @Override
    public void addFirst(E element) {
        if (element == null) {
            throw new NullPointerException("Element cannot be null");
        }

        ensureCapacity(size + 1);
        head = wrapIndex(head - 1);
        elements[head] = element;
        size++;
    }

    @Override
    public void addLast(E element) {
        if (element == null) {
            throw new NullPointerException("Element cannot be null");
        }

        ensureCapacity(size + 1);
        elements[tail] = element;
        tail = wrapIndex(tail + 1);
        size++;
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E getFirst() {
        if (size == 0) {
            throw new NoSuchElementException("Deque is empty");
        }

        @SuppressWarnings("unchecked")
        E element = (E) elements[head];
        return element;
    }

    @Override
    public E getLast() {
        if (size == 0) {
            throw new NoSuchElementException("Deque is empty");
        }

        int lastIndex = wrapIndex(tail - 1);
        @SuppressWarnings("unchecked")
        E element = (E) elements[lastIndex];
        return element;
    }

    @Override
    public E poll() {
        return pollFirst();
    }

    @Override
    public E pollFirst() {
        if (size == 0) {
            return null;
        }

        @SuppressWarnings("unchecked")
        E element = (E) elements[head];
        elements[head] = null; // Помогаем сборщику мусора
        head = wrapIndex(head + 1);
        size--;
        return element;
    }

    @Override
    public E pollLast() {
        if (size == 0) {
            return null;
        }

        tail = wrapIndex(tail - 1);
        @SuppressWarnings("unchecked")
        E element = (E) elements[tail];
        elements[tail] = null; // Помогаем сборщику мусора
        size--;
        return element;
    }

    /////////////////////////////////////////////////////////////////////////
    //////         Остальные методы интерфейса Deque<E>              ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public boolean offer(E e) {
        return offerLast(e);
    }

    @Override
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    @Override
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    @Override
    public E remove() {
        return removeFirst();
    }

    @Override
    public E removeFirst() {
        if (size == 0) {
            throw new NoSuchElementException("Deque is empty");
        }
        return pollFirst();
    }

    @Override
    public E removeLast() {
        if (size == 0) {
            throw new NoSuchElementException("Deque is empty");
        }
        return pollLast();
    }

    @Override
    public E peek() {
        return peekFirst();
    }

    @Override
    public E peekFirst() {
        if (size == 0) {
            return null;
        }
        return getFirst();
    }

    @Override
    public E peekLast() {
        if (size == 0) {
            return null;
        }
        return getLast();
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void push(E e) {
        addFirst(e);
    }

    @Override
    public E pop() {
        return removeFirst();
    }

    /////////////////////////////////////////////////////////////////////////
    //////         Методы интерфейса Collection<E>                   ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        for (int i = 0; i < size; i++) {
            int index = wrapIndex(head + i);
            if (Objects.equals(o, elements[index])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new DequeIterator();
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new DescendingIterator();
    }

    // Внутренний класс для итератора в прямом порядке
    private class DequeIterator implements Iterator<E> {
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
            int index = wrapIndex(head + cursor);
            lastRet = cursor;
            cursor++;
            return (E) elements[index];
        }

        @Override
        public void remove() {
            if (lastRet < 0) {
                throw new IllegalStateException();
            }

            // Сдвигаем элементы
            if (lastRet < size / 2) {
                // Сдвигаем элементы влево от head до lastRet
                for (int i = lastRet; i > 0; i--) {
                    int from = wrapIndex(head + i - 1);
                    int to = wrapIndex(head + i);
                    elements[to] = elements[from];
                }
                elements[head] = null;
                head = wrapIndex(head + 1);
            } else {
                // Сдвигаем элементы вправо от lastRet до tail
                for (int i = lastRet; i < size - 1; i++) {
                    int from = wrapIndex(head + i + 1);
                    int to = wrapIndex(head + i);
                    elements[to] = elements[from];
                }
                tail = wrapIndex(tail - 1);
                elements[tail] = null;
            }

            size--;
            cursor = lastRet;
            lastRet = -1;
        }
    }

    // Внутренний класс для итератора в обратном порядке
    private class DescendingIterator implements Iterator<E> {
        private int cursor = size - 1;

        @Override
        public boolean hasNext() {
            return cursor >= 0;
        }

        @SuppressWarnings("unchecked")
        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            int index = wrapIndex(head + cursor);
            cursor--;
            return (E) elements[index];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    // Остальные методы Collection<E> и Deque<E> (необязательные для реализации)

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        for (int i = 0; i < size; i++) {
            int index = wrapIndex(head + i);
            result[i] = elements[index];
        }
        return result;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            int index = wrapIndex(head + i);
            elements[index] = null;
        }
        head = 0;
        tail = 0;
        size = 0;
    }
}