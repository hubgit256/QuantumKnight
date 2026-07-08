package engine;

public class Move {
    
    // Move type constants
    public static final int NORMAL = 0;
    public static final int CASTLE = 1;
    public static final int EN_PASSANT = 2;
    public static final int PROMOTION = 3;
    
    // Create a packed move integer
    public static int create(int from, int to, int piece, int captured, int promo, int type) {
        return from | (to << 6) | (piece << 12) | (captured << 16) | (promo << 20) | (type << 24);
    }
    
    // Simple create for normal moves
    public static int create(int from, int to, int piece, int captured) {
        return create(from, to, piece, captured, 0, NORMAL);
    }
    
    // Extract fields from packed move
    public static int getFrom(int move) { return move & 0x3F; }
    public static int getTo(int move) { return (move >>> 6) & 0x3F; }
    public static int getPiece(int move) { return (move >>> 12) & 0xF; }
    public static int getCaptured(int move) { return (move >>> 16) & 0xF; }
    public static int getPromotion(int move) { return (move >>> 20) & 0xF; }
    public static int getType(int move) { return (move >>> 24) & 0x3; }
    
    // Convert square to notation
    public static String squareToString(int square) {
        int file = square % 8;
        int rank = square / 8;
        return "" + (char)('a' + file) + (rank + 1);
    }
    
    // Convert move to readable string
    public static String toString(int move) {
        return squareToString(getFrom(move)) + squareToString(getTo(move));
    }
}