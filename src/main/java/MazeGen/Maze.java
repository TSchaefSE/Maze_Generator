package MazeGen;

import java.util.Random;

public class Maze {

    private long seed;
    private int rows;
    private int cols;
    private Cell[][] cells;
    private Random rand = new Random();

    private Cell startCell;
    private Cell endCell;

    public Maze(long seed, int rows, int cols) {
        this.seed = seed;
        this.rows = rows;
        this.cols = cols;
        this.rand.setSeed(seed);
        this.cells = new Cell[rows][cols];

        initializeCells();
    }

    private void initializeCells() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = new Cell(i,j);
            }
        }
    }

    //Getters
    public long getSeed() {
        return seed;
    }
    public int getRows() {
        return rows;
    }
    public int getCols() {
        return cols;
    }
    public Cell getStartCell() {
        return startCell;
    }
    public Cell getEndCell() {
        return endCell;
    }
    public Cell getCell(int row, int col) {
        return cells[row][col];
    }
    public Random getRand() {
        return rand;
    }

    private String getArrow(Direction direction) {
        return switch (direction) {
            case UP -> "↑";
            case DOWN -> "↓";
            case LEFT -> "←";
            case RIGHT -> "→";
        };
    }

    //Setters
    public void setCell(int row, int col, Cell cell) {
        cells[row][col] = cell;
    }
    public void setStartCell(int row, int col) {
        startCell = cells[row][col];
    }
    public void setEndCell(int row, int col) {
        endCell = cells[row][col];
    }

    public boolean isInMaze(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public void printCLIMazeWithSolver(int solverRow, int solverCol, Direction dir) {
        String arrow = switch (dir) {
            case UP -> "↑";
            case DOWN -> "↓";
            case LEFT -> "←";
            case RIGHT -> "→";
        };

        for (int r = 0; r < rows; r++) {
            // Top walls
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                System.out.print("+");
                if (!cell.hasWall(Direction.UP) || isStartEdge(this, cell, Direction.UP)) {
                    System.out.print("   ");
                } else {
                    System.out.print("---");
                }
            }
            System.out.println("+");

            // Content row
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];

                // Left wall
                if (cell.hasWall(Direction.LEFT) && !isStartEdge(this, cell, Direction.LEFT)) {
                    System.out.print("|");
                } else {
                    System.out.print(" ");
                }

                // Content logic
                if (r == solverRow && c == solverCol) {
                    System.out.print(" " + arrow + " ");
                } else if (cell == startCell) {
                    System.out.print("\u001B[32m🟢 \u001B[0m");
                } else if (cell == endCell) {
                    System.out.print(" \u001B[31m🚩\u001B[0m");
                } else {
                    System.out.print("   ");
                }
            }

            // Rightmost wall
            Cell lastCell = cells[r][cols - 1];
            if (lastCell.hasWall(Direction.RIGHT) && !isStartEdge(this, lastCell, Direction.RIGHT)) {
                System.out.println("|");
            } else {
                System.out.println(" ");
            }
        }

        // Bottom walls
        for (int c = 0; c < cols; c++) {
            Cell cell = cells[rows - 1][c];
            System.out.print("+");
            if (!cell.hasWall(Direction.DOWN) || isStartEdge(this, cell, Direction.DOWN)) {
                System.out.print("   ");
            } else {
                System.out.print("---");
            }
        }
        System.out.println("+");
    }


    private boolean isStartEdge(Maze maze, Cell cell, Direction dir) {
        return cell == maze.getStartCell() &&
                switch (dir) {
                    case UP -> cell.getRow() == 0;
                    case DOWN -> cell.getRow() == maze.getRows() - 1;
                    case LEFT -> cell.getCol() == 0;
                    case RIGHT -> cell.getCol() == maze.getCols() - 1;
                };
    }

}
