package engine;

/**
 * Precomputed attack tables for non-sliding pieces (knight, king, pawn).
 * Sliding piece attacks (bishop, rook, queen) are computed on-the-fly.
 */
public class AttackTables {

    // ============================================
    // PRECOMPUTED ATTACK TABLES
    // ============================================

    public static final long[] KNIGHT_ATTACKS = new long[64];
    public static final long[] KING_ATTACKS   = new long[64];
    public static final long[][] PAWN_ATTACKS = new long[2][64]; // [0]=white, [1]=black

    static {
        initKnights();
        initKings();
        initPawns();
    }

    // ============================================
    // PUBLIC API
    // ============================================

    public static long knightAttacks(int square) {
        return KNIGHT_ATTACKS[square];
    }

    public static long kingAttacks(int square) {
        return KING_ATTACKS[square];
    }

    public static long pawnAttacks(int square, boolean isWhite) {
        return PAWN_ATTACKS[isWhite ? 0 : 1][square];
    }

    public static long bishopAttacks(int square, long occupied) {
        long attacks = 0L;
        int f = square % 8;
        int r = square / 8;

        // Up-Right
        for (int ff = f + 1, rr = r + 1; ff < 8 && rr < 8; ff++, rr++) {
            long target = 1L << (rr * 8 + ff);
            attacks |= target;
            if ((occupied & target) != 0) break;
        }
        // Up-Left
        for (int ff = f - 1, rr = r + 1; ff >= 0 && rr < 8; ff--, rr++) {
            long target = 1L << (rr * 8 + ff);
            attacks |= target;
            if ((occupied & target) != 0) break;
        }
        // Down-Right
        for (int ff = f + 1, rr = r - 1; ff < 8 && rr >= 0; ff++, rr--) {
            long target = 1L << (rr * 8 + ff);
            attacks |= target;
            if ((occupied & target) != 0) break;
        }
        // Down-Left
        for (int ff = f - 1, rr = r - 1; ff >= 0 && rr >= 0; ff--, rr--) {
            long target = 1L << (rr * 8 + ff);
            attacks |= target;
            if ((occupied & target) != 0) break;
        }
        return attacks;
    }

    public static long rookAttacks(int square, long occupied) {
        long attacks = 0L;
        int f = square % 8;
        int r = square / 8;

        // Up
        for (int rr = r + 1; rr < 8; rr++) {
            long target = 1L << (rr * 8 + f);
            attacks |= target;
            if ((occupied & target) != 0) break;
        }
        // Down
        for (int rr = r - 1; rr >= 0; rr--) {
            long target = 1L << (rr * 8 + f);
            attacks |= target;
            if ((occupied & target) != 0) break;
        }
        // Right
        for (int ff = f + 1; ff < 8; ff++) {
            long target = 1L << (r * 8 + ff);
            attacks |= target;
            if ((occupied & target) != 0) break;
        }
        // Left
        for (int ff = f - 1; ff >= 0; ff--) {
            long target = 1L << (r * 8 + ff);
            attacks |= target;
            if ((occupied & target) != 0) break;
        }
        return attacks;
    }

    public static long queenAttacks(int square, long occupied) {
        return bishopAttacks(square, occupied) | rookAttacks(square, occupied);
    }

    // ============================================
    // INITIALIZATION
    // ============================================

    private static void initKnights() {
        int[][] moves = {
            {1,2}, {2,1}, {2,-1}, {1,-2},
            {-1,-2}, {-2,-1}, {-2,1}, {-1,2}
        };
        for (int sq = 0; sq < 64; sq++) {
            int f = sq % 8, r = sq / 8;
            long bits = 0L;
            for (int[] m : moves) {
                int nf = f + m[0], nr = r + m[1];
                if (nf >= 0 && nf < 8 && nr >= 0 && nr < 8)
                    bits |= (1L << (nr * 8 + nf));
            }
            KNIGHT_ATTACKS[sq] = bits;
        }
    }

    private static void initKings() {
        int[][] moves = {
            {1,1}, {1,0}, {1,-1}, {0,-1},
            {-1,-1}, {-1,0}, {-1,1}, {0,1}
        };
        for (int sq = 0; sq < 64; sq++) {
            int f = sq % 8, r = sq / 8;
            long bits = 0L;
            for (int[] m : moves) {
                int nf = f + m[0], nr = r + m[1];
                if (nf >= 0 && nf < 8 && nr >= 0 && nr < 8)
                    bits |= (1L << (nr * 8 + nf));
            }
            KING_ATTACKS[sq] = bits;
        }
    }

    private static void initPawns() {
        for (int sq = 0; sq < 64; sq++) {
            int f = sq % 8, r = sq / 8;
            long w = 0L, b = 0L;
            if (r < 7) {
                if (f > 0) w |= (1L << (sq + 7));
                if (f < 7) w |= (1L << (sq + 9));
            }
            if (r > 0) {
                if (f > 0) b |= (1L << (sq - 9));
                if (f < 7) b |= (1L << (sq - 7));
            }
            PAWN_ATTACKS[0][sq] = w;
            PAWN_ATTACKS[1][sq] = b;
        }
    }

    // ============================================
    // DEBUG
    // ============================================

    public static void printKnightAttacks(int sq) {
        System.out.println("Knight from " + Move.squareToString(sq) + ":");
        printBitboard(KNIGHT_ATTACKS[sq]);
    }

    public static void printBishopAttacks(int sq, long occ) {
        System.out.println("Bishop from " + Move.squareToString(sq) + ":");
        printBitboard(bishopAttacks(sq, occ));
    }

    public static void printRookAttacks(int sq, long occ) {
        System.out.println("Rook from " + Move.squareToString(sq) + ":");
        printBitboard(rookAttacks(sq, occ));
    }

    public static void printBitboard(long bb) {
        System.out.println("  +-----------------+");
        for (int r = 7; r >= 0; r--) {
            System.out.print((r + 1) + " | ");
            for (int f = 0; f < 8; f++)
                System.out.print(((bb & (1L << (r * 8 + f))) != 0) ? "X " : ". ");
            System.out.println("| " + (r + 1));
        }
        System.out.println("  +-----------------+");
        System.out.println("    a b c d e f g h");
    }
}