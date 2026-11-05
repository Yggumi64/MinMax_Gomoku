package fr.esisar.in450.gomoku.gamecore;

import fr.esisar.in450.gomoku.gamecore.enums.Direction;
import fr.esisar.in450.gomoku.gamecore.model.CompleteRows;
import fr.esisar.in450.gomoku.gamecore.model.CoordVecteur;
import fr.esisar.in450.gomoku.gamecore.model.Coords;
import fr.esisar.in450.gomoku.gamecore.model.GomokuBoard;
import java.util.HashSet;
import java.util.Set;

public class MinMaxCore {
    private final AbstractPlayer player1;
    private final AbstractPlayer player2;
    private final GomokuBoard board;
    private static final int MAX_GAP = 3;

    public MinMaxCore(AbstractPlayer player1, AbstractPlayer player2, GomokuBoard board) {
        this.player1 = player2;
        this.player2 = player1;
        this.board = board;
    }

    private CompleteRows foundIAR(Coords start, Direction dir, AbstractPlayer player) {
        if (board.isNotInBounds(start.row, start.col) || !player.isMyStone(start.row, start.col)) {
            return null;
        }

        CompleteRows completeRow = new CompleteRows(board);

        // 1. Trouver le premier segment continu de nos pierres
        Coords currentEnd = findSegmentEnd(start, dir, player);
        completeRow.addRow(new CoordVecteur(start, currentEnd, dir));

        // 2. Chercher s'il y a un trou (max 2 cases vides) suivi d'un second segment
        Coords secondStart = findNextMyStoneAfterGap(currentEnd, dir, player);

        if (secondStart != null) {
            Coords secondEnd = findSegmentEnd(secondStart, dir, player);
            completeRow.addRow(new CoordVecteur(secondStart, secondEnd, dir));
        }

        return completeRow;
    }

    /**
     * Trouve la fin d'un segment continu de nos pierres dans une direction,
     * sans instancier d'objets intermédiaires en boucle (optimisation mémoire).
     */
    private Coords findSegmentEnd(Coords start, Direction dir, AbstractPlayer player) {
        int r = start.row;
        int c = start.col;

        while (true) {
            int nextRow = r + dir.getDx();
            int nextCol = c + dir.getDy();
            if (board.isNotInBounds(nextRow, nextCol) || !player.isMyStone(nextRow, nextCol)) {
                break;
            }
            r = nextRow;
            c = nextCol;
        }
        return new Coords(r, c);
    }

    /**
     * Parcourt les cases vides (jusqu'à maxGap) pour trouver une de nos pierres après le trou.
     */
    private Coords findNextMyStoneAfterGap(Coords from, Direction dir, AbstractPlayer player) {
        int r = from.row;
        int c = from.col;
        int emptyCellsCount = 0;

        while (emptyCellsCount < MAX_GAP) {
            r += dir.getDx();
            c += dir.getDy();

            if (board.isNotInBounds(r, c)) {
                return null;
            }

            if (board.getCellColor(r, c) == null) {
                emptyCellsCount++;
            } else {
                // On tombe sur une case non vide : si c'est notre pierre, on la retourne
                if (player.isMyStone(r, c)) {
                    return new Coords(r, c);
                }
                return null; // Bloqué par une pierre adverse
            }
        }

        // Si on a épuisé le maxGap sans tomber sur une pierre valide
        return null;
    }

    /*
     * Renvoie l'ensemble des lignes de la couleur du joueur.
     */
    public Set<CompleteRows> getRows(AbstractPlayer player) {
        // On initialise l'ensemble.
        Set<CompleteRows> rows = new HashSet<>();

        // On parcourt la grille, puis chaque direction.
        for (int i = 0; i < board.SIZE; i++) {
            for (int j = 0; j < board.SIZE; j++) {
                for (Direction dir : Direction.values()) {
                    // On stocke la ligne de pierres de la couleur du joueur dans la direction.
                    CompleteRows iar = foundIAR(new Coords(i, j), dir, player);
                    // Si la ligne existe, on la stocke dans l'ensemble.
                    if (!(iar == null)) rows.add(iar);
                }
            }
        }
        return rows;
    }

    // On va chercher la case la plus proche du centre parmi un ensemble de cases.
    public Coords getClosestToCenter(Set<Coords> moves) {
        int center = board.SIZE / 2;
        Coords bestCoord = null;
        int minDistance = Integer.MAX_VALUE;

        for (Coords coord : moves) {
            int dx = coord.row - center;
            int dy = coord.col - center;
            int distanceSquared = (dx * dx) + (dy * dy);

            if (distanceSquared < minDistance) {
                minDistance = distanceSquared;
                bestCoord = coord;
            }
        }
        return bestCoord;
    }

    public int getBoardScore(AbstractPlayer player) {
        return 0;
    }

    

    public int evaluateBoard(GomokuBoard board) {
        int scorePlayer1 = getBoardScore(player1);
        int scorePlayer2 = getBoardScore(player2);
        return scorePlayer1-scorePlayer2;
    }
}
