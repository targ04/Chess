import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class Piece {
    private String type;       // e.g., "Pawn", "Knight", "Bishop"
    private boolean isWhite;   // True for White, False for Black
    private int rank;          // Determines relative importance (Pawn = 1, King = 100)
    private int x, y;          // Position on the board
    private ImageView icon;    // Visual representation of the piece
    private int ICON_SIZE = 60; // Size of the piece icon
    private boolean selected = false; // Indicates if the piece is selected
    private Board board; // Reference to the board for interaction

    // Constructor
    public Piece(String type, boolean isWhite, int x, int y, Board board) {
        this.type = type;
        this.isWhite = isWhite;
        this.rank = assignRank(type);
        this.x = x;
        this.y = y;
        this.icon = loadImage(); // Load the piece image
        addClickEvent();
        this.board = board; // Set the board reference
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


    private void addClickEvent() {
        icon.setOnMouseClicked(event -> handlePieceClick(event));
    }

    private void handlePieceClick(MouseEvent event) {
        // no other piece on the board can be selected
        if (board.getSelectedPiece() != null && board.getSelectedPiece() != this) {
            board.getSelectedPiece().deselect(); // Deselect the previously selected piece
        }
        else if (selected) {
            deselect(); // Deselect if the same piece is clicked again
            return;
        }
        // Select this piece
        selected = true;
        icon.setOpacity(0.6); // Highlight when selected
        board.setSelectedPiece(this); // Set this piece as the selected piece on the board
        System.out.println((isWhite ? "White " : "Black ") + type + " selected at (" + x + ", " + y + ")");
    }


    protected void deselect() {
        selected = false;
        icon.setOpacity(1.0); // Reset opacity when deselected
        board.setSelectedPiece(null);
        System.out.println((isWhite ? "White " : "Black ") + type + " deselected at (" + x + ", " + y + ")");
    }

    private ImageView loadImage() {
        String color = isWhite ? "white" : "black";
        String imagePath = "/resources/icons/" + color + "_" + type.toLowerCase() + ".png";
        Image pieceImage = new Image(getClass().getResource(imagePath).toString());
        ImageView imageView = new ImageView(pieceImage);
        imageView.setFitWidth(ICON_SIZE);
        imageView.setFitHeight(ICON_SIZE);
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
        selected = false; // Deselect after moving
        icon.setOpacity(1.0); // Reset opacity
    }

    // moveTo method with chess board notation as input
    public void moveTo(String position) {
        int newX = position.charAt(0) - 'A';
        int newY = board.getSize() - Character.getNumericValue(position.charAt(1));

        moveTo(newX, newY);
    }

    @Override
    public String toString() {
        return (isWhite ? "White " : "Black ") + type + " at (" + x + ", " + y + ")";
    }
}
