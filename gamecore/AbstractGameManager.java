package fr.esisar.in450.gomoku.gamecore;

import fr.esisar.in450.gomoku.gamecore.enums.CellColor;
import fr.esisar.in450.gomoku.gamecore.enums.WinnerState;
import fr.esisar.in450.gomoku.gamecore.model.Coords;
import fr.esisar.in450.gomoku.gamecore.model.GomokuBoard;

public abstract class AbstractGameManager {
    protected AbstractPlayer player1;
    protected AbstractPlayer player2;
    protected int roundCount;
    protected AbstractPlayer currentPlayer;
    protected final GomokuBoard board;

    public AbstractGameManager(AbstractPlayer player1, AbstractPlayer player2)
    {
        this.player1 = player1;
        this.player2 = player2;
        this.roundCount = 0;
        this.currentPlayer = player1;
        this.board = new GomokuBoard();
    }

    public AbstractGameManager(AbstractPlayer player1)
    {
        this.player1 = player1;
        this.roundCount = 0;
        this.currentPlayer = player1;
        this.board = new GomokuBoard();
    }

    protected Coords playRound() {
        roundCount++;

        // On affiche le plateau
        board.print();
        System.out.println("Tour " + roundCount + ": " + currentPlayer.getColor());

        // On demande au joueur courant de jouer en mesurant le temps de jeu
        long startTime = System.currentTimeMillis();
        Coords move = currentPlayer.play();
        long moveDuration = System.currentTimeMillis() - startTime;

        currentPlayer.updateStats(moveDuration);

        // Jouer le coup sur le plateau
        board.setCellColor(move.row, move.col, currentPlayer.getColor());
        System.out.println("Coup joué par les "+currentPlayer.getColor()+" Ligne: " + (move.row+1)+" Colonne: " + (move.col+1)+" en "+moveDuration+" ms");
        System.out.println();
        System.out.println();
        return move;
    }

    protected void changePlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }

    protected void winnerBoard(WinnerState winnerState) {
        board.print();

        if (winnerState == WinnerState.TIE)
        {
            System.out.println("Égalité !");
        }
        else
        {
            System.out.println("Vainqueur: " + winnerState);
        }

        System.out.println("\nStatistiques :");
        player1.printStats();
        player2.printStats();
    }

    protected void initMatch() {
        // Les Blancs commencent
        player1.setColor(CellColor.WHITE);
        player2.setColor(CellColor.BLACK);
        player1.setBoard(board);
        player2.setBoard(board);
    }
}
