package Solvers;

import MazeGen.Cell;
import MazeGen.Direction;
import MazeGen.Maze;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class DFS {

    private final Maze maze;
    private final boolean[][] visited;
    private List<Cell> path;
    private Duration timeToSolve;
    private final List<Cell> visitedOrder = new ArrayList<>(); // For animation

    public DFS(Maze maze) {
        this.maze = maze;
        this.visited = new boolean[maze.getRows()][maze.getCols()];
        this.path = new ArrayList<>();
    }

    public List<Cell> solve() {
        long startTime = System.nanoTime();
        path.clear();
        visitedOrder.clear();
        dfs(maze.getStartCell());
        long endTime = System.nanoTime();
        timeToSolve = Duration.ofNanos(endTime - startTime);
        return path;
    }

    private boolean dfs(Cell current) {
        int row = current.getRow();
        int col = current.getCol();
        if (!maze.isInMaze(row, col) || visited[row][col]) {
            return false;
        }

        visited[row][col] = true;
        visitedOrder.add(current);
        path.add(current);

        if (current == maze.getEndCell()) {
            return true;
        }

        for (Direction dir : Direction.values()) {
            if (!current.hasWall(dir)) {
                int newRow = row, newCol = col;
                switch (dir) {
                    case UP -> newRow--;
                    case DOWN -> newRow++;
                    case LEFT -> newCol--;
                    case RIGHT -> newCol++;
                }

                if (maze.isInMaze(newRow, newCol)) {
                    Cell next = maze.getCell(newRow, newCol);
                    if (dfs(next)) {
                        return true;
                    }
                }
            }
        }

        path.remove(path.size() - 1); // backtrack
        return false;
    }

    public List<Cell> getVisitedOrder() {
        return visitedOrder;
    }

    public Duration getTimeToSolve() {
        return timeToSolve;
    }
}
