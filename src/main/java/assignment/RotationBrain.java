package assignment;

import java.util.ArrayList;

public class RotationBrain implements Brain {
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
        return 100 - (newBoard.getMaxHeight() * 5);
    }
}
