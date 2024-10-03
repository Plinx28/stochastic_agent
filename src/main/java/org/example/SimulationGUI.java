package org.example;

import lombok.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

@Data
@NoArgsConstructor
public class SimulationGUI extends JFrame {
    private Environment environment;
    private Agent agent;
    private PathFinder pathFinder;
    private JPanel mainPanel;
    private JPanel gridPanel;
    private JPanel controlPanel;
    private JButton stepButton;
    private JButton upButton;
    private JButton downButton;
    private JButton leftButton;
    private JButton rightButton;
    private JButton restartButton;
    private JLabel statusLabel;
    private JToggleButton shortestPathButton;
    private List<State> shortestPath;
    private boolean showShortestPath = false;

    public SimulationGUI(int width, int height) {
        initializeEnvironmentAndAgent(width, height);
        pathFinder = new PathFinder(environment);

        setTitle("Стохастическая среда");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        mainPanel = new JPanel(new BorderLayout());

        gridPanel = new JPanel(new GridLayout(height, width));
        updateGrid();
        mainPanel.add(gridPanel, BorderLayout.CENTER);

        controlPanel = new JPanel(new GridBagLayout());
        createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        statusLabel = new JLabel("Агент в начальном положении");
        add(statusLabel, BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void initializeEnvironmentAndAgent(int width, int height) {
        environment = new Environment(width, height);
        agent = new Agent(environment, 0, 0);
    }

    private void createControlPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        upButton = new JButton("↑");
        downButton = new JButton("↓");
        leftButton = new JButton("←");
        rightButton = new JButton("→");
        stepButton = new JButton("Случайный шаг");
        restartButton = new JButton("Начать заново");
        shortestPathButton = new JToggleButton("Показать кратчайший путь");

        gbc.gridx = 1;
        gbc.gridy = 0;
        controlPanel.add(upButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        controlPanel.add(downButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        controlPanel.add(leftButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        controlPanel.add(rightButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        controlPanel.add(stepButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        controlPanel.add(restartButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        controlPanel.add(shortestPathButton, gbc);

        ActionListener buttonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == upButton) {
                    step(Agent.Action.UP);
                } else if (e.getSource() == downButton) {
                    step(Agent.Action.DOWN);
                } else if (e.getSource() == leftButton) {
                    step(Agent.Action.LEFT);
                } else if (e.getSource() == rightButton) {
                    step(Agent.Action.RIGHT);
                } else if (e.getSource() == stepButton) {
                    step(null);
                } else if (e.getSource() == restartButton) {
                    restartSimulation();
                }
            }
        };

        upButton.addActionListener(buttonListener);
        downButton.addActionListener(buttonListener);
        leftButton.addActionListener(buttonListener);
        rightButton.addActionListener(buttonListener);
        stepButton.addActionListener(buttonListener);
        restartButton.addActionListener(buttonListener);
        shortestPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showShortestPath = shortestPathButton.isSelected();
                updateShortestPath();
                updateGrid();
            }
        });
    }

    private void updateGrid() {
        gridPanel.removeAll();
        State[][] grid = environment.getGrid();
        for (int y = 0; y < environment.getHeight(); y++) {
            for (int x = 0; x < environment.getWidth(); x++) {
                JPanel cell = new JPanel();
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                cell.setPreferredSize(new Dimension(50, 50));
                State state = grid[y][x];
                if (state.isObstacle()) {
                    cell.setBackground(Color.BLACK);
                } else if (state.getReward() == 1) {
                    cell.setBackground(Color.GREEN);
                } else if (state.getReward() == -1) {
                    cell.setBackground(Color.RED);
                } else if (showShortestPath && shortestPath.contains(state)) {
                    cell.setBackground(Color.YELLOW);
                }
                if (agent.getCurrentState().getX() == x && agent.getCurrentState().getY() == y) {
                    JLabel agentLabel = new JLabel("A");
                    agentLabel.setForeground(Color.BLUE);
                    cell.add(agentLabel);
                }
                gridPanel.add(cell);
            }
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void step(Agent.Action action) {
        if (action == null) {
            agent.move();
        } else {
            agent.moveInDirection(action);
        }
        updateShortestPath();
        updateGrid();
        State currentState = agent.getCurrentState();
        if (currentState.getReward() != 0) {
            disableMovementButtons();
            statusLabel.setText("Агент достиг целевого состояния с наградой " + currentState.getReward());
        } else {
            statusLabel.setText("Агент переместился в позицию (" + currentState.getX() + ", " + currentState.getY() + ")");
        }
    }

    private void disableMovementButtons() {
        upButton.setEnabled(false);
        downButton.setEnabled(false);
        leftButton.setEnabled(false);
        rightButton.setEnabled(false);
        stepButton.setEnabled(false);
    }

    private void enableMovementButtons() {
        upButton.setEnabled(true);
        downButton.setEnabled(true);
        leftButton.setEnabled(true);
        rightButton.setEnabled(true);
        stepButton.setEnabled(true);
    }

    private void restartSimulation() {
        initializeEnvironmentAndAgent(environment.getWidth(), environment.getHeight());
        showShortestPath = false;
        shortestPathButton.setSelected(false);
        shortestPath = Collections.emptyList();
        updateGrid();
        enableMovementButtons();
        statusLabel.setText("Агент в начальном положении");
    }

    private void updateShortestPath() {
        if (showShortestPath) {
            State start = agent.getCurrentState();
            State goal = pathFinder.findGoalState();
            if (goal != null) {
                shortestPath = pathFinder.findShortestPath(start, goal);
            } else {
                shortestPath = Collections.emptyList();
            }
        } else {
            shortestPath = Collections.emptyList();
        }
    }
}