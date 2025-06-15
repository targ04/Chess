import javafx.scene.Cursor;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * Represents a chess piece (Pawn, Knight, Bishop, etc.)
 * Handles its behavior, movement, interaction, and appearance.
 */
public class Piece {
    private String type; // Piece type: Pawn, Knight, etc.
    private boolean isWhite; // True = white piece, False = black piece
    private int value; // Value used for prioritizing captures (e.g., Queen > Pawn)
    private int file, rank; // File = column (0-7), rank = row (0-7)
    private String position; // Chess notation (e.g., "E2")
    private ImageView icon; // GUI image for the piece
    private int ICON_SIZE = 60; // Icon size in pixels
    private boolean selected = false; // True if piece is selected
    private Board board; // Reference to the board for interaction

    private Set<Move> validMoves; // All legal moves for this piece
    private boolean hasMoved = false; // whether king has moved or not, checks for castling

    // Constructor to initialize all required fields and setup visuals
    public Piece(String type, boolean isWhite, int file, int rank, Board board) {
        this.type = type;
        this.isWhite = isWhite;
        this.value = assignValue(type);
        this.file = file;
        this.rank = rank;
        this.board = board;
        this.position = board.getChessCoordinate(rank, file);
        // this.is_selectable = board.isWhiteTurn() == isWhite;
        this.icon = loadImage(); // Load the correct piece image
        this.validMoves = new HashSet<>();

        addClickEvent(); // Allow clicking the piece
        addHoverEffect(); // Show hand cursor on hover
    }

    /**
     * Assigns a numeric value to the piece for prioritizing during AI or
     * evaluations.
     */
    private int assignValue(String type) {
        return switch (type) {
            case "Pawn" -> 1;
            case "Knight", "Bishop" -> 3;
            case "Rook" -> 5;
            case "Queen" -> 9;
            case "King" -> 100; // King is invaluable
            default -> 0;
        };
    }

    // Adds a hand cursor effect when the mouse hovers over a selectable piece
    private void addHoverEffect() {
    icon.setOnMouseEntered(e -> {
        icon.setCursor(isSelectable() ? Cursor.MOVE : Cursor.DEFAULT);
    });
    icon.setOnMouseExited(e -> icon.setCursor(Cursor.DEFAULT));
}

    // Attaches click logic to the piece
    private void addClickEvent() {
        icon.setOnMouseClicked(this::handlePieceClick);
    }

    /**
     * Handles what happens when a piece is clicked
     */
    private void handlePieceClick(MouseEvent event) {
        if (!isSelectable())
            return;

        if (board.getSelectedPiece() != null && board.getSelectedPiece() != this) {
            board.getSelectedPiece().deselect();
        } else if (selected) {
            deselect();
            return;
        }

        // Select this piece
        selected = true;
        showMoveIndicators(); // Show visual hints on valid target squares
        icon.setOpacity(0.6); // Make it look selected
        board.setSelectedPiece(this);

        System.out.println((isWhite ? "White " : "Black ") + type + " selected at " + position);
    }

    // Displays blue dots on all valid move squares
    private void showMoveIndicators() {
        for (Move move : validMoves) {
            board.getSquare(move.getToPosition()).setIndicator();
        }
    }

    // Clears any move indicators from the board
    private void clearMoveIndicators() {
        for (Move move : validMoves) {
            board.getSquare(move.getToPosition()).clearIndicator();
        }
    }

    /**
     * Deselects the piece, removing highlight and indicators
     */
    protected void deselect() {
        selected = false;
        clearMoveIndicators();
        icon.setOpacity(1.0); // Reset visual opacity
        board.setSelectedPiece(null);
        System.out.println((isWhite ? "White " : "Black ") + type + " deselected at " + position);
    }

    // Loads the correct image based on color and type (e.g., white_pawn.png)
    private ImageView loadImage() {
        String color = isWhite ? "white" : "black";
        String path = "/resources/icons/" + color + "_" + type.toLowerCase() + ".png";
        Image img = new Image(getClass().getResource(path).toString());
        ImageView imgView = new ImageView(img);
        imgView.setFitWidth(ICON_SIZE);
        imgView.setFitHeight(ICON_SIZE);
        imgView.setPreserveRatio(true);
        return imgView;
    }

