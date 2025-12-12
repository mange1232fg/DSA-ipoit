package by.it.group410971.kozich.lesson14;

import java.util.*;

public class SitesB {

    // Реализация DSU с двумя эвристиками:
    // 1. Объединение по рангу
    // 2. Сжатие путей
    static class DSU {
        Map<String, String> parent;
        Map<String, Integer> rank;
        Map<String, Integer> size;
        int totalSets;

        DSU() {
            parent = new HashMap<>();
            rank = new HashMap<>();
            size = new HashMap<>();
            totalSets = 0;
        }

        // Добавление нового сайта
        void makeSet(String site) {
            if (!parent.containsKey(site)) {
                parent.put(site, site);
                rank.put(site, 0);
                size.put(site, 1);
                totalSets++;
            }
        }

        // Нахождение корня с полным сжатием путей
        String find(String site) {
            if (!parent.containsKey(site)) {
                makeSet(site);
                return site;
            }

            // Рекурсивное сжатие путей
            if (!parent.get(site).equals(site)) {
                parent.put(site, find(parent.get(site)));
            }
            return parent.get(site);
        }

        // Объединение множеств с эвристикой по рангу
        void union(String site1, String site2) {
            String root1 = find(site1);
            String root2 = find(site2);

            if (root1.equals(root2)) {
                return; // Уже в одном множестве
            }

            // Объединение по рангу
            if (rank.get(root1) < rank.get(root2)) {
                // Присоединяем root1 к root2
                parent.put(root1, root2);
                size.put(root2, size.get(root2) + size.get(root1));
            } else if (rank.get(root1) > rank.get(root2)) {
                // Присоединяем root2 к root1
                parent.put(root2, root1);
                size.put(root1, size.get(root1) + size.get(root2));
            } else {
                // Ранги равны, присоединяем root2 к root1 и увеличиваем ранг root1
                parent.put(root2, root1);
                size.put(root1, size.get(root1) + size.get(root2));
                rank.put(root1, rank.get(root1) + 1);
            }

            totalSets--;
        }

        // Получение размеров всех кластеров
        List<Integer> getClusterSizes() {
            Map<String, Integer> clusterSizes = new HashMap<>();

            // Проходим по всем сайтам и находим размеры кластеров через корни
            for (String site : parent.keySet()) {
                String root = find(site);
                clusterSizes.put(root, size.get(root));
            }

            // Собираем уникальные размеры
            List<Integer> sizes = new ArrayList<>();
            for (String root : clusterSizes.keySet()) {
                // Добавляем только если это корень (проверяем, что parent[root] == root)
                if (parent.get(root).equals(root)) {
                    sizes.add(size.get(root));
                }
            }

            // Сортируем по возрастанию
            Collections.sort(sizes);
            return sizes;
        }

        // Получение всех сайтов в кластере (для отладки)
        Map<String, List<String>> getClusters() {
            Map<String, List<String>> clusters = new HashMap<>();

            for (String site : parent.keySet()) {
                String root = find(site);
                clusters.computeIfAbsent(root, k -> new ArrayList<>()).add(site);
            }

            return clusters;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DSU dsu = new DSU();

        while (true) {
            String line = scanner.nextLine();

            // Проверка на завершение ввода
            if (line.equals("end")) {
                break;
            }

            // Разделение строки на два сайта
            String[] sites = line.split("\\+");

            if (sites.length == 2) {
                String site1 = sites[0].trim();
                String site2 = sites[1].trim();

                // Добавляем сайты в DSU
                dsu.makeSet(site1);
                dsu.makeSet(site2);

                // Объединяем сайты (направление ссылок не учитывается)
                dsu.union(site1, site2);
            }
        }

        // Получение и вывод размеров кластеров
        List<Integer> clusterSizes = dsu.getClusterSizes();

        for (int i = 0; i < clusterSizes.size(); i++) {
            System.out.print(clusterSizes.get(i));
            if (i < clusterSizes.size() - 1) {
                System.out.print(" ");
            }
        }
        System.out.println();

        // Для отладки можно вывести состав кластеров
        // printClusters(dsu);
    }

    // Метод для отладки - выводит состав кластеров
    private static void printClusters(DSU dsu) {
        Map<String, List<String>> clusters = dsu.getClusters();
        System.out.println("\nСостав кластеров:");

        List<Map.Entry<String, List<String>>> sortedClusters = new ArrayList<>(clusters.entrySet());
        sortedClusters.sort((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()));

        for (Map.Entry<String, List<String>> entry : sortedClusters) {
            List<String> sites = entry.getValue();
            Collections.sort(sites); // Сортируем сайты для читаемости
            System.out.println("Кластер размером " + sites.size() + ": " + sites);
        }
    }
}