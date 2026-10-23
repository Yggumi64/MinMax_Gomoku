package fr.esisar.in450.gomoku.player;

import fr.esisar.in450.gomoku.gamecore.AbstractPlayer;
import fr.esisar.in450.gomoku.gamecore.enums.Direction;
import fr.esisar.in450.gomoku.gamecore.enums.XInARow;
import fr.esisar.in450.gomoku.gamecore.model.CompleteRows;
import fr.esisar.in450.gomoku.gamecore.model.CoordVecteur;
import fr.esisar.in450.gomoku.gamecore.model.Coords;

import java.util.*;

public class MinMaxPlayer extends AbstractPlayer {

    private static final int MAX_GAP = 3;

    @Override
    public Coords play() {
        return getBestMove(getRows());
    }

    private CompleteRows foundIAR(Coords start, Direction dir) {
        if (board.isNotInBounds(start.row, start.col) || !this.isMyStone(start.row, start.col)) {
            return null;
        }

        CompleteRows completeRow = new CompleteRows(board);

        // 1. Trouver le premier segment continu de nos pierres
        Coords currentEnd = findSegmentEnd(start, dir);
        completeRow.addRow(new CoordVecteur(start, currentEnd, dir));

        // 2. Chercher s'il y a un trou (max 2 cases vides) suivi d'un second segment
        Coords secondStart = findNextMyStoneAfterGap(currentEnd, dir);

        if (secondStart != null) {
            Coords secondEnd = findSegmentEnd(secondStart, dir);
            completeRow.addRow(new CoordVecteur(secondStart, secondEnd, dir));
        }

        return completeRow;
    }

    /**
     * Trouve la fin d'un segment continu de nos pierres dans une direction,
     * sans instancier d'objets intermédiaires en boucle (optimisation mémoire).
     */
    private Coords findSegmentEnd(Coords start, Direction dir) {
        int r = start.row;
        int c = start.col;

        while (true) {
            int nextRow = r + dir.getDx();
            int nextCol = c + dir.getDy();
            if (board.isNotInBounds(nextRow, nextCol) || !this.isMyStone(nextRow, nextCol)) {
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
    private Coords findNextMyStoneAfterGap(Coords from, Direction dir) {
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
                if (this.isMyStone(r, c)) {
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
    private Set<CompleteRows> getRows() {
        // On initialise l'ensemble.
        Set<CompleteRows> rows = new HashSet<>();

        // On parcourt la grille, puis chaque direction.
        for (int i = 0; i < board.SIZE; i++) {
            for (int j = 0; j < board.SIZE; j++) {
                for (Direction dir : Direction.values()) {
                    // On stocke la ligne de pierres de la couleur du joueur dans la direction.
                    CompleteRows iar = foundIAR(new Coords(i, j), dir);
                    // Si la ligne existe, on la stocke dans l'ensemble.
                    if (!(iar == null)) rows.add(iar);
                }
            }
        }
        return rows;
    }

    /*
     * On va chercher le meilleur coup possible.
     */
    private Coords getBestMove(Set<CompleteRows> rows) {
        // 1. Initialisation du dictionnaire de tous les coups par type
        Map<XInARow, Set<Coords>> xIARs = new HashMap<>();
        for (XInARow xIAR : XInARow.values()) {
            xIARs.put(xIAR, new HashSet<>());
        }

        // 2. On remplit le dictionnaire avec TOUTES les lignes valides
        for (CompleteRows cr : rows) {
            if (cr.isEmpty()) continue;
            cr.fillBestMoves();
            Map<XInARow, List<Coords>> moves = cr.getBestMoves();

            for (Map.Entry<XInARow, List<Coords>> entry : moves.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    xIARs.get(entry.getKey()).addAll(entry.getValue());
                }
            }
        }

        // 3. On cherche le meilleur score disponible (du plus lourd au plus léger).
        XInARow bestScore = XInARow.NONE;
        for (XInARow xIAR : XInARow.values()) {
            if (!xIARs.get(xIAR).isEmpty() && xIAR.getWeight() > bestScore.getWeight()) {
                bestScore = xIAR;
            }
        }

        // 4. On récupère les coups correspondants
        Set<Coords> bestMoves = xIARs.get(bestScore);

        if (bestMoves == null || bestMoves.isEmpty()) {
            return this.getClosestNullToCenter();
        }

        Coords bestMove = getClosestToCenter(bestMoves);
        return (bestMove != null) ? bestMove : this.getClosestNullToCenter();
    }

    // On va chercher la case la plus proche du centre parmi un ensemble de cases.
    private Coords getClosestToCenter(Set<Coords> moves) {
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
}
