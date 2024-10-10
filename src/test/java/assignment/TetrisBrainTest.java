package assignment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class TetrisBrainTest {
    JBrainTetris game;
    TestBrain brain;

    @BeforeEach
    public void newGame() {
        game = new JBrainTetris();
        brain = new TestBrain();
    }

    @Test
    // Test if each move the brain picks is a valid move
    public void testMoveValidity() {
        JTetris.createGUI(game);
        game.startGame();
        Assertions.assertFalse(TestBrain.outOfBounds(game.board, game.board.getCurrentPiecePosition().x,
                game.board.getCurrentPiecePosition().y));
    }

    @Test
    // Test if our brain will make an obviously good move or a bad move
    public void testDecision() throws NoSuchFieldException, IllegalAccessException {
        TetrisBoard board = new TetrisBoard(5, 5);
        board.grid = new Piece.PieceType[][] {{null, null, null, null, null},
                {Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, null},
                {Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, null},
                {Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, null},
                {Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, null}};

        Piece.PieceType[][] expectedGrid = new Piece.PieceType[][] {
                {Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, null},
                {Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, null},
                {Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, null},
                {Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, null},
                {Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, null}};

        board.nextPiece(new TetrisPiece(Piece.PieceType.STICK), new Point(0, 2));
        brain.nextMove(board);

        Field privateField3 = TestBrain.class.getDeclaredField("options");
        privateField3.setAccessible(true);
        ArrayList<Board> options = (ArrayList<Board>) privateField3.get(brain);

        int best = 0;
        int bestIndex = 0;
        for (int i = 0; i < options.size(); i++) {
            Board bestBoard = options.get(i);
            Piece.PieceType[][] bestGrid = new Piece.PieceType[5][5];
            for(int j = 0; j < bestBoard.getWidth(); ++j) {
                for(int k = 0; k < bestBoard.getHeight(); ++k) {
                    bestGrid[j][k] = bestBoard.getGrid(j, k);
                }
            }
            int score = brain.scoreBoard(options.get(i));
            if (score > best) {
                best = score;
                bestIndex = i;
            }
        }

        Board bestBoard = options.get(bestIndex);
        Piece.PieceType[][] bestGrid = new Piece.PieceType[5][5];
        for(int i = 0; i < bestBoard.getWidth(); ++i) {
            for(int j = 0; j < bestBoard.getHeight(); ++j) {
                bestGrid[i][j] = bestBoard.getGrid(i, j);
                Assertions.assertEquals(bestGrid[i][j], expectedGrid[i][j]);
            }
        }
    }

    @Test
    // Test if our brain will avoid certain death
    public void criticalMoveTest() throws NoSuchFieldException, IllegalAccessException {
        TetrisBoard board = new TetrisBoard(5, 5 + JTetris.TOP_SPACE);
        board.grid = new Piece.PieceType[][] {
                {Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, null, null, null, null},
                {Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, null, null, null, null, null},
                {Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, null, null, null, null, null},
                {Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, null, null, null, null, null},
                {Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, null, null, null, null, null}};

        Piece.PieceType[][] expectedGrid = new Piece.PieceType[][] {
                {Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, null, null, null, null},
                {Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, null, null, null, null},
                {Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, null, null, null, null},
                {Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, null, null, null, null},
                {Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, null, null, null, null}};

        board.nextPiece(new TetrisPiece(Piece.PieceType.STICK), new Point(0, 5));
        brain.nextMove(board);

        Field privateField3 = TestBrain.class.getDeclaredField("options");
        privateField3.setAccessible(true);
        ArrayList<Board> options = (ArrayList<Board>) privateField3.get(brain);

        int best = 0;
        int bestIndex = 0;
        for (int i = 0; i < options.size(); i++) {
            Board bestBoard = options.get(i);
            Piece.PieceType[][] bestGrid = new Piece.PieceType[5][5 + JTetris.TOP_SPACE];
            for(int j = 0; j < bestBoard.getWidth(); ++j) {
                for(int k = 0; k < bestBoard.getHeight(); ++k) {
                    bestGrid[j][k] = bestBoard.getGrid(j, k);
                }
            }
            int score = brain.scoreBoard(options.get(i));
            if (score > best) {
                best = score;
                bestIndex = i;
            }
        }

        Board bestBoard = options.get(bestIndex);
        Piece.PieceType[][] bestGrid = new Piece.PieceType[5][5 + JTetris.TOP_SPACE];
        for(int i = 0; i < bestBoard.getWidth(); ++i) {
            for(int j = 0; j < bestBoard.getHeight(); ++j) {
                bestGrid[i][j] = bestBoard.getGrid(i, j);
                Assertions.assertEquals(bestGrid[i][j], expectedGrid[i][j]);
            }
        }
    }
}