import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameBoard extends JPanel {
    private final int GRID_SIZE = 10;
    private Cell[][] cells = new Cell[GRID_SIZE][GRID_SIZE];
    private List<Ship> ships = new ArrayList<>();
    private GameController controller;
    private boolean isPlayerBoard;

    public GameBoard(GameController controller, boolean isPlayerBoard) {
        this.controller = controller;
        this.isPlayerBoard = isPlayerBoard;
        setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));
        initializeBoard();
    }

    private void initializeBoard() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                cells[row][col] = new Cell(row, col, this);
                add(cells[row][col]);
            }
        }
    }

    public boolean placeShipAt(int row, int col, Ship ship) {
        if (canPlaceShip(ship, row, col, true)) { // Assuming horizontal for simplicity
            for (int i = 0; i < ship.getSize(); i++) {
                cells[row][col + i].setShip(ship);
            }
            ships.add(ship);
            return true;
        }
        return false;
    }

    // Check if the ship can be placed at the given location
    private boolean canPlaceShip(Ship ship, int row, int col, boolean horizontal) {
        if (horizontal && col + ship.getSize() > GRID_SIZE) return false;
        if (!horizontal && row + ship.getSize() > GRID_SIZE) return false;

        for (int i = 0; i < ship.getSize(); i++) {
            if (horizontal && cells[row][col + i].hasShip()) return false;
            if (!horizontal && cells[row + i][col].hasShip()) return false;
        }
        return true;
    }

    public void randomlyPlaceShips() {
        int[] shipSizes = {5, 4, 3, 3, 2};
        Random rand = new Random();
        for (int size : shipSizes) {
            Ship ship = new Ship(size);
            boolean placed = false;
            while (!placed) {
                int row = rand.nextInt(GRID_SIZE); // Ensure GRID_SIZE is an integer
                int col = rand.nextInt(GRID_SIZE); // Ensure GRID_SIZE is an integer
                boolean horizontal = rand.nextBoolean();
                placed = canPlaceShip(ship, row, col, horizontal) && placeShipAt(row, col, ship);
            }
            ships.add(ship);
        }
    }

    public boolean allShipsSunk() {
        return ships.stream().allMatch(Ship::isSunk);
    }

    public int getGridSize() {
        return GRID_SIZE;
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    public List<Ship> getShips() {
        return ships;
    }

    public void reset() {
        removeAll();
        initializeBoard();
        ships.clear();
        revalidate();
        repaint();
    }

    public GameController getController() {
        return controller;
    }

    public boolean isPlayerBoard() {
        return isPlayerBoard;
    }
}