package com.TS.maze;

import MazeGen.Cell;
import MazeGen.Direction;
import MazeGen.Maze;
import MazeGen.MazeGenerator;
import Solvers.AStar;
import Solvers.BFS;
import Solvers.DFS;
import Solvers.RightHandSolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        final int rows = 15;
        final int cols = 15;
        Scanner sc = new Scanner(System.in);
        Random rand = new Random();

        while (true) {
            long seed = rand.nextLong();
            Maze maze = new Maze(seed, rows, cols);
            MazeGenerator mg = new MazeGenerator(maze);
            mg.generateMaze();

            System.out.print("Choose solver:\n(1) Right-hand\n(2) DFS\n(3) BFS\n(4) A*\nSelection: ");
            String solverChoice = sc.nextLine().trim().toUpperCase();

            List<Cell> path;
            List<Direction> directions;

            switch (solverChoice) {
                case "1":
                    RightHandSolver rhs = new RightHandSolver(maze);
                    path = rhs.solve();
                    directions = rhs.getFacingHistory();
                    System.out.println(rhs.getTimeToSolve());
                    break;
                case "2":
                    DFS dfs = new DFS(maze);
                    path = dfs.solve();
                    directions = computeDirectionsFromPath(path);
                    System.out.println(dfs.getTimeToSolve());
                    break;
                case "3":
                    BFS bfs = new BFS(maze);
                    path = bfs.solve();
                    directions = computeDirectionsFromPath(path);
                    break;
                case "4":
                    AStar aStar = new AStar(maze);
                    path = aStar.solve();
                    directions = computeDirectionsFromPath(path);
                    System.out.println(aStar.getTimeToSolve());
                    break;
                default:
                    RightHandSolver defaultSolve = new RightHandSolver(maze);
                    path = defaultSolve.solve();
                    directions = defaultSolve.getFacingHistory();
                    System.out.println(defaultSolve.getTimeToSolve());
                    break;
            }

            animateSolver(maze, path, directions);

            System.out.print("Regenerate Maze? (Y/N): ");
            String input = sc.nextLine().trim().toUpperCase();
            if (!input.equals("Y")) {
                break;
            }
        }

        sc.close();
        System.out.println("Goodbye!");
    }

    private static void animateSolver(Maze maze, List<Cell> path, List<Direction> directions) {
        for (int i = 0; i < path.size(); i++) {
            // Clear console before next frame
            System.out.print("\033[H\033[2J");
            System.out.flush();

            Cell cell = path.get(i);
            Direction direction = directions.get(i);
            maze.printCLIMazeWithSolver(cell.getRow(), cell.getCol(), direction);

            try {
                Thread.sleep(1000); // 1-second delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }


    private static List<Direction> computeDirectionsFromPath(List<Cell> path) {
        List<Direction> directions = new ArrayList<>();

        for (int i = 1; i < path.size(); i++) {
            Cell prev = path.get(i - 1);
            Cell curr = path.get(i);
            int dRow = curr.getRow() - prev.getRow();
            int dCol = curr.getCol() - prev.getCol();

            Direction dir = switch ("" + dRow + "," + dCol) {
                case "-1,0" -> Direction.UP;
                case "1,0" -> Direction.DOWN;
                case "0,-1" -> Direction.LEFT;
                case "0,1" -> Direction.RIGHT;
                default -> Direction.UP; // fallback
            };

            directions.add(dir);
        }

        // Repeat last direction for alignment with path length
        if (!directions.isEmpty()) {
            directions.add(directions.get(directions.size() - 1));
        } else {
            directions.add(Direction.UP); // single-cell path
        }

        return directions;
    }

    private static void printMazeWithSolver(Maze maze, int row, int col, Direction dir) {
        String arrow = switch (dir) {
            case UP -> "↑";
            case DOWN -> "↓";
            case LEFT -> "←";
            case RIGHT -> "→";
        };

        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                System.out.print(maze.getCell(r, c).hasWall(Direction.UP) ? "+---" : "+   ");
            }
            System.out.println("+");

            for (int c = 0; c < maze.getCols(); c++) {
                boolean hasLeftWall = maze.getCell(r, c).hasWall(Direction.LEFT);
                String content = (r == row && c == col) ? " " + arrow + " " : "   ";
                System.out.print(hasLeftWall ? "|" : " ");
                System.out.print(content);
            }
            System.out.println("|");
        }

        for (int c = 0; c < maze.getCols(); c++) {
            System.out.print("+---");
        }
        System.out.println("+");
    }
}

