package assignment;

import java.awt.*;
import java.util.ArrayList;

public class TestBrain implements Brain{
    private ArrayList<Board> options;
    private ArrayList<Board.Action> firstMoves;

    public Board.Action nextMove(Board currentBoard) {
        // Fill the our options array with versions of the new Board
        options = new ArrayList<>();
        firstMoves = new ArrayList<>();
        enumerateOptions(currentBoard);

        int best = 0;
        int bestIndex = 0;

        // Check all of the options and get the one with the highest score
        for (int i = 0; i < options.size(); i++) {
            int score = scoreBoard(options.get(i));
            if (score > best) {
                best = score;
                bestIndex = i;
            }
        }

        // We want to return the first move on the way to the best Board
        return firstMoves.get(bestIndex);
    }

    /**
     * Test all of the places we can put the current Piece.
     * Since this is just a Lame Brain, we aren't going to do smart
     * things like rotating pieces.
     */
    private void enumerateOptions(Board currentBoard) {
        // We can always drop our current Piece
        int numDropCases = 5;
        options.add(currentBoard.testMove(Board.Action.DROP));
        firstMoves.add(Board.Action.DROP);

        options.add(currentBoard.testMove(Board.Action.TEST_DROP_LEFT));
        firstMoves.add(Board.Action.TEST_DROP_LEFT);

        options.add(currentBoard.testMove(Board.Action.TEST_DROP_RIGHT));
        firstMoves.add(Board.Action.TEST_DROP_RIGHT);

        options.add(currentBoard.testMove(Board.Action.TEST_DROP_CW));
        firstMoves.add(Board.Action.TEST_DROP_CW);

        options.add(currentBoard.testMove(Board.Action.TEST_DROP_CCW));
        firstMoves.add(Board.Action.TEST_DROP_CCW);

        // Now we'll add all the places to the left we can DROP
        Board left = currentBoard.testMove(Board.Action.LEFT);

        for(int numOrientations = 0; numOrientations < 4; numOrientations++) {
            while (left.getLastResult() == Board.Result.SUCCESS) {
                options.add(left.testMove(Board.Action.DROP));
                options.add(left.testMove(Board.Action.TEST_DROP_LEFT));
                options.add(left.testMove(Board.Action.TEST_DROP_RIGHT));
                options.add(left.testMove(Board.Action.TEST_DROP_CW));
                options.add(left.testMove(Board.Action.TEST_DROP_CCW));

                if(numOrientations == 0) {
                    for(int i = 0; i < numDropCases; ++i) {
                        firstMoves.add(Board.Action.LEFT);
                    }
                }
                else {
                    for(int i = 0; i < numDropCases; ++i) {
                        firstMoves.add(Board.Action.CLOCKWISE);
                    }
                }
                left.move(Board.Action.LEFT);
            }
            left.move(Board.Action.CLOCKWISE);
        }

        // Similarly, we add all the places to the right we can drop
        Board right = currentBoard.testMove(Board.Action.RIGHT);

        for(int numOrientations = 0; numOrientations < 4; numOrientations++) {
            while (right.getLastResult() == Board.Result.SUCCESS) {
                options.add(right.testMove(Board.Action.DROP));
                options.add(right.testMove(Board.Action.TEST_DROP_LEFT));
                options.add(right.testMove(Board.Action.TEST_DROP_RIGHT));
                options.add(right.testMove(Board.Action.TEST_DROP_CW));
                options.add(right.testMove(Board.Action.TEST_DROP_CCW));

                if(numOrientations == 0) {
                    for(int i = 0; i < numDropCases; ++i) {
                        firstMoves.add(Board.Action.RIGHT);
                    }
                }
                else {
                    for(int i = 0; i < numDropCases; ++i) {
                        firstMoves.add(Board.Action.CLOCKWISE);
                    }
                }
                right.move(Board.Action.RIGHT);
            }
            right.move(Board.Action.CLOCKWISE);
        }
    }

    /**
     * Since we're trying to avoid building too high,
     * we're going to give higher scores to Boards with
     * MaxHeights close to 0.
     */

    public int scoreBoard(Board newBoard) {
        // Set up constant weights for scoring
        final int columnDisparityConstant = 10;
        int columnDisparityScore = 0;

        final int stuckSquareConstant = 90;
        int stuckSquareScore = 0;

        // Check the bumpiness of the board
        int prevCol = newBoard.getColumnHeight(0);
        for(int i = 1; i < newBoard.getWidth(); ++i) {
            columnDisparityScore += Math.abs(newBoard.getColumnHeight(i) - prevCol) > 1 ? 2 : 0;
            columnDisparityScore += Math.abs(newBoard.getColumnHeight(i) - prevCol) > 2 ? 5 : 0;
            prevCol = newBoard.getColumnHeight(i);
        }

        // Count how many holes there are on the board
        for(int i = 0; i < newBoard.getWidth(); ++i) {
            for(int j = 0; j < newBoard.getHeight(); ++j) {
                if(newBoard.getGrid(i, j) == null && newBoard.getColumnHeight(i) > j) {
                    stuckSquareScore++;
                }
            }
        }

        // If the board is lost, then make sure we don't pick it
        if(getMaxHeight(newBoard) > 20) {
            return Integer.MIN_VALUE;
        }

        int score = 10000
                - (getMaxHeight(newBoard) * 20)
                - (columnDisparityConstant * columnDisparityScore)
                - (stuckSquareConstant * stuckSquareScore)
                + 50 * newBoard.getRowsCleared();

        //System.out.println(columnDisparityScore);
        return score;
    }

    // Check if a placement is in bounds
    public static boolean outOfBounds(Board board, int x, int y) {
        for(Point p : board.getCurrentPiece().getBody()) {
            if(x < 0 || y < 0 ||
                    x > board.getWidth() - 1 || y > board.getHeight()) {
                return true;
            }
        }
        return false;
    }

    private int getMaxHeight(Board board) {
        int max = 0;
        for(int i = 0; i < board.getWidth(); ++i) {
            max = Math.max(max, board.getColumnHeight(i));
        }
        return max;
    }
}
