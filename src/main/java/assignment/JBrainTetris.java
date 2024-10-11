package assignment;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JBrainTetris extends JTetris{
    LameBrain lameBrain;
    RotationBrain brainRot;
    BigBrain bigBrain;
    MainBrain test;
    BrainMain main;

    JBrainTetris() {
        setPreferredSize(new Dimension(WIDTH*PIXELS+2, (HEIGHT+TOP_SPACE)*PIXELS+2));
        gameOn = false;

        board = new TetrisBoard(WIDTH, HEIGHT + TOP_SPACE);
        lameBrain = new LameBrain();
        brainRot = new RotationBrain();
        bigBrain = new BigBrain();
        test = new MainBrain();
        main = new BrainMain();

        timer = new javax.swing.Timer(DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick(Board.Action.DOWN);
            }
        });

        /*registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick(Board.Action.DROP);
                System.out.println(test.scoreBoard(board));
            }
            }, "drop", KeyStroke.getKeyStroke('w'), WHEN_IN_FOCUSED_WINDOW);*/
        timer = new javax.swing.Timer(0, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick(main.nextMove(board));
            }
        });
    }

    public static void main(String[] args) {
        createGUI(new JBrainTetris());
    }
}
