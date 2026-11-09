package player;

import gamecore.AbstractPlayer;
import gamecore.MinMaxCore;
import gamecore.enums.XInARow;
import gamecore.model.CompleteRows;
import gamecore.model.Coords;
import gamecore.model.GomokuBoard;

import java.util.*;

public class MinMaxPlayer extends AbstractPlayer {

    private MinMaxCore core = null;

    @Override 
    public void setBoard(GomokuBoard board) {
        this.board = board;
        this.core = new MinMaxCore(this, this.board);
    }

    @Override
    public Coords play() {
        return getBestMove(core.getRows());
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

        Coords bestMove = core.getClosestToCenter(bestMoves);
        return (bestMove != null) ? bestMove : this.getClosestNullToCenter();
    }

    
}
