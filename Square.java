import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Square class represents a single tile on the chess board.
 * It handles piece placement, styling, hover effects, and move indicators.
 */
public class Square extends StackPane {
    private final String position; // Chess coordinate like "A1", "E4", etc.
    private Piece piece; // The piece currently occupying this square
    private static final String WHITE_COLOR = "-fx-background-color:rgb(222, 182, 135);"; // Light brown
    private static final String BLACK_COLOR = "-fx-background-color:rgb(140, 68, 20);"; // Dark brown
    private static final Color INDICATOR_COLOR = Color.STEELBLUE; // Color for valid move indicators

    private boolean isWhite; // True if square is a white tile
    private final int TILE_SIZE = 80; // Size (in px) of each square
    private boolean isValidforNextMove = false; // Indicates if this square is valid for a potential move

    /**
     * Constructor initializes the square's position, background color,
     * size, and adds hover interactivity.
     */
    public Square(String position) {
        this.position = position;
        this.piece = null;
        setColor(); // Sets tile color based on its coordinates
        setPrefSize(TILE_SIZE, TILE_SIZE); // Set visual size

        addHoverEffect(); // Adds cursor change when hovered
    }

    /**
     * Adds a visual cursor effect when hovering over a valid move square.
     */
    private void addHoverEffect() {
        this.setOnMouseEntered(event -> {
            // If the square is a valid destination, change cursor
            if (isValidforNextMove) {
                this.setCursor(Cursor.HAND);
            } else {
                this.setCursor(Cursor.DEFAULT);
            }
        });

        // Always reset the cursor on exit or mouse release
        this.setOnMouseExited(event -> this.setCursor(Cursor.DEFAULT));
        this.setOnMouseReleased(event -> this.setCursor(Cursor.DEFAULT));
    }

    /**
     * Sets the background color of the square based on its position
     * to alternate between white and black tiles.
     */
    private void setColor() {
        int row = position.charAt(1) - '1'; // e.g., '1' → 0
        int col = position.charAt(0) - 'A'; // e.g., 'A' → 0

        // Color alternates like a checkerboard
        if ((row + col) % 2 == 1) {
            setStyle(WHITE_COLOR);
            isWhite = true;
        } else {
            setStyle(BLACK_COLOR);
            isWhite = false;
        }
    }

    // --------------------- Getters & Setters -----------------------

    public String getPosition() {
        return position;
    }

    public Piece getPiece() {
        return piece;
    }

    /**
     * Places a piece on this square and updates visuals.
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
        this.getChildren().clear(); // Clear any previous visuals

        if (piece == null)
            return; // Do not add null

        this.getChildren().add(piece.getIcon()); // Add the piece icon
        piece.moveTo(position); // Update the piece's position state
    }

    /**
     * Returns true if this square contains a piece.
     */
    public boolean isOccupied() {
        return piece != null;
    }

    /**
     * Removes any piece from this square (both logic and GUI).
     */
    public void removePiece() {
        this.piece = null;
        this.getChildren().clear(); // Also remove the visual icon
    }

    /**
     * Checks if this square contains an opponent's piece.
     * 
     * @param isWhite current player's color
     * @return true if occupied by an opponent
     */
    public boolean isOpponentPiece(boolean isWhite) {
        return piece != null && piece.isWhite() != isWhite;
    }

    // --------------------- Visual Indicators -----------------------

    /**
     * Displays a circular dot in the center to show a valid move.
     */
    public void setIndicator() {
        Circle indicator = new Circle(7, INDICATOR_COLOR); // Dot radius and color
        indicator.setStroke(INDICATOR_COLOR); // Outline
        indicator.setStrokeWidth(4);
        indicator.setOpacity(0.8);

        this.getChildren().add(indicator); // Add to square
        StackPane.setAlignment(indicator, Pos.CENTER); // Center it visually

        isValidforNextMove = true; // Mark this square as a valid move
    }

    /**
     * Clears any move indicator visuals from the square.
     */
    public void clearIndicator() {
        this.getChildren().removeIf(node -> node instanceof Circle);
        isValidforNextMove = false; // Reset the flag
    }
}
