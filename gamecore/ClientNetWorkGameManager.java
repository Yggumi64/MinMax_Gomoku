package fr.esisar.in450.gomoku.gamecore;

import fr.esisar.in450.gomoku.gamecore.enums.CellColor;
import fr.esisar.in450.gomoku.gamecore.enums.WinnerState;
import fr.esisar.in450.gomoku.gamecore.model.Coords;
import fr.esisar.in450.gomoku.gamecore.network.GomokuProxy;
import fr.esisar.in450.gomoku.gamecore.network.NetworkPlayer;


public class ClientNetWorkGameManager extends AbstractGameManager
{

    public ClientNetWorkGameManager(AbstractPlayer player1) {
        super(player1);
    }

    public void startMatches(int nbGames, String ipServer, String teamName)
	{
		System.out.println("Le nom de votre équipe est : "+teamName);
		System.out.println("Tentative de connexion au serveur "+ipServer+" ...");
		GomokuProxy proxy = new GomokuProxy(ipServer);
		proxy.connect();
		System.out.println("Connexion réussie, attente du joueur adverse ...");
		
		
		int realNbGames = proxy.startAllGames(nbGames,teamName);
		System.out.println("Début du tournoi. Nb de matchs : "+realNbGames);
		
		for (int i = 0; i < realNbGames; i++)
		{
			System.out.println("=============================================");
			System.out.println("Démarrage du match "+(i+1));
			performOneGame(proxy);
		}
	}
	
    
   
    public void performOneGame(GomokuProxy proxy)
    {
        CellColor localCellColor = proxy.startOneGame();
        
    	
		System.out.println("Sur ce match, vous jouez en tant que "+localCellColor);
    	System.out.println("=============================================");
		
        
        if (localCellColor==CellColor.WHITE)
        {
        	player2 = new NetworkPlayer(proxy);
        }
        else
        {
            player2 = player1;
        	player1 = new NetworkPlayer(proxy);
        }
        
        initMatch();
        WinnerState winnerState = WinnerState.NONE;
        

        // Tant que la partie n'est pas finie
        while (winnerState == WinnerState.NONE)
        { 
            Coords move = this.playRound();
            
            // On avertit le serveur principal si nécessaire 
            if (!(currentPlayer instanceof NetworkPlayer))
            {
            	proxy.play(move.row, move.col);
            }
            
            // On récupère l'état du plateau 
            winnerState = proxy.getWinnerState();

            // Changer de joueur
            this.changePlayer();
        }

        // Fin de la partie
        // On affiche le plateau 
        this.winnerBoard(winnerState);
    }
      

}
