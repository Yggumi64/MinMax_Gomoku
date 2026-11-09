package player;

import gamecore.AbstractPlayer;
import gamecore.model.Coords;


/** 
 * IA buggée qui place son pion toujours au même endroit 
 */
public class DummyPlayer extends AbstractPlayer
{
	@Override
	public Coords play()
	{
		return new Coords(5,5);
	}
}
