package fr.esisar.in450.gomoku.gamecore;

import fr.esisar.in450.gomoku.gamecore.enums.WinnerState;
import fr.esisar.in450.gomoku.gamecore.model.GomokuBoard;


public class LocalGameManager extends AbstractGameManager
{

    public LocalGameManager(AbstractPlayer player1, AbstractPlayer player2) {
        super(player1, player2);
    }
    
    /**
     * Lancer une partie entre deux joueurs de Gomoku, en local, sur le PC 
    */
    public void startMatch()
    {
        initMatch();
        WinnerState winnerState;

        // Tant que la partie n'est pas finie
        while ((winnerState = board.getWinnerState()) == WinnerState.NONE)
        { 
            this.playRound();
            this.changePlayer();
        }

        // Fin de la partie
        this.winnerBoard(winnerState);
    }

    public int evaluateBoard(GomokuBoard board) {
        MinMaxCore corePlayer1 = new MinMaxCore(player1, board);
        int scorePlayer1 = corePlayer1.getBoardScore();
        MinMaxCore corePlayer2 = new MinMaxCore(player2, board);
        int scorePlayer2 = corePlayer2.getBoardScore();
        return scorePlayer1-scorePlayer2;
    }

}
