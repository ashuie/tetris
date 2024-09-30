package assignment;


public class CircularLL {
    static class Node {
        TetrisPiece data;
        Node next;
        Node prev;

        public Node(TetrisPiece data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }

        public String toString() {
            return "Type:" + data.getType() + " Rotation: " + data.getRotationIndex();
        }
    }

    private Node head;

    public void insert(TetrisPiece data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = newNode;
            newNode.prev = newNode.next = newNode;
        }
        else {
            Node last = head.prev;
            newNode.next = head;
            newNode.prev = last;
            head.prev = newNode;
            last.next = newNode;
        }
    }

    public Node getHead() {
        return head;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        Node curr = head;
        do {
            s.append("Node: ").append(curr).append("\n");
            curr = curr.next;
        } while (curr != head);
        return s.toString();
    }
}