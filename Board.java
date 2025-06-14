import javafx.scene.layout.GridPane;
import java.util.HashMap;

public class Board {
    private static final int SIZE = 8; // board size
    private GridPane gridPane;
    private HashMap<String, Square> squares; // Map squares by chess coordinates (A1, B2, etc.)
    private Piece selectedPiece = null; // Currently selected piece, if any
    private Player player; // Player object to manage player state
    private AI ai;

    private boolean canWhiteCastleKingside = true;
    private boolean canWhiteCastleQueenside = true;
    private boolean canBlackCastleKingside = true;
    private boolean canBlackCastleQueenside = true;
    private Square enPassantTargetSquare = null;

    private int halfMoveClock = 0; // Half-move clock for the fifty-move rule
    private int fullMoveNumber = 1; // Full move number, starts at 1


    

    public Board(Player player, AI ai) {
        // need to perform following in order
        this.player = player;
        this.ai = ai;
        this.gridPane = new GridPane();
        this.squares = new HashMap<>();
        initializeBoard();
        placePieces();
    }

    private void initializeBoard() {
        for (int file = 0; file < SIZE; file++) {
            for (int rank = 0; rank < SIZE ; rank++) {
                // get the chess coordinate and create a square, and add to gridPane
                String position = getChessCoordinate(rank, file);
                Square cell = new Square(position);
                
                // allow click functionality on each square
                cell.setOnMouseClicked(event -> handleSquareClick(position));
                
                squares.put(position, cell); // Store in HashMap
                gridPane.add(cell, file, SIZE - rank - 1); // squares are displayed bottom to top
            }
        }
    }

    private void placePieces() {
        // Place Pawns for both white and black players
        for (int col = 0; col < SIZE; col++) {
            addPiece(new Piece("Pawn", false, col, 6, this)); // White Pawns
            addPiece(new Piece("Pawn", true, col, 1, this)); // Black Pawns
        }

        // Place Rooks
        addPiece(new Piece("Rook", false, 0, 7, this));
        addPiece(new Piece("Rook", false, 7, 7, this));
        addPiece(new Piece("Rook", true, 0, 0, this));
        addPiece(new Piece("Rook", true, 7, 0, this));

        // Place Knights
        addPiece(new Piece("Knight", false, 1, 7, this));
        addPiece(new Piece("Knight", false, 6, 7, this));
        addPiece(new Piece("Knight", true, 1, 0, this));
        addPiece(new Piece("Knight", true, 6, 0, this));

        // Place Bishops
        addPiece(new Piece("Bishop", false, 2, 7, this));
        addPiece(new Piece("Bishop", false, 5, 7, this));
        addPiece(new Piece("Bishop", true, 2, 0, this));
        addPiece(new Piece("Bishop", true, 5, 0, this));

        // Place Queens
        addPiece(new Piece("Queen", false, 3, 7, this));
        addPiece(new Piece("Queen", true, 3, 0, this));

        // Place Kings
        addPiece(new Piece("King", false, 4, 7, this));
        addPiece(new Piece("King", true, 4, 0, this));
    }


    private void handleSquareClick(String position){
        if (selectedPiece == null) return; // No piece selected

        Square oldSquare = getSquare(selectedPiece.getRank(), selectedPiece.getFile());
        Square newSquare = getSquare(position);

        if (newSquare == oldSquare) return;

        else if (newSquare.isOccupied() && newSquare.getPiece().isWhite() == selectedPiece.isWhite()) {
            return; // Cannot move to a square occupied by own color
        }

        if (!selectedPiece.getValidMoves().contains(position)) return;

        // move piece from old square to new square
        oldSquare.removePiece();
        newSquare.setPiece(selectedPiece); 

        System.out.println(selectedPiece.getType() + " to " + position);

        // Deselect piece after moving
        selectedPiece.deselect();

    }

    // add the piece to the board at its position
    private void addPiece(Piece piece) {
        Square square = getSquare(piece.getPosition());
        square.setPiece(piece);
    }

    public void setSelectedPiece(Piece piece) {
        selectedPiece = piece;
    }

    public String getFEN() {
        StringBuilder fen = new StringBuilder();
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
            if (emptyCount > 0) {
                fen.append(emptyCount);
            }
            if (rank > 0) {
                fen.append('/');
            }
        }

        // now add player color and castling rights
        fen.append(' ').append(player.isWhite() ? 'w' : 'b'); // Player to move
        fen.append(' ');
        if (canWhiteCastleKingside) {
            fen.append('K');
        }
        if (canWhiteCastleQueenside) {
            fen.append('Q');
        }
        if (canBlackCastleKingside) {
            fen.append('k');
        }
        if (canBlackCastleQueenside) {
            fen.append('q');
        }
        if (fen.charAt(fen.length() - 1) == ' ') {
            fen.append('-'); // No castling rights
        }

        fen.append(' ');
        if (enPassantTargetSquare != null) {
            fen.append(enPassantTargetSquare.getPosition());
        } else {
            fen.append('-'); // No en passant target
        }

        fen.append(' ').append(halfMoveClock).append(' ').append(fullMoveNumber); // Half-move clock and full move number


        return fen.toString();
    }
    
    // Converts grid coordinates to chess notation (A1, B2, etc.)
    protected String getChessCoordinate(int rank, int file) {
        char f = (char) ('A' + file); // A-H for columns
        int r = 1 + rank;         // 1-8 for rows 
        return "" + f + r;
    }

    public boolean isValidSquare(int file, int rank) {
        return file >= 0 && file < SIZE && rank >= 0 && rank < SIZE;
    }

    // Get square by its chess notation
    public Square getSquare(String position) {
        return squares.get(position);
    }

    public Square getSquare(int rank, int file) {
        return squares.get(getChessCoordinate(rank, file));
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

    public boolean isPlayerWhite() {
        return player.isWhite();
    }
}