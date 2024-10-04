package assignment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.awt.Point;

public class TetrisBoardTest {

    TetrisBoard board;

    @Test
    public void testBoardZeroSize() {
        board = new TetrisBoard(-1, -1);
        Assertions.assertThrows(IllegalArgumentException.class,
                ()->{
                    board.nextPiece(new TetrisPiece(Piece.PieceType.STICK), new Point(0, 0));
                });
    }

    @Test
    public void testBoardSpawnPiece() {
        board = new TetrisBoard(4, 4);
        board.nextPiece(new TetrisPiece(Piece.PieceType.LEFT_L), new Point(0, 1));
        Assertions.assertEquals(new TetrisPiece(Piece.PieceType.LEFT_L), board.getCurrentPiece());
        Assertions.assertEquals(new Point(0, 1), board.getCurrentPiecePosition());
        /*HashSet<Point> expectedGrid = new HashSet<>();
        expectedGrid.add(new Point(0, 2));
        expectedGrid.add(new Point(1, 2));
        expectedGrid.add(new Point(2, 2));
        expectedGrid.add(new Point(2, 3));
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                System.out.println("i : " + i + " j : " + j + " board : " + board.getGrid(i, j));
                if (expectedGrid.contains(new Point(i, j))) {
                    Assertions.assertEquals(board.getGrid(i, j), Piece.PieceType.LEFT_L);
                } else {
                    Assertions.assertNull(board.getGrid(i, j));
                }
            }
        }*/
    }
}
