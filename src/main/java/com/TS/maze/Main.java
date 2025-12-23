package com.TS.maze;

import MazeGen.Cell;
import MazeGen.Maze;
import MazeGen.MazeGenerator;
import Solvers.AStar;
import Solvers.BFS;
import Solvers.DFS;
import Solvers.RightHandSolver;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class Main extends Application {

    private Maze maze;
    private MazeView mazeView;
    private BorderPane root;
    private long currentSeed = new Random().nextLong();
    private int rows = 10;
    private int cols = 10;

    private Spinner<Integer> rowSpinner;
    private Spinner<Integer> colSpinner;
    private TextField seedInput;
    private Button clearButton;
    private Label timeLabel = new Label("Time to solve: --");
    private Label statusLabel = new Label();
    private ComboBox<String> speedSelector;
    private double animationSpeed = 100.0; // Default speed in ms per step

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();

        setupControls();
        setupBottomBar();
        setupResizeListeners();

        Scene scene = new Scene(root, 1000, 900);
        primaryStage.setTitle("Maze Solver");
        primaryStage.setScene(scene);
        primaryStage.show();

        Platform.runLater(() -> regenerateMaze());
    }

    private void setupControls() {
        VBox controlPanel = new VBox(10);
        controlPanel.setPadding(new Insets(10));
        controlPanel.setStyle("-fx-background-color: #dddddd;");
        root.setLeft(controlPanel);

        seedInput = new TextField(String.valueOf(currentSeed));
        seedInput.setPromptText("Seed");

        rowSpinner = new Spinner<>(5, 100, rows);
        colSpinner = new Spinner<>(5, 100, cols);
        rowSpinner.setEditable(true);
        colSpinner.setEditable(true);

        speedSelector = new ComboBox<>();
        speedSelector.getItems().addAll("Slow", "Normal", "Fast", "Instant");
        speedSelector.setValue("Normal");
        speedSelector.setOnAction(e -> updateAnimationSpeed());
        updateAnimationSpeed();

        Button generateButton = new Button("Generate Maze");
        Button randomSeedButton = new Button("Random Seed");
        Button dfsButton = new Button("Solve with DFS");
        Button bfsButton = new Button("Solve with BFS");
        Button aStarButton = new Button("Solve with A*");
        Button rhsButton = new Button("Solve with RHS");

        generateButton.setOnAction(e -> {
            try {
                rowSpinner.increment(0);
                colSpinner.increment(0);
                rows = rowSpinner.getValue();
                cols = colSpinner.getValue();
                currentSeed = Long.parseLong(seedInput.getText());
            } catch (NumberFormatException ex) {
                currentSeed = new Random().nextLong();
            }
            regenerateMaze();
        });

        randomSeedButton.setOnAction(e -> {
            currentSeed = new Random().nextLong();
            seedInput.setText(String.valueOf(currentSeed));
            regenerateMaze();
        });

        dfsButton.setOnAction(e -> solveWith(new DFS(maze)));
        bfsButton.setOnAction(e -> solveWith(new BFS(maze)));
        aStarButton.setOnAction(e -> solveWith(new AStar(maze)));
        rhsButton.setOnAction(e -> solveWith(new RightHandSolver(maze)));

        HBox generateBox = new HBox(10, generateButton, randomSeedButton);
        generateBox.setPadding(new Insets(5, 0, 5, 0));

        controlPanel.getChildren().addAll(
                new Label("Maze Dimensions"),
                new Label("Rows:"), rowSpinner,
                new Label("Cols:"), colSpinner,
                new Label("Seed:"), seedInput,
                new Label("Animation Speed:"), speedSelector,
                generateBox,
                new Separator(),
                dfsButton, bfsButton, aStarButton, rhsButton
        );
    }

    private void updateAnimationSpeed() {
        String speed = speedSelector.getValue();
        switch (speed) {
            case "Slow" -> animationSpeed = 200.0;
            case "Normal" -> animationSpeed = 100.0;
            case "Fast" -> animationSpeed = 50.0;
            case "Instant" -> animationSpeed = 1.0;
        }
    }

    private void setupBottomBar() {
        clearButton = new Button("Clear Path");
        clearButton.setOnAction(e -> {
            if (mazeView != null) mazeView.clearPath();
        });

        VBox bottomBox = new VBox(10, timeLabel, statusLabel, clearButton);
        bottomBox.setPadding(new Insets(10));
        bottomBox.setStyle("-fx-alignment: center;");
        root.setBottom(bottomBox);
    }

    private void setupResizeListeners() {
        root.widthProperty().addListener((obs, oldVal, newVal) -> resizeMazeView());
        root.heightProperty().addListener((obs, oldVal, newVal) -> resizeMazeView());
    }

    private void resizeMazeView() {
        if (maze == null) return;
        buildMazeView();
    }

    private void regenerateMaze() {
        maze = new Maze(currentSeed, rows, cols);
        new MazeGenerator(maze).generateMaze();
        buildMazeView();
    }

    private void buildMazeView() {
        mazeView = new MazeView(maze);
        mazeView.setMinSize(0, 0);
        mazeView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(mazeView, Priority.ALWAYS);
        root.setCenter(mazeView);
    }

    private void solveWith(Object solver) {
        List<Cell> path = null;
        List<Cell> visited = null;
        Duration duration = null;

        if (solver instanceof DFS dfs) {
            path = dfs.solve();
            visited = dfs.getVisitedOrder();
            duration = dfs.getTimeToSolve();
        } else if (solver instanceof BFS bfs) {
            path = bfs.solve();
            visited = bfs.getVisitedOrder();
            duration = bfs.getTimeToSolve();
        } else if (solver instanceof AStar astar) {
            path = astar.solve();
            visited = astar.getVisitedOrder();
            duration = astar.getTimeToSolve();
        } else if (solver instanceof RightHandSolver rhs) {
            path = rhs.solve();
            duration = rhs.getTimeToSolve();

            clearButton.setDisable(true);
            statusLabel.setText("Solving...");

            // Animate the raw traversal
            mazeView.animatePath(path, animationSpeed, () -> {
                // After that, use A* to find and highlight the true path
                AStar aStar = new AStar(maze);
                List<Cell> optimalPath = aStar.solve();

                mazeView.animateVisitedOrder(List.of(), optimalPath, animationSpeed / 2, () -> {
                    clearButton.setDisable(false);
                    statusLabel.setText("Done (RHS traversal + optimal path)");
                });
            });

            double ms = duration.toNanos() / 1_000_000.0;
            double us = duration.toNanos() / 1_000.0;
            timeLabel.setText(String.format("Time: %.3f ms (%.0f μs)", ms, us));
        }


        if (duration != null) {
            clearButton.setDisable(true);
            statusLabel.setText("Solving...");

            if (solver instanceof RightHandSolver) {
                // Only animate final path
                mazeView.animatePath(path, animationSpeed, () -> {
                    clearButton.setDisable(false);
                    statusLabel.setText("Done");
                });
            } else {
                // Animate visited order and final path
                mazeView.animateVisitedOrder(visited, path, animationSpeed, () -> {
                    clearButton.setDisable(false);
                    statusLabel.setText("Done");
                });
            }

            double ms = duration.toNanos() / 1_000_000.0;
            double us = duration.toNanos() / 1_000.0;
            timeLabel.setText(String.format("Time: %.3f ms (%.0f μs)", ms, us));
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}