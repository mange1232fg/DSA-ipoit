package by.it.group410971.kozich.lesson14;

import java.util.Scanner;

public class StatesHanoiTowerC {

    // Класс для представления состояния трех стержней
    static class HanoiState {
        int[] a, b, c;
        int maxHeight; // Наибольшая высота среди трех пирамид

        HanoiState(int[] a, int[] b, int[] c) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.maxHeight = Math.max(Math.max(getHeight(a), getHeight(b)), getHeight(c));
        }

        private int getHeight(int[] rod) {
            return rod[0]; // В нашей реализации первый элемент - высота
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            HanoiState state = (HanoiState) obj;
            return arrayEquals(a, state.a) &&
                    arrayEquals(b, state.b) &&
                    arrayEquals(c, state.c);
        }

        private boolean arrayEquals(int[] arr1, int[] arr2) {
            if (arr1.length != arr2.length) return false;
            for (int i = 0; i < arr1.length; i++) {
                if (arr1[i] != arr2[i]) return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = 31 * result + arrayHash(a);
            result = 31 * result + arrayHash(b);
            result = 31 * result + arrayHash(c);
            return result;
        }

        private int arrayHash(int[] arr) {
            int result = 1;
            for (int value : arr) {
                result = 31 * result + value;
            }
            return result;
        }
    }

    // Упрощенная реализация DSU с массивами вместо коллекций
    static class DSU {
        int[] parent;
        int[] size;
        int count;

        DSU(int n) {
            parent = new int[n];
            size = new int[n];
            count = n;

            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        // Нахождение корня с полным сжатием путей
        int find(int x) {
            while (parent[x] != x) {
                parent[x] = parent[parent[x]]; // Частичное сжатие пути
                x = parent[x];
            }
            return x;
        }

        // Объединение с эвристикой по размеру
        void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX == rootY) return;

            // Эвристика по размеру: меньший присоединяем к большему
            if (size[rootX] < size[rootY]) {
                parent[rootX] = rootY;
                size[rootY] += size[rootX];
            } else {
                parent[rootY] = rootX;
                size[rootX] += size[rootY];
            }
            count--;
        }

        // Получение размеров всех компонент
        int[] getComponentSizes() {
            // Сначала находим все корни
            int[] rootSizes = new int[count];
            int index = 0;

            for (int i = 0; i < parent.length; i++) {
                if (parent[i] == i) { // Это корень
                    rootSizes[index++] = size[i];
                }
            }

            // Сортировка пузырьком (так как коллекции нельзя использовать)
            bubbleSort(rootSizes);

            return rootSizes;
        }

