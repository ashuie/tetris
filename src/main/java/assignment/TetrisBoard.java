package assignment;

import java.awt.*;
import java.util.HashSet;

/**
 * Represents a Tetris board -- essentially a 2D grid of piece types (or nulls). Supports
 * tetris pieces and row clearing.  Does not do any drawing or have any idea of
 * pixels. Instead, just represents the abstract 2D board.
 */
public final class TetrisBoard implements Board {

    public static final int CLOCKWISE_DIRECTION = 1;
    public static final int COUNTERCLOCKWISE_DIRECTION = 0;

    private final int width;
    private final int height;
    private Piece currPiece;
    private Point currPiecePosition;
    private Result lastResult;
    private Action lastAction;
    private int rowsCleared;
    private int maxHeight;
    private int[] columnHeights;
    private int[] rowWidths;
    Piece.PieceType[][] grid;

    // JTetris will use this constructor
    public TetrisBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currPiece = null;
        currPiecePosition = null;
        lastAction = Action.NOTHING;
        lastResult = Result.NO_PIECE;
        rowsCleared = 0;
        maxHeight = 0;
        columnHeights = new int[width];
        rowWidths = new int[height];
        grid = new Piece.PieceType[width][height];
    }

    @Override
    public Result move(Action act) {
        switch (act) {
            case LEFT:
                tryHorizontalShift(-1);
                break;
            case RIGHT:
                tryHorizontalShift(1);
                break;
            case DOWN:
                if(downOutOfBounds()) {
                    updateGridPlaceBlock();
                    lastAction = Action.DROP;
                    lastResult = Result.PLACE;
                } else {
                    currPiecePosition.setLocation(currPiecePosition.x, currPiecePosition.y - 1);
                    lastAction = Action.DOWN;
                    lastResult = Result.SUCCESS;
                }
                break;
            case DROP:
                currPiecePosition.setLocation(currPiecePosition.x, dropHeight(currPiece, currPiecePosition.x));
                updateGridPlaceBlock();
                lastAction = Action.DROP;
                lastResult = Result.PLACE;
                break;
            case CLOCKWISE:
                lastResult = rotatePiece(CLOCKWISE_DIRECTION);
                break;
            case COUNTERCLOCKWISE:
                lastResult = rotatePiece(COUNTERCLOCKWISE_DIRECTION);
                break;
            case NOTHING:
            default:
                lastAction = Action.NOTHING;
                lastResult = Result.NO_PIECE;
        }
        return lastResult;
    }

    private void updateGridPlaceBlock() {
        Point[] currBody = currPiece.getBody();
        //int[] heights = new int[currPiece.getWidth()];
        int currX = currPiecePosition.x;
        int currY = currPiecePosition.y;
        //int[] skirts = currPiece.getSkirt();
        for (Point p : currBody) {
            //System.out.println("Point: " + p);
            rowWidths[currY + p.y]++;
            //System.out.println("Row Width of  " + (currY + p.y) + " " + rowWidths[currY + p.y]);
            grid[currX + p.x][currY + p.y] = currPiece.getType();
        }
        updateColHeight();
        clearRows();
    }

    // check this
    // ACCOUNT FOR LEFT SKIRT
    private void updateColHeight() {
        //int currX = currPiecePosition.x;
        for (int i = 0; i < width; i++) {
            for (int j = height - 1; j >= 0; j--) {
                if (grid[i][j] != null) {
                    columnHeights[i] = j + 1;
                    if (columnHeights[i] > maxHeight) {
                        maxHeight = columnHeights[i];
                    }
                    break;
                }
            }
        }
    }

    // USE ROWS CLEARED
    private void clearRows() {
        HashSet<Integer> completeRows = new HashSet<>();
        for(int row = 0; row < height; row++) {
            if(rowWidths[row] == width) {
                completeRows.add(row);
            }
        }
        if(completeRows.isEmpty()) {
            return;
        }
        rowsCleared = completeRows.size();
        int j = 0;
        for (int i = 0; i < height; i++) {
            if (!completeRows.contains(i)) {
                for (int k = 0; k < width; k++) {
                    grid[k][j] = grid[k][i];
                    rowWidths[j] = rowWidths[i];
                }
                j++;
            }
        }
        for (int m = 0; m < width; m++) {
            columnHeights[m] -= rowsCleared;
        }
    }

    private boolean downOutOfBounds() {
        for(int i = 0; i < currPiece.getSkirt().length; ++i) {
            int sk = currPiece.getSkirt()[i];
            if(sk != Integer.MAX_VALUE) {
                if(currPiecePosition.y + sk - 1 < 0 ||
                        grid[currPiecePosition.x + i][currPiecePosition.y + sk - 1] != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean outOfBounds(Piece currP, Point currPos, int offsetX, int offsetY) {
        for(Point p : currP.getBody()) {
            int newPointX = p.x + offsetX + currPos.x;
            int newPointY = p.y + offsetY + currPos.y;
            //System.out.println("new point at " + newPointX + ", " + newPointY);
            if(newPointX < 0 || newPointY < 0 ||
                    newPointX > width - 1 || newPointY > height
                    || grid[newPointX][newPointY] != null) {
                return true;
            }
        }
        return false;
    }

    private void tryHorizontalShift(int xOffset) {
        if(outOfBounds(currPiece, currPiecePosition, xOffset, 0)) {
            lastAction = Action.NOTHING;
            lastResult = Result.OUT_BOUNDS;
        } else {
            currPiecePosition.setLocation(currPiecePosition.x + xOffset, currPiecePosition.y);
            lastAction = (xOffset == -1) ? Action.LEFT : Action.RIGHT;
            lastResult = Result.SUCCESS;
        }
    }

    public Result rotatePiece(int direction) {
        Piece rotatedPiece = (direction == CLOCKWISE_DIRECTION) ? currPiece.clockwisePiece() : currPiece.counterclockwisePiece();
        if(outOfBounds(rotatedPiece, currPiecePosition, 0, 0)) {
            return tryKick(direction, rotatedPiece);
        } else {
            currPiece = rotatedPiece;
            lastAction = (direction == CLOCKWISE_DIRECTION) ? Action.CLOCKWISE : Action.COUNTERCLOCKWISE;
            return Result.SUCCESS;
        }
    }

    // direction = 1 : clockwise, 0 : counterclockwise
    public Result tryKick(int direction, Piece rotatedPiece) {
        int rindex = currPiece.getRotationIndex();
        Point[] kicks = (rotatedPiece.getType() == Piece.PieceType.STICK) ?
                ((direction == CLOCKWISE_DIRECTION) ? Piece.I_CLOCKWISE_WALL_KICKS[rindex]
                : Piece.I_COUNTERCLOCKWISE_WALL_KICKS[rindex])
                : ((direction == CLOCKWISE_DIRECTION) ? Piece.NORMAL_CLOCKWISE_WALL_KICKS[rindex]
                : Piece.NORMAL_COUNTERCLOCKWISE_WALL_KICKS[rindex]);
        for (Point p : kicks) {
            if (!outOfBounds(rotatedPiece, currPiecePosition, p.x, p.y)) {
                currPiecePosition.setLocation(currPiecePosition.x + p.x, currPiecePosition.y + p.y);
                currPiece = rotatedPiece;
                lastAction = (direction == CLOCKWISE_DIRECTION) ? Action.CLOCKWISE : Action.COUNTERCLOCKWISE;
                return Result.SUCCESS;
            }
        }
        lastAction = Action.NOTHING;
        return Result.OUT_BOUNDS;
    }

    @Override
    public Board testMove(Action act) {
        TetrisBoard testBoard = new TetrisBoard(width, height);
        testBoard.setGrid(this.getFullGrid());
        testBoard.setCurrentPiece(currPiece);
        testBoard.setCurrentPiecePosition(currPiecePosition);
        testBoard.move(act);
        return testBoard;
    }

    @Override
    public Piece getCurrentPiece() { return currPiece; }

    @Override
    public Point getCurrentPiecePosition() { return currPiecePosition; }

    @Override
    public void nextPiece(Piece p, Point spawnPosition) {
        if(outOfBounds(p, spawnPosition, 0, 0)) {
            throw new IllegalArgumentException();
        }
        else {
            currPiece = p;
            currPiecePosition = spawnPosition;
        }
    }

    @Override
    public boolean equals(Object other) { return false; }

    @Override
    public Result getLastResult() { return lastResult; }

    @Override
    public Action getLastAction() { return lastAction; }

    @Override
    public int getRowsCleared() { return rowsCleared; }

    @Override
    public int getWidth() { return width; }

    @Override
    public int getHeight() { return height; }

    @Override
    public int getMaxHeight() { return maxHeight; }

    @Override
    public int dropHeight(Piece piece, int x) {
        int minVal = Integer.MAX_VALUE;
        int minCol = 0;
        int currX = currPiecePosition.x;
        int currY = currPiecePosition.y;
        int[] skirt = currPiece.getSkirt();
        for(int i = 0; i < skirt.length; i++) {
            if (skirt[i] == Integer.MAX_VALUE) {
                continue;
            }
            int nextHeight = currY + skirt[i] - columnHeights[currX + i];
            if(minVal > nextHeight) {
                minVal = nextHeight;
                minCol = i + currX;
            }
        }
        //System.out.println("Min col: " + minCol + " Drop height: " + columnHeights[minCol]);
        return columnHeights[minCol] - skirt[minCol - currX];
    }

    @Override
    public int getColumnHeight(int x) { return columnHeights[x]; }

    @Override
    public int getRowWidth(int y) { return rowWidths[y]; }

    @Override
    public Piece.PieceType getGrid(int x, int y) { return grid[x][y]; }

    public Piece.PieceType[][] getFullGrid() {
        return grid;
    }

    public void setGrid(Piece.PieceType[][] g) {
        grid = g;
    }

    public void setCurrentPiece(Piece p) {
        currPiece = p;
    }

    public void setCurrentPiecePosition(Point p) {
        currPiecePosition = p;
    }
}