package by.it.group410971.kozich.lesson03;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Lesson 3. C_Heap.
// Задача: построить max-кучу = пирамиду = бинарное сбалансированное дерево на массиве.
// ВАЖНО! НЕЛЬЗЯ ИСПОЛЬЗОВАТЬ НИКАКИЕ КОЛЛЕКЦИИ, КРОМЕ ARRAYLIST (его можно, но только для массива)

public class C_HeapMax {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = C_HeapMax.class.getResourceAsStream("dataC.txt");
        C_HeapMax instance = new C_HeapMax();
        System.out.println("MAX=" + instance.findMaxValue(stream));
    }

    // эта процедура читает данные из файла, ее можно не менять.
    Long findMaxValue(InputStream stream) {
        Long maxValue = 0L;
        MaxHeap heap = new MaxHeap();
        Scanner scanner = new Scanner(stream);
        Integer count = scanner.nextInt();
        scanner.nextLine(); // пропускаем остаток строки после числа

        for (int i = 0; i < count; ) {
            if (!scanner.hasNextLine()) break; // защита от отсутствия строк
            String s = scanner.nextLine().trim();
            if (s.equalsIgnoreCase("extractMax")) {
                Long res = heap.extractMax();
                if (res != null && res > maxValue) maxValue = res;
                System.out.println(res);
                i++;
            } else if (s.toLowerCase().startsWith("insert")) {
                String[] p = s.split(" ");
                if (p.length == 2) {
                    heap.insert(Long.parseLong(p[1]));
                }
                i++;
            }
        }
        return maxValue;
    }

    private class MaxHeap {
        private List<Long> heap = new ArrayList<>();

        // Просеивание вниз (siftDown) - корректируем элемент с индексом i вниз по дереву
        private void siftDown(int i) {
            int size = heap.size();
            while (true) {
                int left = 2 * i + 1;
                int right = 2 * i + 2;
                int largest = i;

                if (left < size && heap.get(left) > heap.get(largest)) {
                    largest = left;
                }
                if (right < size && heap.get(right) > heap.get(largest)) {
                    largest = right;
                }
                if (largest == i) break;

                swap(i, largest);
                i = largest;
            }
        }

        // Просеивание вверх (siftUp) - корректируем элемент с индексом i вверх по дереву
        private void siftUp(int i) {
            while (i > 0) {
                int parent = (i - 1) / 2;
                if (heap.get(i) <= heap.get(parent)) break;
                swap(i, parent);
                i = parent;
            }
        }

        // Вставка нового элемента
        void insert(Long value) {
            heap.add(value);
            siftUp(heap.size() - 1);
        }

        // Извлечение максимума (корня)
        Long extractMax() {
            if (heap.isEmpty()) return null;
            Long max = heap.get(0);
            Long last = heap.remove(heap.size() - 1);
            if (!heap.isEmpty()) {
                heap.set(0, last);
                siftDown(0);
            }
            return max;
        }

        // Вспомогательный метод для обмена элементов
        private void swap(int i, int j) {
            Long temp = heap.get(i);
            heap.set(i, heap.get(j));
            heap.set(j, temp);
        }
    }
}
