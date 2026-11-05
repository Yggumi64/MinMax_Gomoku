package fr.esisar.in450.gomoku.gamecore.server;

import fr.esisar.in450.gomoku.gamecore.enums.CellColor;
import fr.esisar.in450.gomoku.gamecore.enums.WinnerState;
import fr.esisar.in450.gomoku.gamecore.model.Coords;
import fr.esisar.in450.gomoku.gamecore.model.GomokuBoard;

public class ConcurrentServer
{

	private GomokuBoard board;
	
	private final Coords[] lastMoves = { null, null };
	private final int[] nbGames = { -1 , -1 };
	private final int[] winnerStates = { -1 , -1 };
	

	public void initializeBoard()
	{
		board = new GomokuBoard();
	}
	
	public int getRealNbGame(int nbGame, int pcNumber)
	{
		nbGames[pcNumber] = nbGame;
		while (nbGames[0]==-1 || nbGames[1]==-1)
		{
			sleep();
		}		
		return Math.min(nbGames[0],nbGames[1]);
	}

	
	

	public String play(int row, int col, boolean isWhite,int pcNumber, int currentGameNumber,String teamName)
	{
		// On vérifie d'abord si le coup est valide
		if (isValid(row) || isValid(col))
		{
			return "Les coordonnées sont incorrectes : row = "+row +" col = "+col;
		}
		if (board.getCellColor(row, col)!=null)
		{
			return "La case jouée est déjà occupée";
		}
		
		// On sauvegarde le coup joué 
		lastMoves[pcNumber] = new Coords(row, col);
		board.setCellColor(row, col, isWhite ? CellColor.WHITE : CellColor.BLACK);
		
		WinnerState winnerState = board.getWinnerState();
		int w = getWinnerState(winnerState);
		winnerStates[0] = w;
		winnerStates[1] = w;
		if (w!=0)
		{
			System.out.println("Partie "+currentGameNumber+" terminée. "+getLabel(winnerState,pcNumber,teamName));			
			board = new GomokuBoard();
		}
		
		return null;
	}
	
	private boolean isValid(int rowOrCol)
	{
		return rowOrCol < 0 || rowOrCol >= board.SIZE;
	}


	private String getLabel(WinnerState winnerState, int pcNumber,String teamName)
	{
		if (winnerState==WinnerState.TIE)
		{
			return "Égalité";
		}
		return "Victoire du joueur "+(pcNumber+1)+"-"+teamName+" en tant que "+winnerState;
	}

	public int getWinnerState(int pcNumber)
	{
		return winnerStates[pcNumber];
	}
	

	private int getWinnerState(WinnerState winnerState)
	{
        return switch (winnerState) {
            case NONE -> 0;
            case WHITE -> 1;
            case BLACK -> 2;
            case TIE -> 3;
        };
	}


	public Coords waitingOppositePlayer(int pcNumber)
	{
		int index = 1-pcNumber;
		
		while(lastMoves[index]==null)
		{
			sleep();
		}
		
		Coords res = lastMoves[index];
		lastMoves[index] = null;
		return res;
	}
	
	private void sleep()
	{
		try
		{
			Thread.sleep(10);
		} 
		catch (InterruptedException _)
		{
		}	
	}
}
