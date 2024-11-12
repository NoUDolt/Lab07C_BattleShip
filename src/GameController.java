import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Random;

public class GameController {
    private GameBoard playerBoard;
    private GameBoard aiBoard;
    private int playerMissCounter = 0;
    private int playerHitCounter = 0;
    private int aiMissCounter = 0;
    private int aiHitCounter = 0;
    private final int MAX_SHIPS = 5;
    private boolean placingShips = true;

    private JFrame frame;
    private JLabel playerMissLabel;
    private JLabel playerHitLabel;
    private JButton quitButton;
    private JButton playAgainButton;
    private JButton cheatButton; // Button to trigger automatic loss

    public GameController() {
        frame = new JFrame("Battleship");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout());

        // Initialize game boards for player and AI
        playerBoard = new GameBoard(this, true);
        aiBoard = new GameBoard(this, false);

        // Set up borders for each board to label them
        JPanel playerPanel = new JPanel(new BorderLayout());
        playerPanel.add(playerBoard, BorderLayout.CENTER);
        playerPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLUE, 2), "Player's Board", TitledBorder.CENTER, TitledBorder.TOP));

        JPanel aiPanel = new JPanel(new BorderLayout());
        aiPanel.add(aiBoard, BorderLayout.CENTER);
        aiPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.RED, 2), "AI's Board", TitledBorder.CENTER, TitledBorder.TOP));

        // Create a panel to hold both boards with separation between them
        JPanel boardsPanel = new JPanel(new GridLayout(1, 2, 20, 0)); // 20px horizontal gap between boards
        boardsPanel.add(playerPanel);
        boardsPanel.add(aiPanel);

        // Create a panel for controls and counters
        JPanel controlPanel = new JPanel(new GridLayout(5, 1, 0, 10)); // 10px vertical gap between controls
        playerMissLabel = new JLabel("Player Misses: 0");
        playerHitLabel = new JLabel("Player Hits: 0");
        quitButton = new JButton("Quit");
        playAgainButton = new JButton("Play Again");

        quitButton.addActionListener(e -> System.exit(0));
        playAgainButton.addActionListener(e -> resetGame());

        controlPanel.add(playerMissLabel);
        controlPanel.add(playerHitLabel);
        controlPanel.add(quitButton);
        controlPanel.add(playAgainButton);

        // Add all components to the frame
        frame.add(boardsPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.EAST);
        frame.setVisible(true);

        // Begin with player placing their own ships
        JOptionPane.showMessageDialog(frame, "Place your ships by clicking on your board.");
    }

    public void placeShip(Cell cell) {
        if (placingShips && playerBoard.getShips().size() < MAX_SHIPS) {
            Ship newShip = new Ship(new Random().nextInt(3) + 2); // Random ship size 2-4
            if (playerBoard.placeShipAt(cell.getRow(), cell.getCol(), newShip)) {
                if (playerBoard.getShips().size() == MAX_SHIPS) {
                    placingShips = false;
                    JOptionPane.showMessageDialog(frame, "Ships placed! Game starts.");
                    aiBoard.randomlyPlaceShips(); // AI places its ships
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid placement. Try again.");
            }
        }
    }

    public void playerMove(Cell cell) {
        if (!placingShips && cell.getBoard() == aiBoard) {
            if (cell.hasShip()) {
                cell.markHit();
                playerHitCounter++;
                playerHitLabel.setText("Player Hits: " + playerHitCounter);

                // Check if all AI ships are sunk for a win
                if (aiBoard.allShipsSunk()) {
                    JOptionPane.showMessageDialog(frame, "You Win!");
                    promptPlayAgain();
                    return; // Stop the game after winning
                }
            } else {
                cell.markMiss();
                playerMissCounter++;
                playerMissLabel.setText("Player Misses: " + playerMissCounter);
            }

            cell.setEnabled(false); // Disable cell after use

            // Now call aiMove() after each player move, whether it was a hit or miss
            aiMove();
        }
    }

    private void aiMove() {
        Random rand = new Random();
        Cell target;
        int gridSize = playerBoard.getGridSize(); // Use getGridSize() to obtain the board size
        do {
            int row = rand.nextInt(gridSize);
            int col = rand.nextInt(gridSize);
            target = playerBoard.getCell(row, col);
        } while (target.isHit() || target.isMiss());

        if (target.hasShip()) {
            target.markHit();
            aiHitCounter++;
            if (playerBoard.allShipsSunk()) {
                JOptionPane.showMessageDialog(frame, "AI Wins!");
                promptPlayAgain();
            }
        } else {
            target.markMiss();
            aiMissCounter++;
        }
    }

    // Method to prompt the player to play again after a game over
    private void promptPlayAgain() {
        int choice = JOptionPane.showConfirmDialog(frame, "Would you like to play again?",
                "Game Over", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            resetGame();
        } else {
            System.exit(0); // Exit the game if the player chooses not to play again
        }
    }

    // Method to reset the game
    private void resetGame() {
        playerMissCounter = 0;
        playerHitCounter = 0;
        aiMissCounter = 0;
        aiHitCounter = 0;
        playerBoard.reset();
        aiBoard.reset();
        placingShips = true;
        JOptionPane.showMessageDialog(frame, "Game reset. Place your ships.");
    }
}