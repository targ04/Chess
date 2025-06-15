import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public class Player {
    private String name;
    private boolean isWhite; // True if playing as White
    private boolean isAI; // True if this player is an AI
    private List<Move> moveHistory; // List of moves made by this player
    private Map<String, Set<Move>> possibleMovesToPos;
    private Set<Move> possibleMoves;
    private Set<Piece> piecesOnBoard;

    public Player(String name, boolean isWhite) {
        this.name = name;
        this.isWhite = isWhite;
        // Decide AI status based on name or other logic
        this.isAI = name.toLowerCase().contains("ai") || name.toLowerCase().contains("computer");
        this.moveHistory = new ArrayList<>();
        this.possibleMoves = new HashSet<>();
        this.piecesOnBoard = new HashSet<>();
        this.possibleMovesToPos = new HashMap<>();
    }

    public boolean canPlayerMove(Piece piece, String toPosition){
        // check if there exists a move with these characteristics
        if (possibleMovesToPos.containsKey(toPosition)){
            Set<Move> movesToPosition = possibleMovesToPos.get(toPosition);
            for (Move move : movesToPosition) {
                if (move.getMovedPiece().equals(piece)) {
                    return true; // Found a valid move for this piece to the target position
                }
            }
        }
        return false;
        
    }

    public String getName() {
        return name;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public boolean isAI() {
        return isAI;
    }

    public List<Move> getMoveHistory() {
        return moveHistory;
    }

    public void addMove(Move move) {
        moveHistory.add(move);
    }
    public void addPossibleMove(Move move) {
        possibleMoves.add(move);
        if (!possibleMovesToPos.containsKey(move.getToPosition())){
            possibleMovesToPos.put(move.getToPosition(), new HashSet<>());
        }
        possibleMovesToPos.get(move.getToPosition()).add(move);
    }

    public Map<String, Set<Move>> getPossibleMovesToPos() {
        return possibleMovesToPos;
    }
    public Set<Move> getPossibleMoves() {
        return possibleMoves;
    }

    public Set<Piece> getPiecesOnBoard() {
        return piecesOnBoard;
    }

    public void resetMoves(){
        possibleMoves.clear();
        possibleMovesToPos.clear();
    }
}
