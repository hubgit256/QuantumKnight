package engine;

public class Main {
    public static void main(String[] args) {
        System.out.println("=========================");
        System.out.println("  QutanKnight Chess Engine");
        System.out.println("=========================");
        
        // Test knight from g1 (square 6)
        // Should show: e2, f3, h3
        AttackTables.printKnightAttacks(6);
        
        // Test knight from e4 (square 28)
        // Should show: c3, c5, d2, d6, f2, f6, g3, g5
        AttackTables.printKnightAttacks(28);
        
        // Test knight from a1 (square 0) - corner
        // Should show: b3, c2 only
        AttackTables.printKnightAttacks(0);
        
        // Test king from e1 (square 4)
        // Should show: d1, d2, e2, f1, f2
        AttackTables.printKingAttacks(4);
        
        // Test white pawn from e4 (square 28)
        // Should show: d5, f5
        AttackTables.printWhitePawnAttacks(28);
        
        // Show board
        System.out.println("--- Starting Position ---");
        Bitboard board = new Bitboard();
        board.printBoard();
    }
}