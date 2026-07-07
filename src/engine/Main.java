package engine;

public class Main {
    public static void main(String[] args) {
        
        Bitboard board = new Bitboard();
        
        // Show starting position
        System.out.println("\n--- Starting Position ---");
        board.printBoard();
        
        // Make move: e2e4
        Move e2e4 = new Move(12, 28, Bitboard.WHITE_PAWN, Bitboard.EMPTY);
        System.out.println("\nPlaying: " + e2e4);
        board.makeMove(e2e4);
        board.printBoard();
        
        // Make move: e7e5
        Move e7e5 = new Move(52, 36, Bitboard.BLACK_PAWN, Bitboard.EMPTY);
        System.out.println("\nPlaying: " + e7e5);
        board.makeMove(e7e5);
        board.printBoard();
        
        // Make move: g1f3
        Move g1f3 = new Move(6, 21, Bitboard.WHITE_KNIGHT, Bitboard.EMPTY);
        System.out.println("\nPlaying: " + g1f3);
        board.makeMove(g1f3);
        board.printBoard();
    }
}