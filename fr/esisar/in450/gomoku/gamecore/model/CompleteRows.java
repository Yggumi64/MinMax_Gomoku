package fr.esisar.in450.gomoku.gamecore.model;

import fr.esisar.in450.gomoku.gamecore.enums.Direction;
import fr.esisar.in450.gomoku.gamecore.enums.XInARow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompleteRows {
    private final List<CoordVecteur> rows;
    private final Map<XInARow, List<Coords>> bestMoves;
    private XInARow iar;
    private int total_norm;
    private final GomokuBoard board;

    public CompleteRows(GomokuBoard board) {
        this.rows = new ArrayList<>();
        this.bestMoves = new HashMap<>();
        this.initBestMoves();
        this.total_norm = 0;
        this.iar = XInARow.NONE;
        this.board = board;
    }

    public void initBestMoves() {
        for (XInARow iar : XInARow.values()) this.bestMoves.put(iar, new ArrayList<>());
    }

    // Renvoie l'IAR correspondant à un vecteur.
    private XInARow matchIAR(int norm) {
        for (XInARow iar : XInARow.values()) {
            if (iar.getValue() == norm) return iar;
        }
        return XInARow.NONE;
    }

    // Renvoie l'IAR résultant d'un coup dans une ligne.
    private XInARow getIarInARow(Coords move, int indexStart, int indexEnd) {
        CoordVecteur vecStart = this.rows.get(indexStart);
        CoordVecteur vecEnd = this.rows.get(indexEnd);

        // La norme du coup unifié va du tout début du premier segment jusqu'à la toute fin du second
        int dx = vecEnd.getEnd().row - vecStart.getStart().row;
        int dy = vecEnd.getEnd().col - vecStart.getStart().col;
        int totalNorm = Math.max(Math.abs(dx), Math.abs(dy)) + 1;

        return this.matchIAR(totalNorm);
    }

    private List<Coords> getCriticMoves(CoordVecteur start, CoordVecteur end) {
        List<Coords> moves = new ArrayList<>();

        // On prend la direction du premier vecteur
        Direction dir = start.getDir();

        // Point de départ de l'analyse (juste après la fin du premier vecteur)
        Coords target = end.getStart();
        Coords current = new Coords(start.getEnd().row + dir.getDx(), start.getEnd().col + dir.getDy());

        // VÉRIFICATION DE LA DIRECTION :
        // Si en appliquant 'dir' on s'éloigne de la cible au lieu de s'en rapprocher,
        // c'est qu'on est dans le mauvais sens, on utilise la direction inversée !
        if (!isMovingTowards(current, target, dir)) {
            dir = dir.reverse();
            current = new Coords(start.getEnd().row + dir.getDx(), start.getEnd().col + dir.getDy());
        }

        // On avance pas à pas de manière propre jusqu'à atteindre le début du second vecteur
        for (int i = 0; i < 5; i++) {
            if (!board.isNotInBounds(current.row, current.col) && board.getCellColor(current.row, current.col) == null) {
                moves.add(current);
                if (current.row == target.row && current.col == target.col) break;
            }
            current = new Coords(current.row + dir.getDx(), current.col + dir.getDy());
            System.out.println(current.row + " " + current.col);
        }

        return moves;
    }

    // Petite fonction utilitaire pour vérifier si on va dans le bon sens
    private boolean isMovingTowards(Coords current, Coords target, Direction dir) {
        // On compare la distance avant et après un pas
        int distBefore = Math.abs(target.row - (current.row - dir.getDx())) + Math.abs(target.col - (current.col - dir.getDy()));
        int distAfter = Math.abs(target.row - current.row) + Math.abs(target.col - current.col);
        return distAfter < distBefore;
    }

    // Remplit le dictionnaire des meilleurs coups de la ligne, en fonction d'un CoordVecteur.
    private void fillBestMovesByCoordVecteur(int start, int end) {
        // On récupère les meilleurs coups DANS la ligne.
        List<Coords> moves = getCriticMoves(rows.get(start), rows.get(end));
        // On remplit le dictionnaire.
        for (Coords move : moves) {
            XInARow iar = this.getIarInARow(move, start, end);
            bestMoves.get(iar).add(move);
        }
    }

    // Remplit le dictionnaire des meilleurs coups de la ligne.
    public void fillBestMoves() {
        // On vide les anciens coups dans bestMoves.
        this.initBestMoves();
        // On vérifie si la ligne est vide.
        if (rows.isEmpty()) return;

        // si la ligne ne contient qu'un vecteur, on ajoute seulement ses extrémités.
        if (rows.size() == 1) {
            CoordVecteur lengthen = rows.getFirst().getLengthen();
            XInARow Iar = matchIAR(lengthen.getNorm()-1);
            addInBestMoves(Iar, lengthen.getStart());
            addInBestMoves(Iar, lengthen.getEnd());
        }
        // Sinon, on ajoute les meilleurs coups trouvés.
        else for (int i = 0; i < rows.size() - 1; i++) fillBestMovesByCoordVecteur(i, i + 1);
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }

    public List<CoordVecteur> getRows() {
        return rows;
    }

    public XInARow getIAR() {
        return iar;
    }

    public int getTotal_norm() {
        return total_norm;
    }

    public Map<XInARow, List<Coords>> getBestMoves() {
        return bestMoves;
    }

    public void addInBestMoves(XInARow iar, Coords move) {
        if (isAllowedMove(move)) bestMoves.get(iar).add(move);
    }

    private boolean isAllowedMove(Coords move) {
        return !board.isNotInBounds(move.row, move.col) && board.getCellColor(move.row, move.col) == null;
    }

    public void addRow(CoordVecteur row) {
        this.rows.add(row);
        Coords firstStart = rows.getFirst().getStart();
        Coords lastEnd = rows.getLast().getEnd();
        int dx = lastEnd.row - firstStart.row;
        int dy = lastEnd.col - firstStart.col;
        this.total_norm = Math.max(Math.abs(dx), Math.abs(dy)) + 1;
        this.iar = matchIAR(this.total_norm);
    }
}
