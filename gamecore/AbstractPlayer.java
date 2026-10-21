package fr.esisar.in450.gomoku.gamecore;

import fr.esisar.in450.gomoku.gamecore.enums.CellColor;
import fr.esisar.in450.gomoku.gamecore.model.Coords;
import fr.esisar.in450.gomoku.gamecore.model.GomokuBoard;



/**
 * Représente un joueur de Gomoku  
 * */
public abstract class AbstractPlayer 
{
	protected CellColor playerColor;
	
	protected GomokuBoard board;

    protected long totalPlayTime;
    protected long longestPlayTime;
    protected long shortestPlayTime;
    protected long moveCount;

    protected AbstractPlayer() {
        this.totalPlayTime = 0;
        this.longestPlayTime = 0;
        this.shortestPlayTime = Long.MAX_VALUE;
        this.moveCount = 0;
    }
	
	public void updateStats(long moveDuration)
	{
		totalPlayTime += moveDuration;
		longestPlayTime = Math.max(longestPlayTime, moveDuration);
		shortestPlayTime = Math.min(shortestPlayTime, moveDuration);
		moveCount++;
	}
    public void printStats() {
        System.out.println("Blanc: " + moveCount + " coups, " + (totalPlayTime/moveCount) + " ms/coup, " + longestPlayTime + " ms (max), " + shortestPlayTime + " ms (min)");
    }

	/**
	 * Permet d'indiquer au joueur quelle sera sa couleur 
	 */
	final public void setColor(CellColor playerColor)
	{
		this.playerColor = playerColor;
	}
	

	final public CellColor getColor()
	{
		return playerColor;
	}
	
	/**
	 * Permet d'indiquer au joueur le plateau
	 */
	final public void setBoard(GomokuBoard board)
	{
		this.board = board;
	}
	
	
    /**
     * Permet à un joueur de jouer un coup
     */
    public abstract Coords play();

    public Coords getClosestNullToCenter() {
        for (int i = 0; i < board.SIZE; i++)
        {
            for (int j = 0; j < board.SIZE; j++)
            {
                int k = ( (board.SIZE-1)/2+i) % board.SIZE;
                int l = ( (board.SIZE-1)/2+j) % board.SIZE;

                if (board.getCellColor(k, l)==null)
                {
                    return new Coords(k,l);
                }
            }
        }

        // Le tableau est plein : pas de solution
        throw new RuntimeException();
    }

    /**
     * Retourne true si < i, j > sont des coordonnées valides et si
     * la cellule < i, j > contient une pierre de la couleur de ce joueur
     *
     */
    protected boolean isMyStone(int i, int j)
    {
        if (isValid(i) && isValid(j))
        {
            return board.getCellColor(i, j)==playerColor;
        }
        else
        {
            return false;
        }
    }

    /**
     * Retourne true si la cellule < i, j > est vide et qu'une cellule adjacente contient
     * une pierre de la couleur de ce joueur
     */
    protected boolean isMatching(int i, int j)
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

    protected boolean isValid(int rowOrCol)
    {
        return rowOrCol>=0 && rowOrCol <board.SIZE;
    }
}
