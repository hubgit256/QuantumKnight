package engine;

public class Main {
    public static void main(String[] args) {
        System.out.println("=========================");
        System.out.println("  QutanKnight Chess Engine");
        System.out.println("=========================");
        
        // Create a board
        Bitboard board = new Bitboard();
        board.printBoard();
        
        // Test bishop attacks from c1 (square 2) — empty board
        System.out.println("\n--- Bishop from c1 (empty board) ---");
        AttackTables.printBishopAttacks(2, 0L);
        
        // Test bishop attacks from c1 — with pawn on d2 blocking
        System.out.println("\n--- Bishop from c1 (pawn on d2) ---");
        long pawnOnD2 = 1L << 11;  // d2 = square 11
        AttackTables.printBishopAttacks(2, pawnOnD2);
        
        // Test rook attacks from a1 — empty board
        System.out.println("\n--- Rook from a1 (empty board) ---");
        AttackTables.printRookAttacks(0, 0L);
        
        // Test queen attacks from d1 (square 3)
        System.out.println("\n--- Queen from d1 (empty board) ---");
        long queenAttacks = AttackTables.getQueenAttacks(3, 0L);
        AttackTables.printBitboard(queenAttacks);
    }
}