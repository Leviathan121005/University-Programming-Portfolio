import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class GomokuGameFX extends Application {
    // Declare GUI main variables
    private static final int boxSize = 40;
    private static final int boardSize = 16;
    private static final int boardLength = 16 * 40;
    private GomokuGame game;
    // To create blocks in the game grid
    private static StackPane createBlock() {
        StackPane block = new StackPane();
        block.setPrefSize(boxSize, boxSize);
        block.setStyle("-fx-background-color: #FFF2D7;" + "-fx-border-color: black;" + "-fx-border-width: 1.5;");
        return block;
    }
    // To create sections in the labels and buttons grid
    private static StackPane createSection() {
        StackPane section = new StackPane();
        section.setPrefSize(200, 106);
        return section;
    }
    @Override
    public void start(Stage stage) {

        // Declare the main grid divided into left (large one for the game grid) and right (smaller one for labels & buttons)
        GridPane containerGrid = new GridPane();
        containerGrid.getColumnConstraints().add(new ColumnConstraints(640));
        containerGrid.getColumnConstraints().add(new ColumnConstraints(200));
        // Create two grids for the game and for the labels & buttons
        StackPane gameGrid = new StackPane();
        GridPane labelsButtonsGrid = new GridPane();
        // Make the outer and inner border width of the game grid similar
        gameGrid.setStyle("-fx-border-width: 1.5;" + "-fx-border-color: black;");
        // Add the game grid and label & buttons grid into the main grid
        containerGrid.add(gameGrid, 0, 0);
        containerGrid.add(labelsButtonsGrid, 1, 0);

        // Organize the game grid
        // Create two types of grid for block grid and circle grid
        GridPane blockGrid = new GridPane();
        GridPane circleGrid = new GridPane();
        // Create a circle 2D array to store and control the circles
        Circle[][] circles = new Circle[boardSize - 1][boardSize - 1];
        // Loop to fill the block grid and circle grid
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                // Fill the block grid
                StackPane block = createBlock();
                blockGrid.add(block, j, i);
                // Fill the circle grid and store the circle into the "circles" array
                if (i > 0 && j > 0) {
                    StackPane invisibleBlock = new StackPane();
                    invisibleBlock.setPrefSize(boxSize, boxSize);
                    Circle circle = new Circle(0.4 * boxSize);
                    // All circles are set to be invisible initially
                    circle.setVisible(false);
                    invisibleBlock.getChildren().add(circle);
                    circles[i - 1][j - 1] = circle;
                    circleGrid.add(invisibleBlock, j - 1, i - 1);
                }
            }
        }
        // Place both block and circle grids into the game grid
        gameGrid.getChildren().add(blockGrid);
        gameGrid.getChildren().add(circleGrid);
        // Translate the circle grid to make the circles' position be on the blocks' intersections
        circleGrid.setTranslateX(17);
        circleGrid.setTranslateY(17);

        // Organize the labels & buttons grid
        // Create 6 sections namely to display the board size, current player's turn, current turns, the winner of the game, start a new game button, and exit button
        StackPane[] sections = new StackPane[6];
        for (int i = 0; i < 6; i++) {
            sections[i] = createSection();
        }
        // Create the labels & buttons variables
        Label gameBoardSize = new Label("Board Size : " + (boardSize - 1) + " x " + (boardSize - 1));
        Label gameCurrentPlayer = new Label();
        Label gameTurn = new Label("");
        Label gameWinner = new Label();
        Button startNewGame = new Button("Start a New Game");
        Button exit = new Button("Exit");
        // Assign font for the labels' & buttons' text
        Font font = new Font("Brawler", 20);
        gameBoardSize.setFont(font);
        gameTurn.setFont(font);
        gameCurrentPlayer.setFont(font);
        gameWinner.setFont(font);
        startNewGame.setFont(font);
        exit.setFont(font);
        // Add some modifications for the "gameWinner" label
        gameWinner.setWrapText(true);
        gameWinner.setPadding(new Insets(12));
        gameWinner.setTextAlignment(TextAlignment.CENTER);
        gameWinner.setStyle("-fx-font-weight: bold");
        // Place the labels & buttons into the sections pane
        sections[0].getChildren().add(gameBoardSize);
        sections[1].getChildren().add(gameCurrentPlayer);
        sections[2].getChildren().add(gameTurn);
        sections[3].getChildren().add(gameWinner);
        sections[4].getChildren().add(startNewGame);
        sections[5].getChildren().add(exit);
        // Place the sections into the labels & buttons grid
        for (int i = 0; i < 6; i++) {
            labelsButtonsGrid.add(sections[i], 0,  i);
        }

        // Event handler for when the "startNewGame" button is clicked
        startNewGame.setOnAction(event -> {
            // Create new GomokuGame variable
            game = new GomokuGame(boardSize);
            // Set texts for the game
            gameTurn.setText("Turn : 1");
            gameCurrentPlayer.setText("Player " + game.getCurrentPlayer() + "'s " + "turn");
            gameWinner.setText("");
            // Reset the circles visibility into false
            for (int i = 0; i < boardSize - 1; i++) {
                for (int j = 0; j < boardSize - 1; j++) {
                    circles[i][j].setVisible(false);
                }
            }
            // Event handler for when the invisible circle is clicked
            circleGrid.setOnMouseClicked(e -> {
                // Get the StackPane position of the clicked coordinates
                int x = (int) (e.getX() / boxSize);
                int y = (int) (e.getY() / boxSize);
                // Find the distance of the clicked coordinates in reference to the center of the circle located in the StackPane
                double xc = (e.getX() % boxSize) - ((double) boxSize / 2);
                double yc = (e.getY() % boxSize) - ((double) boxSize / 2);
                // Make a move only if the clicked coordinates is in region of the circle in the StackPane
                if (xc*xc + yc*yc <= 0.16 * boxSize * boxSize) {
                    // Store the game's current player as it will change after move is called
                    int currentPlayer = game.getCurrentPlayer();
                    if (game.move(x, y)) {
                        // Show the circle
                        if (y < 15 && x < 15) {
                            circles[y][x].setVisible(true);
                        }
                        // Determine the color of the circle by the player who clicked the circle
                        if (currentPlayer == 1) {
                            circles[y][x].setFill(Color.BLACK);
                            circles[y][x].setStroke(Color.WHITE);
                        }
                        else if (currentPlayer == 2) {
                            circles[y][x].setStroke(Color.BLACK);
                            circles[y][x].setFill(Color.WHITE);
                        }
                        // Declare game over with its result
                        if (game.isGameOver()) {
                            if (game.getWinner() == 0) {
                                gameWinner.setText("Game over! It's a draw!");
                            }
                            else {
                                gameWinner.setText("Game over! The winner is Player " + game.getWinner() + "!");
                            }
                        }
                        // Only change the turn number and current player if the game is not yet over
                        if (!game.isGameOver()) {
                            gameTurn.setText("Turn : " + ((game.getTotalMoves() / 2) + 1));
                            gameCurrentPlayer.setText("Player " + game.getCurrentPlayer() + "'s " + "Turn");
                        }
                    }
                }
                else {
                    System.out.println("Invalid move!");
                }
            });
        });

        // Event handler for when the exit button is clicked
        exit.setOnAction(e -> System.exit(0));

        // Create scene, place it into stage, and show it
        Scene scene = new Scene(containerGrid, boardLength + 200, boardLength);
        stage.setTitle("Gomoku Game");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
