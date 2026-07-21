package engine;

public class Main {
    public static void main(String[] args) {
        System.out.println("=========================");
        System.out.println("  QutanKnight Chess Engine");
        System.out.println("=========================");

        // Test knight from g1 (square 6)
        AttackTables.printKnightAttacks(6);

        // Test knight from e4 (square 28) — center, should have 8 moves
        System.out.println("\n--- Knight from e4 (center) ---");
        AttackTables.printKnightAttacks(28);

        // Test bishop from c1 (empty board)
        System.out.println("\n--- Bishop from c1 (empty) ---");
        AttackTables.printBishopAttacks(2, 0L);

        // Test bishop from c1 (pawn on d2 blocking)
        System.out.println("\n--- Bishop from c1 (pawn on d2) ---");
        AttackTables.printBishopAttacks(2, 1L << 11); // d2 = square 11

        // Test rook from a1 (empty board)
        System.out.println("\n--- Rook from a1 (empty) ---");
        AttackTables.printRookAttacks(0, 0L);

        // Test rook from a1 (blocked on a3 and c1)
        System.out.println("\n--- Rook from a1 (blocked on a3, c1) ---");
        long blockers = (1L << 16) | (1L << 2); // a3=16, c1=2
        AttackTables.printRookAttacks(0, blockers);

        // Test queen from d4 (center, empty board)
        System.out.println("\n--- Queen from d4 (empty) ---");
        long queenAttacks = AttackTables.queenAttacks(27, 0L); // d4 = 27
        AttackTables.printBitboard(queenAttacks);

        // Show board
        System.out.println("\n--- Starting Position ---");
        Bitboard board = new Bitboard();
        board.printBoard();
    }
}