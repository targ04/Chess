import javafx.scene.layout.GridPane;
import java.util.HashMap;

public class Board {
    private static final int SIZE = 8; // board size
    private GridPane gridPane;
    private HashMap<String, Square> squares; // Map squares by chess coordinates (A1, B2, etc.)
    private Piece selectedPiece = null; // Currently selected piece, if any

    public Board() {
        gridPane = new GridPane();
        squares = new HashMap<>();
        initializeBoard();
        placePieces();
    }

    private void initializeBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                // get the chess coordinate and create a square, and add to gridPane
                String position = getChessCoordinate(row, col);
                Square cell = new Square(position);
                
                // allow click functionality on each square
                cell.setOnMouseClicked(event -> handleSquareClick(position));
                
                squares.put(position, cell); // Store in HashMap
                gridPane.add(cell, col, row);
            }
        }
    }

    private void placePieces() {
        // Place Pawns for both white and black players
        for (int col = 0; col < SIZE; col++) {
            addPiece(new Piece("Pawn", true, col, 6, this)); // White Pawns
            addPiece(new Piece("Pawn", false, col, 1, this)); // Black Pawns
        }

        // Place Rooks
        addPiece(new Piece("Rook", true, 0, 7, this));
        addPiece(new Piece("Rook", true, 7, 7, this));
        addPiece(new Piece("Rook", false, 0, 0, this));
        addPiece(new Piece("Rook", false, 7, 0, this));

        // Place Knights
        addPiece(new Piece("Knight", true, 1, 7, this));
        addPiece(new Piece("Knight", true, 6, 7, this));
        addPiece(new Piece("Knight", false, 1, 0, this));
        addPiece(new Piece("Knight", false, 6, 0, this));

        // Place Bishops
        addPiece(new Piece("Bishop", true, 2, 7, this));
        addPiece(new Piece("Bishop", true, 5, 7, this));
        addPiece(new Piece("Bishop", false, 2, 0, this));
        addPiece(new Piece("Bishop", false, 5, 0, this));

        // Place Queens
        addPiece(new Piece("Queen", true, 3, 7, this));
        addPiece(new Piece("Queen", false, 3, 0, this));

        // Place Kings
        addPiece(new Piece("King", true, 4, 7, this));
        addPiece(new Piece("King", false, 4, 0, this));
    }


    private void handleSquareClick(String position){
        if (selectedPiece == null) return; // No piece selected

        Square oldSquare = getSquare(selectedPiece.getY(), selectedPiece.getX());
        Square newSquare = getSquare(position);

        if (newSquare == oldSquare) return;

        else if (newSquare.isOccupied() && newSquare.getPiece().isWhite() == selectedPiece.isWhite()) {
            return; // Cannot move to a square occupied by own color
        }

        // move piece to new square
        selectedPiece.moveTo(position);

        // remove the icon from old square
        oldSquare.removePiece();
        newSquare.setPiece(selectedPiece); 

        System.out.println(selectedPiece.getType() + " to " + position);

        // Deselect piece after moving
        selectedPiece.deselect();

    }

    private void addPiece(Piece piece) {
        // add the piece to the board at its position
        Square square = getSquare(getChessCoordinate(piece.getY(), piece.getX()));
        square.setPiece(piece);

    }

    public void setSelectedPiece(Piece piece) {
        selectedPiece = piece;
    }

    
    // Converts grid coordinates to chess notation (A1, B2, etc.)
    private String getChessCoordinate(int row, int col) {
        char file = (char) ('A' + col); // A-H for columns
        int rank = SIZE - row;         // 1-8 for rows (flipped for correct chess orientation)
        return "" + file + rank;
    }

    // Get square by its chess notation
    public Square getSquare(String position) {
        return squares.get(position);
    }

    public Square getSquare(int row, int col) {
        return squares.get(getChessCoordinate(row, col));
    }

    public GridPane getBoardLayout() {
        return gridPane;
    }

    public Piece getSelectedPiece() {
        return selectedPiece;
    }

    public int getSize() {
        return SIZE;
    }
}