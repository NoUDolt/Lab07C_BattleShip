import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;

public class Cell extends JButton implements ActionListener {
    private int row;
    private int col;
    private Ship ship;
    private GameBoard board;

    public Cell(int row, int col, GameBoard board) {
        this.row = row;
        this.col = col;
        this.board = board;
        setText("~");  // BLANK cell
        addActionListener(this);
        setBackground(Color.CYAN);  // Initial color for blank cells
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public GameBoard getBoard() {
        return board;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
        if (board.isPlayerBoard()) {
            setBackground(Color.GRAY);  // Show player's ships as gray cells
            setText("S");  // Optional: Label player's ships with "S"
        }
    }

    public boolean hasShip() {
        return ship != null;
    }

    public boolean isHit() {
        return getText().equals("X");
    }

    public boolean isMiss() {
        return getText().equals("M");
    }

    public void markHit() {
        setText("X");
        setBackground(Color.RED);
        if (ship != null) {
            ship.hit();
            if (ship.isSunk()) {
                JOptionPane.showMessageDialog(this, "Ship Sunk!");
            }
        }
    }

    public void markMiss() {
        setText("M");
        setBackground(Color.YELLOW);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (board.isPlayerBoard()) {
            board.getController().placeShip(this);
        } else {
            board.getController().playerMove(this);
        }
    }
}