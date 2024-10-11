package assignment;

import java.awt.*;
import java.util.ArrayList;

// USE THIS ONE FOR NOW
public class BigBrain implements Brain {
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
        if(firstMoves.get(bestIndex) == Board.Action.DROP) {
            System.out.println(scoreBoard(options.get(bestIndex)));
        }
        return firstMoves.get(bestIndex);
    }

    /**
     * Test all of the places we can put the current Piece.
     * Since this is just a Lame Brain, we aren't going to do smart
     * things like rotating pieces.
     */
    private void enumerateOptions(Board currentBoard) {
        // We can always drop our current Piece
        options.add(currentBoard.testMove(Board.Action.DROP));
        firstMoves.add(Board.Action.DROP);

        // Now we'll add all the places to the left we can DROP
        Board left = currentBoard.testMove(Board.Action.LEFT);

        for(int numOrientations = 0; numOrientations < 4; numOrientations++) {
            while (left.getLastResult() == Board.Result.SUCCESS) {
                options.add(left.testMove(Board.Action.DROP));
                if(numOrientations == 0) {
                    firstMoves.add(Board.Action.LEFT);
                }
                else {
                    firstMoves.add(Board.Action.CLOCKWISE);
                }
                left.move(Board.Action.LEFT);
            }
            left.move(Board.Action.CLOCKWISE);
        }

        Board right = currentBoard.testMove(Board.Action.RIGHT);
        for(int numOrientations = 0; numOrientations < 4; numOrientations++) {
            while (right.getLastResult() == Board.Result.SUCCESS) {
                options.add(right.testMove(Board.Action.DROP));
                if(numOrientations == 0) {
                    firstMoves.add(Board.Action.RIGHT);
                }
                else {
                    firstMoves.add(Board.Action.CLOCKWISE);
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
    private int scoreBoard(Board newBoard) {
        final int columnDisparityConstant = 30;
        int columnDisparityScore = 0;

        final int stuckSquareConstant = 80;
        int stuckSquareScore = 0;

        int prevCol = newBoard.getColumnHeight(0);
        for(int i = 1; i < newBoard.getWidth(); ++i) {
            columnDisparityScore += newBoard.getColumnHeight(i) - prevCol > 2 ? 5 : 0;
            prevCol = newBoard.getColumnHeight(i);
        }

        for(int i = 0; i < newBoard.getWidth(); ++i) {
            for(int j = 0; j < newBoard.getHeight(); ++j) {
                if(newBoard.getGrid(i, j) == null && newBoard.getColumnHeight(i) > j) {
                    stuckSquareScore++;
                }
            }
        }

        if(newBoard.getMaxHeight() > 20) {
            return 1;
        }

        int score = 10000 - (newBoard.getMaxHeight() * 5) - (columnDisparityConstant * columnDisparityScore)
                - (stuckSquareConstant * stuckSquareScore);

        return score;
    }

    private boolean outOfBounds(Board board, int x, int y) {
        for(Point p : board.getCurrentPiece().getBody()) {
            if(x < 0 || y < 0 ||
                    x > board.getWidth() - 1 || y > board.getHeight()) {
                return true;
            }
        }
        return false;
    }
}
