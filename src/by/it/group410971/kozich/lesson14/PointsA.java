package by.it.group410971.kozich.lesson14;

import java.util.*;

public class PointsA {

    // Класс для представления точки в 3D пространстве
    static class Point {
        int x, y, z;
        int index; // индекс точки для DSU

        Point(int x, int y, int z, int index) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.index = index;
        }

        // Метод для вычисления расстояния между точками (квадрат расстояния для оптимизации)
        double distanceSquared(Point other) {
            int dx = this.x - other.x;
            int dy = this.y - other.y;
            int dz = this.z - other.z;
            return dx*dx + dy*dy + dz*dz;
        }

        // Евклидово расстояние
        double distance(Point other) {
            return Math.sqrt(distanceSquared(other));
        }
    }

    // Реализация DSU (Disjoint Set Union)
    static class DSU {
        int[] parent;
        int[] rank;
        int[] size;

        DSU(int n) {
            parent = new int[n];
            rank = new int[n];
            size = new int[n];

            for (int i = 0; i < n; i++) {
                parent[i] = i;
                rank[i] = 0;
                size[i] = 1;
            }
        }

        // Нахождение корня сжатием путей
        int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]); // Сжатие пути
            }
            return parent[x];
        }

        // Объединение множеств с эвристикой по рангу
        void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX == rootY) return;

            // Объединение по рангу
            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
                size[rootY] += size[rootX];
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
                size[rootX] += size[rootY];
            } else {
                parent[rootY] = rootX;
                size[rootX] += size[rootY];
                rank[rootX]++;
            }
        }

        // Получение размера компоненты
        int getSize(int x) {
            return size[find(x)];
        }

        // Получение всех размеров кластеров
        List<Integer> getClusterSizes() {
            Set<Integer> roots = new HashSet<>();
            List<Integer> sizes = new ArrayList<>();

            for (int i = 0; i < parent.length; i++) {
                int root = find(i);
                if (roots.add(root)) {
                    sizes.add(size[root]);
                }
            }

            Collections.sort(sizes);
            return sizes;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Чтение D и N
        int D = scanner.nextInt();
        int N = scanner.nextInt();

        // Чтение точек
        Point[] points = new Point[N];
        for (int i = 0; i < N; i++) {
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            int z = scanner.nextInt();
            points[i] = new Point(x, y, z, i);
        }

        // Инициализация DSU
        DSU dsu = new DSU(N);

        // Объединение близких точек
        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {
                double distance = points[i].distance(points[j]);
                // Объединяем, если расстояние строго меньше D
                if (distance < D) {
                    dsu.union(i, j);
                }
            }
        }

        // Получение размеров кластеров
        List<Integer> clusterSizes = dsu.getClusterSizes();

        // Вывод результатов
        for (int i = 0; i < clusterSizes.size(); i++) {
            System.out.print(clusterSizes.get(i));
            if (i < clusterSizes.size() - 1) {
                System.out.print(" ");
            }
        }
        System.out.println();
    }
}