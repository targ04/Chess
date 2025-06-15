public class Move {
    private final String fromPosition; // e.g., "E2"
    private final String toPosition;   // e.g., "E4"
    private final Piece movedPiece;
    private final Piece capturedPiece; // null if no capture
    private final boolean isEnPassant;
    private final boolean isCastling;
    private final boolean isPromotion;
    private Board board;

    public Move(String fromPosition, String toPosition, Piece movedPiece,
                Piece capturedPiece, Board board, boolean isEnPassant, boolean isCastling, boolean isPromotion) {
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.board = board;
        this.isEnPassant = isEnPassant;
        this.isCastling = isCastling;
        this.isPromotion = isPromotion;
    }

    public Move(String fromPosition, String toPosition, Piece movedPiece,
                Piece capturedPiece, Board board) {
        this(fromPosition, toPosition, movedPiece, capturedPiece, board, false, false, false);
    }

    public void execute(){
        Square fromSquare = board.getSquare(fromPosition);
        Square toSquare = board.getSquare(toPosition);

        // Handle capture if any
        if (capturedPiece != null) {
            toSquare.removePiece(); // Remove captured piece from the square
        }

        // Move the piece visually and logically
        fromSquare.removePiece();
        toSquare.setPiece(movedPiece);
        movedPiece.moveTo(toPosition);

        

        // Handle special moves like en passant, castling, or promotion if needed
        handleEnPassant();
        handleCastling();
        handlePromotion();
        // if (isEnPassant) {
        //     // Logic for en passant capture
        //     // ...
        // } else if (isCastling) {
        //     // Logic for castling move
        //     // ...
        // } else if (isPromotion) {
        //     // Logic for promotion (e.g., promote pawn to queen)
        //     // ...
        // }
    }

    private void handleEnPassant() {
        if (isEnPassant){
            
        }
    }

    private void handleCastling() {

    }

    private void handlePromotion() {

    }

    public String getFromPosition() {
        return fromPosition;
    }

    public String getToPosition() {
        return toPosition;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public boolean isEnPassant() {
        return isEnPassant;
    }

    public boolean isCastling() {
        return isCastling;
    }

    public boolean isPromotion() {
        return isPromotion;
    }
}