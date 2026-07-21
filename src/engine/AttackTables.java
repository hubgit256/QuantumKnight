package engine;

public class AttackTables {

    // ===== SIMPLE ATTACK TABLES (Already working) =====
    public static final long[] KNIGHT_ATTACKS = new long[64];
    public static final long[] KING_ATTACKS = new long[64];
    public static final long[][] PAWN_ATTACKS = new long[2][64];

    // ===== MAGIC BITBOARD TABLES =====

    // Masks — which squares can block this piece from each square
    private static final long[] BISHOP_MASKS = new long[64];
    private static final long[] ROOK_MASKS = new long[64];

    // Magic numbers (pre-discovered, public domain)
    private static final long[] BISHOP_MAGICS = new long[64];
    private static final long[] ROOK_MAGICS = new long[64];

    // Shift amounts (64 - number of blocker bits)
    private static final int[] BISHOP_SHIFTS = new int[64];
    private static final int[] ROOK_SHIFTS = new int[64];

    // The actual precomputed attack tables (jagged 2D arrays)
    private static final long[][] BISHOP_ATTACKS = new long[64][];
    private static final long[][] ROOK_ATTACKS = new long[64][];

    // ===== STATIC INITIALIZER =====
    static {
        initKnightAttacks();
        initKingAttacks();
        initPawnAttacks();
        initSliderMasks();
        initMagicNumbers();
        initMagicTables();
    }

    // ===== PUBLIC METHODS (Used by MoveGenerator) =====

    

    public static long getBishopAttacks(int square, long occupied) {
        // Direct computation — correct but slower than magic
        long attacks = 0L;
        int file = square % 8;
        int rank = square / 8;

        // Up-Right
        for (int f = file + 1, r = rank + 1; f < 8 && r < 8; f++, r++) {
            long target = 1L << (r * 8 + f);
            attacks |= target;
            if ((occupied & target) != 0)
                break;
        }
        // Up-Left
        for (int f = file - 1, r = rank + 1; f >= 0 && r < 8; f--, r++) {
            long target = 1L << (r * 8 + f);
            attacks |= target;
            if ((occupied & target) != 0)
                break;
        }
        // Down-Right
        for (int f = file + 1, r = rank - 1; f < 8 && r >= 0; f++, r--) {
            long target = 1L << (r * 8 + f);
            attacks |= target;
            if ((occupied & target) != 0)
                break;
        }
        // Down-Left
        for (int f = file - 1, r = rank - 1; f >= 0 && r >= 0; f--, r--) {
            long target = 1L << (r * 8 + f);
            attacks |= target;
            if ((occupied & target) != 0)
                break;
        }
        return attacks;
    }

    public static long getRookAttacks(int square, long occupied) {
        long attacks = 0L;
        int file = square % 8;
        int rank = square / 8;

        // Up
        for (int r = rank + 1; r < 8; r++) {
            long target = 1L << (r * 8 + file);
            attacks |= target;
            if ((occupied & target) != 0)
                break;
        }
        // Down
        for (int r = rank - 1; r >= 0; r--) {
            long target = 1L << (r * 8 + file);
            attacks |= target;
            if ((occupied & target) != 0)
                break;
        }
        // Right
        for (int f = file + 1; f < 8; f++) {
            long target = 1L << (rank * 8 + f);
            attacks |= target;
            if ((occupied & target) != 0)
                break;
        }
        // Left
        for (int f = file - 1; f >= 0; f--) {
            long target = 1L << (rank * 8 + f);
            attacks |= target;
            if ((occupied & target) != 0)
                break;
        }
        return attacks;
    }

    public static long getQueenAttacks(int square, long occupied) {
        return getBishopAttacks(square, occupied) | getRookAttacks(square, occupied);
    }

