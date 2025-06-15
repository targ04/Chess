import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private boolean isWhite; // True if playing as White
    private boolean isAI; // True if this player is an AI
    private List<Move> moveHistory; // List of moves made by this player

    public Player(String name, boolean isWhite) {
        this.name = name;
        this.isWhite = isWhite;
        // Decide AI status based on name or other logic
        this.isAI = name.toLowerCase().contains("ai") || name.toLowerCase().contains("computer");
        this.moveHistory = new ArrayList<>();
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
}
