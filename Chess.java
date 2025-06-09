import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Chess extends Application {
    private Stage primaryStage;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Welcome to Chess");

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

        primaryStage.setScene(new Scene(layout, 400, 300));
        primaryStage.show();
    }

    private void startGame(String difficulty) {
        Board board = new Board();
        AI ai = new AI(difficulty);
        Player player = new Player("User", true);

        System.out.println(player.getName() + " is playing as White.");
        System.out.println(ai.makeMove(board)); // AI makes a move

        primaryStage.setScene(new Scene(board.getBoardLayout(), 640, 640));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
