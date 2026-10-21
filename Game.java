package fr.esisar.in450.gomoku;

import java.io.IOException;

import fr.esisar.in450.gomoku.gamecore.ClientNetWorkGameManager;
import fr.esisar.in450.gomoku.gamecore.LocalGameManager;
import fr.esisar.in450.gomoku.gamecore.server.GomokuServer;
import fr.esisar.in450.gomoku.player.BasicAIPlayer;
import fr.esisar.in450.gomoku.player.DummyPlayer;
import fr.esisar.in450.gomoku.player.HumanPlayer;
import fr.esisar.in450.gomoku.player.MinMaxPlayer;


/**
 * Cette classe permet de lancer le jeu de Gomoku 
 * Merci de lire les commentaires des fonctions ci-dessous pour voir
 * les différents cas d'usage.
 */
public class Game
{
    static void main(String[] args)
	{
		// ex1();
        ex2();
		ex3();
	}
	
	/**
	 * Exemple 1 : permet de faire une partie en local entre une IA basique et un joueur humain 
	 */
	public static void ex1()
	{
		new LocalGameManager(new DummyPlayer(), new HumanPlayer()).startMatch();
	}
	
	
	/**
	 * Exemple 2 : permet de faire une partie en local entre deux IA basiques  
	 */
	public static void ex2()
	{
		new LocalGameManager(new BasicAIPlayer(), new BasicAIPlayer()).startMatch();
	}

    public static void ex3()
    {
        new LocalGameManager(new MinMaxPlayer(), new HumanPlayer()).startMatch();
    }
	
	
	/**
	 * Exemple 3
     * ---
	 * Vous souhaitez faire un match en réseau avec un autre binôme de TP.
	 * On considère que vous disposez de trois machines :
	 * la machine avec l'IP 192.168.130.9 est le serveur,
	 * la machine avec l'IP 192.168.130.1 est le joueur 1,
	 * la machine avec l'IP 192.168.130.2 est le joueur 2.
	 * ---
	 * Voici les étapes à réaliser
     * ---
	 * Étape 0 : sur la machine serveur (192.168.130.9), vous lancez le code suivant
	 * 		GomokuServer.main(null);
	 * Étape 1 : sur la machine joueur 1 (192.168.130.1), vous lancez le code suivant
	 * 		new ClientNetWorkGameManager().startMatches(new BasicAIPlayer(),4,"192.168.130.9","équipe 1");
	 * Ceci va lancer un tournoi de quatre matchs, qui démarrera dès que le joueur n°2 va se connecter
	 * Étape 2 : sur la machine joueur 2 (192.168.130.2), vous lancez le code suivant
	 * 		new ClientNetWorkGameManager().startMatches(new BasicAIPlayer(),4,"192.168.130.9","équipe 2");
	 * ---
	 * Le tournoi démarre, vous pouvez voir son avancement à la fois sur les clients (joueur1 et joueur2)
	 * et sur le serveur.
	 * 
	 */

	public static void stage0() throws IOException
	{
		GomokuServer.main(null);
	}
	
	public static void stage1() throws IOException
	{
		new ClientNetWorkGameManager(new BasicAIPlayer()).startMatches(4,"192.168.130.9","équipe 1");
	}
	
	public static void stage2() throws IOException
	{
		new ClientNetWorkGameManager(new DummyPlayer()).startMatches(4,"192.168.130.9","équipe 2");
	}


}
