package com.TS.maze.Helpers;


import MazeGen.Cell;
import MazeGen.Direction;
import MazeGen.Maze;

import java.util.LinkedList;
import java.util.Queue;

public class PathVerifier {

    public static boolean isReachable(Cell start, Cell end, Maze maze){
        if(start == null || end == null || maze == null){ return false; }

        int rows = maze.getRows();
        int cols = maze.getCols();
        boolean[][] visited = new boolean[rows][cols];

        Queue<Cell> queue = new LinkedList<Cell>();
        queue.add(start);
        visited[start.getRow()][start.getCol()] = true;

        while(!queue.isEmpty()){
            Cell current = queue.poll();
            if(current.equals(end)){
                return true;
            }

            for (Direction direction : Direction.values()) {
                if(current.hasWall(direction)) continue;

                int newRow = current.getRow();
                int newCol = current.getCol();
                switch(direction){
                    case UP -> newRow--;
                    case DOWN -> newRow++;
                    case LEFT -> newCol--;
                    case RIGHT -> newCol++;
                }

                if (!maze.isInMaze(newRow, newCol)) continue;
                if(visited[newRow][newCol]) continue;

                visited[newRow][newCol] = true;
                queue.add(maze.getCell(newRow, newCol));

            }
        }

        return false;

    }

    public static int countReachableCells(Cell start, Maze maze) {
        if (start == null || maze == null) return 0;

        int rows = maze.getRows();
        int cols = maze.getCols();
        boolean[][] visited = new boolean[rows][cols];

        Queue<Cell> queue = new LinkedList<>();
        queue.add(start);
        visited[start.getRow()][start.getCol()] = true;

        int count = 1;

        while (!queue.isEmpty()) {
            Cell current = queue.poll();

            for (Direction dir : Direction.values()) {
                if (current.hasWall(dir)) continue;

                int newRow = current.getRow();
                int newCol = current.getCol();
                switch (dir) {
                    case UP -> newRow--;
                    case DOWN -> newRow++;
                    case LEFT -> newCol--;
                    case RIGHT -> newCol++;
                }

                if (!maze.isInMaze(newRow, newCol)) continue;
                if (visited[newRow][newCol]) continue;

                visited[newRow][newCol] = true;
                count++;
                queue.add(maze.getCell(newRow, newCol));
            }
        }

        return count;
    }


}
