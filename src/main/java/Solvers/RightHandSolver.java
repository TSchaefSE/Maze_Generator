package Solvers;

import MazeGen.Cell;
import MazeGen.Direction;
import MazeGen.Maze;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/*
 * Right-Hand Rule Solver
 *
 * Implements the wall-following (right-hand) maze solving strategy.
 *
 * Characteristics:
 * - Maintains contact with the right wall while traversing the maze.
 * - Does not guarantee the shortest path.
 * - Guaranteed to find an exit in simply connected (perfect) mazes.
 *
 * The solver tracks the current facing direction and prioritizes movement
 * in the order: right, forward, left, then back.
 */
public class RightHandSolver {

    private Maze maze;
    private final List<Cell> path = new ArrayList<>();
    private Duration timeToSolve;

    public RightHandSolver(Maze maze) {
        this.maze = maze;
    }

    /*
     * Determines the initial facing direction based on the start cell's
     * position along the maze boundary and its open edge.
     *
     * This ensures the solver begins with a consistent orientation so that
     * the right-hand rule can be applied correctly.
     */
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

    /**
     * Solves the maze using the Right-Hand Rule starting from the maze entrance.
     *
     * @return A valid path from start to end following the wall on the right.
     */
    public List<Cell> solve() {
        long startTime = System.nanoTime();

        Cell current = maze.getStartCell();
        Direction facing = findStartingDirection(current);
        path.add(current);
        facingHistory.add(facing);

        while (current != maze.getEndCell()) {
            // Movement priority for right-hand rule: right → forward → left → back
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