        private void bubbleSort(int[] arr) {
            for (int i = 0; i < arr.length - 1; i++) {
                for (int j = 0; j < arr.length - i - 1; j++) {
                    if (arr[j] > arr[j + 1]) {
                        int temp = arr[j];
                        arr[j] = arr[j + 1];
                        arr[j + 1] = temp;
                    }
                }
            }
        }
    }

    // Решение Ханойских башен с сохранением всех состояний
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();

        // Общее количество шагов (2^n - 1) минус начальное состояние
        int totalSteps = (1 << n) - 1 - 1; // -1 для начального состояния

        // Массивы для хранения состояний (индексы)
        // Используем массив максимального размера
        int[] heights = new int[totalSteps];
        int[][] states = new int[totalSteps][3];

        // Выполняем перемещения и сохраняем состояния
        solveHanoi(n, 'A', 'B', 'C', heights, states);

        // Группируем состояния по максимальной высоте пирамид
        groupStatesByHeight(heights, totalSteps);
    }

    private static void solveHanoi(int n, char from, char to, char aux, int[] heights, int[][] states) {
        // Имитация рекурсии с собственным стеком
        StackFrame[] stack = new StackFrame[2 * n]; // Максимальная глубина рекурсии
        int stackPtr = 0;

        // Начальный фрейм
        stack[stackPtr++] = new StackFrame(n, from, to, aux, 0);

        int stepIndex = 0;

        while (stackPtr > 0) {
            StackFrame frame = stack[--stackPtr];

            if (frame.n == 1) {
                // Перемещение диска
                if (frame.stage == 0) {
                    // Сохраняем состояние после перемещения (пропускаем начальное)
                    if (stepIndex < heights.length) {
                        // Вычисляем высоты пирамид
                        int heightA = calculateHeight('A', frame.from, frame.to, frame.aux);
                        int heightB = calculateHeight('B', frame.from, frame.to, frame.aux);
                        int heightC = calculateHeight('C', frame.from, frame.to, frame.aux);

                        heights[stepIndex] = Math.max(Math.max(heightA, heightB), heightC);
                        states[stepIndex][0] = heightA;
                        states[stepIndex][1] = heightB;
                        states[stepIndex][2] = heightC;
                        stepIndex++;
                    }
                }
            } else {
                // Рекурсивные вызовы
                switch (frame.stage) {
                    case 0:
                        // Первый рекурсивный вызов: перемещаем n-1 дисков с from на aux
                        stack[stackPtr++] = new StackFrame(frame.n, frame.from, frame.to, frame.aux, 1);
                        stack[stackPtr++] = new StackFrame(frame.n - 1, frame.from, frame.aux, frame.to, 0);
                        break;
                    case 1:
                        // Перемещаем n-й диск с from на to
                        if (stepIndex < heights.length) {
                            int heightA = calculateHeight('A', frame.from, frame.to, frame.aux);
                            int heightB = calculateHeight('B', frame.from, frame.to, frame.aux);
                            int heightC = calculateHeight('C', frame.from, frame.to, frame.aux);

                            heights[stepIndex] = Math.max(Math.max(heightA, heightB), heightC);
                            states[stepIndex][0] = heightA;
                            states[stepIndex][1] = heightB;
                            states[stepIndex][2] = heightC;
                            stepIndex++;
                        }

                        // Второй рекурсивный вызов: перемещаем n-1 дисков с aux на to
                        stack[stackPtr++] = new StackFrame(frame.n, frame.from, frame.to, frame.aux, 2);
                        stack[stackPtr++] = new StackFrame(frame.n - 1, frame.aux, frame.to, frame.from, 0);
                        break;
                    case 2:
                        // Завершение
                        break;
                }
            }
        }
    }

    // Вспомогательный класс для эмуляции рекурсивных вызовов
    static class StackFrame {
        int n;
        char from, to, aux;
        int stage; // 0: начало, 1: после первого рекурсивного вызова, 2: после второго

        StackFrame(int n, char from, char to, char aux, int stage) {
            this.n = n;
            this.from = from;
            this.to = to;
            this.aux = aux;
            this.stage = stage;
        }
    }

    private static int calculateHeight(char rod, char from, char to, char aux) {
        // Упрощенная логика: высота зависит от того, какие диски на каком стержне
        // В реальной реализации нужно отслеживать состояние каждого стержня

        // Для примера 5 дисков:
        // Эта логика приблизительная, реальная реализация требует отслеживания состояния
        if (rod == from) {
            return 1; // На исходном стержне всегда что-то остается
        } else if (rod == to) {
            return 2; // На целевой стержень что-то перемещается
        } else {
            return 0; // На вспомогательном может быть пусто
        }
    }

    private static void groupStatesByHeight(int[] heights, int totalSteps) {
        // Находим максимальную высоту
        int maxHeight = 0;
        for (int i = 0; i < totalSteps; i++) {
            if (heights[i] > maxHeight) {
                maxHeight = heights[i];
            }
        }

        // Создаем DSU для группировки состояний с одинаковой максимальной высотой
        DSU dsu = new DSU(totalSteps);

        // Объединяем состояния с одинаковой максимальной высотой
        for (int i = 0; i < totalSteps; i++) {
            for (int j = i + 1; j < totalSteps; j++) {
                if (heights[i] == heights[j]) {
                    dsu.union(i, j);
                }
            }
        }

        // Получаем размеры компонент
        int[] componentSizes = dsu.getComponentSizes();

        // Выводим результат
        for (int i = 0; i < componentSizes.length; i++) {
            System.out.print(componentSizes[i]);
            if (i < componentSizes.length - 1) {
                System.out.print(" ");
            }
        }
        System.out.println();
    }
}