package fr.esisar.in450.gomoku.gamecore;

import fr.esisar.in450.gomoku.gamecore.enums.WinnerState;


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
}
