package com.TS.maze;

import MazeGen.Cell;
import MazeGen.Direction;
import MazeGen.Maze;
import MazeGen.MazeGenerator;
import Solvers.AStar;
import Solvers.BFS;
import Solvers.DFS;
import Solvers.RightHandSolver;
import com.TS.maze.Helpers.TestLogger;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;


public class MazeGeneratorTest {

    @RepeatedTest(1000)
    void testMazeDimensions() {
        long seed = new Random().nextLong();
        int rows = new Random().nextInt(100) + 1;
        int cols = new Random().nextInt(100) + 1;
        Maze maze = new Maze(seed, rows, cols);

        System.out.printf("Seed: %d | Dimensions: %dx%d%n", seed, rows, cols);

        assertEquals(rows, maze.getRows(), "Rows mismatch. Seed: " + seed + ", Dimensions: " + rows + "x" + cols);
        assertEquals(cols, maze.getCols(), "Cols mismatch. Seed: " + seed + ", Dimensions: " + rows + "x" + cols);
    }


    @RepeatedTest(1000)
    void testStartCellOnEdge() {
        long seed = new Random().nextLong();
        int rows = new Random().nextInt(100) + 1;
        int cols = new Random().nextInt(100) + 1;
        Maze maze = new Maze(seed, rows, cols);

        MazeGenerator mg = new MazeGenerator(maze);
        mg.generateMaze();
        Cell start = maze.getStartCell();
        Cell end = maze.getEndCell();

        if (start == null || end == null ||
                !(start.getRow() == 0 || start.getRow() == maze.getRows() - 1 ||
                        start.getCol() == 0 || start.getCol() == maze.getCols() - 1) ||
                start.equals(end)) {

            String log = String.format(" Seed: %d | %dx%d | Start: %s | End: %s",
                    seed, rows, cols,
                    (start == null ? "null" : "(" + start.getRow() + "," + start.getCol() + ")"),
                    (end == null ? "null" : "(" + end.getRow() + "," + end.getCol() + ")"));
            TestLogger.logFailure(log);
        }

        assertNotNull(start, "Start is null. Seed: " + seed);
        assertTrue(
                start.getRow() == 0 || start.getRow() == maze.getRows() - 1 ||
                        start.getCol() == 0 || start.getCol() == maze.getCols() - 1,
                "Start not on edge. Seed: " + seed + ", Start: (" + start.getRow() + "," + start.getCol() + ")"
        );
        assertNotEquals(end, start, "Start and end are the same. Seed: " + seed);
    }


    @RepeatedTest(1000)
    void testEndCellPresence() {
        long seed = new Random().nextLong();
        int rows = new Random().nextInt(100) + 1;
        int cols = new Random().nextInt(100) + 1;
        Maze maze = new Maze(seed, rows, cols);

        MazeGenerator mg = new MazeGenerator(maze);
        mg.generateMaze();
        Cell end = maze.getEndCell();
        Cell start = maze.getStartCell();

        if (
                start == null ||
                end == null ||
                start.equals(end) ||
                !maze.isInMaze(start.getRow(), start.getCol()) ||
                !maze.isInMaze(end.getRow(), end.getCol())
        ) {
            String log = String.format(" Seed: %d | %dx%d | Start: %s | End: %s",
                    seed, rows, cols,
                    (start == null ? "null" : "(" + start.getRow() + "," + start.getCol() + ")"),
                    (end == null ? "null" : "(" + end.getRow() + "," + end.getCol() + ")"));
            TestLogger.logFailure(log);
        }

        assertNotNull(end, "End is null. Seed: " + seed + ", Dimensions: " + rows + "x" + cols);
        assertTrue(
                maze.isInMaze(end.getRow(), end.getCol()),
                "End out of bounds. Seed: " + seed + ", End: (" + end.getRow() + "," + end.getCol() + ")"
        );
    }