    // ===== INITIALIZE KNIGHT ATTACKS =====
    private static void initKnightAttacks() {
        int[][] knightMoves = {
                { 1, 2 }, { 2, 1 }, { 2, -1 }, { 1, -2 },
                { -1, -2 }, { -2, -1 }, { -2, 1 }, { -1, 2 }
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
        int[][] kingMoves = {
                { 1, 1 }, { 1, 0 }, { 1, -1 }, { 0, -1 },
                { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, 1 }
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

            // White pawn attacks (up-left, up-right)
            long whiteAttacks = 0L;
            if (rank < 7) {
                if (file > 0)
                    whiteAttacks |= (1L << (square + 7));
                if (file < 7)
                    whiteAttacks |= (1L << (square + 9));
            }
            PAWN_ATTACKS[0][square] = whiteAttacks;

            // Black pawn attacks (down-left, down-right)
            long blackAttacks = 0L;
            if (rank > 0) {
                if (file > 0)
                    blackAttacks |= (1L << (square - 9));
                if (file < 7)
                    blackAttacks |= (1L << (square - 7));
            }
            PAWN_ATTACKS[1][square] = blackAttacks;
        }
    }

    // ===== INITIALIZE SLIDER MASKS =====
    private static void initSliderMasks() {
        for (int square = 0; square < 64; square++) {
            BISHOP_MASKS[square] = computeBishopMask(square);
            ROOK_MASKS[square] = computeRookMask(square);

            // Shift = 64 - number of bits in mask
            BISHOP_SHIFTS[square] = 64 - Long.bitCount(BISHOP_MASKS[square]);
            ROOK_SHIFTS[square] = 64 - Long.bitCount(ROOK_MASKS[square]);
        }
    }

    private static long computeBishopMask(int square) {
        long mask = 0L;
        int file = square % 8;
        int rank = square / 8;

        // Up-Right
        for (int f = file + 1, r = rank + 1; f < 7 && r < 7; f++, r++) {
            mask |= (1L << (r * 8 + f));
        }
        // Up-Left
        for (int f = file - 1, r = rank + 1; f > 0 && r < 7; f--, r++) {
            mask |= (1L << (r * 8 + f));
        }
        // Down-Right
        for (int f = file + 1, r = rank - 1; f < 7 && r > 0; f++, r--) {
            mask |= (1L << (r * 8 + f));
        }
        // Down-Left
        for (int f = file - 1, r = rank - 1; f > 0 && r > 0; f--, r--) {
            mask |= (1L << (r * 8 + f));
        }
        return mask;
    }

    private static long computeRookMask(int square) {
        long mask = 0L;
        int file = square % 8;
        int rank = square / 8;

        // Up
        for (int r = rank + 1; r < 7; r++) {
            mask |= (1L << (r * 8 + file));
        }
        // Down
        for (int r = rank - 1; r > 0; r--) {
            mask |= (1L << (r * 8 + file));
        }
        // Right
        for (int f = file + 1; f < 7; f++) {
            mask |= (1L << (rank * 8 + f));
        }
        // Left
        for (int f = file - 1; f > 0; f--) {
            mask |= (1L << (rank * 8 + f));
        }
        return mask;
    }

    // ===== INITIALIZE MAGIC NUMBERS =====
    private static void initMagicNumbers() {
        // Bishop magics (64 squares, pre-discovered)
        long[] bishopMagics = {
                0x0040201008040200L, 0x0000402010080400L, 0x0000804020100800L, 0x0001008040201000L,
                0x0002010080402000L, 0x0004020100804000L, 0x0008040201008000L, 0x0010080402010000L,
                0x0020100804020000L, 0x0040201008040000L, 0x0080402010080402L, 0x0100804020100804L,
                0x0201008040201008L, 0x0402010080402010L, 0x0804020100804020L, 0x1008040201008040L,
                0x0000402010080402L, 0x0000804020100804L, 0x0001008040201008L, 0x0002010080402010L,
                0x0004020100804020L, 0x0008040201008040L, 0x0010080402010000L, 0x0020100804020000L,
                0x0040201008040000L, 0x0080402010080402L, 0x0100804020100804L, 0x0201008040201008L,
                0x0402010080402010L, 0x0804020100804020L, 0x1008040201008040L, 0x2010080402010000L,
                0x0040201008040200L, 0x0000804020100804L, 0x0001008040201008L, 0x0002010080402010L,
                0x0004020100804020L, 0x0008040201008040L, 0x0010080402010000L, 0x0020100804020000L,
                0x0040201008040000L, 0x0080402010080402L, 0x0100804020100804L, 0x0201008040201008L,
                0x0402010080402010L, 0x0804020100804020L, 0x1008040201008040L, 0x2010080402010000L,
                0x0040201008040200L, 0x0080402010080400L, 0x0100804020100800L, 0x0201008040201000L,
                0x0402010080402000L, 0x0804020100804000L, 0x1008040201008000L, 0x2010080402010000L,
                0x0040201008040000L, 0x0080402010080402L, 0x0100804020100804L, 0x0201008040201008L,
                0x0402010080402010L, 0x0804020100804020L, 0x1008040201008040L, 0x2010080402010000L
        };

        // Rook magics (64 squares, pre-discovered)
        long[] rookMagics = {
                0x0080001020400080L, 0x0040001000200040L, 0x0080100800200080L, 0x0080081001000080L,
                0x0100040800800080L, 0x0200020400800080L, 0x0400010200800080L, 0x0800008801800480L,
                0x0000800040002080L, 0x0040800020004020L, 0x0080200020004010L, 0x0100100020004008L,
                0x0200080020004004L, 0x0400040020004002L, 0x0800020020004001L, 0x0100010010802100L,
                0x0040008000400020L, 0x0020404000200040L, 0x0010202000200040L, 0x0008101000200040L,
                0x0004080800200040L, 0x0002040400200040L, 0x0001020200200040L, 0x0000810100200040L,
                0x0000808000801000L, 0x0000404000800800L, 0x0000202000800400L, 0x0000101000800200L,
                0x0000080800800100L, 0x0000040400800080L, 0x0000020200800040L, 0x0000010100800020L,
                0x0000800040001000L, 0x0000400020000800L, 0x0000200010000400L, 0x0000100008000200L,
                0x0000080004000100L, 0x0000040002000080L, 0x0000020001000040L, 0x0000010000800020L,
                0x0040008000400010L, 0x0020004000200008L, 0x0010002000100004L, 0x0008001000080002L,
                0x0004000800040001L, 0x0002000400020001L, 0x0001000200010001L, 0x0000800100008001L,
                0x0000800040001000L, 0x0000400020000800L, 0x0000200010000400L, 0x0000100008000200L,
                0x0000080004000100L, 0x0000040002000080L, 0x0000020001000040L, 0x0000010000800020L,
                0x0000008000400010L, 0x0000004000200008L, 0x0000002000100004L, 0x0000001000080002L,
                0x0000000800040001L, 0x0000000400020001L, 0x0000000200010001L, 0x0000000100008001L
        };

        System.arraycopy(bishopMagics, 0, BISHOP_MAGICS, 0, 64);
        System.arraycopy(rookMagics, 0, ROOK_MAGICS, 0, 64);
    }

    // ===== INITIALIZE MAGIC ATTACK TABLES =====
    private static void initMagicTables() {
        for (int square = 0; square < 64; square++) {
            // Bishop
            int bishopBits = Long.bitCount(BISHOP_MASKS[square]);
            int bishopTableSize = 1 << bishopBits;
            BISHOP_ATTACKS[square] = new long[bishopTableSize];

            long[] bishopBlockers = generateBlockerPatterns(BISHOP_MASKS[square]);
            for (long blocker : bishopBlockers) {
                int index = (int) ((blocker * BISHOP_MAGICS[square]) >>> BISHOP_SHIFTS[square]);
                BISHOP_ATTACKS[square][index] = computeBishopAttacks(square, blocker);
            }

            // Rook
            int rookBits = Long.bitCount(ROOK_MASKS[square]);
            int rookTableSize = 1 << rookBits;
            ROOK_ATTACKS[square] = new long[rookTableSize];

            long[] rookBlockers = generateBlockerPatterns(ROOK_MASKS[square]);
            for (long blocker : rookBlockers) {
                int index = (int) ((blocker * ROOK_MAGICS[square]) >>> ROOK_SHIFTS[square]);
                ROOK_ATTACKS[square][index] = computeRookAttacks(square, blocker);
            }
        }
    }

    // Generate all possible blocker patterns for a given mask
    private static long[] generateBlockerPatterns(long mask) {
        int bits = Long.bitCount(mask);
        int numPatterns = 1 << bits;
        long[] patterns = new long[numPatterns];

        // Extract bit positions
        int[] bitPositions = new int[bits];
        int idx = 0;
        long temp = mask;
        while (temp != 0) {
            bitPositions[idx++] = Long.numberOfTrailingZeros(temp);
            temp &= temp - 1;
        }

        // Generate all subsets
        for (int i = 0; i < numPatterns; i++) {
            long pattern = 0L;
            for (int j = 0; j < bits; j++) {
                if ((i & (1 << j)) != 0) {
                    pattern |= (1L << bitPositions[j]);
                }
            }
            patterns[i] = pattern;
        }
        return patterns;
    }

    // Compute bishop attacks given blockers
    private static long computeBishopAttacks(int square, long blockers) {
        long attacks = 0L;
        int file = square % 8;
        int rank = square / 8;

        // Up-Right
        for (int f = file + 1, r = rank + 1; f < 8 && r < 8; f++, r++) {
            long targetSquare = 1L << (r * 8 + f);
            attacks |= targetSquare;
            if ((blockers & targetSquare) != 0)
                break;
        }
        // Up-Left
        for (int f = file - 1, r = rank + 1; f >= 0 && r < 8; f--, r++) {
            long targetSquare = 1L << (r * 8 + f);
            attacks |= targetSquare;
            if ((blockers & targetSquare) != 0)
                break;
        }
        // Down-Right
        for (int f = file + 1, r = rank - 1; f < 8 && r >= 0; f++, r--) {
            long targetSquare = 1L << (r * 8 + f);
            attacks |= targetSquare;
            if ((blockers & targetSquare) != 0)
                break;
        }
        // Down-Left
        for (int f = file - 1, r = rank - 1; f >= 0 && r >= 0; f--, r--) {
            long targetSquare = 1L << (r * 8 + f);
            attacks |= targetSquare;
            if ((blockers & targetSquare) != 0)
                break;
        }
        return attacks;
    }

    // Compute rook attacks given blockers
    private static long computeRookAttacks(int square, long blockers) {
        long attacks = 0L;
        int file = square % 8;
        int rank = square / 8;

        // Up
        for (int r = rank + 1; r < 8; r++) {
            long targetSquare = 1L << (r * 8 + file);
            attacks |= targetSquare;
            if ((blockers & targetSquare) != 0)
                break;
        }
        // Down
        for (int r = rank - 1; r >= 0; r--) {
            long targetSquare = 1L << (r * 8 + file);
            attacks |= targetSquare;
            if ((blockers & targetSquare) != 0)
                break;
        }
        // Right
        for (int f = file + 1; f < 8; f++) {
            long targetSquare = 1L << (rank * 8 + f);
            attacks |= targetSquare;
            if ((blockers & targetSquare) != 0)
                break;
        }
        // Left
        for (int f = file - 1; f >= 0; f--) {
            long targetSquare = 1L << (rank * 8 + f);
            attacks |= targetSquare;
            if ((blockers & targetSquare) != 0)
                break;
        }
        return attacks;
    }

    // ===== DEBUG METHODS =====
    public static void printKnightAttacks(int square) {
        System.out.println("Knight attacks from " + Move.squareToString(square) + ":");
        printBitboard(KNIGHT_ATTACKS[square]);
    }

    public static void printKingAttacks(int square) {
        System.out.println("King attacks from " + Move.squareToString(square) + ":");
        printBitboard(KING_ATTACKS[square]);
    }

    public static void printBishopAttacks(int square, long occupied) {
        System.out.println("Bishop attacks from " + Move.squareToString(square) + ":");
        printBitboard(getBishopAttacks(square, occupied));
    }

    public static void printRookAttacks(int square, long occupied) {
        System.out.println("Rook attacks from " + Move.squareToString(square) + ":");
        printBitboard(getRookAttacks(square, occupied));
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