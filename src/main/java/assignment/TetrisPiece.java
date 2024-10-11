package assignment;

import java.awt.*;
import java.util.Arrays;

/**
 * An immutable representation of a tetris piece in a particular rotation.
 * All operations on a TetrisPiece should be constant time, except for its
 * initial construction. This means that rotations should also be fast - calling
 * clockwisePiece() and counterclockwisePiece() should be constant time! You may
 * need to do pre-computation in the constructor to make this possible.
 */
public final class TetrisPiece implements Piece {

    /**
     * Construct a tetris piece of the given type. The piece should be in its spawn orientation,
     * i.e., a rotation index of 0.
     * You may freely add additional constructors, but please leave this one - it is used both in
     * the runner code and testing code.
     */
    private static final int NUM_ROTATIONS = 4;

    private Point[] body;
    private PieceType type;
    private int rotationIndex;
    private int[] skirt;
    private CircularLL rotations;
    private CircularLL.Node pieceNode;
    private int width;
    private int height;

    public TetrisPiece(PieceType type) {
        this(type, 0, type.getSpawnBody());
        computeRotations(body);
    }


    public TetrisPiece(PieceType type, int rindex, Point[] body) {
        this.type = type;
        this.body = body;
        rotationIndex = rindex;
        Dimension boundingBox = type.getBoundingBox();
        width = (int) boundingBox.getWidth();
        height = (int) boundingBox.getHeight();
        generateSkirt(body);
    }

    public void computeRotations(Point[] body) {
        rotations = new CircularLL();
        rotations.insert(this);
        CircularLL.Node curr = rotations.getHead();
        for (int i = 1; i < NUM_ROTATIONS; i++) {
            Point[] newBody = new Point[body.length];
            for (int j = 0; j < body.length; j++) {
                Point currPoint = curr.data.getBody()[j];
                // Compute new X and Y values for a 90 degree CW rotation
                int newX = (int)currPoint.getY();
                int newY = width - (int)currPoint.getX() - 1;
                newBody[j] = new Point(newX, newY);
            }
            rotations.insert(new TetrisPiece(type, i, newBody));
            curr = curr.next;
        }
        // Set rotations linked list for all nodes in current piece's rotations
        CircularLL.Node head = rotations.getHead();
        curr = head;
        do {
            curr.data.setNode(curr);
            curr.data.setRotations(rotations);
            curr = curr.next;
        } while (curr != head);
    }

    public void setNode(CircularLL.Node n) {
        pieceNode = n;
    }

    public void generateSkirt(Point[] body) {
        skirt = new int[width];
        Arrays.fill(skirt, Integer.MAX_VALUE);
        // Set skirt to the minimum y value in a given row
        for (Point p : body) {
            if (p.y < skirt[p.x]) {
                skirt[p.x] = p.y;
            }
        }
    }

    public void setRotations(CircularLL rotations) {
        this.rotations = rotations;
    }

    @Override
    public PieceType getType() {
        return type;
    }

    @Override
    public int getRotationIndex() {
        return rotationIndex;
    }

    public void setRotationIndex(int rindex) {
        rotationIndex = rindex;
    }

    @Override
    public Piece clockwisePiece() {
        return pieceNode.next.data;
    }

    @Override
    public Piece counterclockwisePiece() {
        return pieceNode.prev.data;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Point[] getBody() {
        return body;
    }

    @Override
    public int[] getSkirt() {
        return skirt;
    }

    public CircularLL getRotations() {
        return rotations;
    }

    @Override
    public boolean equals(Object other) {
        // Ignore objects which aren't also tetris pieces.
        if(!(other instanceof TetrisPiece)) return false;
        TetrisPiece otherPiece = (TetrisPiece) other;
        return this.rotationIndex == otherPiece.rotationIndex && this.type == otherPiece.type;
    }
}