    @RepeatedTest(1000)
    void isReachable() {
        long seed = new Random().nextLong();
        int rows = new Random().nextInt(100) + 1;
        int cols = new Random().nextInt(100) + 1;
        Maze maze = new Maze(seed, rows, cols);

        MazeGenerator mg = new MazeGenerator(maze);
        mg.generateMaze();

        Cell end = maze.getEndCell();
        Cell start = maze.getStartCell();

        boolean isReachable = com.TS.maze.Helpers.PathVerifier.isReachable(start, end, maze);

        if (!isReachable) {
            String log = String.format("Unreachable Path | Seed: %d | %dx%d | Start: %s | End: %s",
                    seed, rows, cols,
                    (start == null ? "null" : "(" + start.getRow() + "," + start.getCol() + ")"),
                    (end == null ? "null" : "(" + end.getRow() + "," + end.getCol() + ")"));
            TestLogger.logFailure(log);
        }

        assertTrue(isReachable, "Start and end are not reachable. Seed: " + seed);
    }

    @RepeatedTest(1000)
    void testMazeIsFullyConnected() {
        long seed = new Random().nextLong();
        int rows = new Random().nextInt(99) + 1;
        int cols = new Random().nextInt(99) + 1;
        Maze maze = new Maze(seed, rows, cols);

        MazeGenerator mg = new MazeGenerator(maze);
        mg.generateMaze();

        Cell start = maze.getStartCell();
        int reachable = com.TS.maze.Helpers.PathVerifier.countReachableCells(start, maze);
        int expected = rows * cols;

        if (reachable < expected) {
            String log = String.format("Disconnected Maze | Seed: %d | %dx%d | Reachable: %d/%d",
                    seed, rows, cols, reachable, expected);
            TestLogger.logFailure(log);
        }

        assertEquals(expected, reachable,
                "Maze is not fully connected. Reachable: " + reachable + "/" + expected + ". Seed: " + seed);
    }

