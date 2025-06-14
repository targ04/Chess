import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Game extends Application {
    private Stage primaryStage;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // this is the box title, change it to whatever you want
        // it will be displayed in the title bar of the window
        primaryStage.setTitle("Chess Game");

        // Difficulty Selector
        ComboBox<String> difficultyBox = new ComboBox<>();
        difficultyBox.getItems().addAll("Easy", "Medium", "Hard");
        difficultyBox.setValue("Medium");

        // Play Button
        Button playButton = new Button("Play");
        playButton.setOnAction(e -> startGame(difficultyBox.getValue()));

        // Layout
        VBox layout = new VBox(20, difficultyBox, playButton);
        layout.setStyle("-fx-alignment: center; -fx-padding: 50;");

        // opening page to the game
        primaryStage.setScene(new Scene(layout, 400, 300));
        primaryStage.show();
    }

    private void startGame(String difficulty) {
        // order of creation - Player, AI, Board
        Player player = new Player("User", false); // Player is White
        AI ai = new AI(difficulty);
        Board board = new Board(player, ai);
        

        System.out.println(player.getName() + " is playing as " + (player.isWhite() ? "White" : "Black"));
        System.out.println(ai.makeMove(board)); // AI makes a move
        // Piece knight = new Piece("Knight", true, 2, 1);
        // System.out.println(knight.getPossibleMoves());
        // System.out.println(knight);


        primaryStage.setScene(new Scene(board.getBoardLayout(), 640, 640));
        primaryStage.setResizable(false); // avoids resizing the window
        primaryStage.setTitle("Chess Game - " + difficulty + " Mode");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
