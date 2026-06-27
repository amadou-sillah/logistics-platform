package com.logistics.adt;

import java.util.*;

public class Graph<T> {
    private final Map<T, List<T>> adjacency = new HashMap<>();

    public void addNode(T node) {
        adjacency.putIfAbsent(node, new ArrayList<>());
    }

    public void addEdge(T from, T to) {
        addNode(from);
        addNode(to);
        adjacency.get(from).add(to);
        // undirected: adjacency.get(to).add(from);
    }

    public List<T> shortestPath(T start, T end) {
        if (!adjacency.containsKey(start) || !adjacency.containsKey(end)) return null;
        Map<T, T> parent = new HashMap<>();
        Queue<T> queue = new LinkedList<>();
        Set<T> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);
        parent.put(start, null);
        while (!queue.isEmpty()) {
            T current = queue.poll();
            if (current.equals(end)) break;
            for (T neighbor : adjacency.getOrDefault(current, Collections.emptyList())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }
        if (!visited.contains(end)) return null;
        List<T> path = new LinkedList<>();
        T node = end;
        while (node != null) {
            path.add(0, node);
            node = parent.get(node);
        }
        return path;
    }
}