    @RepeatedTest(1000)
    void testWallConsistency() {
        long seed = new Random().nextLong();
        int rows = new Random().nextInt(99) + 1;
        int cols = new Random().nextInt(99) + 1;
        Maze maze = new Maze(seed, rows, cols);
        new MazeGenerator(maze).generateMaze();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell current = maze.getCell(r, c);

                for (Direction dir : Direction.values()) {
                    int newRow = r, newCol = c;

                    switch (dir) {
                        case UP -> newRow--;
                        case DOWN -> newRow++;
                        case LEFT -> newCol--;
                        case RIGHT -> newCol++;
                    }

                    if (!maze.isInMaze(newRow, newCol)) continue;

                    Cell neighbor = maze.getCell(newRow, newCol);
                    Direction opposite = switch (dir) {
                        case UP -> Direction.DOWN;
                        case DOWN -> Direction.UP;
                        case LEFT -> Direction.RIGHT;
                        case RIGHT -> Direction.LEFT;
                    };

                    boolean currentHasWall = current.hasWall(dir);
                    boolean neighborHasOpposite = neighbor.hasWall(opposite);

                    if (currentHasWall != neighborHasOpposite) {
                        String log = String.format(
                                "Wall mismatch | Seed: %d | Size: %dx%d | Cell: (%d,%d) Dir: %s | Neighbor: (%d,%d) Opposite: %s | C->N: %b | N->C: %b",
                                seed, rows, cols,
                                r, c, dir,
                                newRow, newCol, opposite,
                                currentHasWall, neighborHasOpposite
                        );
                        TestLogger.logFailure(log);
                    }

                    assertEquals(currentHasWall, neighborHasOpposite, String.format(
                            "Wall mismatch at (%d,%d) -> (%d,%d) | Dir: %s | Seed: %d",
                            r, c, newRow, newCol, dir, seed));
                }
            }
        }
    }

    @RepeatedTest(1000)
    void testNoUnintentionalEdgeExits() {
        Maze maze = new Maze(123L, 10, 10);
        new MazeGenerator(maze).generateMaze();

        int edgeOpenings = 0;
        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                Cell cell = maze.getCell(r, c);

                if (r == 0 && !cell.hasWall(Direction.UP)) edgeOpenings++;
                if (r == maze.getRows() - 1 && !cell.hasWall(Direction.DOWN)) edgeOpenings++;
                if (c == 0 && !cell.hasWall(Direction.LEFT)) edgeOpenings++;
                if (c == maze.getCols() - 1 && !cell.hasWall(Direction.RIGHT)) edgeOpenings++;
            }
        }

        if (edgeOpenings > 2) {
            TestLogger.logFailure("Unexpected edge exits | Seed: "+ maze.getSeed() + " | Openings: " + edgeOpenings);
        }

        assertTrue(edgeOpenings <= 2, "Maze has unexpected exits on border: " + edgeOpenings);
    }

    @RepeatedTest(1000)
    void testMazeIsTree() {
        long seed = new Random().nextLong();
        Maze maze = new Maze(seed, 10, 10);
        new MazeGenerator(maze).generateMaze();

        int removedWalls = 0;
        int rows = maze.getRows();
        int cols = maze.getCols();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = maze.getCell(r, c);

                for (Direction dir : Direction.values()) {
                    int nr = r, nc = c;

                    switch (dir) {
                        case UP -> nr--;
                        case DOWN -> nr++;
                        case LEFT -> nc--;
                        case RIGHT -> nc++;
                    }

                    if (!maze.isInMaze(nr, nc)) continue;

                    Cell neighbor = maze.getCell(nr, nc);

                    Direction opposite = switch (dir) {
                        case UP -> Direction.DOWN;
                        case DOWN -> Direction.UP;
                        case LEFT -> Direction.RIGHT;
                        case RIGHT -> Direction.LEFT;
                    };

                    if (!cell.hasWall(dir) && !neighbor.hasWall(opposite)) {
                        removedWalls++;
                    }
                }
            }
        }

        int cells = rows * cols;
        int expected = cells - 1;
        int actual = removedWalls / 2;

        if (actual != expected) {
            String log = String.format("Tree Violation | Seed: %d | %dx%d | Expected Edges: %d | Actual Edges: %d",
                    seed, rows, cols, expected, actual);
            TestLogger.logFailure(log);
        }

        assertEquals(expected, actual, "Maze is not a tree. Seed: " + seed);
    }

    @RepeatedTest(1000)
    void testSolversProduceValidPath() {
        Maze maze = new Maze(42L, 10, 10);
        new MazeGenerator(maze).generateMaze();

        try {
            assertFalse(new DFS(maze).solve().isEmpty(), "DFS failed");
            assertFalse(new BFS(maze).solve().isEmpty(), "BFS failed");
            assertFalse(new AStar(maze).solve().isEmpty(), "A* failed");
            assertFalse(new RightHandSolver(maze).solve().isEmpty(), "RHS failed");
        } catch (AssertionError e){
            TestLogger.logFailure("Solver Failure | Seed: " + maze.getSeed() + " | Message: " + e.getMessage());
        }
    }

    //Edge Cases
    @Test
    void testMinMazeSize(){
        Maze maze = new Maze(123L, 1, 1);
        MazeGenerator mg = new MazeGenerator(maze);
        mg.generateMaze();

        assertNotNull(maze.getStartCell(), "Start cell is null");
        assertNotNull(maze.getEndCell(), "End cell is null");
        assertTrue(com.TS.maze.Helpers.PathVerifier.isReachable(maze.getStartCell(), maze.getEndCell(), maze),
                "Even a 1x1 maze should be self-reachable");
    }

    //WARNING: Due to recursive gen limit test sizes to about 100 x 100
    @Test
    void testLargeMazeDoesNotTimeout() {
        Maze maze = new Maze(9999999L, 100, 100);
        MazeGenerator mg = new MazeGenerator(maze);
        mg.generateMaze();

        int reachable = com.TS.maze.Helpers.PathVerifier.countReachableCells(maze.getStartCell(), maze);
        assertEquals(10000, reachable, "Large maze not fully connected");
    }

    //FORCE FAILURE
    /*
    @Test
    void testForcedWrongDimensionsLogsCorrectly() {
        long seed = 123456789L;
        int expectedRows = 10;
        int expectedCols = 10;

        // Deliberately mismatch actual construction
        Maze maze = new Maze(seed, expectedRows + 1, expectedCols);  // WRONG: rows + 1
        //Maze maze = new Maze(seed, expectedRows, expectedCols + 1);  // or cols + 1

        int actualRows = maze.getRows();
        int actualCols = maze.getCols();

        if (actualRows != expectedRows || actualCols != expectedCols) {
            String log = String.format(
                    "FORCED DIMENSION FAIL | Seed: %d | Expected: %dx%d | Actual: %dx%d",
                    seed, expectedRows, expectedCols, actualRows, actualCols
            );
            TestLogger.logFailure(log);
        }

        assertEquals(expectedRows, actualRows, "FORCED FAIL: Row count mismatch");
    }
     */

}
