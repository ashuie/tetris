package assignment;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JBrainTetris extends JTetris{
    BigBrain bigBrain;

    JBrainTetris() {
        gameOn = false;

        board = new TetrisBoard(WIDTH, HEIGHT + TOP_SPACE);
        bigBrain = new BigBrain();

        // initialize tick down to lower piece
        timer = new javax.swing.Timer(DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick(Board.Action.DOWN);
            }
        });

        timer = new javax.swing.Timer(0, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick(bigBrain.nextMove(board));
            }
        });
    }

    public static void main(String[] args) {
        createGUI(new JBrainTetris());
    }
}
