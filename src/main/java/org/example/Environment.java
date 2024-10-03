package org.example;

import lombok.*;
import java.util.Random;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Environment {
    private State[][] grid;
    private int width;
    private int height;
    private Random random = new Random();

    public Environment(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new State[height][width];
        initializeGrid();
    }

    private void initializeGrid() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = new State(x, y, 0);
            }
        }
        grid[0][width-1].setReward(1);
        grid[1][width-1].setReward(-1);

        // Добавляем преграды
        addObstacle(2, 0);
        addObstacle(1, 1);
        addObstacle(3, 3);
        addObstacle(4, 2);
        addObstacle(7, 2);
        addObstacle(7, 4);
    }

    private void addObstacle(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            grid[y][x].setObstacle(true);
        }
    }

    public State executeAction(State currentState, Agent.Action action) {
        double probability = random.nextDouble();
        if (probability < 0.8) {
            return moveInDirection(currentState, action);
        } else if (probability < 0.9) {
            return moveInDirection(currentState, rotateLeft(action));
        } else {
            return moveInDirection(currentState, rotateRight(action));
        }
    }

    private State moveInDirection(State currentState, Agent.Action action) {
        int newX = currentState.getX();
        int newY = currentState.getY();

        switch (action) {
            case UP: newY = Math.max(0, newY - 1); break;
            case DOWN: newY = Math.min(height - 1, newY + 1); break;
            case LEFT: newX = Math.max(0, newX - 1); break;
            case RIGHT: newX = Math.min(width - 1, newX + 1); break;
        }

        if (grid[newY][newX].isObstacle()) {
            return currentState; // Если новая позиция - преграда, остаемся на месте
        }

        return grid[newY][newX];
    }

    private Agent.Action rotateLeft(Agent.Action action) {
        switch (action) {
            case UP: return Agent.Action.LEFT;
            case LEFT: return Agent.Action.DOWN;
            case DOWN: return Agent.Action.RIGHT;
            case RIGHT: return Agent.Action.UP;
            default: return action;
        }
    }

    private Agent.Action rotateRight(Agent.Action action) {
        switch (action) {
            case UP: return Agent.Action.RIGHT;
            case RIGHT: return Agent.Action.DOWN;
            case DOWN: return Agent.Action.LEFT;
            case LEFT: return Agent.Action.UP;
            default: return action;
        }
    }
}