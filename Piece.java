import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import java.util.Set;
import java.util.HashSet;

public class Piece {
    private String type;       // type of piece (Pawn, Knight, Bishop, Rook, Queen, King)
    private boolean isWhite;   // True for White, False for Black
    private int value;          // Determines relative importance 
    private int file, rank;          // Position on the board (x is file, y is rank)
    private String position; // Position in chess notation (e.g., "A1", "B2")
    private ImageView icon;    // Visual representation of the piece
    private int ICON_SIZE = 60; // Size of the piece icon
    private boolean selected = false; // Indicates if the piece is selected
    private Board board; // Reference to the board for interaction

    // set of all valid moves for current position
    private Set<String> validMoves;

    // Constructor
    public Piece(String type, boolean isWhite, int file, int rank, Board board) {
        this.type = type;
        this.isWhite = isWhite;
        this.value = assignValue(type);
        this.file = file;
        this.rank = rank;
        this.icon = loadImage(); // Load the piece image
        addClickEvent();
        this.board = board; // Set the board reference
        this.position = board.getChessCoordinate(rank, file); // Convert to chess notation

        validMoves = new HashSet<>(); // Initialize valid moves set
        updateMoves(); // Initialize valid moves

    }

    // Assign rank values based on piece type
    private int assignValue(String type) {
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
        updateMoves();
        icon.setOpacity(0.6); // Highlight when selected
        board.setSelectedPiece(this); // Set this piece as the selected piece on the board
        System.out.println((isWhite ? "White " : "Black ") + type + " selected at " + position);
    }


    protected void deselect() {
        selected = false;
        icon.setOpacity(1.0); // Reset opacity when deselected
        board.setSelectedPiece(null);
        System.out.println((isWhite ? "White " : "Black ") + type + " deselected at " + position);
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
    public void updateMoves() {
        validMoves.clear();
        switch (type) {
            case "Pawn" -> calculatePawnMoves();
            case "Knight" -> calculateKnightMoves();

            case "Bishop" -> calculateBishopMoves();
            case "Rook" -> calculateRookMoves();
            case "Queen" -> {calculateBishopMoves(); calculateRookMoves();}
            case "King" -> calculateKingMoves();
            default -> calculatePawnMoves();

        };
    }

    void calculatePawnMoves() {
        int direction = isWhite ? -1 : 1; // white moves up (-1), black down
        int startRank = isWhite ? 6 : 1; // Starting rank for pawns
        Square fwdOne = board.getSquare(rank + direction, file);
        if (!fwdOne.isOccupied()) validMoves.add(fwdOne.getPosition()); // Move forward one square
        
        if (rank == startRank) { // If on starting rank, can move two squares forward
            Square fwdTwo = board.getSquare(rank + 2 * direction, file);
            if (!fwdTwo.isOccupied() && !fwdOne.isOccupied()) validMoves.add(fwdTwo.getPosition());
        }

        // capture diagonally
        if (file > 0) { // Check left diagonal
            Square leftCapture = board.getSquare(rank + direction, file - 1);
            if (leftCapture.isOccupied() && leftCapture.isOpponentPiece(isWhite)) {
                validMoves.add(leftCapture.getPosition());
            }
        }

        if (file < 7) { // Check right diagonal
            Square rightCapture = board.getSquare(rank + direction, file + 1);
            if (rightCapture.isOccupied() && rightCapture.isOpponentPiece(isWhite)) {
                validMoves.add(rightCapture.getPosition());
            }
        }

    }

    void calculateKnightMoves() {
        // Knight moves in an "L" shape: 2 squares in one direction and 1 square perpendicular
        int[][] knightMoves = {
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] move : knightMoves) {
            int newFile = file + move[0];
            int newRank = rank + move[1];
            if (board.isValidSquare(newFile, newRank)) {
                Square targetSquare = board.getSquare(newRank, newFile);
                if (!targetSquare.isOccupied() || targetSquare.isOpponentPiece(isWhite)) {
                    validMoves.add(targetSquare.getPosition());
                }
            }
        }
    }
    
    void calculateRookMoves() {
        calculateLinearMoves(1, 0); // down
        calculateLinearMoves(0, 1); // right
        calculateLinearMoves(-1, 0); // up
        calculateLinearMoves(0, -1); // left
    }

    void calculateLinearMoves(int dRank, int dFile) {
        
        int newRank = rank + dRank;
        int newFile = file + dFile;
        while (board.isValidSquare(newFile, newRank)){
            Square targetSquare = board.getSquare(newRank, newFile);
            if (targetSquare.isOccupied()) {
                if (targetSquare.isOpponentPiece(isWhite)) {
                    validMoves.add(targetSquare.getPosition()); // Capture
                }
                break; // Stop if occupied
            }
            validMoves.add(targetSquare.getPosition()); // Add empty square
            newRank += dRank;
            newFile += dFile;
        }
    }
    
    void calculateBishopMoves() {
        calculateLinearMoves(-1, -1);
        calculateLinearMoves(-1, 1);
        calculateLinearMoves(1, -1);
        calculateLinearMoves(1, 1);
    }

    void calculateKingMoves() {
        int[][] kingMoves = {
            {1, 0}, {0, 1}, {-1, 0}, {0, -1}, // Horizontal and vertical
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1} // Diagonal
        };

        for (int[] move : kingMoves) {
            int newFile = file + move[0];
            int newRank = rank + move[1];
            if (board.isValidSquare(newFile, newRank)) {
                Square targetSquare = board.getSquare(newRank, newFile);
                if (!targetSquare.isOccupied() || targetSquare.isOpponentPiece(isWhite)) {
                    validMoves.add(targetSquare.getPosition());
                }
            }
        }
    }

    // Update position
    public void moveTo(int new_F, int new_R) {
        this.file = new_F;
        this.rank = new_R;
        selected = false; // Deselect after moving
        icon.setOpacity(1.0); // Reset opacity
        this.position = board.getChessCoordinate(new_R, new_F); // Update position in chess notation
        updateMoves();
    }

    // moveTo method with chess board notation as input
    public void moveTo(String position) {
        int newX = position.charAt(0) - 'A';
        int newY = board.getSize() - Character.getNumericValue(position.charAt(1));

        moveTo(newX, newY);
    }


    // Getters
    public String getType() { return type; }
    public boolean isWhite() { return isWhite; }
    public int getValue() { return rank; }
    public int getRank() { return rank; }
    public int getFile() { return file; }
    public String getPosition() { return position; }
    public Set<String> getValidMoves() { return validMoves; }

    @Override
    public String toString() {
        return (isWhite ? "White " : "Black ") + type + " at " + position;
    }
}
