package fr.esisar.in450.gomoku.player;

import fr.esisar.in450.gomoku.gamecore.AbstractPlayer;
import fr.esisar.in450.gomoku.gamecore.enums.Direction;
import fr.esisar.in450.gomoku.gamecore.enums.XInARow;
import fr.esisar.in450.gomoku.gamecore.model.CompleteRows;
import fr.esisar.in450.gomoku.gamecore.model.CoordVecteur;
import fr.esisar.in450.gomoku.gamecore.model.Coords;

import java.util.*;

public class MinMaxPlayer extends AbstractPlayer {

    @Override
    public Coords play() {
        return getBestMove(getRows());
    }

    /*
     * Renvoie, à partir des coordonnées de départ, la plus longue ligne de pierres de la couleur du joueur dans une direction donnée.
     */
    private CompleteRows foundIAR(Coords start, Direction dir) {
        // Si la case de départ est vide, ou qu'elle ne contient pas une de nos pierres, on ne peut pas trouver de lignes.
        if (board.isNotInBounds(start.row, start.col) || !this.isMyStone(start.row, start.col)) return null;

        // On commence par initialiser la fin de la ligne = au départ.
        Coords currentStart = start;
        Coords currentEnd = start;
        CompleteRows row = new CompleteRows();

        for (int i = 0; i < 5; i++) {
            // On se déplace dans la direction qui nous est donnée.
            int nextRow = currentEnd.row + dir.getDx();
            int nextCol = currentEnd.col + dir.getDy();

            // Si la case suivante est vide, ou qu'elle ne contient pas une de nos pierres, on ajoute le CoordVecteur à notre ligne.
            if (board.isNotInBounds(nextRow, nextCol) || !this.isMyStone(nextRow, nextCol)) {
                row.addRow(new CoordVecteur(currentStart, currentEnd, dir));
                currentStart = currentEnd;
            }

            // Sinon, on augmente la ligne.
            currentEnd = new Coords(nextRow, nextCol);
        }

        // On retourne la ligne.
        return row;
    }

    /*
     * Renvoie un dictionnaire contenant une liste de toutes les lignes de la couleur du joueur pour chaque XInARow dans la grille.
     */
    private Set<CompleteRows> getRows() {
        // On initialise le dictionnaire
        Set<CompleteRows> rows = new HashSet<>();

        // On parcourt la grille, puis chaque direction.
        for (int i = 0; i < board.SIZE; i++) {
            for (int j = 0; j < board.SIZE; j++) {
                for (Direction dir : Direction.values()) {
                    // On stocke la plus longue ligne de pierres de la couleur du joueur dans la direction.
                    CompleteRows iar = foundIAR(new Coords(i, j), dir);
                    // Si la ligne existe, on la stocke dans le dictionnaire, en fonction de sa taille.
                    if (!(iar == null)) rows.add(iar);
                }
            }
        }
        return rows;
    }

    /*
     * On va chercher le meilleur coup possible en fonction de la valeur de chaque XInARow.
     */
    private Coords getBestMove(Set<CompleteRows> rows) {
        // On initialise la liste des meilleurs coups, avec une liste vide et un score de 0.
        Set<Coords> bestMoves;
        XInARow bestScore = XInARow.NONE;
        Map<XInARow, Set<Coords>> xIARs = new HashMap<>();
        for (XInARow xIAR : XInARow.values()) {
            xIARs.put(xIAR, new HashSet<>());
        }
        for (CompleteRows cr : rows) {
            if (cr.isEmpty()) continue;
            Map<XInARow, List<Coords>> moves = cr.getBestMoves();
            if (moves.isEmpty()) continue;
            for (XInARow xIAR : moves.keySet()) {
                if (xIAR.getWeight() > bestScore.getWeight() && !moves.get(xIAR).isEmpty()) {
                    xIARs.get(xIAR).addAll(moves.get(xIAR));
                    bestScore = xIAR;
                }
            }
        }
        bestMoves = xIARs.get(bestScore);
        // Si la liste des meilleurs coups est vide, on renvoie la case vide la plus proche du centre.
        if (bestMoves.isEmpty()) return this.getClosestNullToCenter();
        Coords bestMove = getClosestToCenter(bestMoves);
        if (bestMove == null) return this.getClosestNullToCenter();
        // Sinon, on renvoie le meilleur coup le plus proche du centre.
        return bestMove;
    }

    // On vérifie si la case est vide et qu'elle n'est pas hors limites.
    private boolean isAllowedMove(Coords move) {
        return !board.isNotInBounds(move.row, move.col) && board.getCellColor(move.row, move.col) == null;
    }

    // On va chercher la case la plus proche du centre parmi un ensemble de cases.
    private Coords getClosestToCenter(Set<Coords> moves) {
        int center = board.SIZE / 2;
        Coords bestCoord = null;
        int minDistance = Integer.MAX_VALUE;

        for (Coords coord : moves) {
            if (isAllowedMove(coord)) {
                int dx = coord.row - center;
                int dy = coord.col - center;
                int distanceSquared = (dx * dx) + (dy * dy);

                if (distanceSquared < minDistance) {
                    minDistance = distanceSquared;
                    bestCoord = coord;
                }
            }

        }
        return bestCoord;
    }
}
