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
    public static final int MAX_PIECE_HEIGHT = 4;

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
        this.width = Math.max(width, 0);
        this.height = Math.max(height, 0);
        currPiece = null;
        currPiecePosition = null;
        lastAction = Action.NOTHING;
        lastResult = Result.NO_PIECE;
        rowsCleared = 0;
        maxHeight = 0;
        columnHeights = new int[this.width];
        rowWidths = new int[this.height];
        grid = new Piece.PieceType[this.width][this.height];
    }

    @Override
    public Result move(Action act) {
        rowsCleared = 0;
        if (act == null || currPiece == null || currPiecePosition == null || !(currPiece instanceof TetrisPiece)) {
            return Result.NO_PIECE;
        }
        lastAction = act;
        switch (act) {
            case LEFT:
                tryHorizontalShift(-1);
                break;
            case RIGHT:
                tryHorizontalShift(1);
                break;
            case DOWN:
                // Place if going down is not possible
                if(downOutOfBounds(currPiece, currPiecePosition.x, currPiecePosition.y)) {
                    updateGridPlaceBlock();
                    lastResult = Result.PLACE;
                } else {
                    currPiecePosition.setLocation(currPiecePosition.x, currPiecePosition.y - 1);
                    lastResult = Result.SUCCESS;
                }
                break;
            case DROP:
                currPiecePosition.setLocation(currPiecePosition.x, dropHeight(currPiece, currPiecePosition.x));
                updateGridPlaceBlock();
                lastResult = Result.PLACE;
                break;
            case TEST_DROP_LEFT:
                currPiecePosition.setLocation(currPiecePosition.x, dropHeight(currPiece, currPiecePosition.x + 1));
                tryHorizontalShift(-1);
                lastResult = Result.PLACE;
                updateGridPlaceBlock();
                break;
            case TEST_DROP_RIGHT:
                currPiecePosition.setLocation(currPiecePosition.x, dropHeight(currPiece, currPiecePosition.x + 1));
                tryHorizontalShift(1);
                lastResult = Result.PLACE;
                updateGridPlaceBlock();
                break;
            case TEST_DROP_CW:
                currPiecePosition.setLocation(currPiecePosition.x, dropHeight(currPiece, currPiecePosition.x));
                tryRotatePiece(CLOCKWISE_DIRECTION);
                lastResult = Result.PLACE;
                updateGridPlaceBlock();
                break;
            case TEST_DROP_CCW:
                currPiecePosition.setLocation(currPiecePosition.x, dropHeight(currPiece, currPiecePosition.x));
                tryRotatePiece(COUNTERCLOCKWISE_DIRECTION);
                lastResult = Result.PLACE;
                updateGridPlaceBlock();
                break;
            case CLOCKWISE:
                lastResult = tryRotatePiece(CLOCKWISE_DIRECTION);
                break;
            case COUNTERCLOCKWISE:
                lastResult = tryRotatePiece(COUNTERCLOCKWISE_DIRECTION);
                break;
            case NOTHING:
            default:
                lastResult = Result.NO_PIECE;
                break;
        }
        return lastResult;
    }

    private void updateGridPlaceBlock() {
        Point[] currBody = currPiece.getBody();
        int currX = currPiecePosition.x;
        int currY = currPiecePosition.y;
        // Set piece on grid on its current position after being dropped
        for (Point p : currBody) {
            rowWidths[currY + p.y]++;
            grid[currX + p.x][currY + p.y] = currPiece.getType();
        }
        updateColHeight(0, currX + currPiece.getWidth());
        clearRows();
    }

    private void updateColHeight(int hLeft, int hRight) {
        // Check all columns of which the block placed occupies
        for (int i = hLeft; i < hRight; i++) {
            for (int j = height - 1; j >= 0; j--) {
                if (i >= 0 && i < width && grid[i][j] != null) {
                    columnHeights[i] = j + 1;
                    // Update max height as new max height encountered
                    if (columnHeights[i] > maxHeight) {
                        maxHeight = columnHeights[i];
                    }
                    break;
                }
            }
        }
    }

    private void clearRows() {
        HashSet<Integer> completeRows = new HashSet<>();
        // Add all full row indices to the hash set
        for(int row = 0; row < height; row++) {
            if(rowWidths[row] == width) {
                completeRows.add(row);
            }
        }
        if(completeRows.isEmpty()) {
            return;
        }
        rowsCleared = completeRows.size();
        // Modify the grid in place by adding only rows with an index not in complete rows
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
        // Recalculate column heights due to potential hole behavior
        for (int k = 0; k < width; k++) {
            columnHeights[k] = 0;
        }
        maxHeight = 0;
        updateColHeight(0, width);
    }

    private boolean downOutOfBounds(Piece piece, int currX, int yHeight) {
        // Check across piece width if space below is ground or occupied by a piece
        for(int i = 0; i < piece.getSkirt().length; ++i) {
            int sk = piece.getSkirt()[i];
            if(sk != Integer.MAX_VALUE) {
                if(yHeight + sk - 1 < 0 ||
                        grid[currX + i][yHeight + sk - 1] != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean outOfBounds(Piece currP, Point currPos, int offsetX, int offsetY) {
        // Offset all points in the body and check if new position would violate grid bounds
        for(Point p : currP.getBody()) {
            int newPointX = p.x + offsetX + currPos.x;
            int newPointY = p.y + offsetY + currPos.y;
            if(newPointX < 0 || newPointY < 0 ||
                    newPointX > width - 1 || newPointY > height - 1
                    || grid[newPointX][newPointY] != null) {
                return true;
            }
        }
        return false;
    }

    private void tryHorizontalShift(int xOffset) {
        // Only shift the piece by horizontal offset if not out of bounds in new position
        if(outOfBounds(currPiece, currPiecePosition, xOffset, 0)) {
            lastAction = Action.NOTHING;
            lastResult = Result.OUT_BOUNDS;
        } else {
            currPiecePosition.setLocation(currPiecePosition.x + xOffset, currPiecePosition.y);
            lastAction = (xOffset == -1) ? Action.LEFT : Action.RIGHT;
            lastResult = Result.SUCCESS;
        }
    }

    public Result tryRotatePiece(int direction) {
        // Try a rotation, then try a kick if out of bounds. Otherwise piece rotates successfully
        Piece rotatedPiece = (direction == CLOCKWISE_DIRECTION) ? currPiece.clockwisePiece() : currPiece.counterclockwisePiece();
        if(outOfBounds(rotatedPiece, currPiecePosition, 0, 0)) {
            return tryKick(direction, rotatedPiece);
        } else {
            currPiece = rotatedPiece;
            lastAction = (direction == CLOCKWISE_DIRECTION) ? Action.CLOCKWISE : Action.COUNTERCLOCKWISE;
            return Result.SUCCESS;
        }
    }

    public Result tryKick(int direction, Piece rotatedPiece) {
        int rindex = currPiece.getRotationIndex();
        // Get all point offset kicks based on direction and type of piece
        Point[] kicks = (rotatedPiece.getType() == Piece.PieceType.STICK) ?
                ((direction == CLOCKWISE_DIRECTION) ? Piece.I_CLOCKWISE_WALL_KICKS[rindex]
                : Piece.I_COUNTERCLOCKWISE_WALL_KICKS[rindex])
                : ((direction == CLOCKWISE_DIRECTION) ? Piece.NORMAL_CLOCKWISE_WALL_KICKS[rindex]
                : Piece.NORMAL_COUNTERCLOCKWISE_WALL_KICKS[rindex]);
        // Use points in kicks as offset to check for in bounds
        for (Point p : kicks) {
            if (!outOfBounds(rotatedPiece, currPiecePosition, p.x, p.y)) {
                // Update the piece with new rotation from first wall kick offset that's in bounds
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
        testBoard.setColumnHeights(this.columnHeights);
        testBoard.setRowWidths(this.rowWidths);
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
        // Only allow new piece if its spawn position is in bounds;
        if(!(p instanceof TetrisPiece) || spawnPosition == null || outOfBounds(p, spawnPosition, 0, 0)) {
            throw new IllegalArgumentException();
        }
        else {
            currPiece = p;
            currPiecePosition = new Point(spawnPosition.x, spawnPosition.y);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TetrisBoard)) {
            return false;
        }
        TetrisBoard otherBoard = (TetrisBoard) other;
        // Equality iff equal grid and equal current piece
        boolean equalGrid = this.grid == otherBoard.getFullGrid();
        boolean equalCurrPiece = this.currPiece == otherBoard.getCurrentPiece() &&
                this.currPiecePosition == otherBoard.getCurrentPiecePosition();
        return equalGrid && equalCurrPiece;
    }

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
        int dropHeightY = height - MAX_PIECE_HEIGHT;
        // Set height to current piece y height if drop height is being called on currPiece
        if (piece.equals(currPiece)) {
            dropHeightY = currPiecePosition.y;
        }
        // Lower piece incrementally while not out of bounds
        while (!downOutOfBounds(piece, x, dropHeightY)) {
            dropHeightY--;
        }
        return dropHeightY;
    }

    @Override
    public int getColumnHeight(int x) { return columnHeights[x]; }

    public int[] getAllColumnHeights() {
        return columnHeights;
    }

    @Override
    public int getRowWidth(int y) { return rowWidths[y]; }

    public int[] getAllRowWidths() {
        return rowWidths;
    }


    @Override
    public Piece.PieceType getGrid(int x, int y) { return grid[x][y]; }

    public Piece.PieceType[][] getFullGrid() {
        return grid;
    }

    public void setGrid(Piece.PieceType[][] g) {
        for(int i = 0; i < g.length; ++i) {
            for(int j = 0; j < g[i].length; ++j) {
                grid[i][j] = g[i][j];
            }
        }
    }

    public void setColumnHeights(int[] colHeights) {
        for(int i = 0; i < colHeights.length; ++i) {
            columnHeights[i] = colHeights[i];
        }
    }

    public void setRowWidths(int[] rWidths) {
        for(int i = 0; i < rWidths.length; ++i) {
            rowWidths[i] = rWidths[i];
        }
    }

    public void setCurrentPiece(Piece p) {
        currPiece = p;
    }

    public void setCurrentPiecePosition(Point p) {
        currPiecePosition = new Point(p.x, p.y);
    }
}