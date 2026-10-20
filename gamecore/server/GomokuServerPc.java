package fr.esisar.in450.gomoku.gamecore.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.net.Socket;

import fr.esisar.in450.gomoku.gamecore.model.Coords;
import fr.esisar.in450.gomoku.gamecore.network.GomokuRequest;
import fr.esisar.in450.gomoku.gamecore.network.GomokuResponse;

public class GomokuServerPc extends Thread
{
	private int realNbGame = -1;
	private boolean localPlayerColorIsWhite;
	private int currentGameNumber = 0;
	
	private final int pcNumber;
	private String teamName;
	
	private final ConcurrentServer cc;
	private final Socket socket;
	private boolean stayConnected;
	
	 
	
	public GomokuServerPc(int pcNumber,Socket socket, ConcurrentServer cc)
	{
		this.pcNumber = pcNumber;
		this.socket = socket;
		this.cc = cc;
		stayConnected = true;
	}
	
	 
	 public GomokuResponse process(GomokuRequest req)
	 {
         return switch (req.type) {
             case START_ALL_GAMES -> startAllGames(req);
             case START_ONE_GAME -> startOneGame();
             case PLAY -> play(req);
             case GET_WINNER_STATE -> getWinnerState();
             case WAITING_OPPOSING_PLAYER -> waitingOppositePlayer();
         };
	 }


	private GomokuResponse startAllGames(GomokuRequest req)
	{
		realNbGame = cc.getRealNbGame(req.nbGame,pcNumber);
		localPlayerColorIsWhite = pcNumber==1;
		teamName = req.teamName;
		cc.initializeBoard();
				
		GomokuResponse res = new GomokuResponse();
		res.realNbGames = realNbGame;
		return res;
	}

	
	private GomokuResponse startOneGame()
	{
		localPlayerColorIsWhite = !localPlayerColorIsWhite;
		currentGameNumber++;
		
		if (localPlayerColorIsWhite)
		{
			System.out.println("Démarrage du match "+currentGameNumber+". Les WHITE sont joués par l'équipe "+(pcNumber+1)+"-"+teamName);
		}
		
		
		GomokuResponse res = new GomokuResponse();
		res.isWhite = localPlayerColorIsWhite;
		return res;
	}
	
	
	private GomokuResponse play(GomokuRequest req)
	{  
		String errorMessage = cc.play(req.row, req.col, localPlayerColorIsWhite , pcNumber , currentGameNumber,teamName);

        return GomokuResponse.error(errorMessage);
	}
	
	
	private GomokuResponse getWinnerState()
	{
		int winnerState = cc.getWinnerState(pcNumber);
		
		if (winnerState!=0 && currentGameNumber==realNbGame)
		{
			stayConnected = false;
			System.out.println("Fin du tournoi.");
		}
		
		GomokuResponse res = new GomokuResponse();
		res.winnerState = winnerState;
		return res;
	}
	
	
	
	private GomokuResponse waitingOppositePlayer()
	{
		Coords c = cc.waitingOppositePlayer(pcNumber);

		GomokuResponse res = new GomokuResponse();
		res.row = c.row;
		res.col = c.col;
		return res;
	}
	
	@Override
	public void run()
	{
		try
		{
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			
			
			
			while(stayConnected)  
			{
				// Deserialisation de la requête
				GomokuRequest req = GomokuRequest.deserialize(dis);
				
				// Calcul de la réponse
				GomokuResponse res = process(req);
				
				// Serialisation de la réponse
				res.serialize(dos);
				dos.flush();
				
				if (res.errorMessage!=null)
				{
					System.out.println("Le joueur "+(pcNumber+1)+"-"+teamName+" vient de réaliser une erreur : "+res.errorMessage);
					System.out.println("Fin du tournoi");
					System.exit(0);
				}
			}
			
			socket.close();
		} 
		catch (EOFException e) 
		{
			System.out.println("Le joueur "+(pcNumber+1)+" vient de se déconnecter ! ");
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
