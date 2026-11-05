package fr.esisar.in450.gomoku.player;

import fr.esisar.in450.gomoku.gamecore.AbstractPlayer;
import fr.esisar.in450.gomoku.gamecore.model.Coords;


/** 
 * IA très naive, qui place ses pierres à proximité des pierres déjà placées
 */
public class BasicAIPlayer extends AbstractPlayer
{
	@Override
	public Coords play()
	{
		// On recherche une cellule vide à côté d'une de nos pierres, en privilégiant le centre
		for (int i = 0; i < board.SIZE; i++)
		{
			for (int j = 0; j < board.SIZE; j++)
			{	
				int k = ( (board.SIZE-1)/2+i) % board.SIZE;
				int l = ( (board.SIZE-1)/2+j) % board.SIZE;
				
				if (isMatching(k,l))
				{
					return new Coords(k,l);
				}
			}
		}
		
		// Si on n'a pas trouvé : on retourne n'importe quelle cellule vide, en privilégiant le centre
		return this.getClosestNullToCenter();
	}

	
}
