package fr.esisar.in450.gomoku.gamecore.enums;

public enum Direction {
    Nord(0, -1),
    NordEast(1, -1),
    East(1, 0),
    SouthEast(1, 1),
    South(0, 1),
    SouthWest(-1, 1),
    West(-1, 0),
    NorthWest(-1, -1);

    private final int dx;
    private final int dy;

    Direction(int col, int row) {
        this.dx = row;
        this.dy = col;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public Direction reverse() {
        return switch(this) {
            case Nord -> South;
            case NordEast -> SouthWest;
            case East -> West;
            case SouthEast -> NorthWest;
            case South -> Nord;
            case SouthWest -> NordEast;
            case West -> East;
            case NorthWest -> SouthEast;
        };
    }
}
