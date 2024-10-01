package assignment;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

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
    int[] columnHeights;
    int[] rowWidths;
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
                if(outOfBounds(currPiece, currPiecePosition, -1, 0)) {
                    lastResult = Result.OUT_BOUNDS;
                } else {
                    currPiecePosition.setLocation(currPiecePosition.x - 1, currPiecePosition.y);
                    lastResult = Result.SUCCESS;
                }
                break;
            case RIGHT:
                if(outOfBounds(currPiece, currPiecePosition, 1, 0)) {
                    lastResult = Result.OUT_BOUNDS;
                } else {
                    currPiecePosition.setLocation(currPiecePosition.x + 1, currPiecePosition.y);
                    lastResult = Result.SUCCESS;
                }
                break;
            case DOWN:
                // Implement placing
                if(!downInBounds()) {
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
            case CLOCKWISE:
                Piece newClockwise = currPiece.clockwisePiece();
                if(outOfBounds(newClockwise, currPiecePosition, 0, 0)) {
                    //try wall kicks
                    lastResult = Result.NO_PIECE;
                } else {
                    currPiece = newClockwise;
                    lastResult = Result.SUCCESS;
                }
                break;
            case COUNTERCLOCKWISE:
                break;
            case NOTHING:
            default:
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
            //heights[p.x]++;
        }
        updateColHeight();
        clearRows();

        /*for (int j = 0; j < heights.length; j++) {
            System.out.println("Adding column" + (j + currX) + " amount: " + heights[j]);
            columnHeights[j + currX] += heights[j];
            if (columnHeights[j + currX] > maxHeight) {
                maxHeight = columnHeights[j + currX];
            }
        }*/
    }

   /* private int setMaxHeight() {
        int maxHeight = 0;
        for(int height : columnHeights) {
            maxHeight = Math.max(maxHeight, height);
        }
        return maxHeight;
    }*/

    // check this
    private void updateColHeight() {
        int currX = currPiecePosition.x;
        for (int i = currX; i < currX + currPiece.getWidth(); i++) {
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
        System.out.println("Column heights " + Arrays.toString(columnHeights));
      /* int currX = currPiecePosition.x;
        int currY = currPiecePosition.y;
        int changeHeight = 0;
        for (int i = 0; i < currPiece.getWidth(); i++) {
            if (currPiece.getSkirt()[i] != Integer.MAX_VALUE) {
                changeHeight = 0;
                //System.out.println("X: " + currX + " " + i + "  Y: " + currY + " + " + changeHeight);
                while (grid[currX + i][height - currY + changeHeight] != null && currY + changeHeight < height) {
                    ;changeHeight++;
                }
            }
            columnHeights[i + currX] += changeHeight;
        }*/
        /*for(Point p : getCurrentPiece().getBody()) {
            int colX = getCurrentPiecePosition().x + p.x;
            columnHeights[colX] += p.y + 1;
        }*/
    }

    private void clearRows() {
        ArrayList<Integer> completeRows = new ArrayList<>();
        for(int row = 0; row < height; row++) {
            if(rowWidths[row] == width) {
                completeRows.add(row);
            }
        }
        if(completeRows.isEmpty()) return;
        int numCompleteRows = completeRows.size();
        int lastCompleteRow = completeRows.get(0);
        int completeRowsPassed = 1;
        for(int i = 1; i < numCompleteRows; i++) {
            int currCompleteRow = completeRows.get(i);
            for(int row = lastCompleteRow; row < currCompleteRow; row++) {
                grid[row] = grid[row + completeRowsPassed];
            }
            completeRowsPassed++;
            lastCompleteRow = currCompleteRow;
        }
    }

    private boolean downInBounds() {
        for(int i = 0; i < currPiece.getSkirt().length; ++i) {
            int sk = currPiece.getSkirt()[i];
            if(sk != Integer.MAX_VALUE) {
                if(currPiecePosition.y + sk - 1 < 0 ||
                        grid[currPiecePosition.x + i][currPiecePosition.y + sk - 1] != null) {
                    return false;
                }
            }
        }
        return true;
    }
    private boolean outOfBounds(Piece currP, Point currPos, int offsetX, int offsetY) {
        for(Point p : currP.getBody()) {
            int newPointX = p.x + offsetX + currPos.x;
            int newPointY = p.y + offsetY + currPos.y;
            if(newPointX < 0 || newPointY < 0 || newPointX > width || newPointY > height
            || grid[newPointX][newPointY] != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Board testMove(Action act) { return null; }

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
        System.out.println("Min col: " + minCol + " Drop height: " + columnHeights[minCol]);
        return columnHeights[minCol] - skirt[minCol - currX];
    }

    @Override
    public int getColumnHeight(int x) { return columnHeights[x]; }

    @Override
    public int getRowWidth(int y) { return rowWidths[y]; }

    @Override
    public Piece.PieceType getGrid(int x, int y) { return grid[x][y]; }
}
