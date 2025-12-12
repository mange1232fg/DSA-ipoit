package by.it.group410971.kozich.lesson13;

import java.util.*;

public class GraphA {

    private Map<String, List<String>> graph;
    private Set<String> vertices;

    public GraphA() {
        graph = new HashMap<>();
        vertices = new TreeSet<>(); // TreeSet для лексикографического порядка
    }

    public void addEdge(String from, String to) {
        graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
        vertices.add(from);
        vertices.add(to);
    }

    public void addVertex(String vertex) {
        vertices.add(vertex);
        graph.putIfAbsent(vertex, new ArrayList<>());
    }

    // Топологическая сортировка с использованием алгоритма Кана
    public List<String> topologicalSort() {
        Map<String, Integer> inDegree = new HashMap<>();

        // Инициализация степеней входа
        for (String vertex : vertices) {
            inDegree.put(vertex, 0);
        }

        // Вычисление степеней входа
        for (List<String> edges : graph.values()) {
            for (String to : edges) {
                inDegree.put(to, inDegree.get(to) + 1);
            }
        }

        // Очередь вершин с нулевой степенью входа
        PriorityQueue<String> queue = new PriorityQueue<>(); // для лексикографического порядка
        for (String vertex : vertices) {
            if (inDegree.get(vertex) == 0) {
                queue.offer(vertex);
            }
        }

        List<String> result = new ArrayList<>();

        while (!queue.isEmpty()) {
            String current = queue.poll();
            result.add(current);

            // Уменьшаем степень входа для соседей
            if (graph.containsKey(current)) {
                for (String neighbor : graph.get(current)) {
                    inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                    if (inDegree.get(neighbor) == 0) {
                        queue.offer(neighbor);
                    }
                }
            }
        }

        // Проверка на наличие цикла
        if (result.size() != vertices.size()) {
            throw new IllegalStateException("Граф содержит цикл, топологическая сортировка невозможна");
        }

        return result;
    }

    // Топологическая сортировка с использованием DFS (рекурсивный метод)
    public List<String> topologicalSortDFS() {
        Set<String> visited = new HashSet<>();
        Set<String> onStack = new HashSet<>();
        List<String> result = new ArrayList<>();

        List<String> sortedVertices = new ArrayList<>(vertices);
        Collections.sort(sortedVertices); // Лексикографический порядок

        for (String vertex : sortedVertices) {
            if (!visited.contains(vertex)) {
                if (!dfs(vertex, visited, onStack, result)) {
                    throw new IllegalStateException("Граф содержит цикл, топологическая сортировка невозможна");
                }
            }
        }

        Collections.reverse(result);
        return result;
    }

    private boolean dfs(String vertex, Set<String> visited, Set<String> onStack, List<String> result) {
        visited.add(vertex);
        onStack.add(vertex);

        // Получаем соседей и сортируем их для лексикографического порядка
        List<String> neighbors = graph.getOrDefault(vertex, new ArrayList<>());
        Collections.sort(neighbors);

        for (String neighbor : neighbors) {
            if (!visited.contains(neighbor)) {
                if (!dfs(neighbor, visited, onStack, result)) {
                    return false;
                }
            } else if (onStack.contains(neighbor)) {
                // Обнаружен цикл
                return false;
            }
        }

        onStack.remove(vertex);
        result.add(vertex);
        return true;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GraphA graph = new GraphA();

        // Считываем строку с описанием графа
        String input = scanner.nextLine();

        // Обработка ввода
        processInput(graph, input);

        // Выполняем топологическую сортировку
        List<String> sorted;
        try {
            sorted = graph.topologicalSort(); // Используем алгоритм Кана
            // или можно использовать DFS: sorted = graph.topologicalSortDFS();
        } catch (IllegalStateException e) {
            System.out.println("Граф содержит цикл");
            return;
        }

        // Выводим результат
        for (int i = 0; i < sorted.size(); i++) {
            System.out.print(sorted.get(i));
            if (i < sorted.size() - 1) {
                System.out.print(" ");
            }
        }
        System.out.println();
    }

    private static void processInput(GraphA graph, String input) {
        // Удаляем пробелы для упрощения разбора
        String cleaned = input.replaceAll("\\s+", "");

        // Разбиваем на рёбра
        String[] edges = cleaned.split(",");

        for (String edge : edges) {
            // Разбиваем на вершины
            String[] vertices = edge.split("->");

            if (vertices.length == 2) {
                String from = vertices[0].trim();
                String to = vertices[1].trim();
                graph.addEdge(from, to);
            } else if (vertices.length == 1) {
                // Отдельная вершина без рёбер
                graph.addVertex(vertices[0].trim());
            }
        }
    }
}