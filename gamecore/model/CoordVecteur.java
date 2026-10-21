package fr.esisar.in450.gomoku.gamecore.model;

import fr.esisar.in450.gomoku.gamecore.enums.Direction;
import fr.esisar.in450.gomoku.gamecore.enums.XInARow;

import java.util.Objects;

public class CoordVecteur {
    private final Coords start;
    private final Coords end;
    private final XInARow iar;
    private final Direction dir;

    public CoordVecteur(Coords start, Coords end, Direction dir) {
        this.start = start;
        this.end = end;
        this.dir = dir;

        int dx = end.row - start.row;
        int dy = end.col - start.col;
        int norm = Math.max(Math.abs(dx), Math.abs(dy)) + 1;
        this.iar = matchIAR(norm);
    }

    private XInARow matchIAR(int norm) {
        return switch (norm) {
            case 1 -> XInARow.IAR1;
            case 2 -> XInARow.IAR2;
            case 3 -> XInARow.IAR3;
            case 4 -> XInARow.IAR4;
            case 5 -> XInARow.IAR5;
            default -> XInARow.NONE;
        };
    }

    public Coords getStart() {
        return start;
    }

    public Coords getEnd() {
        return end;
    }

    public Direction getDir() {
        return dir;
    }

    public XInARow getIAR() {
        return iar;
    }

    public CoordVecteur getLengthen() {
        Coords newStart = new Coords(start.row - dir.getDx(), start.col - dir.getDy());
        Coords newEnd = new Coords(end.row + dir.getDx(), end.col + dir.getDy());
        return new CoordVecteur(newStart, newEnd, dir);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoordVecteur that = (CoordVecteur) o;
        return start.equals(that.start) && end.equals(that.end) && dir == that.dir;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, dir);
    }
}
