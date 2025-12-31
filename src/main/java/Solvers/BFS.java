package Solvers;

import MazeGen.Cell;
import MazeGen.Direction;
import MazeGen.Maze;

import java.time.Duration;
import java.util.*;

/*
 * BFS Solver
 *
 * Implements Breadth-First Search over the maze graph to find a path from start to end.
 * Because BFS explores in layers on an unweighted grid, it guarantees the shortest path
 * (fewest steps) when a solution exists.
 *
 * Records visitedOrder to support UI animation/visualization and uses parentMap to
 * reconstruct the final path once the goal is reached.
 */

public class BFS {

    private final Maze maze;
    private final boolean[][] visited;
    private final Map<Cell, Cell> parentMap;
    private List<Cell> path;
    private Duration timeToSolve;
    private final List<Cell> visitedOrder = new ArrayList<>(); // For animation

    public BFS(Maze maze) {
        this.maze = maze;
        this.visited = new boolean[maze.getRows()][maze.getCols()];
        this.parentMap = new HashMap<>();
        this.path = new ArrayList<>();
    }

    /**
     * Runs Breadth-First Search from the maze start cell to the maze end cell.
     *
     * @return The shortest path from start to end, or an empty list if no path is found.
     */
    public List<Cell> solve() {
        long startTime = System.nanoTime();
        path.clear();
        visitedOrder.clear();
        Queue<Cell> queue = new LinkedList<>();

        Cell startCell = maze.getStartCell();
        Cell endCell = maze.getEndCell();

        visited[startCell.getRow()][startCell.getCol()] = true;
        visitedOrder.add(startCell);
        queue.add(startCell);
        parentMap.put(startCell, null);

        while (!queue.isEmpty()) {
            Cell current = queue.poll();

            if (current.equals(endCell)) {
                // Goal reached: reconstruct shortest path using parent pointers
                reconstructPath(endCell);
                long endTime = System.nanoTime();
                timeToSolve = Duration.ofNanos(endTime - startTime);
                return path;
            }

            for (Direction direction : Direction.values()) {
                if (!current.hasWall(direction)) {
                    int newRow = current.getRow();
                    int newCol = current.getCol();
                    switch (direction) {
                        case UP -> newRow--;
                        case DOWN -> newRow++;
                        case LEFT -> newCol--;
                        case RIGHT -> newCol++;
                    }

                    if (maze.isInMaze(newRow, newCol)) {
                        Cell neighbor = maze.getCell(newRow, newCol);
                        if (!visited[neighbor.getRow()][neighbor.getCol()]) {
                            visited[neighbor.getRow()][neighbor.getCol()] = true;
                            visitedOrder.add(neighbor);
                            queue.add(neighbor);
                            parentMap.put(neighbor, current);
                        }
                    }
                }
            }
        }

        long endTime = System.nanoTime(); // Corrected to nanoTime
        timeToSolve = Duration.ofNanos(endTime - startTime);
        return Collections.emptyList();
    }

    private void reconstructPath(Cell end) {
        LinkedList<Cell> fullPath = new LinkedList<>();
        for (Cell at = end; at != null; at = parentMap.get(at)) {
            fullPath.addFirst(at);
        }
        path = fullPath;
    }

    public List<Cell> getVisitedOrder() {
        return visitedOrder;
    }

    public Duration getTimeToSolve() {
        return timeToSolve;
    }
}