    // ------------------ Move Calculations by Piece Type ------------------

    public void createNewMove(String toPosition, Piece targetPiece, boolean isEnPassant, boolean isCastling, boolean isPromotion) {
        Move move = new Move(position, toPosition, this, targetPiece, board, isEnPassant, isCastling, isPromotion);
        this.validMoves.add(move);
        if (isWhite) {
            board.getWhitePlayer().addPossibleMove(move);
        }
        else {
            board.getBlackPlayer().addPossibleMove(move);
        }
    }

    public void createNewMove(String toPosition, Piece targetPiece) {
        createNewMove(toPosition, targetPiece, false, false, false);
    }

    public void updateMoves() {
        validMoves.clear();
        switch (type) {
            case "Pawn" -> calculatePawnMoves();
            case "Knight" -> calculateKnightMoves();
            case "Bishop" -> calculateBishopMoves();
            case "Rook" -> calculateRookMoves();
            case "Queen" -> {
                calculateBishopMoves();
                calculateRookMoves();
            }
            case "King" -> calculateKingMoves();
        }
    }

    private void calculatePawnMoves() {
        int dir = isWhite ? 1 : -1;
        int startRank = isWhite ? 1 : 6;

        // calculate a single forward move
        Square fwdOne = board.getSquare(rank + dir, file);
        if (fwdOne != null && !fwdOne.isOccupied())
            createNewMove(fwdOne.getPosition(), null, false, false, false);

        // calculate two forward moves from starting position
        if (rank == startRank) {
            Square fwdTwo = board.getSquare(rank + 2 * dir, file);
            if (fwdTwo != null && !fwdTwo.isOccupied() && !fwdOne.isOccupied())
                createNewMove(fwdTwo.getPosition(), null);    
        }

        // Diagonal captures
        if (file > 0) {
            Square leftCap = board.getSquare(rank + dir, file - 1);
            if (leftCap != null && leftCap.isOpponentPiece(isWhite))
                createNewMove(leftCap.getPosition(), leftCap.getPiece());
        }

        if (file < 7) {
            Square rightCap = board.getSquare(rank + dir, file + 1);
            if (rightCap != null && rightCap.isOpponentPiece(isWhite))
                createNewMove(rightCap.getPosition(), rightCap.getPiece());
        }
    }

    private void calculateKnightMoves() {
        int[][] moves = {
                { 2, 1 }, { 2, -1 }, { -2, 1 }, { -2, -1 },
                { 1, 2 }, { 1, -2 }, { -1, 2 }, { -1, -2 }
        };

        for (int[] m : moves) {
            int newFile = file + m[0], newRank = rank + m[1];
            if (board.isValidSquare(newFile, newRank)) {
                Square sq = board.getSquare(newRank, newFile);
                if (!sq.isOccupied() || sq.isOpponentPiece(isWhite)) {
                    if (sq.isOpponentPiece(isWhite)) {
                        createNewMove(sq.getPosition(), sq.getPiece());
                    } else {
                        createNewMove(sq.getPosition(), null);
                    }
                }
            }
        }
    }

    private void calculateRookMoves() {
        calculateLinearMoves(1, 0); // down
        calculateLinearMoves(0, 1); // right
        calculateLinearMoves(-1, 0); // up
        calculateLinearMoves(0, -1); // left
    }

    private void calculateBishopMoves() {
        calculateLinearMoves(-1, -1); // up-left
        calculateLinearMoves(-1, 1); // up-right
        calculateLinearMoves(1, -1); // down-left
        calculateLinearMoves(1, 1); // down-right
    }

    private void calculateKingMoves() {
        int[][] moves = {
                { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 },
                { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }
        };

        for (int[] move : moves) {
            int newF = file + move[0], newR = rank + move[1];
            if (board.isValidSquare(newF, newR)) {
                Square sq = board.getSquare(newR, newF);
                if (!sq.isOccupied() || sq.isOpponentPiece(isWhite))
                    if (sq.isOpponentPiece(isWhite)) {
                        createNewMove(sq.getPosition(), sq.getPiece());
                    } else {
                        createNewMove(sq.getPosition(), null);
                    }
            }
        }
        // ---------------- Castling ----------------
        if (hasMoved)
            return; // King has moved

        // Kingside Castling
        if (canCastle(file, rank, 7)) {
            createNewMove(board.getChessCoordinate(rank, 6), null); // f1/f8
        }

        // Queenside Castling
        if (canCastle(file, rank, 0)) {
            createNewMove(board.getChessCoordinate(rank, 2), null); // c1/c8
        }
    }

