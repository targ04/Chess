import javafx.scene.layout.GridPane;
import java.util.HashMap;

/**
 * The Board class represents the chess board and manages all its logic,
 * including square setup, piece placement, movement, and FEN generation.
 */
public class Board {
    private static final int SIZE = 8; // Standard chess board is 8x8
    private GridPane gridPane; // JavaFX layout for GUI representation
    private HashMap<String, Square> squares; // Map of positions (e.g. "E2") to Square objects
    private Piece selectedPiece = null; // Currently selected piece for movement
    private Player whitePlayer;
    private Player blackPlayer;
    private Game game;
    private boolean isWhiteTurn = true; // Track whose turn it is

    // Castling rights
    private boolean canWhiteCastleKingside = true;
    private boolean canWhiteCastleQueenside = true;
    private boolean canBlackCastleKingside = true;
    private boolean canBlackCastleQueenside = true;

    // En passant state
    private Square enPassantTargetSquare = null;

    // Clock for 50-move rule and full move count
    private int halfMoveClock = 0;
    private int fullMoveNumber = 1;

    /**
     * Constructor initializes the board with player and AI references,
     * builds the grid and places pieces.
     */
    public Board(Player whitePlayer, Player blackPlayer, Game game) {
        this.whitePlayer = whitePlayer; // Default to white, can be changed for turn logic
        this.blackPlayer = blackPlayer; // Default to black, can be changed for turn logic
        this.gridPane = new GridPane();
        this.squares = new HashMap<>();
        this.game = game;
        initializeBoard();
        placePieces();
    }

    /**
     * Sets up the gridPane with Square objects and binds click events.
     */
    private void initializeBoard() {
        for (int file = 0; file < SIZE; file++) {
            for (int rank = 0; rank < SIZE; rank++) {
                String position = getChessCoordinate(rank, file); // E.g., "A1"
                Square cell = new Square(position);

                // Allow user interaction on square
                cell.setOnMouseClicked(event -> handleSquareClick(position));

                squares.put(position, cell); // Save in map
                gridPane.add(cell, file, SIZE - rank - 1); // GUI positioning
            }
        }
    }

    /**
     * Adds all chess pieces to their standard initial positions.
     */
    private void placePieces() {
        // Pawns
        for (int col = 0; col < SIZE; col++) {
            addPiece(new Piece("Pawn", false, col, 6, this)); // White
            addPiece(new Piece("Pawn", true, col, 1, this)); // Black
        }

        // Rooks
        addPiece(new Piece("Rook", false, 0, 7, this));
        addPiece(new Piece("Rook", false, 7, 7, this));
        addPiece(new Piece("Rook", true, 0, 0, this));
        addPiece(new Piece("Rook", true, 7, 0, this));

        // Knights
        addPiece(new Piece("Knight", false, 1, 7, this));
        addPiece(new Piece("Knight", false, 6, 7, this));
        addPiece(new Piece("Knight", true, 1, 0, this));
        addPiece(new Piece("Knight", true, 6, 0, this));

        // Bishops
        addPiece(new Piece("Bishop", false, 2, 7, this));
        addPiece(new Piece("Bishop", false, 5, 7, this));
        addPiece(new Piece("Bishop", true, 2, 0, this));
        addPiece(new Piece("Bishop", true, 5, 0, this));

        // Queens
        addPiece(new Piece("Queen", false, 3, 7, this));
        addPiece(new Piece("Queen", true, 3, 0, this));

        // Kings
        addPiece(new Piece("King", false, 4, 7, this));
        addPiece(new Piece("King", true, 4, 0, this));
    }

    /**
     * Handles click events on a square for selecting and moving a piece.
     */
    private void handleSquareClick(String position) {
        if (selectedPiece == null)
            return; // No piece selected yet

        Square oldSquare = getSquare(selectedPiece.getRank(), selectedPiece.getFile());
        Square newSquare = getSquare(position);

        if (newSquare == oldSquare)
            return; // Clicked same square
        if (newSquare.isOccupied() && newSquare.getPiece().isWhite() == selectedPiece.isWhite())
            return; // Same-color capture

        if (!selectedPiece.getValidMoves().contains(position))
            return; // Invalid move

        // Move piece visually and logically
        // create a move
        Move move = new Move(oldSquare.getPosition(), position, selectedPiece, newSquare.getPiece());
        oldSquare.removePiece();
        newSquare.setPiece(selectedPiece);

        System.out.println(selectedPiece.getType() + " to " + position);

        selectedPiece.deselect(); // Deselect after move
    }

