package gamecore;

import gamecore.enums.Direction;
import gamecore.enums.WinnerState;
import gamecore.enums.XInARow;
import gamecore.model.CompleteRows;
import gamecore.model.CoordVecteur;
import gamecore.model.Coords;
import gamecore.model.GomokuBoard;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MinMaxCore {
    private final AbstractPlayer player;
    private final GomokuBoard board;
    private static final int MAX_GAP = 3;

    public MinMaxCore(AbstractPlayer player1, GomokuBoard board) {
        this.player = player1;
        this.board = board;
    }

    private CompleteRows foundIAR(Coords start, Direction dir) {
        if (board.isNotInBounds(start.row, start.col) || !player.isMyStone(start.row, start.col)) {
            return null;
        }

        CompleteRows completeRow = new CompleteRows(board);

        // 1. Trouver le premier segment continu de nos pierres
        Coords currentEnd = findSegmentEnd(start, dir);
        completeRow.addRow(new CoordVecteur(start, currentEnd, dir));

        // 2. Chercher s'il y a un trou (max 2 cases vides) suivi d'un second segment
        Coords secondStart = findNextMyStoneAfterGap(currentEnd, dir);

        if (secondStart != null) {
            Coords secondEnd = findSegmentEnd(secondStart, dir);
            completeRow.addRow(new CoordVecteur(secondStart, secondEnd, dir));
        }

        return completeRow;
    }

    /**
     * Trouve la fin d'un segment continu de nos pierres dans une direction,
     * sans instancier d'objets intermédiaires en boucle (optimisation mémoire).
     */
    private Coords findSegmentEnd(Coords start, Direction dir) {
        int r = start.row;
        int c = start.col;

        while (true) {
            int nextRow = r + dir.getDx();
            int nextCol = c + dir.getDy();
            if (board.isNotInBounds(nextRow, nextCol) || !player.isMyStone(nextRow, nextCol)) {
                break;
            }
            r = nextRow;
            c = nextCol;
        }
        return new Coords(r, c);
    }

    /**
     * Parcourt les cases vides (jusqu'à maxGap) pour trouver une de nos pierres après le trou.
     */
    private Coords findNextMyStoneAfterGap(Coords from, Direction dir) {
        int r = from.row;
        int c = from.col;
        int emptyCellsCount = 0;

        while (emptyCellsCount < MAX_GAP) {
            r += dir.getDx();
            c += dir.getDy();

            if (board.isNotInBounds(r, c)) {
                return null;
            }

            if (board.getCellColor(r, c) == null) {
                emptyCellsCount++;
            } else {
                // On tombe sur une case non vide : si c'est notre pierre, on la retourne
                if (player.isMyStone(r, c)) {
                    return new Coords(r, c);
                }
                return null; // Bloqué par une pierre adverse
            }
        }

        // Si on a épuisé le maxGap sans tomber sur une pierre valide
        return null;
    }

    /*
     * Renvoie l'ensemble des lignes de la couleur du joueur.
     */
    public Set<CompleteRows> getRows() {
        // On initialise l'ensemble.
        Set<CompleteRows> rows = new HashSet<>();

        // On parcourt la grille, puis chaque direction.
        for (int i = 0; i < board.SIZE; i++) {
            for (int j = 0; j < board.SIZE; j++) {
                for (Direction dir : Direction.values()) {
                    // On stocke la ligne de pierres de la couleur du joueur dans la direction.
                    CompleteRows iar = foundIAR(new Coords(i, j), dir);
                    // Si la ligne existe, on la stocke dans l'ensemble.
                    if (!(iar == null)) rows.add(iar);
                }
            }
        }
        return rows;
    }

    // On va chercher la case la plus proche du centre parmi un ensemble de cases.
    public Coords getClosestToCenter(Set<Coords> moves) {
        int center = board.SIZE / 2;
        Coords bestCoord = null;
        int minDistance = Integer.MAX_VALUE;

        for (Coords coord : moves) {
            int dx = coord.row - center;
            int dy = coord.col - center;
            int distanceSquared = (dx * dx) + (dy * dy);

            if (distanceSquared < minDistance) {
                minDistance = distanceSquared;
                bestCoord = coord;
            }
        }
        return bestCoord;
    }

    /**
     * Évalue le plateau pour le joueur actuel par rapport à l'adversaire.
     */
    public int getBoardScore() {
        int myScore = evaluateRowsForColor(true);      // Nos lignes
        int opponentScore = evaluateRowsForColor(false); // Lignes adverses
        
        // On maximise notre score et on minimise (punit) celui de l'adversaire
        return myScore - (opponentScore * 2); // Le *2 donne un peu plus d'importance défensive
    }

    private int evaluateRowsForColor(boolean isMe) {
        int totalScore = 0;
        // Note: il faudrait pouvoir interroger les lignes pour une couleur donnée.
        // Si getRows() utilise this.player.isMyStone(), on peut adapter pour tester l'adversaire.
        Set<CompleteRows> targetRows = getRowsForTest(isMe);
        
        for (CompleteRows cr : targetRows) {
            if (cr.isEmpty()) continue;
            cr.fillBestMoves();
            Map<XInARow, java.util.List<Coords>> moves = cr.getBestMoves();
            
            for (Map.Entry<XInARow, java.util.List<Coords>> entry : moves.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    totalScore += entry.getKey().getWeight() * entry.getValue().size();
                }
            }
        }
        return totalScore;
    }

    // Version adaptable de getRows pour tester l'IA ou l'adversaire
    private Set<CompleteRows> getRowsForTest(boolean isMe) {
        Set<CompleteRows> rows = new HashSet<>();
        for (int i = 0; i < board.SIZE; i++) {
            for (int j = 0; j < board.SIZE; j++) {
                Coords c = new Coords(i, j);
                // Vérifie si la pierre appartient au camp testé
                boolean matches = isMe ? player.isMyStone(i, j) : (board.getCellColor(i, j) != null && !player.isMyStone(i, j));
                
                if (matches) {
                    for (Direction dir : Direction.values()) {
                        CompleteRows iar = foundIAR(c, dir);
                        if (iar != null) rows.add(iar);
                    }
                }
            }
        }
        return rows;
    }

    /**
     * Algorithme MinMax récursif
     */
    public int minMax(int depth, boolean isMaximizing, int alpha, int beta) {
        // Condition d'arrêt : profondeur atteinte ou fin de partie 
        if (depth == 0 || board.getWinnerState() != WinnerState.NONE) {
            return getBoardScore();
        }
        
        // Récupérer tous les coups possibles (cases vides proches de pierres existantes par exemple, ou toutes les cases vides)
        Set<Coords> possibleMoves = getAllAllowedMoves();

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (Coords move : possibleMoves) {
                // Jouer le coup temporairement
                board.setCellColor(move.row, move.col, player.getColor()); // (Vérifie la méthode exacte de ton GomokuBoard pour poser un pion)
                
                int eval = minMax(depth - 1, false, alpha, beta);
                
                // Annuler le coup (Backtracking)
                board.setCellColor(move.row, move.col, null);

                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break; // Élagage Alpha-Beta
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Coords move : possibleMoves) {
                board.setCellColor(move.row, move.col, player.getOpponentColor());
                
                int eval = minMax(depth - 1, true, alpha, beta);
                
                board.setCellColor(move.row, move.col, null);

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    private Set<Coords> getAllAllowedMoves() {
        Set<Coords> moves = new HashSet<>();
        for (int i = 0; i < board.SIZE; i++) {
            for (int j = 0; j < board.SIZE; j++) {
                if (board.getCellColor(i, j) == null) {
                    moves.add(new Coords(i, j));
                }
            }
        }
        return moves;
    }

}
