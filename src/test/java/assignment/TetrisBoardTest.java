package assignment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.awt.Point;
import java.util.Arrays;
import java.util.HashSet;

public class TetrisBoardTest {

    public static final int NUM_ROTATIONS = 4;
    public static final int NUM_TESTS = 5;
    public static final int CLOCKWISE_DIRECTION = 1;
    public static final int COUNTERCLOCKWISE_DIRECTION = 0;

    TetrisBoard board;
    TetrisPiece stick;
    TetrisPiece t;
    TetrisPiece square;
    TetrisPiece leftl;
    TetrisPiece rightl;
    TetrisPiece leftdog;
    TetrisPiece rightdog;
    TetrisPiece[] allPieces;
    TetrisPiece[] allWallKickPieces;

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
        allWallKickPieces = new TetrisPiece[] {stick, t, leftl, rightl, leftdog, rightdog};

    }

    @Test
    public void testBoardZeroSize() {
        board = new TetrisBoard(-1, -1);
        Assertions.assertThrows(IllegalArgumentException.class,
                ()->{
                    board.nextPiece(stick, new Point(0, 0));
                });
    }

    @Test
    public void testBoardSpawnPiece() {
        board = new TetrisBoard(4, 4);
        board.nextPiece(leftl, new Point(0, 1));
        Assertions.assertEquals(leftl, board.getCurrentPiece());
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

    @Test
    public void testBoardInvalidSpawnPiece() {
        board = new TetrisBoard(3, 3);
        Assertions.assertThrows(IllegalArgumentException.class,
                ()->{
                    board.nextPiece(stick, new Point(0, 0));
                });
    }

    @Test
    public void testBoardMoveLeft() {
        board = new TetrisBoard(5, 5);
        board.nextPiece(leftdog, new Point(1,1));
        board.move(Board.Action.LEFT);

        Assertions.assertEquals(Board.Result.SUCCESS, board.getLastResult());
        Assertions.assertEquals(new Point(0, 1), board.getCurrentPiecePosition());
    }

    @Test
    public void testBoardMoveRight() {
        board = new TetrisBoard(5, 5);
        board.nextPiece(rightdog, new Point(1,1));
        board.move(Board.Action.RIGHT);

        Assertions.assertEquals(Board.Result.SUCCESS, board.getLastResult());
        Assertions.assertEquals(new Point(2, 1), board.getCurrentPiecePosition());
    }

    @Test
    public void testBoardMoveDown() {
        board = new TetrisBoard(5, 5);
        board.nextPiece(leftl, new Point(1,1));
        board.move(Board.Action.DOWN);

        Assertions.assertEquals(Board.Result.SUCCESS, board.getLastResult());
        Assertions.assertEquals(new Point(1, 0), board.getCurrentPiecePosition());
    }

    @Test
    public void testBoardMoveLeftFail() {
        board = new TetrisBoard(4, 4);
        board.nextPiece(rightl, new Point(0,1));
        board.move(Board.Action.LEFT);

        Assertions.assertEquals(Board.Result.OUT_BOUNDS, board.getLastResult());
        Assertions.assertEquals(new Point(0, 1), board.getCurrentPiecePosition());
    }

    @Test
    public void testBoardMoveRightFail() {
        board = new TetrisBoard(4, 4);
        board.nextPiece(square, new Point(2,2));
        board.move(Board.Action.RIGHT);

        Assertions.assertEquals(Board.Result.OUT_BOUNDS, board.getLastResult());
        Assertions.assertEquals(new Point(2, 2), board.getCurrentPiecePosition());
    }

    @Test
    public void testBoardMoveDownGroundPlace() {
        board = new TetrisBoard(4, 3);
        board.nextPiece(t, new Point(0,-1));
        board.move(Board.Action.DOWN);
        Piece.PieceType[][] expectedGrid = new Piece.PieceType[][] {
                {Piece.PieceType.T, null, null},
                {Piece.PieceType.T, Piece.PieceType.T, null},
                {Piece.PieceType.T, null, null},
                {null, null, null}
        };
        Assertions.assertEquals(Board.Result.PLACE, board.getLastResult());
        Assertions.assertArrayEquals(new int[] {1, 2, 1, 0}, board.getAllColumnHeights());
        Assertions.assertArrayEquals(new int[] {3, 1, 0}, board.getAllRowWidths());
        Assertions.assertArrayEquals(expectedGrid, board.getFullGrid());
        Assertions.assertEquals(2, board.getMaxHeight());
    }

    @Test
    public void testBoardMoveDownPiecePlace() {
        board = new TetrisBoard(5, 5);
        board.nextPiece(stick, new Point(0, 0));
        board.setGrid(new Piece.PieceType[][] {
                {Piece.PieceType.T, null, null, null, null},
                {Piece.PieceType.T, Piece.PieceType.T, null, null, null},
                {Piece.PieceType.T, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
        });
        Piece.PieceType[][] expectedGrid = new Piece.PieceType[][] {
                {Piece.PieceType.T, null, Piece.PieceType.STICK, null, null},
                {Piece.PieceType.T, Piece.PieceType.T, Piece.PieceType.STICK, null, null},
                {Piece.PieceType.T, null, Piece.PieceType.STICK, null, null},
                {null, null, Piece.PieceType.STICK, null, null},
                {null, null, null, null, null}
        };
        board.move(Board.Action.DOWN);
        Assertions.assertEquals(Board.Result.PLACE, board.getLastResult());
        Assertions.assertArrayEquals(new int[] {3, 3, 3, 3, 0}, board.getAllColumnHeights());
        Assertions.assertArrayEquals(new int[] {0, 0, 4, 0, 0}, board.getAllRowWidths());
        Assertions.assertArrayEquals(expectedGrid, board.getFullGrid());
        Assertions.assertEquals(3, board.getMaxHeight());
    }

    @Test
    public void testBoardDropPiece() {
        board = new TetrisBoard(5, 8);
        board.nextPiece(leftl, new Point(0, 2));
        Piece.PieceType[][] expectedGrid = new Piece.PieceType[][] {
                {Piece.PieceType.LEFT_L, Piece.PieceType.LEFT_L, null, null, null, null, null, null},
                {Piece.PieceType.LEFT_L, null, null, null, null, null, null, null},
                {Piece.PieceType.LEFT_L, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}

        };

        board.move(Board.Action.DROP);
        Assertions.assertEquals(Board.Result.PLACE, board.getLastResult());
        Assertions.assertArrayEquals(new int[] {2, 1, 1, 0, 0}, board.getAllColumnHeights());
        Assertions.assertArrayEquals(new int[] {3, 1, 0, 0, 0, 0, 0, 0}, board.getAllRowWidths());
        Assertions.assertArrayEquals(expectedGrid, board.getFullGrid());
        Assertions.assertEquals(2, board.getMaxHeight());
    }

    @Test
    public void testBoardDropRotatedPiece() {
        board = new TetrisBoard(5, 5);
        board.nextPiece(rightdog, new Point(0, 2));
        Piece.PieceType[][] expectedGrid = new Piece.PieceType[][] {
                {Piece.PieceType.RIGHT_DOG, null, null, null, null},
                {Piece.PieceType.RIGHT_DOG, Piece.PieceType.RIGHT_DOG, null, null, null},
                {null, Piece.PieceType.RIGHT_DOG, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}

        };

        board.move(Board.Action.CLOCKWISE);
        Assertions.assertEquals(Board.Result.SUCCESS, board.getLastResult());
        Assertions.assertEquals(Board.Action.CLOCKWISE, board.getLastAction());
        board.move(Board.Action.CLOCKWISE);
        board.move(Board.Action.DROP);
        Assertions.assertEquals(Board.Result.PLACE, board.getLastResult());
        Assertions.assertArrayEquals(new int[] {1, 2, 2, 0, 0}, board.getAllColumnHeights());
        Assertions.assertArrayEquals(new int[] {2, 2, 0, 0, 0}, board.getAllRowWidths());
        Assertions.assertArrayEquals(expectedGrid, board.getFullGrid());
        Assertions.assertEquals(2, board.getMaxHeight());
    }

    @Test
    public void testBoardPlaceManyPieces() {
        board = new TetrisBoard(5, 5);
        board.nextPiece(t, new Point(0, -1));
        board.move(Board.Action.DOWN);
        Piece.PieceType[][] expectedGridMoveOne = new Piece.PieceType[][] {
                {Piece.PieceType.T, null, null, null, null},
                {Piece.PieceType.T, Piece.PieceType.T, null, null, null},
                {Piece.PieceType.T, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
        };
        Assertions.assertEquals(Board.Result.PLACE, board.getLastResult());
        Assertions.assertArrayEquals(expectedGridMoveOne, board.getFullGrid());
        Assertions.assertArrayEquals(new int[] {1, 2, 1, 0, 0}, board.getAllColumnHeights());
        Assertions.assertArrayEquals(new int[] {3, 1, 0, 0, 0}, board.getAllRowWidths());
        Assertions.assertEquals(2, board.getMaxHeight());

        board.nextPiece(stick, new Point(0, 1));
        board.move(Board.Action.COUNTERCLOCKWISE);
        board.move(Board.Action.LEFT);
        board.move(Board.Action.DROP);
        Piece.PieceType[][] expectedGridMoveTwo = new Piece.PieceType[][] {
                {Piece.PieceType.T, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK},
                {Piece.PieceType.T, Piece.PieceType.T, null, null, null},
                {Piece.PieceType.T, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
        };

        Assertions.assertEquals(Board.Result.PLACE, board.getLastResult());
        Assertions.assertArrayEquals(expectedGridMoveTwo, board.getFullGrid());
        Assertions.assertArrayEquals(new int[] {5, 2, 1, 0, 0}, board.getAllColumnHeights());
        Assertions.assertArrayEquals(new int[] {3, 2, 1, 1, 1}, board.getAllRowWidths());
        Assertions.assertEquals(5, board.getMaxHeight());

        board.nextPiece(leftl, new Point(2, 2));
        board.move(Board.Action.CLOCKWISE);
        board.move(Board.Action.DROP);
        Piece.PieceType[][] expectedGridMoveThree = new Piece.PieceType[][] {
                {Piece.PieceType.T, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK},
                {Piece.PieceType.T, Piece.PieceType.T, null, null, null},
                {Piece.PieceType.T, null, null, null, null},
                {Piece.PieceType.LEFT_L, Piece.PieceType.LEFT_L, Piece.PieceType.LEFT_L, null, null},
                {null, null, Piece.PieceType.LEFT_L, null, null}
        };

        Assertions.assertEquals(Board.Result.PLACE, board.getLastResult());
        Assertions.assertArrayEquals(expectedGridMoveThree, board.getFullGrid());
        Assertions.assertArrayEquals(new int[] {5, 2, 1, 3, 3}, board.getAllColumnHeights());
        Assertions.assertArrayEquals(new int[] {4, 3, 3, 1, 1}, board.getAllRowWidths());
        Assertions.assertEquals(5, board.getMaxHeight());

        board.nextPiece(leftdog, new Point(1, 2));
        board.move(Board.Action.CLOCKWISE);
        board.move(Board.Action.DOWN);
        Piece.PieceType[][] expectedGridMoveFour = new Piece.PieceType[][] {
                {Piece.PieceType.T, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK, Piece.PieceType.STICK},
                {Piece.PieceType.T, Piece.PieceType.T, null, null, null},
                {Piece.PieceType.T, null, Piece.PieceType.LEFT_DOG, Piece.PieceType.LEFT_DOG, null},
                {Piece.PieceType.LEFT_L, Piece.PieceType.LEFT_L, Piece.PieceType.LEFT_L, Piece.PieceType.LEFT_DOG, Piece.PieceType.LEFT_DOG},
                {null, null, Piece.PieceType.LEFT_L, null, null}
        };

        Assertions.assertEquals(Board.Result.PLACE, board.getLastResult());
        Assertions.assertArrayEquals(expectedGridMoveFour, board.getFullGrid());
        Assertions.assertArrayEquals(new int[] {5, 2, 4, 5, 3}, board.getAllColumnHeights());
        Assertions.assertArrayEquals(new int[] {4, 3, 4, 3, 2}, board.getAllRowWidths());
        Assertions.assertEquals(5, board.getMaxHeight());

    }

    @Test
    public void testCWWallKicks() {
        testAllWallKicks(CLOCKWISE_DIRECTION);
    }

    @Test
    public void testCCWWallKicks() {
        testAllWallKicks(COUNTERCLOCKWISE_DIRECTION);
    }

    public void testAllWallKicks(int direction) {
        Point startingSpawn = new Point(3, 3);
        for (int i = 0; i < allWallKickPieces.length; i ++) {
            TetrisPiece currentPiece = allWallKickPieces[i];
            TetrisPiece rotatedPiece;
            for (int j = 0; j < NUM_TESTS - 1; j++) {
                for (int k = 0; k < NUM_ROTATIONS; k++) {
                    board = new TetrisBoard(12, 12);
                    int boardWidth = board.getWidth();
                    int boardHeight = board.getHeight();
                    currentPiece.setRotationIndex(k);
                    rotatedPiece = (direction == CLOCKWISE_DIRECTION) ?
                            (TetrisPiece) currentPiece.clockwisePiece() : (TetrisPiece) currentPiece.counterclockwisePiece();
                    Point postKickOffset = (currentPiece.getType() == Piece.PieceType.STICK) ?
                            ((direction == CLOCKWISE_DIRECTION) ? Piece.I_CLOCKWISE_WALL_KICKS[k][j] : Piece.I_COUNTERCLOCKWISE_WALL_KICKS[k][j])
                            : ((direction == CLOCKWISE_DIRECTION) ? Piece.NORMAL_CLOCKWISE_WALL_KICKS[k][j] : Piece.NORMAL_COUNTERCLOCKWISE_WALL_KICKS[k][j]);
                    if (currentPiece.getType() == Piece.PieceType.T && (postKickOffset.equals(new Point(1, 1)) ||
                            postKickOffset.equals(new Point(0, -2)) || postKickOffset.equals(new Point(-1, 1)))) {
                        currentPiece = rotatedPiece;
                        continue;
                    }
                    board.nextPiece(currentPiece, startingSpawn);
                    board.setGrid(setUpWallKickGrid(startingSpawn, postKickOffset, currentPiece, rotatedPiece, boardWidth, boardHeight));
                    Board.Action nextAction = (direction == CLOCKWISE_DIRECTION) ? Board.Action.CLOCKWISE : Board.Action.COUNTERCLOCKWISE;
                    board.move(nextAction);
                    Assertions.assertEquals(Board.Result.SUCCESS, board.getLastResult());
                    Assertions.assertEquals(nextAction, board.getLastAction());
                    Assertions.assertEquals(new Point(startingSpawn.x + postKickOffset.x, startingSpawn.y + postKickOffset.y), board.getCurrentPiecePosition());
                    currentPiece = (TetrisPiece) currentPiece.clockwisePiece();
                }
            }
        }
    }

    public Piece.PieceType[][] setUpWallKickGrid(Point spawn, Point offset, TetrisPiece currentPiece, TetrisPiece rotatedPiece, int width, int height) {
        Piece.PieceType[][] initGrid = new Piece.PieceType[width][height];{};
        HashSet<Point> clearSpaces = new HashSet<>();
        for (Point p : currentPiece.getBody()) {
            System.out.println("Crurent piece at point: (" + (p.x + spawn.x) + ", " + (p.y + spawn.y) + ")");
            clearSpaces.add(new Point(p.x + spawn.x, p.y + spawn.y));
        }
        for (Point p : rotatedPiece.getBody()) {
            System.out.println("rotated piece at point: (" + (p.x + spawn.x + offset.x) + ", " + (p.y + spawn.y  + offset.y) + ")");
            clearSpaces.add(new Point(p.x + spawn.x + offset.x, p.y + spawn.y + offset.y));
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                initGrid[i][j] = (clearSpaces.contains(new Point (i, j))) ? null : Piece.PieceType.T;
                //System.out.println("(" + i + "," + j + "): " + initGrid[i][j]);
            }
        }
        return initGrid;
    }


    @Test
    public void testWallKick0RTest2() {
        board = new TetrisBoard(5, 5);
        Piece.PieceType[][] initialGrid = new Piece.PieceType[][]{
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, Piece.PieceType.STICK},
                {null, null, null, null},
        };
        board.setGrid(initialGrid);
        board.nextPiece(leftdog, new Point(0, 1));
        board.move(Board.Action.CLOCKWISE);

        Assertions.assertEquals(Board.Result.SUCCESS, board.getLastResult());
        Assertions.assertEquals(Board.Action.CLOCKWISE, board.getLastAction());
        Assertions.assertEquals(new Point(-1, 1), board.getCurrentPiecePosition());
    }

    @Test
    public void testWallKick0RTest3() {
        board = new TetrisBoard(5,4);
        Piece.PieceType[][] initialGrid = new Piece.PieceType[][]{
                {null, null, null, null},
                {null, null, null, null},
                {null, null, Piece.PieceType.STICK, null},
                {null, null, null, null},
                {null, null, null, null},
        };
        board.setGrid(initialGrid);
        board.nextPiece(leftl, new Point(1,0));
        board.move(Board.Action.CLOCKWISE);

        Assertions.assertEquals(Board.Result.SUCCESS, board.getLastResult());
        Assertions.assertEquals(Board.Action.CLOCKWISE, board.getLastAction());
        Assertions.assertEquals(new Point(0, 1), board.getCurrentPiecePosition());
    }

    @Test
    public void testWallKick0RTest4() {
        board = new TetrisBoard(5,5);
        Piece.PieceType[][] initialGrid = new Piece.PieceType[][]{
                {null, null, null, null, null},
                {null, null, null, null, Piece.PieceType.T},
                {null, null, null, null, null},
                {null, null, null, Piece.PieceType.LEFT_L, null},
                {null, null, null, null, null},
        };
        board.setGrid(initialGrid);
        board.nextPiece(rightdog, new Point(1,2));
        board.move(Board.Action.CLOCKWISE);

        Assertions.assertEquals(Board.Result.SUCCESS, board.getLastResult());
        Assertions.assertEquals(Board.Action.CLOCKWISE, board.getLastAction());
        Assertions.assertEquals(new Point(1, 0), board.getCurrentPiecePosition());
    }

    @Test
    public void testWallKick0RTest5() {
        board = new TetrisBoard(5,6);
        Piece.PieceType[][] initialGrid = new Piece.PieceType[][]{
                {null, null, null, null, null, null},
                {null, null, null, null, Piece.PieceType.T, null},
                {null, null, null, null, null, null},
                {Piece.PieceType.T, null, Piece.PieceType.T, null, null, null},
                {null, null, null, null, null, null},
        };

        board.setGrid(initialGrid);
        board.nextPiece(rightl, new Point(1, 2));
        board.move(Board.Action.CLOCKWISE);

        Assertions.assertEquals(Board.Result.SUCCESS, board.getLastResult());
        Assertions.assertEquals(Board.Action.CLOCKWISE, board.getLastAction());
        Assertions.assertEquals(new Point(0, 0), board.getCurrentPiecePosition());
    }

}
