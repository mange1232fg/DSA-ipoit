package by.it.group410971.kozich.lesson13;

import java.util.*;

public class GraphC {

    private Map<String, List<String>> graph;
    private Map<String, List<String>> reversedGraph;
    private Set<String> vertices;

    public GraphC() {
        graph = new HashMap<>();
        reversedGraph = new HashMap<>();
        vertices = new TreeSet<>(); // TreeSet для лексикографического порядка
    }

    public void addEdge(String from, String to) {
        graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
        reversedGraph.computeIfAbsent(to, k -> new ArrayList<>()).add(from);
        vertices.add(from);
        vertices.add(to);
    }

    public void addVertex(String vertex) {
        vertices.add(vertex);
        graph.putIfAbsent(vertex, new ArrayList<>());
        reversedGraph.putIfAbsent(vertex, new ArrayList<>());
    }

    // Алгоритм Косарайю для поиска компонент сильной связности
    public List<Set<String>> findSCCs() {
        List<Set<String>> sccs = new ArrayList<>();

        // Шаг 1: Первый обход DFS для получения порядка завершения
        Stack<String> stack = new Stack<>();
        Set<String> visited = new HashSet<>();

        List<String> sortedVertices = new ArrayList<>(vertices);
        Collections.sort(sortedVertices); // Лексикографический порядок

        for (String vertex : sortedVertices) {
            if (!visited.contains(vertex)) {
                dfsFirstPass(vertex, visited, stack);
            }
        }

        // Шаг 2: Второй обход DFS на обратном графе
        visited.clear();

        while (!stack.isEmpty()) {
            String vertex = stack.pop();
            if (!visited.contains(vertex)) {
                Set<String> scc = new TreeSet<>(); // TreeSet для лексикографического порядка
                dfsSecondPass(vertex, visited, scc);
                sccs.add(scc);
            }
        }

        // Сортируем компоненты по размеру и лексикографически
        sccs.sort((scc1, scc2) -> {
            // Сначала сортируем по размеру (в обратном порядке - большие первыми)
            int sizeCompare = Integer.compare(scc2.size(), scc1.size());
            if (sizeCompare != 0) {
                return sizeCompare;
            }
            // Если размеры равны, сортируем по первой вершине
            String first1 = scc1.iterator().next();
            String first2 = scc2.iterator().next();
            return first1.compareTo(first2);
        });

        return sccs;
    }

    // Первый обход DFS (прямой граф)
    private void dfsFirstPass(String vertex, Set<String> visited, Stack<String> stack) {
        visited.add(vertex);

        List<String> neighbors = graph.getOrDefault(vertex, new ArrayList<>());
        Collections.sort(neighbors); // Лексикографический порядок

        for (String neighbor : neighbors) {
            if (!visited.contains(neighbor)) {
                dfsFirstPass(neighbor, visited, stack);
            }
        }

        stack.push(vertex);
    }

    // Второй обход DFS (обратный граф)
    private void dfsSecondPass(String vertex, Set<String> visited, Set<String> scc) {
        visited.add(vertex);
        scc.add(vertex);

        List<String> neighbors = reversedGraph.getOrDefault(vertex, new ArrayList<>());
        Collections.sort(neighbors); // Лексикографический порядок

        for (String neighbor : neighbors) {
            if (!visited.contains(neighbor)) {
                dfsSecondPass(neighbor, visited, scc);
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GraphC graph = new GraphC();

        // Считываем строку с описанием графа
        String input = scanner.nextLine();

        // Обработка ввода
        processInput(graph, input);

        // Находим компоненты сильной связности
        List<Set<String>> sccs = graph.findSCCs();

        // Выводим результат
        for (Set<String> scc : sccs) {
            for (String vertex : scc) {
                System.out.print(vertex);
            }
            System.out.println();
        }
    }

    private static void processInput(GraphC graph, String input) {
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