    private boolean canCastle(int kingFile, int kingRank, int rookFile) {
        Piece rook = board.getPieceAt(kingRank, rookFile);
        if (rook == null || !rook.getType().equals("Rook") || rook.hasMoved())
            return false;

        int direction = (rookFile == 0) ? -1 : 1;
        int start = kingFile + direction;
        int end = rookFile;

        // Check squares between king and rook are empty
        for (int f = start; f != end; f += direction) {
            if (board.getPieceAt(kingRank, f) != null)
                return false;
        }

        // TODO: Check if king is in check or crosses check
        // Skipped for now

        return true;
    }

    /**
     * Handles movement along straight or diagonal paths (used by rook, bishop,
     * queen)
     */
    private void calculateLinearMoves(int dR, int dF) {
        int newR = rank + dR;
        int newF = file + dF;
        while (board.isValidSquare(newF, newR)) {
            Square sq = board.getSquare(newR, newF);
            if (sq.isOccupied()) {
                if (sq.isOpponentPiece(isWhite))
                    createNewMove(sq.getPosition(), sq.getPiece());
                break;
            }
            createNewMove(sq.getPosition(), null);
            newR += dR;
            newF += dF;
        }
    }

    private void promotePawn(String newType) {
        this.type = newType;
        this.value = assignValue(newType);
        this.icon.setImage(new Image(getClass().getResource(
                "/resources/icons/" + (isWhite ? "white" : "black") + "_" + newType.toLowerCase() + ".png")
                .toString()));

        System.out.println((isWhite ? "White" : "Black") + " pawn promoted to " + newType + " at " + position);
    }

    // give choice to user for pawn promotion
    private void promotePawnWithChoice() {
        List<String> choices = List.of("Queen", "Rook", "Bishop", "Knight");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("Queen", choices);
        dialog.setTitle("Pawn Promotion");
        dialog.setHeaderText("Choose a piece to promote your pawn to:");
        dialog.setContentText("Promote to:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(this::promotePawn); // Call promotePawn with user choice
    }

    // ------------------ Movement & Metadata ------------------

    public void moveTo(int newF, int newR) {
        this.file = newF;
        this.rank = newR;
        this.position = board.getChessCoordinate(newR, newF);
        // Check for promotion
        if (type.equals("Pawn") && (rank == 7 || rank == 0)) {
            promotePawnWithChoice();
        }
        System.out.println(board.getFEN());
    }

    public void moveTo(String pos) {
        this.file = pos.charAt(0) - 'A';
        this.rank = Character.getNumericValue(pos.charAt(1)) - 1;
        this.position = pos;
        // Check for promotion
        if (type.equals("Pawn") && (rank == 7 || rank == 0)) {
            promotePawnWithChoice();
        }
        System.out.println(board.getFEN());

    }

    // ------------------ Getters ------------------

    public String getType() {
        return type;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public int getValue() {
        return value;
    }

    public int getRank() {
        return rank;
    }

    public int getFile() {
        return file;
    }

    public String getPosition() {
        return position;
    }

    public Set<Move> getValidMoves() {
        return validMoves;
    }

    public ImageView getIcon() {
        return icon;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setMoved(boolean moved) {
        this.hasMoved = moved;
    }

    @Override
    public String toString() {
        return (isWhite ? "White " : "Black ") + type + " at " + position;
    }

    // Returns piece symbol in FEN notation
    Character getFENChar() {
        return switch (type) {
            case "Pawn" -> isWhite ? 'P' : 'p';
            case "Knight" -> isWhite ? 'N' : 'n';
            case "Bishop" -> isWhite ? 'B' : 'b';
            case "Rook" -> isWhite ? 'R' : 'r';
            case "Queen" -> isWhite ? 'Q' : 'q';
            case "King" -> isWhite ? 'K' : 'k';
            default -> '?';
        };
    }

    public boolean isSelectable() {
        return isWhite == board.isWhiteTurn(); // Only allow selecting pieces of current turn
    }
}
