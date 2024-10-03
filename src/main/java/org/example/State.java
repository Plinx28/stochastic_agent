package org.example;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class State {
    private int x;
    private int y;
    private double reward;
    private boolean isObstacle;

    public State(int x, int y, double reward) {
        this.x = x;
        this.y = y;
        this.reward = reward;
        this.isObstacle = false;
    }
}