    /**
     * Adds a piece to its corresponding square.
     */
    private void addPiece(Piece piece) {
        Square square = getSquare(piece.getPosition());
        square.setPiece(piece);
    }

    /**
     * Updates the selected piece (used by Square or Piece interaction).
     */
    public void setSelectedPiece(Piece piece) {
        selectedPiece = piece;
    }

    /**
     * Generates a FEN (Forsyth–Edwards Notation) string representing current board
     * state.
     */
    public String getFEN() {
        StringBuilder fen = new StringBuilder();

        // Piece placement
        for (int rank = SIZE - 1; rank >= 0; rank--) {
            int emptyCount = 0;
            for (int file = 0; file < SIZE; file++) {
                Square square = getSquare(rank, file);
                if (square.isOccupied()) {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    fen.append(square.getPiece().getFENChar());
                } else {
                    emptyCount++;
                }
            }
            if (emptyCount > 0)
                fen.append(emptyCount);
            if (rank > 0)
                fen.append('/');
        }

        // Active color
        fen.append(' ').append(isWhiteTurn ? 'w' : 'b');

        // Castling rights
        fen.append(' ');
        if (canWhiteCastleKingside)
            fen.append('K');
        if (canWhiteCastleQueenside)
            fen.append('Q');
        if (canBlackCastleKingside)
            fen.append('k');
        if (canBlackCastleQueenside)
            fen.append('q');
        if (fen.charAt(fen.length() - 1) == ' ')
            fen.append('-');

        // En passant target
        fen.append(' ');
        fen.append(enPassantTargetSquare != null ? enPassantTargetSquare.getPosition() : "-");

        // Half-move clock and full move number
        fen.append(' ').append(halfMoveClock).append(' ').append(fullMoveNumber);

        return fen.toString();
    }

    /**
     * Converts board indices to chess notation like "A1", "B3", etc.
     */
    protected String getChessCoordinate(int rank, int file) {
        char f = (char) ('A' + file); // Convert file to letter
        int r = 1 + rank; // Convert rank to 1-based index
        return "" + f + r;
    }

    /**
     * Validates if a given file and rank are within the board boundaries.
     */
    public boolean isValidSquare(int file, int rank) {
        return file >= 0 && file < SIZE && rank >= 0 && rank < SIZE;
    }

    /**
     * Get a square using chess notation like "E4".
     */
    public Square getSquare(String position) {
        return squares.get(position);
    }

    /**
     * Get a square using file and rank.
     */
    public Square getSquare(int rank, int file) {
        return squares.get(getChessCoordinate(rank, file));
    }

    /**
     * Returns the JavaFX grid layout for display in the UI.
     */
    public GridPane getBoardLayout() {
        return gridPane;
    }

    /**
     * Returns the currently selected piece.
     */
    public Piece getSelectedPiece() {
        return selectedPiece;
    }

    /**
     * Returns board size (always 8).
     */
    public int getSize() {
        return SIZE;
    }

    /**
     * Checks if the current player is white.
     */
    // public boolean isPlayerWhite() {
    //     return player.isWhite();
    // }

    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }

    public Player getPlayerTurn() {
        return isWhiteTurn ? whitePlayer : blackPlayer;
    }

    public Piece getPieceAt(int file, int rank) {
        if (!isValidSquare(file, rank))
            return null;
        Square sq = getSquare(rank, file); // Note: rank, file order here is important
        return sq.isOccupied() ? sq.getPiece() : null;
    }


    public void setEnPassantTargetSquare(Square square) {
        this.enPassantTargetSquare = square;
    }
    public Square getEnPassantTargetSquare() {
        return enPassantTargetSquare;
    }
}
