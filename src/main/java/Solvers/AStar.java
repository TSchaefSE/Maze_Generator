package Solvers;

import MazeGen.Cell;
import MazeGen.Maze;

import java.time.Duration;
import java.util.*;

public class AStar {

    private final Maze maze;
    private final boolean[][] visited;
    private Duration timeToSolve;
    private final Map<Cell, Integer> gScore;
    private final Map<Cell, Cell> cameFrom;
    private List<Cell> path;
    private List<Cell> visitedOrder = new ArrayList<>(); //For Animation

    public AStar(Maze maze) {
        this.maze = maze;
        this.visited = new boolean[maze.getRows()][maze.getCols()];
        this.gScore = new HashMap<>();
        this.cameFrom = new HashMap<>();
        this.path = new ArrayList<>();
    }

    public List<Cell> solve(){
        long startTime = System.nanoTime();
        path.clear();
        Cell start = maze.getStartCell();
        Cell end = maze.getEndCell();

        PriorityQueue<CellNode> pq = new PriorityQueue<>();
        gScore.put(start, 0);
        int estimated = manhattan(start, end);
        pq.add(new CellNode(start, 0, estimated, null));

        while(!pq.isEmpty()){
            CellNode currentNode = pq.poll();
            Cell currentCell = currentNode.cell;

            if(visited[currentCell.getRow()][currentCell.getCol()]) continue;
            visited[currentCell.getRow()][currentCell.getCol()] = true;
            visitedOrder.add(currentCell);

            if(currentCell.equals(end)){
                reconstructPath(currentCell);
                long endTime = System.nanoTime();
                timeToSolve = Duration.ofNanos(endTime - startTime);
                return path;
            }

            for (MazeGen.Direction dir : MazeGen.Direction.values()) {
                if (!currentCell.hasWall(dir)) {
                    int newRow = currentCell.getRow(), newCol = currentCell.getCol();
                    switch (dir) {
                        case UP -> newRow--;
                        case DOWN -> newRow++;
                        case LEFT -> newCol--;
                        case RIGHT -> newCol++;
                    }

                    if (maze.isInMaze(newRow, newCol)) {
                        Cell neighbor = maze.getCell(newRow, newCol);
                        int tentativeG = gScore.get(currentCell) + 1;

                        if (!gScore.containsKey(neighbor) || tentativeG < gScore.get(neighbor)) {
                            gScore.put(neighbor, tentativeG);
                            int h = manhattan(neighbor, end);
                            int f = tentativeG + h;
                            cameFrom.put(neighbor, currentCell);
                            pq.add(new CellNode(neighbor, tentativeG, f, currentCell));
                        }
                    }
                }
            }
        }
        long endTime = System.currentTimeMillis();
        timeToSolve = Duration.ofNanos(endTime - startTime);
        return path; // Empty path if not found
    }

    private int manhattan(Cell a, Cell b){
        return Math.abs(a.getRow() - b.getRow()) + Math.abs(a.getCol() - b.getCol());
    }

    private void reconstructPath(Cell end) {
        LinkedList<Cell> fullPath = new LinkedList<>();
        for (Cell at = end; at != null; at = cameFrom.get(at)) {
            fullPath.addFirst(at);
        }
        path = fullPath;
    }

    private class CellNode implements Comparable<CellNode> {
        Cell cell;
        int cost;
        int estimatedCost;
        Cell parent;

        CellNode(Cell cell, int cost, int estimatedCost, Cell parent) {
            this.cell = cell;
            this.cost = cost;
            this.estimatedCost = estimatedCost;
            this.parent = parent;
        }

        @Override
        public int compareTo(CellNode other) {
            return Integer.compare(estimatedCost, other.estimatedCost);
        }
    }

    public List<Cell> getVisitedOrder() {
        return visitedOrder;
    }

    public Duration getTimeToSolve() {
        return timeToSolve;
    }
}



