package fr.esisar.in450.gomoku.player;

import fr.esisar.in450.gomoku.gamecore.AbstractPlayer;
import fr.esisar.in450.gomoku.gamecore.MinMaxCore;
import fr.esisar.in450.gomoku.gamecore.enums.XInARow;
import fr.esisar.in450.gomoku.gamecore.model.CompleteRows;
import fr.esisar.in450.gomoku.gamecore.model.Coords;
import java.util.*;

public class MinMaxPlayer extends AbstractPlayer {

    private MinMaxCore core = null;
    
    public void setMinMaxCore(AbstractPlayer player2) {
        this.core = new MinMaxCore(this, player2, this.board);
    }

    public boolean isCoreSet() {
        if (core == null) {
            System.out.println("You must add a core to this player first");
            return false;
        } 
        return true;
    }

    @Override
    public Coords play() {
        if (!isCoreSet()) throw new RuntimeException();
        return getBestMove(core.getRows(this));
    }

    /*
     * On va chercher le meilleur coup possible.
     */
    private Coords getBestMove(Set<CompleteRows> rows) {
        if (!isCoreSet()) throw new RuntimeException();
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

        Coords bestMove = core.getClosestToCenter(bestMoves);
        return (bestMove != null) ? bestMove : this.getClosestNullToCenter();
    }

    
}
