package fr.esisar.in450.gomoku.player;

import fr.esisar.in450.gomoku.gamecore.AbstractPlayer;
import fr.esisar.in450.gomoku.gamecore.enums.Direction;
import fr.esisar.in450.gomoku.gamecore.enums.XInARow;
import fr.esisar.in450.gomoku.gamecore.model.CoordVecteur;
import fr.esisar.in450.gomoku.gamecore.model.Coords;

import java.util.*;

public class MinMaxPlayer extends AbstractPlayer {

    @Override
    public Coords play() {
        return getBestMove(getXIARs());
    }

    /*
     * Renvoie, à partir des coordonnées de départ, la plus longue ligne de pierres de la couleur du joueur dans une direction donnée.
     */
    private CoordVecteur foundIAR(Coords start, Direction dir) {
        // Si la case de départ est vide, ou qu'elle ne contient pas une de nos pierres, on ne peut pas trouver de lignes.
        if (board.isNotInBounds(start.row, start.col) || !this.isMyStone(start.row, start.col)) return null;

        // On commence par initialiser la fin de la ligne = au départ.
        Coords currentEnd = start;

        while (true) {
            // On se déplace dans la direction qui nous est donnée.
            int nextRow = currentEnd.row + dir.getDx();
            int nextCol = currentEnd.col + dir.getDy();

            // Si la case suivante est vide, ou qu'elle ne contient pas une de nos pierres, on s'arrête.
            if (board.isNotInBounds(nextRow, nextCol) || !this.isMyStone(nextRow, nextCol)) break;

            // Sinon, on augmente la ligne.
            currentEnd = new Coords(nextRow, nextCol);
        }

        // On retourne la ligne.
        return new CoordVecteur(start, currentEnd, dir);
    }

    /*
     * Renvoie un dictionnaire contenant une liste de toutes les lignes de la couleur du joueur pour chaque XInARow dans la grille.
     */
    private Map<XInARow, Set<CoordVecteur>> getXIARs() {
        // On initialise le dictionnaire
        Map<XInARow, Set<CoordVecteur>> xIARs = new HashMap<>();
        for (XInARow xIAR : XInARow.values()) {
            xIARs.put(xIAR, new HashSet<>());
        }

        // On parcourt la grille, puis chaque direction.
        for (int i = 0; i < board.SIZE; i++) {
            for (int j = 0; j < board.SIZE; j++) {
                for (Direction dir : Direction.values()) {
                    // On stocke la plus longue ligne de pierres de la couleur du joueur dans la direction.
                    CoordVecteur iar = foundIAR(new Coords(i, j), dir);
                    // Si la ligne existe, on la stocke dans le dictionnaire, en fonction de sa taille.
                    if (!(iar == null)) xIARs.get(iar.getIAR()).add(iar);
                }
            }
        }
        return xIARs;
    }

    /*
     * On va chercher le meilleur coup possible en fonction de la valeur de chaque XInARow.
     */
    private Coords getBestMove(Map<XInARow, Set<CoordVecteur>> xIARs) {
        // On initialise la liste des meilleurs coups, avec une liste vide et un score de 0.
        Set<Coords> bestMoves = new HashSet<>();
        XInARow bestScore = XInARow.NONE;

        // On parcourt le dictionnaire de lignes.
        for (XInARow xIAR : xIARs.keySet()) {
            // On crée un ensemble de coups temporaire, vide.
            Set<Coords> temp = new HashSet<>();
            // Si le score de la ligne est plus grand que le score actuel, on parcourt les lignes de ce score. (Optimisation)
            if (xIAR.getWeight() > bestScore.getWeight()) {
                for (CoordVecteur iar : xIARs.get(xIAR)) {
                    // On va venir chercher les deux extrémités de la ligne, pour voir si l'on peut y placer une pierre.
                    // Si c'est le cas, on les ajoute à la liste des meilleurs coups temporaire.
                    if (isAllowedMove(iar.getLengthen().getStart())) temp.add(iar.getLengthen().getStart());
                    if (isAllowedMove(iar.getLengthen().getEnd())) temp.add(iar.getLengthen().getEnd());
                }
            }
            // Si la liste temporaire contient des coups, on remplace la liste des meilleurs coups actuels par la nouvelle liste.
            if (!temp.isEmpty()) {
                bestMoves = temp;
                bestScore = xIAR;
            }
        }
        // Si la liste des meilleurs coups est vide, on renvoie la case vide la plus proche du centre.
        if (bestMoves.isEmpty()) return this.getClosestNullToCenter();
        // Sinon, on renvoie le meilleur coup le plus proche du centre.
        return getClosestToCenter(bestMoves);
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
