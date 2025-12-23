package Solvers;

import MazeGen.Cell;
import MazeGen.Direction;
import MazeGen.Maze;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class RightHandSolver {

    private Maze maze;
    private final List<Cell> path = new ArrayList<>();
    private Duration timeToSolve;

    public RightHandSolver(Maze maze) {
        this.maze = maze;
    }

    private Direction findStartingDirection(Cell startCell) {
        if(!startCell.hasWall(Direction.UP) && startCell.getRow() == 0){
            return Direction.DOWN;
        }
        if(!startCell.hasWall(Direction.DOWN) && startCell.getRow() == maze.getRows() - 1){
            return Direction.UP;
        }
        if(!startCell.hasWall(Direction.LEFT) && startCell.getCol() == 0){
            return Direction.RIGHT;
        }
        if(!startCell.hasWall(Direction.RIGHT) && startCell.getCol() == maze.getCols() - 1){
            return Direction.LEFT;
        }
        return Direction.RIGHT;
    }

    //Clockwise
    private Direction getRight(Direction direction) {
        return switch (direction){
            case UP -> Direction.RIGHT;
            case DOWN -> Direction.LEFT;
            case LEFT -> Direction.UP;
            case RIGHT -> Direction.DOWN;
        };
    }

    //Counter-Clockwise
    private Direction getLeft(Direction direction) {
        return switch(direction){
            case UP -> Direction.LEFT;
            case DOWN -> Direction.RIGHT;
            case LEFT -> Direction.DOWN;
            case RIGHT -> Direction.UP;
        };
    }

    //180 turn
    private Direction getBack(Direction direction) {
        return switch (direction){
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case LEFT -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
        };
    }

    private int getRowOffset(Direction dir) {
        return switch (dir) {
            case UP -> -1;
            case DOWN -> 1;
            default -> 0;
        };
    }

    private int getColOffset(Direction dir) {
        return switch (dir) {
            case LEFT -> -1;
            case RIGHT -> 1;
            default -> 0;
        };
    }


    private final List<Direction> facingHistory = new ArrayList<>();

    public List<Cell> solve() {
        long startTime = System.nanoTime();

        Cell current = maze.getStartCell();
        Direction facing = findStartingDirection(current);
        path.add(current);
        facingHistory.add(facing);

        while (current != maze.getEndCell()) {
            Direction[] priorities = { getRight(facing), facing, getLeft(facing), getBack(facing) };

            for (Direction dir : priorities) {
                if (!current.hasWall(dir)) {
                    int newRow = current.getRow() + getRowOffset(dir);
                    int newCol = current.getCol() + getColOffset(dir);

                    if (maze.isInMaze(newRow, newCol)) {
                        Cell next = maze.getCell(newRow, newCol);
                        current = next;
                        path.add(current);
                        facing = dir;
                        facingHistory.add(facing);
                        break;
                    }
                }
            }
        }
        long endTime = System.nanoTime();
        timeToSolve = Duration.ofNanos(endTime - startTime);
        return path;
    }

    public List<Direction> getFacingHistory() {
        return facingHistory;
    }

    public Duration getTimeToSolve() {
        return timeToSolve;
    }
}
