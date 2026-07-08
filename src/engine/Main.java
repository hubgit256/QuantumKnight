package engine;

public class Main {
    public static void main(String[] args) {
        
        Bitboard board = new Bitboard();
        
        // Show starting position
        System.out.println("\n--- Starting Position ---");
        board.printBoard();
        
        // Create moves using packed int format
        int e2e4 = Move.create(12, 28, Bitboard.WHITE_PAWN, Bitboard.EMPTY);
        int e7e5 = Move.create(52, 36, Bitboard.BLACK_PAWN, Bitboard.EMPTY);
        int g1f3 = Move.create(6, 21, Bitboard.WHITE_KNIGHT, Bitboard.EMPTY);
        int b8c6 = Move.create(57, 42, Bitboard.BLACK_KNIGHT, Bitboard.EMPTY);
        int f3e5 = Move.create(21, 36, Bitboard.WHITE_KNIGHT, Bitboard.BLACK_PAWN);
        
        // Play moves
        System.out.println("\nPlaying: " + Move.toString(e2e4));
        board.makeMove(e2e4);
        board.printBoard();
        
        System.out.println("\nPlaying: " + Move.toString(e7e5));
        board.makeMove(e7e5);
        board.printBoard();
        
        System.out.println("\nPlaying: " + Move.toString(g1f3));
        board.makeMove(g1f3);
        board.printBoard();
        
        System.out.println("\nPlaying: " + Move.toString(b8c6));
        board.makeMove(b8c6);
        board.printBoard();
        
        System.out.println("\nPlaying: " + Move.toString(f3e5));
        board.makeMove(f3e5);
        board.printBoard();
    }
}