package assignment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import java.awt.Point;

public class TetrisPieceTest {

    TetrisPiece stick;
    TetrisPiece t;
    TetrisPiece square;
    TetrisPiece leftl;
    TetrisPiece rightl;
    TetrisPiece leftdog;
    TetrisPiece rightdog;
    TetrisPiece[] allPieces;

    Point[][] tCWRotations = {{new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1,2)},
            {new Point(1, 2), new Point (1, 1), new Point(1, 0), new Point (2, 1)},
            {new Point(2, 1), new Point (1, 1), new Point (0,1), new Point(1,0)},
            {new Point(1, 0), new Point(1, 1), new Point (1,2), new Point(0,1) }};
    Point[][] squareCWRotations = {{new Point(0 ,0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
            {new Point(0, 1), new Point(1, 1), new Point(0, 0), new Point(1, 0)},
            {new Point(1, 1), new Point(1, 0), new Point(0,1), new Point(0, 0)},
            {new Point(1, 0), new Point(0, 0), new Point(1, 1), new Point(0, 1)}};
    Point[][] stickCWRotations = {{new Point(0, 2), new Point(1, 2), new Point(2, 2), new Point(3, 2)},
            {new Point(2, 3), new Point(2, 2), new Point(2, 1), new Point(2, 0)},
            {new Point(3, 1), new Point(2, 1), new Point(1, 1), new Point(0, 1)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)}};

    static final int NUM_ROTATIONS = 4;
    static final int NUM_PIECES = 7;
    static final int[] widths = new int[] {4, 2, 3, 3, 3, 3, 3};
    static final int[] heights = new int[] {4, 2, 3, 3, 3, 3, 3};

    @BeforeEach
    public void setUpPieces() {
        stick = new TetrisPiece(Piece.PieceType.STICK);
        leftl = new TetrisPiece(Piece.PieceType.LEFT_L);
        rightl = new TetrisPiece(Piece.PieceType.RIGHT_L);
        square = new TetrisPiece(Piece.PieceType.SQUARE);
        leftdog = new TetrisPiece(Piece.PieceType.LEFT_DOG);
        rightdog = new TetrisPiece(Piece.PieceType.RIGHT_DOG);
        t = new TetrisPiece(Piece.PieceType.T);
        allPieces = new TetrisPiece[] {stick, square, t, leftl, rightl, leftdog, rightdog};
    }

    @Test
    public void testPiecesSpawnBody() {
        Point[] tBody = new Point[] { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2) };
        Point[] squareBody = new Point[] { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) };
        Point[] stickBody = new Point[] { new Point(0, 2), new Point(1, 2), new Point(2, 2), new Point(3, 2) };
        Point[] leftlBody = new Point[] { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2) };
        Point[] rightlBody = new Point[] { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2) };
        Point[] leftdogBody = new Point[] { new Point(0, 2), new Point(1, 2), new Point(1, 1), new Point(2, 1) };
        Point[] rightdogBody = new Point[] { new Point(0, 1), new Point(1, 1), new Point(1, 2), new Point(2, 2) };

        Assertions.assertArrayEquals(tBody, t.getBody());
        Assertions.assertArrayEquals(squareBody, square.getBody());
        Assertions.assertArrayEquals(stickBody, stick.getBody());
        Assertions.assertArrayEquals(leftlBody, leftl.getBody());
        Assertions.assertArrayEquals(rightlBody, rightl.getBody());
        Assertions.assertArrayEquals(leftdogBody, leftdog.getBody());
        Assertions.assertArrayEquals(rightdogBody, rightdog.getBody());
    }

    @Test
    public void testGetType() {
        Assertions.assertEquals(Piece.PieceType.T, t.getType());
        Assertions.assertEquals(Piece.PieceType.SQUARE, square.getType());
        Assertions.assertEquals(Piece.PieceType.STICK, stick.getType());
        Assertions.assertEquals(Piece.PieceType.LEFT_L, leftl.getType());
        Assertions.assertEquals(Piece.PieceType.RIGHT_L, rightl.getType());
        Assertions.assertEquals(Piece.PieceType.LEFT_DOG, leftdog.getType());
        Assertions.assertEquals(Piece.PieceType.RIGHT_DOG, rightdog.getType());
    }

    @Test
    public void testGetRotationIndex() {
        for (int i = 0; i < NUM_ROTATIONS; i++) {
            for (int j = 0; j < NUM_PIECES; j++) {
                Assertions.assertEquals(i, allPieces[j].getRotationIndex());
                allPieces[j] = (TetrisPiece) allPieces[j].clockwisePiece();
            }
        }
    }

    @Test
    public void testPiecesSpawnSkirts() {
        int[] tSkirt = {1, 1, 1};
        int[] squareSkirt = {0, 0};
        int[] stickSkirt = {2, 2, 2, 2};
        int[] leftlSkirt = {1, 1, 1};
        int[] rightlSkirt = {1, 1, 1};
        int[] leftdogSkirt = {2, 1, 1};
        int[] rightdogSkirt = {1, 1, 2};
        Assertions.assertArrayEquals(tSkirt, t.getSkirt());
        Assertions.assertArrayEquals(squareSkirt, square.getSkirt());
        Assertions.assertArrayEquals(stickSkirt, stick.getSkirt());
        Assertions.assertArrayEquals(leftlSkirt, leftl.getSkirt());
        Assertions.assertArrayEquals(rightlSkirt, rightl.getSkirt());
        Assertions.assertArrayEquals(leftdogSkirt, leftdog.getSkirt());
        Assertions.assertArrayEquals(rightdogSkirt, rightdog.getSkirt());
    }

    @Test
    public void testPiecesGetWidth() {
        for (int i = 0; i < NUM_PIECES; i++) {
            Assertions.assertEquals(widths[i], allPieces[i].getWidth());
        }
    }

    @Test
    public void testPiecesGetHeight() {
        for (int i = 0; i < NUM_PIECES; i++) {
            Assertions.assertEquals(heights[i], allPieces[i].getHeight());
        }
    }

    @Test
    public void testPiecesCWRotation() {
        CircularLL tRotations = t.getRotations();
        CircularLL.Node currT = tRotations.getHead();
        CircularLL.Node headT = tRotations.getHead();

        CircularLL squareRotations = square.getRotations();
        CircularLL.Node currSquare = squareRotations.getHead();
        CircularLL.Node headSquare = squareRotations.getHead();

        CircularLL stickRotations = stick.getRotations();
        CircularLL.Node currStick = stickRotations.getHead();
        CircularLL.Node headStick = stickRotations.getHead();

        int i = 0;
        do {
            Assertions.assertArrayEquals(tCWRotations[i], currT.data.getBody());
            Assertions.assertArrayEquals(squareCWRotations[i], currSquare.data.getBody());
            Assertions.assertArrayEquals(stickCWRotations[i], currStick.data.getBody());
            currT = currT.next;
            currSquare = currSquare.next;
            currStick = currStick.next;

            i++;
        } while (currT != headT && currSquare != headSquare && currStick != headStick);
    }

    @Test
    public void testPiecesCCWRotation() {
        for (int i = 0; i < NUM_PIECES; i++) {
            Assertions.assertEquals(allPieces[i].clockwisePiece().clockwisePiece(), allPieces[i].counterclockwisePiece().counterclockwisePiece());
        }
    }

    @Test
    public void testPiecesEqual() {
        TetrisPiece tCWRotationOne = new TetrisPiece(Piece.PieceType.T);
        tCWRotationOne = (TetrisPiece) tCWRotationOne.clockwisePiece();
        Assertions.assertEquals(tCWRotationOne, t.clockwisePiece());
        Assertions.assertNotEquals(tCWRotationOne, t.counterclockwisePiece());
        Assertions.assertNotEquals(t, new TetrisPiece(Piece.PieceType.SQUARE));
        Assertions.assertNotEquals(t, new Object());
    }
}