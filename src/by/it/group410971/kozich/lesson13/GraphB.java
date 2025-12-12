package by.it.group410971.kozich.lesson13;

import java.util.*;

public class GraphB {

    private Map<String, List<String>> graph;
    private Set<String> vertices;

    public GraphB() {
        graph = new HashMap<>();
        vertices = new HashSet<>();
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

    // Проверка наличия цикла с использованием DFS
    public boolean hasCycle() {
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();

        for (String vertex : vertices) {
            if (!visited.contains(vertex)) {
                if (hasCycleDFS(vertex, visited, recursionStack)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasCycleDFS(String vertex, Set<String> visited, Set<String> recursionStack) {
        // Помечаем вершину как посещенную и добавляем в стек рекурсии
        visited.add(vertex);
        recursionStack.add(vertex);

        // Проверяем всех соседей
        List<String> neighbors = graph.getOrDefault(vertex, new ArrayList<>());
        for (String neighbor : neighbors) {
            if (!visited.contains(neighbor)) {
                if (hasCycleDFS(neighbor, visited, recursionStack)) {
                    return true;
                }
            } else if (recursionStack.contains(neighbor)) {
                // Если сосед уже в стеке рекурсии, значит найден цикл
                return true;
            }
        }

        // Убираем вершину из стека рекурсии перед возвратом
        recursionStack.remove(vertex);
        return false;
    }

    // Альтернативный метод: проверка циклов с помощью топологической сортировки (алгоритм Кана)
    public boolean hasCycleKahn() {
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
        Queue<String> queue = new LinkedList<>();
        for (String vertex : vertices) {
            if (inDegree.get(vertex) == 0) {
                queue.offer(vertex);
            }
        }

        int count = 0;

        while (!queue.isEmpty()) {
            String current = queue.poll();
            count++;

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

        // Если количество обработанных вершин меньше общего числа вершин, значит есть цикл
        return count != vertices.size();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GraphB graph = new GraphB();

        // Считываем строку с описанием графа
        String input = scanner.nextLine();

        // Обработка ввода
        processInput(graph, input);

        // Проверяем наличие циклов
        boolean hasCycle = graph.hasCycle(); // Используем DFS метод
        // или можно использовать: boolean hasCycle = graph.hasCycleKahn();

        // Выводим результат
        System.out.println(hasCycle ? "yes" : "no");
    }

    private static void processInput(GraphB graph, String input) {
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