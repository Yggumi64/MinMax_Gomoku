package fr.esisar.in450.gomoku.gamecore.model;

import fr.esisar.in450.gomoku.gamecore.enums.WinnerState;

public class TestGomokuBoard
{
	static void main(String[] args)
	{
		new TestGomokuBoard().test();
	}

	private void test()
	{
		String s = """
                              \s
                              \s
                WWWW          \s
                              \s
                              \s
                       B      \s
                       B      \s
                              \s
                              \s
                              \s
                              \s
                 W            \s
                 W            \s
                              \s
                              \s
                """;
		
		
		check(s,WinnerState.NONE);
		
		
		s = """
                W             \s
                 W            \s
                  W           \s
                   W          \s
                    W         \s
                       B      \s
                       B      \s
                              \s
                              \s
                              \s
                              \s
                 W            \s
                 W            \s
                              \s
                              \s
                """;
	
	
		check(s,WinnerState.WHITE);
		
		
		
		s = """
                    B         \s
                   B          \s
                  B           \s
                 B            \s
                B             \s
                       B      \s
                       B      \s
                              \s
                              \s
                              \s
                              \s
                 W            \s
                 W            \s
                              \s
                              \s
                """;
	
	
		check(s,WinnerState.BLACK);
		

		
		
	}

	private void check(String s, WinnerState expected)
	{
		GomokuBoard board = GomokuBoard.fromString(s);
		WinnerState actual = board.getWinnerState();
		
		if (actual!=expected)
		{
			throw new RuntimeException("Erreur actual = "+actual+" expected = "+expected);
		}
	}
}
