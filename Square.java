import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Square extends StackPane {
    private final String position; // e.g., "A1"
    private Piece piece;           // The piece currently on this square, or null
    private static final String WHITE_COLOR = "-fx-background-color:rgb(222, 182, 135);"; // Light brown
    private static final String BLACK_COLOR = "-fx-background-color:rgb(140, 68, 20);"; // Dark brown
    private static final Color INDICATOR_COLOR = Color.STEELBLUE; // Color for valid move indicators
    private boolean isWhite; // True if the square is white, false if black
    int TILE_SIZE = 80; // Size of each square
    private boolean isValidforNextMove = false; // Flag to indicate if the square is valid for the next move

    public Square(String position) {
        this.position = position;
        this.piece = null;
        setColor();
        setPrefSize(TILE_SIZE, TILE_SIZE); // Set the size of the square

        // add hover effect if the square is a valid square for the next move
        addHoverEffect();
    }

    private void addHoverEffect(){
        this.setOnMouseEntered(event -> {
            if (isValidforNextMove) {
                this.setCursor(Cursor.HAND); // Change cursor to hand
            }
            else {
                this.setCursor(Cursor.DEFAULT); // Reset cursor to default
            }
        });

        this.setOnMouseExited(event -> {
            this.setCursor(Cursor.DEFAULT); // Reset cursor to default
        });
        this.setOnMouseReleased(event -> {
            this.setCursor(Cursor.DEFAULT);
        });

    }

    private void setColor(){
        int row = position.charAt(1) - '1'; // Convert '1'-'8' to 0-7
        int col = position.charAt(0) - 'A'; // Convert 'A'-'H' to 0-7
        if ((row + col) % 2 == 1) {
            setStyle(WHITE_COLOR); // Light color
            isWhite = true; // This square is white
        } else {
            setStyle(BLACK_COLOR); // Dark color
            isWhite = false; // This square is black
        }
    }

    public String getPosition() {
        return position;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
        this.getChildren().add(piece.getIcon());
        piece.moveTo(position);
    }

    public boolean isOccupied() {
        return piece != null;
    }

    public void removePiece() {
        this.piece = null;
        this.getChildren().clear(); // Remove the piece icon from the square
    }

    // return whether the square is an opponent's piece
    public boolean isOpponentPiece(boolean isWhite) {
        return piece != null && piece.isWhite() != isWhite;
    }

    // set small circular indicator for valid moves
    public void setIndicator(){
        Circle indicator = new Circle(7, INDICATOR_COLOR);
        indicator.setStroke(INDICATOR_COLOR);
        indicator.setStrokeWidth(4);
        indicator.setOpacity(0.8);
        this.getChildren().add(indicator);
        StackPane.setAlignment(indicator, Pos.CENTER); // Center the indicator in the square
        isValidforNextMove = true; // Set the flag to indicate this square is valid for the next move
    }

    // remove all indicators from the square
    public void clearIndicator() {
        this.getChildren().removeIf(node -> node instanceof Circle); // Remove all indicators
        isValidforNextMove = false; // Reset the flag
    }

}