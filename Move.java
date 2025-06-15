public class Move {
    private final String fromPosition; // e.g., "E2"
    private final String toPosition;   // e.g., "E4"
    private final Piece movedPiece;
    private final Piece capturedPiece; // null if no capture
    private final boolean isEnPassant;
    private final boolean isCastling;
    private final boolean isPromotion;

    public Move(String fromPosition, String toPosition, Piece movedPiece,
                Piece capturedPiece, boolean isEnPassant, boolean isCastling, boolean isPromotion) {
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.isEnPassant = isEnPassant;
        this.isCastling = isCastling;
        this.isPromotion = isPromotion;
    }

    public Move(String fromPosition, String toPosition, Piece movedPiece,
                Piece capturedPiece) {
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.isEnPassant = false;
        this.isCastling = false;
        this.isPromotion = false;
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