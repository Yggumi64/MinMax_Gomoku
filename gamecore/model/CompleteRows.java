package fr.esisar.in450.gomoku.gamecore.model;

import fr.esisar.in450.gomoku.gamecore.enums.XInARow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompleteRows {
    private final List<CoordVecteur> rows;
    private final Map<XInARow, List<Coords>> bestMoves;
    private XInARow iar;
    private int total_norm;

    public CompleteRows() {
        this.rows = new ArrayList<>();
        this.bestMoves = new HashMap<>();
        for (XInARow iar : XInARow.values()) {this.bestMoves.put(iar, new ArrayList<>());}
        this.total_norm = 0;
        this.iar = XInARow.NONE;
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

    private XInARow getIarInARow(Coords move, int indexStart, int indexEnd) {
        CoordVecteur vecStart = this.rows.get(indexStart);
        CoordVecteur vecEnd = this.rows.get(indexEnd);
        if (move.equals(vecStart.getEnd())) return this.matchIAR(new CoordVecteur(vecStart.getStart(), move, vecStart.getDir()).getNorm());
        return this.matchIAR(new CoordVecteur(move, vecEnd.getEnd(), vecEnd.getDir()).getNorm());
    }

    private List<Coords> getCriticMoves(CoordVecteur start, CoordVecteur end) {
        List<Coords> moves = new ArrayList<>();
        for (int i = start.getEnd().row; i < end.getStart().row; i++) {
            for (int j = start.getEnd().col; j < end.getStart().col; j++) {
                moves.add(new Coords(i, j));
            }
        }
        return moves;
    }

    private XInARow getBestIAR(List<XInARow> IARs) {
        XInARow bestIAR = XInARow.NONE;
        for (XInARow iar : IARs) {
            if (iar.getWeight() > bestIAR.getWeight()) bestIAR = iar;
        }
        return bestIAR;
    }

    private void fillBestMovesByCoordVecteur(int start, int end) {
        List<Coords> moves = getCriticMoves(rows.get(start), rows.get(end));
        for (Coords move : moves) {
            XInARow iar = this.getIarInARow(move, start, end);
            bestMoves.get(iar).add(move);
        }
    }

    public void fillBestMoves() {
        bestMoves.clear();
        switch (rows.size()) {
            case 1 -> {
                CoordVecteur lengthen = rows.getFirst().getLengthen();
                XInARow Iar = matchIAR(lengthen.getNorm()-1);
                bestMoves.get(Iar).add(lengthen.getStart());
                bestMoves.get(Iar).add(lengthen.getEnd());
            }
            case 2 -> {
                fillBestMovesByCoordVecteur(0, 1);
            }
            case 3 -> {
                fillBestMovesByCoordVecteur(0, 1);
                fillBestMovesByCoordVecteur(1, 2);
            }
        }
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }

    public List<CoordVecteur> getRows() {
        return rows;
    }

    public XInARow getIAR() {
        return iar;
    }

    public int getTotal_norm() {
        return total_norm;
    }

    public Map<XInARow, List<Coords>> getBestMoves() {
        return bestMoves;
    }

    public void addRow(CoordVecteur row) {
        this.rows.add(row);
        this.total_norm = row.getNorm();
        this.iar = matchIAR(this.total_norm);
    }
}
