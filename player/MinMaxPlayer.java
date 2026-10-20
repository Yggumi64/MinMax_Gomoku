package fr.esisar.in450.gomoku.player;

import fr.esisar.in450.gomoku.gamecore.AbstractPlayer;
import fr.esisar.in450.gomoku.gamecore.enums.XInARow;
import fr.esisar.in450.gomoku.gamecore.model.Coords;
import fr.esisar.in450.gomoku.gamecore.model.GomokuBoard;

import java.lang.reflect.Array;
import java.util.List;

public class MinMaxPlayer extends AbstractPlayer {
    private final static int DOUBLE = 2;
    private final static int TRIPLE = 8;
    private final static int QUADRUPLE = 16;
    private final static int VICTOIRE = 1000;

    private int valeur_coup;
    private GomokuBoard board;


    @Override
    public Coords play() {
        return null;
    }

    record coordVecteur(Coords start, Coords end) {}

    private coordVecteur foundIAR(int length, int t, Coords start) {

    }

    private List<coordVecteur> getXIARs(XInARow x) {
        switch (x) {
            case IAR2 ->
            case IAR3 ->
            case IAR4 ->
        }
    }

    private boolean isMatching(int i, int j)
    {
        // On vérifie si la case est vide
        if (board.getCellColor(i, j)!=null)
        {
            return false;
        }

        // Horizontal
        if (isMyStone(i-1,j)) return true;
        if (isMyStone(i+1,j)) return true;

        // Vertical
        if (isMyStone(i,j-1)) return true;
        if (isMyStone(i,j+1)) return true;

        // Diagonal
        if (isMyStone(i-1,j-1)) return true;
        if (isMyStone(i-1,j+1)) return true;
        if (isMyStone(i+1,j-1)) return true;
        return isMyStone(i + 1, j + 1);
    }
}
