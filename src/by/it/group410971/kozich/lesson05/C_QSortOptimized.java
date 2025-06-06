package by.it.group410971.kozich.lesson05;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

/*
Видеорегистраторы и площадь 2.
Условие то же что и в задаче А.

По сравнению с задачей A доработайте алгоритм так, чтобы
1) он оптимально использовал время и память:
    - за стек отвечает элиминация хвостовой рекурсии
    - за сам массив отрезков - сортировка на месте
    - рекурсивные вызовы должны проводиться на основе 3-разбиения

2) при поиске подходящих отрезков для точки реализуйте метод бинарного поиска
для первого отрезка решения, а затем найдите оставшуюся часть решения
(т.е. отрезков, подходящих для точки, может быть много)

Sample Input:
2 3
0 5
7 10
1 6 11
Sample Output:
1 0 0
*/

public class C_QSortOptimized {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = C_QSortOptimized.class.getResourceAsStream("dataC.txt");
        C_QSortOptimized instance = new C_QSortOptimized();
        int[] result = instance.getAccessory2(stream);
        for (int index : result) {
            System.out.print(index + " ");
        }
    }

    int[] getAccessory2(InputStream stream) throws FileNotFoundException {
        Scanner scanner = new Scanner(stream);

        int n = scanner.nextInt();
        Segment[] segments = new Segment[n];
        int m = scanner.nextInt();
        int[] points = new int[m];
        int[] result = new int[m];

        for (int i = 0; i < n; i++) {
            int start = scanner.nextInt();
            int stop = scanner.nextInt();
            if (start > stop) {
                int tmp = start;
                start = stop;
                stop = tmp;
            }
            segments[i] = new Segment(start, stop);
        }

        for (int i = 0; i < m; i++) {
            points[i] = scanner.nextInt();
        }

        // Сортируем массив отрезков по start с 3-разбиением (быстрая сортировка)
        quickSort3Way(segments, 0, n - 1);

        // Для каждой точки ищем количество покрывающих отрезков
        for (int i = 0; i < m; i++) {
            int point = points[i];
            int firstIndex = binarySearchFirstCover(segments, point);
            if (firstIndex == -1) {
                result[i] = 0;
            } else {
                // Перебираем все отрезки, начиная с firstIndex, пока покрывают точку
                int count = 0;
                for (int j = firstIndex; j < n && segments[j].start <= point; j++) {
                    if (segments[j].stop >= point) count++;
                }
                result[i] = count;
            }
        }

        return result;
    }

    // Быстрая сортировка с 3-разбиением (Dutch National Flag)
    private void quickSort3Way(Segment[] arr, int low, int high) {
        while (low < high) {
            int lt = low, gt = high;
            Segment pivot = arr[low];
            int i = low + 1;
            while (i <= gt) {
                int cmp = arr[i].compareTo(pivot);
                if (cmp < 0) {
                    swap(arr, lt++, i++);
                } else if (cmp > 0) {
                    swap(arr, i, gt--);
                } else {
                    i++;
                }
            }
            // Рекурсивно сортируем меньшую часть, итеративно — большую (элиминация хвостовой рекурсии)
            if (lt - low < high - gt) {
                quickSort3Way(arr, low, lt - 1);
                low = gt + 1;
            } else {
                quickSort3Way(arr, gt + 1, high);
                high = lt - 1;
            }
        }
    }

    // Бинарный поиск первого отрезка, у которого start <= point и stop >= point
    private int binarySearchFirstCover(Segment[] segments, int point) {
        int left = 0;
        int right = segments.length - 1;
        int result = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (segments[mid].start <= point) {
                if (segments[mid].stop >= point) {
                    result = mid;
                    right = mid - 1; // ищем более левый подходящий отрезок
                } else {
                    left = mid + 1;
                }
            } else {
                right = mid - 1;
            }
        }
        return result;
    }

    private void swap(Segment[] arr, int i, int j) {
        Segment temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    private class Segment implements Comparable<Segment> {
        int start;
        int stop;

        Segment(int start, int stop) {
            this.start = start;
            this.stop = stop;
        }

        @Override
        public int compareTo(Segment o) {
            return Integer.compare(this.start, o.start);
        }
    }
}
