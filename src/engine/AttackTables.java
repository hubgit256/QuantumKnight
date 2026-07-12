package engine;

public class AttackTables {
    
    // ===== KNIGHT ATTACKS =====
    public static final long[] KNIGHT_ATTACKS = new long[64];
    
    // ===== KING ATTACKS =====
    public static final long[] KING_ATTACKS = new long[64];
    
    // ===== PAWN ATTACKS =====
    // [0][square] = white pawn attacks, [1][square] = black pawn attacks
    public static final long[][] PAWN_ATTACKS = new long[2][64];
    
    // Static initializer — runs ONCE when class loads
    static {
        initKnightAttacks();
        initKingAttacks();
        initPawnAttacks();
    }
    
    // ===== INITIALIZE KNIGHT ATTACKS =====
    private static void initKnightAttacks() {
        // All 8 knight moves as (deltaFile, deltaRank)
        int[][] knightMoves = {
            {1, 2}, {2, 1}, {2, -1}, {1, -2},
            {-1, -2}, {-2, -1}, {-2, 1}, {-1, 2}
        };
        
        for (int square = 0; square < 64; square++) {
            int file = square % 8;
            int rank = square / 8;
            
            long attacks = 0L;
            
            for (int[] move : knightMoves) {
                int newFile = file + move[0];
                int newRank = rank + move[1];
                
                if (newFile >= 0 && newFile < 8 && newRank >= 0 && newRank < 8) {
                    attacks |= (1L << (newRank * 8 + newFile));
                }
            }
            
            KNIGHT_ATTACKS[square] = attacks;
        }
    }
    
    // ===== INITIALIZE KING ATTACKS =====
    private static void initKingAttacks() {
        // All 8 king moves (surrounding squares)
        int[][] kingMoves = {
            {1, 1}, {1, 0}, {1, -1}, {0, -1},
            {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}
        };
        
        for (int square = 0; square < 64; square++) {
            int file = square % 8;
            int rank = square / 8;
            
            long attacks = 0L;
            
            for (int[] move : kingMoves) {
                int newFile = file + move[0];
                int newRank = rank + move[1];
                
                if (newFile >= 0 && newFile < 8 && newRank >= 0 && newRank < 8) {
                    attacks |= (1L << (newRank * 8 + newFile));
                }
            }
            
            KING_ATTACKS[square] = attacks;
        }
    }
    
    // ===== INITIALIZE PAWN ATTACKS =====
    private static void initPawnAttacks() {
        for (int square = 0; square < 64; square++) {
            int file = square % 8;
            int rank = square / 8;
            
            // White pawn attacks (move UP: rank + 1, file ± 1)
            long whiteAttacks = 0L;
            if (rank < 7) {
                if (file > 0) whiteAttacks |= (1L << (square + 7));   // up-left
                if (file < 7) whiteAttacks |= (1L << (square + 9));   // up-right
            }
            PAWN_ATTACKS[0][square] = whiteAttacks;
            
            // Black pawn attacks (move DOWN: rank - 1, file ± 1)
            long blackAttacks = 0L;
            if (rank > 0) {
                if (file > 0) blackAttacks |= (1L << (square - 9));   // down-left
                if (file < 7) blackAttacks |= (1L << (square - 7));   // down-right
            }
            PAWN_ATTACKS[1][square] = blackAttacks;
        }
    }
    
    // ===== DEBUG: PRINT ATTACKS =====
    public static void printKnightAttacks(int square) {
        System.out.println("Knight attacks from " + Move.squareToString(square) + ":");
        printBitboard(KNIGHT_ATTACKS[square]);
    }
    
    public static void printKingAttacks(int square) {
        System.out.println("King attacks from " + Move.squareToString(square) + ":");
        printBitboard(KING_ATTACKS[square]);
    }
    
    public static void printWhitePawnAttacks(int square) {
        System.out.println("White pawn attacks from " + Move.squareToString(square) + ":");
        printBitboard(PAWN_ATTACKS[0][square]);
    }
    
    public static void printBlackPawnAttacks(int square) {
        System.out.println("Black pawn attacks from " + Move.squareToString(square) + ":");
        printBitboard(PAWN_ATTACKS[1][square]);
    }
    
    public static void printBitboard(long bitboard) {
        System.out.println("  +-----------------+");
        for (int rank = 7; rank >= 0; rank--) {
            System.out.print((rank + 1) + " | ");
            for (int file = 0; file < 8; file++) {
                int square = rank * 8 + file;
                long mask = 1L << square;
                System.out.print(((bitboard & mask) != 0) ? "X " : ". ");
            }
            System.out.println("| " + (rank + 1));
        }
        System.out.println("  +-----------------+");
        System.out.println("    a b c d e f g h");
    }
}