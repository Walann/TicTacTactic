import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TicTacTactic extends JFrame implements ActionListener {
    private JButton[][] buttons;
    private JLabel statusLabel;
    private JLabel scoreLabel;
    private int[] scores = {0, 0};
    private boolean xTurn = true;
    private boolean gameEnded = false;
    private String playerXName = "Player X";
    private String playerOName = "AI";

    private JTextField playerXField;
    private JTextField playerOField;

    public TicTacTactic() {
        setTitle("Advanced Tic Tac Toe");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel settingsPanel = new JPanel(new GridLayout(5, 2));

        JLabel playerXLabel = new JLabel("Player X Name:");
        playerXField = new JTextField();
        settingsPanel.add(playerXLabel);
        settingsPanel.add(playerXField);

        JLabel playerOLabel = new JLabel("Player O Name:");
        playerOField = new JTextField();
        settingsPanel.add(playerOLabel);
        settingsPanel.add(playerOField);

        JRadioButton vsAIRadioButton = new JRadioButton("Play vs AI");
        JRadioButton vsPlayerRadioButton = new JRadioButton("Play vs Player");
        ButtonGroup group = new ButtonGroup();
        group.add(vsAIRadioButton);
        group.add(vsPlayerRadioButton);
        vsAIRadioButton.setSelected(true);
        settingsPanel.add(vsAIRadioButton);
        settingsPanel.add(vsPlayerRadioButton);

        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerXName = playerXField.getText();
                playerOName = playerOField.getText();
                if (vsAIRadioButton.isSelected()) {
                    playerOName = "AI";
                    playerOField.setEnabled(false);
                } else {
                    playerOField.setEnabled(true);
                }
                startGame();
            }
        });
        settingsPanel.add(startButton);

        add(settingsPanel, BorderLayout.NORTH);

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(3, 3));
        buttons = new JButton[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 40));
                buttons[i][j].addActionListener(this);
                boardPanel.add(buttons[i][j]);
            }
        }
        add(boardPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        statusLabel = new JLabel("Player X's Turn");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottomPanel.add(statusLabel);

        scoreLabel = new JLabel("Score: " + playerXName + " - " + scores[0] + "   " + playerOName + " - " + scores[1]);
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottomPanel.add(scoreLabel);

        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void startGame() {
        resetGame();
        if (!xTurn && playerOName.equals("AI")) {
            makeAIMove();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameEnded)
            return;

        JButton clickedButton = (JButton) e.getSource();
        if (clickedButton.getText().isEmpty()) {
            clickedButton.setText(xTurn ? "X" : "O");
            if (checkWin()) {
                String winner = xTurn ? playerXName : playerOName;
                statusLabel.setText(winner + " wins!");
                updateScore(xTurn);
                gameEnded = true;
                return;
            } else if (checkDraw()) {
                statusLabel.setText("Draw!");
                gameEnded = true;
                return;
            }
            if (!playerOName.equals("AI")) {
                xTurn = !xTurn;
                statusLabel.setText("Player " + (xTurn ? playerXName : playerOName) + "'s Turn");
            } else {
                makeAIMove();
                if (checkWin()) {
                    statusLabel.setText(playerOName + " wins!");
                    updateScore(false);
                    gameEnded = true;
                } else if (checkDraw()) {
                    statusLabel.setText("Draw!");
                    gameEnded = true;
                }
            }
        }
    }

    private void makeAIMove() {
        int[] move = getBestMove();
        buttons[move[0]][move[1]].setText("O");
    }

    private int[] getBestMove() {
        int[] bestMove = new int[]{-1, -1};
        int bestScore = Integer.MIN_VALUE;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().isEmpty()) {
                    buttons[i][j].setText("O");
                    int score = minimax(0, false);
                    buttons[i][j].setText("");
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove[0] = i;
                        bestMove[1] = j;
                    }
                }
            }
        }
        return bestMove;
    }

    private int minimax(int depth, boolean isMaximizing) {
        if (checkWin()) {
            return isMaximizing ? -1 : 1;
        }
        if (checkDraw()) {
            return 0;
        }

        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().isEmpty()) {
                    buttons[i][j].setText(isMaximizing ? "O" : "X");
                    int score = minimax(depth + 1, !isMaximizing);
                    buttons[i][j].setText("");
                    bestScore = isMaximizing ? Math.max(score, bestScore) : Math.min(score, bestScore);
                }
            }
        }
        return bestScore;
    }

    private boolean checkWin() {

        String[][] board = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = buttons[i][j].getText();
            }
        }
        for (int i = 0; i < 3; i++) {
            if (board[i][0].equals(board[i][1]) && board[i][0].equals(board[i][2]) && !board[i][0].isEmpty())
                return true;
            if (board[0][i].equals(board[1][i]) && board[0][i].equals(board[2][i]) && !board[0][i].isEmpty())
                return true;
        }
        if (board[0][0].equals(board[1][1]) && board[0][0].equals(board[2][2]) && !board[0][0].isEmpty())
            return true;
        if (board[0][2].equals(board[1][1]) && board[0][2].equals(board[2][0]) && !board[0][2].isEmpty())
            return true;
        return false;
    }

    private boolean checkDraw() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().isEmpty())
                    return false;
            }
        }
        return true;
    }

    private void updateScore(boolean xWon) {
        if (xWon)
            scores[0]++;
        else
            scores[1]++;
        scoreLabel.setText("Score: " + playerXName + " - " + scores[0] + "   " + playerOName + " - " + scores[1]);
    }

    private void resetGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }
        gameEnded = false;
        xTurn = true;
        statusLabel.setText("Player X's Turn");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TicTacTactic());
    }
}