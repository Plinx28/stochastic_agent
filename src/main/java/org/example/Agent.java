package org.example;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Agent {
    private State currentState;
    private Environment environment;

    public enum Action {
        UP, DOWN, LEFT, RIGHT
    }

    public Agent(Environment environment, int startX, int startY) {
        this.environment = environment;
        this.currentState = environment.getGrid()[startY][startX];
    }

    public Action chooseAction() {
        return Action.values()[(int)(Math.random() * Action.values().length)];
    }

    public void move() {
        Action chosenAction = chooseAction();
        currentState = environment.executeAction(currentState, chosenAction);
    }

    public void moveInDirection(Action action) {
        currentState = environment.executeAction(currentState, action);
    }
}