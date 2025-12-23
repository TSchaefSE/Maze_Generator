package MazeGen;

import java.util.EnumSet;
import MazeGen.Direction;

public class Cell {

    private final int row;
    private final int col;
    public final EnumSet<Direction> walls;
    private boolean visited;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.visited = false;
        this.walls = EnumSet.allOf(Direction.class);
    }

    //Getters
    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
    public EnumSet<Direction> getWalls() {
        return walls;
    }
    public boolean isVisited() {
        return visited;
    }

    //Setters
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean hasWall(Direction direction) {
        return walls.contains(direction);
    }

    public void removeWall(Direction direction) {
        walls.remove(direction);
    }

}
