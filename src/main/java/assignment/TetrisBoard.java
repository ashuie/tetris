package assignment;

import java.awt.*;

/**
 * Represents a Tetris board -- essentially a 2D grid of piece types (or nulls). Supports
 * tetris pieces and row clearing.  Does not do any drawing or have any idea of
 * pixels. Instead, just represents the abstract 2D board.
 */
public final class TetrisBoard implements Board {
    int width;
    int height;
    Piece currPiece;
    Point currPiecePosition;
    Result lastResult;
    Action lastAction;
    int rowsCleared;
    int maxHeight;
    int[] columnHeight;
    int[] rowWidth;
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
        columnHeight = new int[width];
        rowWidth = new int[height];
        grid = new Piece.PieceType[width][height];
    }

    @Override
    public Result move(Action act) {
        switch (act) {
            case LEFT:
                if(!inBounds(currPiece, currPiecePosition,-1, 0)) {
                    lastResult = Result.OUT_BOUNDS;
                    return Result.OUT_BOUNDS;
                }
                currPiecePosition.setLocation(currPiecePosition.x - 1, currPiecePosition.y);
                lastResult = Result.SUCCESS;
                return Result.SUCCESS;
            case RIGHT:
                if(!inBounds(currPiece,currPiecePosition,1, 0)) {
                    lastResult = Result.OUT_BOUNDS;
                    return Result.OUT_BOUNDS;
                }
                currPiecePosition.setLocation(currPiecePosition.x + 1, currPiecePosition.y);
                lastResult = Result.SUCCESS;
                return Result.SUCCESS;
            case DOWN:
                // Implement placing
                if(!downInBounds()) {
                    lastResult = Result.PLACE;
                    maxHeight = setMaxHeight();
                    updateColHeight();
                    return lastResult;
                }
                else {
                    currPiecePosition.setLocation(currPiecePosition.x, currPiecePosition.y - 1);
                    lastResult = Result.SUCCESS;
                    return lastResult;
                }
            case DROP:
                currPiecePosition.setLocation(currPiecePosition.x, dropHeight(currPiece, currPiecePosition.x));
                lastResult = Result.PLACE;
                maxHeight = setMaxHeight();
                updateColHeight();
                return lastResult;
            case CLOCKWISE:
                //Piece newClockwise = currPiece.clockwisePiece();
                //if(!inBounds(newClockwise., currPiecePosition.y))
                  //  return Result.OUT_BOUNDS;
                break;
            case COUNTERCLOCKWISE:
                break;
            case NOTHING:
                break;
        }
        return Result.NO_PIECE;
    }

    private int setMaxHeight() {
        int maxHeight = 0;
        for(int height : columnHeight) {
            maxHeight = Math.max(maxHeight, height);
        }
        return maxHeight;
    }

    private void updateColHeight() {
        for(Point p : getCurrentPiece().getBody()) {
            int colX = getCurrentPiecePosition().x + p.x - 1;
            columnHeight[colX] += p.y + 1;
        }
    }

    private boolean downInBounds() {
        for(int i = 0; i < getCurrentPiece().getSkirt().length; ++i) {
            int p = getCurrentPiece().getSkirt()[i];
            if(p == Integer.MAX_VALUE) {
                continue;
            }
            else {
                if(getGrid(getCurrentPiecePosition().x + i, getCurrentPiecePosition().y + p - 1) != null
                        || getCurrentPiecePosition().y + p - 1 < 0) {
                    return false;
                }
            }
        }
        return true;
    }
    private boolean inBounds(Piece currP, Point currPos, int offsetX, int offsetY) {
        for(Point p : currP.getBody()) {
            int newPointX = p.x + offsetX + currPos.x;
            int newPointY = p.y + offsetY + currPos.y;
            if(getGrid(newPointX, newPointY) != null || newPointX < 0 || newPointY < 0
                    || newPointX > getWidth() || newPointY > getHeight()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Board testMove(Action act) { return null; }

    @Override
    public Piece getCurrentPiece() { return currPiece; }

    @Override
    public Point getCurrentPiecePosition() { return currPiecePosition; }

    @Override
    public void nextPiece(Piece p, Point spawnPosition) {
        if(!inBounds(p, spawnPosition, 0, 0)) {
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
        for(int i = 0; i < getCurrentPiece().getSkirt().length; ++i) {
            int p = getCurrentPiece().getSkirt()[i];
            int nextHeight = p - getColumnHeight(getCurrentPiecePosition().x - 1 + i)
                    + getCurrentPiecePosition().y;
            if(minVal > nextHeight) {
                minVal = nextHeight;
                minCol = i;
            }
        }
        return getColumnHeight(minCol) + 1;
    }

    @Override
    public int getColumnHeight(int x) { return columnHeight[x]; }

    @Override
    public int getRowWidth(int y) { return rowWidth[y]; }

    @Override
    public Piece.PieceType getGrid(int x, int y) { return grid[x][y]; }
}
