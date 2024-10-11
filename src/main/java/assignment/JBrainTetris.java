package assignment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JBrainTetris extends JTetris{
    FinalBrain bigBrain;
    javax.swing.Timer downTimer;

    JBrainTetris() {
        gameOn = false;

        board = new TetrisBoard(WIDTH, HEIGHT + TOP_SPACE);
        bigBrain = new FinalBrain();

        // initialize tick down to lower piece
        downTimer = new javax.swing.Timer(DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick(Board.Action.DOWN);
            }
        });

        timer = new javax.swing.Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tick(bigBrain.nextMove(board));
            }
        });
    }

    @Override
    public void startGame() {
        // cheap way to reset the board state
        super.startGame();
        downTimer.start();
    }

    public static void main(String[] args) {
        createGUI(new JBrainTetris());
    }
}
