import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.HashMap;

public class Board {
    private static final int SIZE = 8;
    private static final int TILE_SIZE = 80;
    private GridPane gridPane;
    private HashMap<String, StackPane> squares; // Map squares by chess coordinates (A1, B2, etc.)

    public Board() {
        gridPane = new GridPane();
        squares = new HashMap<>();
        initializeBoard();
        placePieces();
    }

    private void initializeBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                String position = getChessCoordinate(row, col);
                Rectangle square = new Rectangle(TILE_SIZE, TILE_SIZE);
                square.setFill((row + col) % 2 == 0 ? Color.BURLYWOOD : Color.SADDLEBROWN);
                StackPane cell = new StackPane();
                cell.getChildren().add(square);
                squares.put(position, cell); // Store in HashMap
                gridPane.add(cell, col, row);
            }
        }
    }

    private void placePieces() {
        // Place Pawns
        for (int col = 0; col < SIZE; col++) {
            addPiece(new Piece("Pawn", true, col, 6)); // White Pawns
            addPiece(new Piece("Pawn", false, col, 1)); // Black Pawns
        }

        // Place Rooks
        addPiece(new Piece("Rook", true, 0, 7));
        addPiece(new Piece("Rook", true, 7, 7));
        addPiece(new Piece("Rook", false, 0, 0));
        addPiece(new Piece("Rook", false, 7, 0));

        // Place Knights
        addPiece(new Piece("Knight", true, 1, 7));
        addPiece(new Piece("Knight", true, 6, 7));
        addPiece(new Piece("Knight", false, 1, 0));
        addPiece(new Piece("Knight", false, 6, 0));

        // Place Bishops
        addPiece(new Piece("Bishop", true, 2, 7));
        addPiece(new Piece("Bishop", true, 5, 7));
        addPiece(new Piece("Bishop", false, 2, 0));
        addPiece(new Piece("Bishop", false, 5, 0));

        // Place Queens
        addPiece(new Piece("Queen", true, 3, 7));
        addPiece(new Piece("Queen", false, 3, 0));

        // Place Kings
        addPiece(new Piece("King", true, 4, 7));
        addPiece(new Piece("King", false, 4, 0));
    }


    private void addPiece(Piece piece) {
        StackPane square = (StackPane) gridPane.getChildren().get(piece.getY() * SIZE + piece.getX());
        square.getChildren().add(piece.getIcon());
    }

    
    // Converts grid coordinates to chess notation (A1, B2, etc.)
    private String getChessCoordinate(int row, int col) {
        char file = (char) ('A' + col); // A-H for columns
        int rank = SIZE - row;         // 1-8 for rows (flipped for correct chess orientation)
        return "" + file + rank;
    }

    // Get square by its chess notation
    public StackPane getSquare(String position) {
        return squares.get(position);
    }

    public GridPane getBoardLayout() {
        return gridPane;
    }
}