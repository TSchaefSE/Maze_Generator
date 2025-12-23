package MazeGen;

import java.util.*;

public class MazeGenerator {

    private final Maze maze;
    private final boolean[][] visited;

    public MazeGenerator(Maze maze) {
        this.maze = maze;
        this.visited = new boolean[maze.getRows()][maze.getCols()];
    }

    public void generateMaze() {
        int startRow = maze.getRand().nextInt(maze.getRows());
        int startCol = maze.getRand().nextInt(maze.getCols());

        dfs(startRow, startCol);
        placeStartAndEndAtDeadEnd();
    }

    private void dfs(int row, int col) {
        visited[row][col] = true;
        Cell currentCell = maze.getCell(row, col);

        List<Direction> directions = new ArrayList<>(List.of(Direction.values()));
        Collections.shuffle(directions, maze.getRand());

        for (Direction direction : directions) {
            int newRow = row;
            int newCol = col;

            switch (direction) {
                case UP -> newRow--;
                case DOWN -> newRow++;
                case LEFT -> newCol--;
                case RIGHT -> newCol++;
            }

            if (maze.isInMaze(newRow, newCol) && !visited[newRow][newCol]) {
                Cell nextCell = maze.getCell(newRow, newCol);

                currentCell.removeWall(direction);
                nextCell.removeWall(getOpposite(direction));

                dfs(newRow, newCol);
            }
        }
    }

    public void placeStartAndEndAtDeadEnd() {
        int[] start = getRandomEdgeCell();
        int startRow = start[0];
        int startCol = start[1];

        maze.setStartCell(startRow, startCol);

        int[][] distances = new int[maze.getRows()][maze.getCols()];
        boolean[][] visited = new boolean[maze.getRows()][maze.getCols()];
        Queue<Cell> queue = new LinkedList<>();
        List<Cell> eligibleDeadEnds = new ArrayList<>();

        queue.add(maze.getStartCell());
        visited[startRow][startCol] = true;
        distances[startRow][startCol] = 0;

        int maxDistance = 0;

        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            int row = current.getRow();
            int col = current.getCol();
            int openNeighbors = 0;

            for (Direction dir : Direction.values()) {
                int newRow = row, newCol = col;
                switch (dir) {
                    case UP -> newRow--;
                    case DOWN -> newRow++;
                    case LEFT -> newCol--;
                    case RIGHT -> newCol++;
                }

                if (!maze.isInMaze(newRow, newCol)) continue;
                if (!current.hasWall(dir)) {
                    openNeighbors++;
                    if (!visited[newRow][newCol]) {
                        visited[newRow][newCol] = true;
                        distances[newRow][newCol] = distances[row][col] + 1;
                        queue.add(maze.getCell(newRow, newCol));
                        maxDistance = Math.max(maxDistance, distances[newRow][newCol]);
                    }
                }
            }

            // If it's a dead end and far enough from start
            int minDistance = Math.max(15, maxDistance / 2); // require at least 15 steps or half of max

            if (openNeighbors == 1 && distances[row][col] >= minDistance) {
                eligibleDeadEnds.add(current);
            }

        }

        if (!eligibleDeadEnds.isEmpty()) {
            Cell end = eligibleDeadEnds.get(maze.getRand().nextInt(eligibleDeadEnds.size()));
            maze.setEndCell(end.getRow(), end.getCol());
        } else {
            // Fallback: use the farthest reachable cell instead
            Cell farthest = maze.getCell(startRow, startCol);
            for (int r = 0; r < maze.getRows(); r++) {
                for (int c = 0; c < maze.getCols(); c++) {
                    if (distances[r][c] == maxDistance) {
                        farthest = maze.getCell(r, c);
                    }
                }
            }
            maze.setEndCell(farthest.getRow(), farthest.getCol());
        }

        //DEBUG
        //System.out.println("END placed at: (" + maze.getEndCell().getRow() + "," + maze.getEndCell().getCol() + ")");

        //if (maze.getEndCell() == null) {
        //    System.out.println("WARNING: End cell was never assigned.");
        //}

    }


    private Direction getOpposite(Direction direction) {
        return switch (direction){
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case LEFT -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
        };
    }

    private int[] getRandomEdgeCell() {
        int edge = maze.getRand().nextInt(4); // 0 = top, 1 = bottom, 2 = left, 3 = right
        int row = 0, col = 0;

        switch (edge) {
            case 0 -> { // top row
                row = 0;
                col = maze.getRand().nextInt(maze.getCols());
            }
            case 1 -> { // bottom row
                row = maze.getRows() - 1;
                col = maze.getRand().nextInt(maze.getCols());
            }
            case 2 -> { // left col
                row = maze.getRand().nextInt(maze.getRows());
                col = 0;
            }
            case 3 -> { // right col
                row = maze.getRand().nextInt(maze.getRows());
                col = maze.getCols() - 1;
            }
        }

        return new int[]{row, col};
    }

}
