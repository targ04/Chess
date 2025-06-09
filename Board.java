import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Board {
    private static final int SIZE = 8;
    private static final int TILE_SIZE = 80;
    private GridPane gridPane;

    // Constructor to initialize the board
    public Board() {
        gridPane = new GridPane();
        initializeBoard();
    }

    // Method to generate the chessboard
    private void initializeBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Rectangle square = new Rectangle(TILE_SIZE, TILE_SIZE);
                square.setFill((row + col) % 2 == 0 ? Color.BURLYWOOD : Color.SADDLEBROWN);
                gridPane.add(square, col, row);
            }
        }
    }

    // Getter for the board layout
    public GridPane getBoardLayout() {
        return gridPane;
    }
}
