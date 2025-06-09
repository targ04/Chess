import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Piece {
    private String type;       // e.g., "Pawn", "Knight", "Bishop"
    private boolean isWhite;   // True for White, False for Black
    private int rank;          // Determines relative importance (Pawn = 1, King = 100)
    private int x, y;          // Position on the board
    private ImageView icon;    // Visual representation of the piece

    // Constructor
    public Piece(String type, boolean isWhite, int x, int y) {
        this.type = type;
        this.isWhite = isWhite;
        this.rank = assignRank(type);
        this.x = x;
        this.y = y;
        this.icon = loadImage(); // Load the piece image
    }

    // Assign rank values based on piece type
    private int assignRank(String type) {
        return switch (type) {
            case "Pawn" -> 1;
            case "Knight", "Bishop" -> 3;
            case "Rook" -> 5;
            case "Queen" -> 9;
            case "King" -> 100; // The most valuable piece
            default -> 0;
        };
    }

    private ImageView loadImage() {
        String color = isWhite ? "white" : "black";
        String imagePath = "/resources/icons/" + color + "_" + type.toLowerCase() + ".png";
        Image pieceImage = new Image(getClass().getResource(imagePath).toString());
        ImageView imageView = new ImageView(pieceImage);
        imageView.setFitWidth(70); // Adjust size to fit tiles
        imageView.setFitHeight(70);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    public ImageView getIcon() {
        // ICONS are loading properly
        // System.out.println(icon);
        return icon;
    }

    // Get possible moves based on piece type
    public String getPossibleMoves() {
        return switch (type) {
            case "Pawn" -> "Forward 1 or 2 squares (first move), diagonal captures.";
            case "Knight" -> "L-shaped moves (2+1 or 1+2 in any direction).";
            case "Bishop" -> "Diagonal moves.";
            case "Rook" -> "Vertical and horizontal moves.";
            case "Queen" -> "Combination of Rook and Bishop moves.";
            case "King" -> "1 square in any direction.";
            default -> "Unknown piece.";
        };
    }

    // Getters
    public String getType() { return type; }
    public boolean isWhite() { return isWhite; }
    public int getRank() { return rank; }
    public int getX() { return x; }
    public int getY() { return y; }

    // Update position
    public void moveTo(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    @Override
    public String toString() {
        return (isWhite ? "White " : "Black ") + type + " at (" + x + ", " + y + ")";
    }
}
