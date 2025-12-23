package com.TS.maze;

import MazeGen.Cell;
import MazeGen.Direction;
import MazeGen.Maze;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.List;

public class MazeView extends GridPane {
    private final Maze maze;

    public MazeView(Maze maze) {
        this.maze = maze;
        setHgap(0);
        setVgap(0);
        setStyle("-fx-background-color: black;");
        buildCells();
    }

    private void buildCells() {
        int rows = maze.getRows();
        int cols = maze.getCols();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = maze.getCell(r, c);
                Pane cellPane = new Pane();

                // Make each cell resize with the grid
                cellPane.prefWidthProperty().bind(widthProperty().divide(cols));
                cellPane.prefHeightProperty().bind(heightProperty().divide(rows));

                Rectangle bg = new Rectangle();
                bg.widthProperty().bind(cellPane.widthProperty());
                bg.heightProperty().bind(cellPane.heightProperty());

                if (cell == maze.getStartCell()) {
                    bg.setFill(Color.LIMEGREEN);
                } else if (cell == maze.getEndCell()) {
                    bg.setFill(Color.RED);
                } else {
                    bg.setFill(Color.WHITE);
                }

                cellPane.getChildren().add(bg);

                // Draw walls using listeners to dynamically redraw on resize
                cellPane.widthProperty().addListener((obs, oldVal, newVal) ->
                        drawWalls(cellPane, cell, newVal.doubleValue(), cellPane.getHeight()));
                cellPane.heightProperty().addListener((obs, oldVal, newVal) ->
                        drawWalls(cellPane, cell, cellPane.getWidth(), newVal.doubleValue()));

                add(cellPane, c, r);
            }
        }
    }

    private void drawWalls(Pane pane, Cell cell, double w, double h) {
        pane.getChildren().removeIf(n -> n instanceof Line);

        double thickness = 1.5;

        if (cell.hasWall(Direction.UP)) {
            Line top = new Line(0, 0, w, 0);
            top.setStrokeWidth(thickness);
            top.setStroke(Color.BLACK);
            pane.getChildren().add(top);
        }
        if (cell.hasWall(Direction.LEFT)) {
            Line left = new Line(0, 0, 0, h);
            left.setStrokeWidth(thickness);
            left.setStroke(Color.BLACK);
            pane.getChildren().add(left);
        }
        if (cell.hasWall(Direction.RIGHT)) {
            Line right = new Line(w, 0, w, h);
            right.setStrokeWidth(thickness);
            right.setStroke(Color.BLACK);
            pane.getChildren().add(right);
        }
        if (cell.hasWall(Direction.DOWN)) {
            Line bottom = new Line(0, h, w, h);
            bottom.setStrokeWidth(thickness);
            bottom.setStroke(Color.BLACK);
            pane.getChildren().add(bottom);
        }
    }

    public void clearPath() {
        int cols = maze.getCols();
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < cols; c++) {
                Pane cellPane = (Pane) getChildren().get(r * cols + c);
                Rectangle bg = (Rectangle) cellPane.getChildren().get(0);

                Cell cell = maze.getCell(r, c);
                if (cell == maze.getStartCell()) {
                    bg.setFill(Color.LIMEGREEN);
                } else if (cell == maze.getEndCell()) {
                    bg.setFill(Color.RED);
                } else {
                    bg.setFill(Color.WHITE);
                }

                cellPane.getChildren().removeIf(n -> n instanceof Circle);
            }
        }
    }

    //Overload just in case
    public void animatePath(List<Cell> path, Runnable onFinished) {
        animatePath(path, 100.0, onFinished); // Default speed used
    }

    // New method with configurable speed
    public void animatePath(List<Cell> path, double speedMillis, Runnable onFinished) {
        Timeline timeline = new Timeline();
        int cols = maze.getCols();

        for (int i = 0; i < path.size(); i++) {
            final int index = i;
            KeyFrame frame = new KeyFrame(Duration.millis(i * speedMillis), e -> {
                if (index > 0) {
                    Cell prev = path.get(index - 1);
                    if (!prev.equals(maze.getStartCell()) && !prev.equals(maze.getEndCell())) {
                        Pane prevPane = (Pane) getChildren().get(prev.getRow() * cols + prev.getCol());
                        Rectangle prevBg = (Rectangle) prevPane.getChildren().get(0);
                        prevBg.setFill(Color.GRAY); // breadcrumb trail
                    }
                }

                Cell curr = path.get(index);
                if (!curr.equals(maze.getStartCell()) && !curr.equals(maze.getEndCell())) {
                    Pane currPane = (Pane) getChildren().get(curr.getRow() * cols + curr.getCol());
                    Rectangle currBg = (Rectangle) currPane.getChildren().get(0);
                    currBg.setFill(Color.YELLOW); // current step
                }
            });

            timeline.getKeyFrames().add(frame);
        }

        timeline.setOnFinished(e -> {
            if (onFinished != null) {
                onFinished.run();
            }
        });

        timeline.play();
    }


    public void animateVisitedOrder(List<Cell> visitedOrder, List<Cell> finalPath, double speedMillis, Runnable onFinished) {
        Timeline timeline = new Timeline();
        int cols = maze.getCols();

        // Animate the visited order
        for (int i = 0; i < visitedOrder.size(); i++) {
            final int index = i;
            KeyFrame frame = new KeyFrame(Duration.millis(i * speedMillis), e -> {
                if (index > 0) {
                    Cell prev = visitedOrder.get(index - 1);
                    if (!prev.equals(maze.getStartCell()) && !prev.equals(maze.getEndCell())) {
                        Pane prevPane = (Pane) getChildren().get(prev.getRow() * cols + prev.getCol());
                        Rectangle prevBg = (Rectangle) prevPane.getChildren().get(0);
                        prevBg.setFill(Color.GRAY); // Visited
                    }
                }

                Cell curr = visitedOrder.get(index);
                if (!curr.equals(maze.getStartCell()) && !curr.equals(maze.getEndCell())) {
                    Pane currPane = (Pane) getChildren().get(curr.getRow() * cols + curr.getCol());
                    Rectangle currBg = (Rectangle) currPane.getChildren().get(0);
                    currBg.setFill(Color.YELLOW); // Current
                }
            });
            timeline.getKeyFrames().add(frame);
        }

        //CleanUp Frame
        if (!visitedOrder.isEmpty()) {
            Cell lastVisited = visitedOrder.get(visitedOrder.size() - 1);
            if (!lastVisited.equals(maze.getStartCell()) && !lastVisited.equals(maze.getEndCell()) && (finalPath == null || !finalPath.contains(lastVisited))) {
                KeyFrame cleanup = new KeyFrame(Duration.millis(visitedOrder.size() * speedMillis), e -> {
                    Pane pane = (Pane) getChildren().get(lastVisited.getRow() * cols + lastVisited.getCol());
                    Rectangle bg = (Rectangle) pane.getChildren().get(0);
                    bg.setFill(Color.GRAY); // Reset artifact
                });
                timeline.getKeyFrames().add(cleanup);
            }
        }

        // Animate the final path
        if (finalPath != null) {
            int offset = visitedOrder.size() + 1;
            for (int i = 0; i < finalPath.size(); i++) {
                final int index = i;
                KeyFrame frame = new KeyFrame(Duration.millis((offset + i) * speedMillis), e -> {
                    Cell cell = finalPath.get(index);
                    if (!cell.equals(maze.getStartCell()) && !cell.equals(maze.getEndCell())) {
                        Pane pane = (Pane) getChildren().get(cell.getRow() * cols + cell.getCol());
                        Rectangle bg = (Rectangle) pane.getChildren().get(0);
                        bg.setFill(Color.YELLOW); // Final path
                    }
                });
                timeline.getKeyFrames().add(frame);
            }
        }

        timeline.setOnFinished(e -> {
            if (onFinished != null) onFinished.run();
        });

        timeline.play();
    }



}
