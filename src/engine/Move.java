package engine;

public class Move {
    int fromSquare;
    int toSquare;
    int pieceType;
    int capturedPiece;
    
    public Move(int from, int to, int piece, int captured) {
        this.fromSquare = from;
        this.toSquare = to;
        this.pieceType = piece;
        this.capturedPiece = captured;
    }
    
    public static String squareToString(int square) {
        int file = square % 8;
        int rank = square / 8;
        return "" + (char)('a' + file) + (rank + 1);
    }
    
    public String toString() {
        return squareToString(fromSquare) + squareToString(toSquare);
    }
}