package org.example;

import java.util.*;

public class PathFinder {
    private Environment environment;

    public PathFinder(Environment environment) {
        this.environment = environment;
    }

    public List<State> findShortestPath(State start, State goal) {
        Queue<State> queue = new LinkedList<>();
        Map<State, State> parentMap = new HashMap<>();
        Set<State> visited = new HashSet<>();

        queue.offer(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            State current = queue.poll();

            if (current.equals(goal)) {
                return reconstructPath(parentMap, start, goal);
            }

            for (State neighbor : getNeighbors(current)) {
                if (!visited.contains(neighbor) && !neighbor.isObstacle() && !isNearRed(neighbor)) {
                    queue.offer(neighbor);
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                }
            }
        }
        System.out.println("No path found");
        return Collections.emptyList(); // Путь не найден
    }

    private List<State> reconstructPath(Map<State, State> parentMap, State start, State goal) {
        List<State> path = new ArrayList<>();
        State current = goal;

        while (!current.equals(start)) {
            path.add(current);
            current = parentMap.get(current);
        }

        Collections.reverse(path);
        return path;
    }

    private List<State> getNeighbors(State state) {
        List<State> neighbors = new ArrayList<>();
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        for (int[] dir : directions) {
            int newX = state.getX() + dir[0];
            int newY = state.getY() + dir[1];

            if (newX >= 0 && newX < environment.getWidth() && newY >= 0 && newY < environment.getHeight()) {
                neighbors.add(environment.getGrid()[newY][newX]);
            }
        }

        return neighbors;
    }

    private boolean isNearRed(State state) {
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int[] dir : directions) {
            int newX = state.getX() + dir[0];
            int newY = state.getY() + dir[1];
            if (newX >= 0 && newX < environment.getWidth() && newY >= 0 && newY < environment.getHeight()) {
                if (environment.getGrid()[newY][newX].getReward() == -1) {
                    return true;
                }
            }
        }
        return false;
    }

    public State findGoalState() {
        State[][] grid = environment.getGrid();
        for (int y = 0; y < environment.getHeight(); y++) {
            for (int x = 0; x < environment.getWidth(); x++) {
                if (grid[y][x].getReward() == 1) {
                    return grid[y][x];
                }
            }
        }
        return null;
    }
}