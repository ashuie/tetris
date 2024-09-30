package assignment;

import java.awt.Point;

public class TetrisPieceTest {
    public static void main(String[] args) {
        TetrisPiece test = new TetrisPiece(Piece.PieceType.T);
        CircularLL rotations = test.getRotations();
        System.out.println("Rotations: \n" + rotations);
        CircularLL.Node curr = rotations.getHead();
        do {
            Point[] currPiece = curr.data.getBody();
            System.out.println("Body: ");
            for (Point x : currPiece) {
                System.out.print(x + " ");
            }
            System.out.println();
            System.out.println("Skirt: ");
            for (int j = 0; j < curr.data.getWidth(); j++) {
                System.out.print(curr.data.getSkirt()[j] + " ");
            }
            System.out.println();
            curr = curr.next;
        } while (curr != rotations.getHead());
    }
}