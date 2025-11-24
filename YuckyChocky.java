import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

/**
 * Class to create GUI and run the Yucky ChockyGame and Play it
 *
 * @author Abul Hasan
 * @StudentID: 202310535
 * 
 * @version javac 21.0.5
 */
public class YuckyChocky extends JFrame {
    private JPanel chocolatePanel;
    private boolean[][] chocolate;
    private int rows, cols;
    private boolean isPlayerTurn;
    private Random random = new Random();
    
    public YuckyChocky() {
        setTitle("Yucky Chocky Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setupStartScreen();
        pack();
        setLocationRelativeTo(null);
    }
    
    private void setupStartScreen() {
        getContentPane().removeAll();
        
        JPanel startPanel = new JPanel();
        startPanel.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));
        
        JLabel titleLabel = new JLabel("Choose Chocolate Bar Size:");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        startPanel.add(titleLabel);
        
        String[] sizes = {"4x4", "5x5", "6x6"};
        for (String size : sizes) {
            JButton sizeButton = new JButton(size);
            sizeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            sizeButton.addActionListener(e -> {
                int barSize = Integer.parseInt(size.substring(0, 1));
                startGame(barSize, barSize);
            });
            startPanel.add(sizeButton);
        }
        
        getContentPane().add(startPanel);
        pack();
    }
    
    private void startGame(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        chocolate = new boolean[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                chocolate[i][j] = true;
            }
        }
        
        isPlayerTurn = random.nextBoolean();
        
        setupGameBoard();

        if (!isPlayerTurn) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Computer goes first!");
                makeComputerMove();
            });
        } else {
            JOptionPane.showMessageDialog(this, "You go first!");
        }
    }
    
    private void setupGameBoard() {
        getContentPane().removeAll();
        
        chocolatePanel = new JPanel(new GridLayout(rows, cols));
        chocolatePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JButton square = createChocolateSquare(i, j);
                chocolatePanel.add(square);
            }
        }
        
        getContentPane().add(chocolatePanel);
        pack();
    }
    
    private JButton createChocolateSquare(int row, int col) {
        JButton square = new JButton();
        square.setPreferredSize(new Dimension(65, 65));
        square.setBackground(row == 0 && col == 0 ? Color.GREEN : new Color(139, 69, 19));
        square.setOpaque(true);
        
        if (chocolate[row][col]) {
            square.addActionListener(e -> handlePlayerMove(row, col));
        } 
        else {
            square.setEnabled(false);
            square.setBackground(Color.LIGHT_GRAY);
        }
        
        return square;
    }
    
    private void handlePlayerMove(int row, int col) {
        if (!isPlayerTurn || !isValidMove(row, col)) return;
        
        makeMove(row, col);
        
        if (isGameOver()) {
            endGame(true);
            return;
        }
        
        isPlayerTurn = false;
        if (!isPlayerTurn) {
            Timer timer = new Timer(1000, e -> makeComputerMove());
            timer.setRepeats(false);
            timer.start();
        }
    }
    
    private boolean isValidMove(int row, int col) {
        if (row != 0 || col != 0)
            return chocolate[row][col] && !(row > 0 && col > 0);
        
        return true;
    }
    
    private void makeMove(int row, int col) {
        for (int i = row; i < rows; i++) {
            for (int j = col; j < cols; j++) {
                chocolate[i][j] = false;
                updateBoard();
            }
        }    
    }
    
    private void makeComputerMove() {
        java.util.List<Point> validMoves = new java.util.ArrayList<>();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (isValidMove(i, j)) {
                    validMoves.add(new Point(i, j));
                }
            }
        }
        if (!validMoves.isEmpty()) {
            Point move = validMoves.get(random.nextInt(validMoves.size()));
            makeMove(move.x, move.y);
            
            if (isGameOver()) {
                endGame(false);
                return;
            }
            
            isPlayerTurn = true;
        }
    }
    
    private void updateBoard() {
        chocolatePanel.removeAll();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                chocolatePanel.add(createChocolateSquare(i, j));
            }
        }
        
        chocolatePanel.revalidate();
        chocolatePanel.repaint();
    }
    
    private boolean isGameOver() {
        return !chocolate[0][0];
    }
    
    private void endGame(boolean playerLost) {
        String message = playerLost ? "You lost! You ate the soap! Yuk!" : "You won! Computer ate the soap! Yuk!";
        int choice = JOptionPane.showConfirmDialog(
            this,
            message + "\nWould you like to play again?",
            "Game Over",
            JOptionPane.YES_NO_OPTION
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            setupStartScreen();
        } else {
            System.exit(0);
        }
    }
}