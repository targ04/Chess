import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Game class represents the main entry point of the Chess GUI application.
 * It starts with a difficulty selector and launches the board on clicking
 * "Play".
 */
public class Game extends Application {
    private Stage primaryStage;

    /**
     * JavaFX start method, runs when the application launches.
     * Sets up the initial UI where the user selects the difficulty and starts the
     * game.
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Set the title of the game window
        primaryStage.setTitle("Chess Game");

        // Difficulty Selector Dropdown
        ComboBox<String> difficultyBox = new ComboBox<>();
        difficultyBox.getItems().addAll("Easy", "Medium", "Hard"); // Difficulty options
        difficultyBox.setValue("Medium"); // Default selection

        // Play Button
        Button playButton = new Button("Play");
        // When clicked, it starts the game with the selected difficulty
        playButton.setOnAction(e -> startGame(difficultyBox.getValue()));

        // Vertical layout container for the dropdown and button
        VBox layout = new VBox(20, difficultyBox, playButton);
        layout.setStyle("-fx-alignment: center; -fx-padding: 50;");

        // Set the scene to the menu screen and show the window
        primaryStage.setScene(new Scene(layout, 400, 300));
        primaryStage.show();
    }

    /**
     * Initializes the player, AI, and board based on selected difficulty,
     * then transitions to the main game board view.
     *
     * @param difficulty the difficulty level selected by the user
     */
    private void startGame(String difficulty) {
        // Create the player (assumed white)
        Player player = new Player("User", false); // false = White

        // Create the AI opponent with chosen difficulty
        AI ai = new AI(difficulty);

        // Create the board (handles layout, piece setup, etc.)
        Board board = new Board(player, ai);

        // Output basic game info to console
        System.out.println(player.getName() + " is playing as " + (player.isWhite() ? "White" : "Black"));
        System.out.println(ai.makeMove(board)); // AI makes an initial move (if implemented)

        // Display the board on a new scene
        primaryStage.setScene(new Scene(board.getBoardLayout(), 640, 640));
        primaryStage.setResizable(false); // Prevent window resizing
        primaryStage.setTitle("Chess Game - " + difficulty + " Mode");
    }

    /**
     * Main method launches the JavaFX application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
