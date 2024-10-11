package assignment;

import java.awt.*;
import java.util.ArrayList;

public class FinalBrain implements Brain{
    private ArrayList<Board> options;
    private ArrayList<Board.Action> firstMoves;
    Board.Action lastMove = Board.Action.NOTHING;
    Board.Action lastLastMove = Board.Action.NOTHING;

    public Board.Action nextMove(Board currentBoard) {
        // Fill the our options array with versions of the new Board
        options = new ArrayList<>();
        firstMoves = new ArrayList<>();
        enumerateOptions(currentBoard);

        int best = 0;
        int bestIndex = 0;

        // Check all of the options and get the one with the highest score
        for (int i = 0; i < firstMoves.size(); i++) {
            int score = scoreBoard(options.get(i));
            if (score > best) {
                best = score;
                bestIndex = i;
            }
        }

        // Last move checks for redundant moves
        lastLastMove = lastMove;
        lastMove = firstMoves.get(bestIndex);

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

        options.add(currentBoard.testMove(Board.Action.DROP));
        firstMoves.add(Board.Action.DROP);

        // Add rotation drops and check if this move is redundant with our last moves
        Board rotationTest = currentBoard.testMove(Board.Action.CLOCKWISE);
        if(lastMove != Board.Action.COUNTERCLOCKWISE && lastLastMove != Board.Action.COUNTERCLOCKWISE &&
                lastLastMove != Board.Action.CLOCKWISE) {
            for(int numOrientations = 0; numOrientations < 2; ++numOrientations) {
                options.add(rotationTest.testMove(Board.Action.DROP));
                firstMoves.add(Board.Action.CLOCKWISE);
                rotationTest.move(Board.Action.CLOCKWISE);
            }
        }

        Board rotationTestCCW = currentBoard.testMove(Board.Action.COUNTERCLOCKWISE);
        if(lastMove != Board.Action.CLOCKWISE && lastLastMove != Board.Action.CLOCKWISE &&
                lastLastMove != Board.Action.COUNTERCLOCKWISE) {
            options.add(rotationTestCCW.testMove(Board.Action.DROP));
            firstMoves.add(Board.Action.COUNTERCLOCKWISE);
        }

        // Now we'll add all the places to the left we can DROP
        // We first make sure that our last move wasn't moving the opposite way

        if(lastMove != Board.Action.RIGHT) {
            Board left = currentBoard.testMove(Board.Action.LEFT);

            for(int numOrientations = 0; numOrientations < 4; numOrientations++) {
                while (left.getLastResult() == Board.Result.SUCCESS) {
                    options.add(left.testMove(Board.Action.DROP));

                    if(numOrientations == 0) {
                        firstMoves.add(Board.Action.LEFT);
                    }
                    else if(numOrientations == 1 || numOrientations == 2){
                        firstMoves.add(Board.Action.CLOCKWISE);
                    }

                    // We want to add moves that move down in case we can make a precise move
                    Board leftD = left.testMove(Board.Action.DOWN);
                    Board testLeftD = leftD.testMove(Board.Action.DOWN);
                    while(testLeftD.getLastResult() != Board.Result.PLACE && leftD.getLastResult() == Board.Result.SUCCESS) {
                        leftD.move(Board.Action.DOWN);
                        testLeftD.move(Board.Action.DOWN);
                    }

                    Board newBoard = leftD.testMove(Board.Action.LEFT);
                    options.add(newBoard.testMove(Board.Action.DROP));
                    newBoard = leftD.testMove(Board.Action.RIGHT);
                    options.add(newBoard.testMove(Board.Action.DROP));
                    newBoard = leftD.testMove(Board.Action.CLOCKWISE);
                    options.add(newBoard.testMove(Board.Action.DROP));
                    newBoard = leftD.testMove(Board.Action.COUNTERCLOCKWISE);
                    options.add(newBoard.testMove(Board.Action.DROP));
                    if(numOrientations == 0) {
                        firstMoves.add(Board.Action.LEFT);
                        firstMoves.add(Board.Action.LEFT);
                        firstMoves.add(Board.Action.LEFT);
                        firstMoves.add(Board.Action.LEFT);
                    }
                    else if(numOrientations == 1 || numOrientations == 2){
                        firstMoves.add(Board.Action.CLOCKWISE);
                        firstMoves.add(Board.Action.CLOCKWISE);
                        firstMoves.add(Board.Action.CLOCKWISE);
                        firstMoves.add(Board.Action.CLOCKWISE);
                    }
                    else {
                        firstMoves.add(Board.Action.COUNTERCLOCKWISE);
                        firstMoves.add(Board.Action.COUNTERCLOCKWISE);
                        firstMoves.add(Board.Action.COUNTERCLOCKWISE);
                        firstMoves.add(Board.Action.COUNTERCLOCKWISE);
                    }

                    left.move(Board.Action.LEFT);
                }
                left.move(Board.Action.CLOCKWISE);
            }
        }

        // Similarly, we add all the places to the right we can drop
        if(lastMove != Board.Action.LEFT) {
            Board right = currentBoard.testMove(Board.Action.RIGHT);

            for(int numOrientations = 0; numOrientations < 4; numOrientations++) {
                while (right.getLastResult() == Board.Result.SUCCESS) {
                    options.add(right.testMove(Board.Action.DROP));

                    if(numOrientations == 0) {
                        firstMoves.add(Board.Action.RIGHT);
                    }
                    else if(numOrientations == 1 || numOrientations == 2){
                        firstMoves.add(Board.Action.CLOCKWISE);
                    }
                    else {
                        firstMoves.add(Board.Action.COUNTERCLOCKWISE);
                    }

                    Board rightD = right.testMove(Board.Action.DOWN);
                    Board testRightD = rightD.testMove(Board.Action.DOWN);
                    while(testRightD.getLastResult() != Board.Result.PLACE && rightD.getLastResult() == Board.Result.SUCCESS) {
                        rightD.move(Board.Action.DOWN);
                        testRightD.move(Board.Action.DOWN);
                    }
                    Board newBoard = rightD.testMove(Board.Action.LEFT);
                    options.add(newBoard.testMove(Board.Action.DROP));
                    newBoard = rightD.testMove(Board.Action.RIGHT);
                    options.add(newBoard.testMove(Board.Action.DROP));
                    newBoard = rightD.testMove(Board.Action.CLOCKWISE);
                    options.add(newBoard.testMove(Board.Action.DROP));
                    newBoard = rightD.testMove(Board.Action.COUNTERCLOCKWISE);
                    options.add(newBoard.testMove(Board.Action.DROP));

                    if(numOrientations == 0) {
                        firstMoves.add(Board.Action.RIGHT);
                        firstMoves.add(Board.Action.RIGHT);
                        firstMoves.add(Board.Action.RIGHT);
                        firstMoves.add(Board.Action.RIGHT);
                    }
                    else if(numOrientations == 1 || numOrientations == 2){
                        firstMoves.add(Board.Action.CLOCKWISE);
                        firstMoves.add(Board.Action.CLOCKWISE);
                        firstMoves.add(Board.Action.CLOCKWISE);
                        firstMoves.add(Board.Action.CLOCKWISE);
                    }
                    else {
                        firstMoves.add(Board.Action.COUNTERCLOCKWISE);
                        firstMoves.add(Board.Action.COUNTERCLOCKWISE);
                        firstMoves.add(Board.Action.COUNTERCLOCKWISE);
                        firstMoves.add(Board.Action.COUNTERCLOCKWISE);
                    }

                    right.move(Board.Action.RIGHT);
                }
                right.move(Board.Action.CLOCKWISE);
            }
        }

        // Now we add moves that move down without dropping
        Board down = currentBoard.testMove(Board.Action.DOWN);
        Board downD = down.testMove(Board.Action.DOWN);
        while(downD.getLastResult() != Board.Result.PLACE && down.getLastResult() == Board.Result.SUCCESS) {
            down.move(Board.Action.DOWN);
            downD.move(Board.Action.DOWN);
        }

        Board downBoard = down.testMove(Board.Action.LEFT);
        options.add(downBoard.testMove(Board.Action.DROP));
        firstMoves.add(Board.Action.DOWN);
        downBoard = down.testMove(Board.Action.RIGHT);
        options.add(downBoard.testMove(Board.Action.DROP));
        firstMoves.add(Board.Action.DOWN);
        downBoard = down.testMove(Board.Action.CLOCKWISE);
        options.add(downBoard.testMove(Board.Action.DROP));
        firstMoves.add(Board.Action.DOWN);
        downBoard = down.testMove(Board.Action.COUNTERCLOCKWISE);
        options.add(downBoard.testMove(Board.Action.DROP));
        firstMoves.add(Board.Action.DOWN);
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
