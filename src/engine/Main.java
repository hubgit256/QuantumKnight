package engine;

public class Main {
    public static void main(String[] args) {

        Bitboard board = new Bitboard();
        board.printBoard();

        // Generate all moves
        int[] moves = MoveGenerator.generateMoves(board);
        System.out.println("\n--- All Legal Moves for White ---");
        System.out.println("Total: " + moves.length + " moves");
        for (int move : moves) {
            System.out.println("  " + Move.toString(move));
        }
        // In Main.java, add:
        board.makeMove(Move.create(12, 28, Bitboard.WHITE_PAWN, Bitboard.EMPTY)); // e2e4
        board.printBoard();
        int[] moves2 = MoveGenerator.generateMoves(board);
        System.out.println("\nBlack's moves after e4: " + moves2.length);
        for (int move : moves2) {
            System.out.println("  " + Move.toString(move));
        }
        board.makeMove(Move.create(62, 45, Bitboard.BLACK_KNIGHT, Bitboard.EMPTY)); // g8f6
        board.printBoard();
        int[] moves3 = MoveGenerator.generateMoves(board);
        System.out.println("\nWhite's moves after g8f6: " + moves3.length);
        for (int move : moves3) {
            System.out.println("  " + Move.toString(move));
        }
    }
}