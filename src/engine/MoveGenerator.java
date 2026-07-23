package engine;

/**
 * Generates all legal moves for the side to move.
 * Uses AttackTables for attack patterns, then applies game rules.
 */
public class MoveGenerator {

    private static final int MAX_MOVES = 256;

    /**
     * Generate all legal moves for the current side to move.
     * Returns an array of packed int moves.
     */
    public static int[] generateMoves(Bitboard board) {
        int[] moves = new int[MAX_MOVES];
        int count = 0;

        if (board.whiteToMove) {
            count = generatePawnMoves(board, moves, count, true);
            count = generateKnightMoves(board, moves, count, true);
            count = generateBishopMoves(board, moves, count, true);
            count = generateRookMoves(board, moves, count, true);
            count = generateQueenMoves(board, moves, count, true);
            count = generateKingMoves(board, moves, count, true);
        } else {
            count = generatePawnMoves(board, moves, count, false);
            count = generateKnightMoves(board, moves, count, false);
            count = generateBishopMoves(board, moves, count, false);
            count = generateRookMoves(board, moves, count, false);
            count = generateQueenMoves(board, moves, count, false);
            count = generateKingMoves(board, moves, count, false);
        }

        // Trim to exact size
        int[] result = new int[count];
        System.arraycopy(moves, 0, result, 0, count);
        return result;
    }

    // ============================================
    // PAWN MOVES
    // ============================================

    private static int generatePawnMoves(Bitboard board, int[] moves, int count, boolean isWhite) {
        long pawns = isWhite ? board.whitePawns : board.blackPawns;
        long ownPieces = isWhite ? board.whitePieces : board.blackPieces;
        long enemyPieces = isWhite ? board.blackPieces : board.whitePieces;
        long empty = board.emptySquares;
        int pieceType = isWhite ? Bitboard.WHITE_PAWN : Bitboard.BLACK_PAWN;
        int forward = isWhite ? 8 : -8;
        int startRank = isWhite ? 1 : 6;      // rank 2 for white, rank 7 for black
        int promoRank = isWhite ? 6 : 1;      // rank 7 for white, rank 2 for black

        while (pawns != 0) {
            int from = Long.numberOfTrailingZeros(pawns);
            int rank = from / 8;

            // ---- Single push ----
            int to = from + forward;
            if (to >= 0 && to < 64 && (empty & (1L << to)) != 0) {
                if (rank == promoRank) {
                    // Promotion
                    count = addPromotions(moves, count, from, to, pieceType, Bitboard.EMPTY, isWhite);
                } else {
                    moves[count++] = Move.create(from, to, pieceType, Bitboard.EMPTY);
                }
            }

            // ---- Double push (only from starting rank) ----
            if (rank == startRank) {
                int mid = from + forward;
                int to2 = from + forward * 2;
                if ((empty & (1L << mid)) != 0 && (empty & (1L << to2)) != 0) {
                    moves[count++] = Move.create(from, to2, pieceType, Bitboard.EMPTY);
                }
            }

            // ---- Captures ----
            long attacks = AttackTables.pawnAttacks(from, isWhite) & enemyPieces;
            while (attacks != 0) {
                int target = Long.numberOfTrailingZeros(attacks);
                int captured = getPieceAt(board, target);
                if (rank == promoRank) {
                    count = addPromotions(moves, count, from, target, pieceType, captured, isWhite);
                } else {
                    moves[count++] = Move.create(from, target, pieceType, captured);
                }
                attacks &= attacks - 1;
            }

            pawns &= pawns - 1;
        }
        return count;
    }

    private static int addPromotions(int[] moves, int count, int from, int to,
                                      int pieceType, int captured, boolean isWhite) {
        int[] promos = isWhite ?
            new int[]{Bitboard.WHITE_QUEEN, Bitboard.WHITE_ROOK, Bitboard.WHITE_BISHOP, Bitboard.WHITE_KNIGHT} :
            new int[]{Bitboard.BLACK_QUEEN, Bitboard.BLACK_ROOK, Bitboard.BLACK_BISHOP, Bitboard.BLACK_KNIGHT};
        for (int promo : promos) {
            moves[count++] = Move.create(from, to, pieceType, captured, promo, Move.PROMOTION);
        }
        return count;
    }

    // ============================================
    // KNIGHT MOVES
    // ============================================

    private static int generateKnightMoves(Bitboard board, int[] moves, int count, boolean isWhite) {
        long knights = isWhite ? board.whiteKnights : board.blackKnights;
        long ownPieces = isWhite ? board.whitePieces : board.blackPieces;
        long enemyPieces = isWhite ? board.blackPieces : board.whitePieces;
        int pieceType = isWhite ? Bitboard.WHITE_KNIGHT : Bitboard.BLACK_KNIGHT;

        while (knights != 0) {
            int from = Long.numberOfTrailingZeros(knights);
            long attacks = AttackTables.knightAttacks(from) & ~ownPieces;

            while (attacks != 0) {
                int to = Long.numberOfTrailingZeros(attacks);
                int captured = ((enemyPieces & (1L << to)) != 0) ? getPieceAt(board, to) : Bitboard.EMPTY;
                moves[count++] = Move.create(from, to, pieceType, captured);
                attacks &= attacks - 1;
            }
            knights &= knights - 1;
        }
        return count;
    }

    // ============================================
    // BISHOP MOVES
    // ============================================

    private static int generateBishopMoves(Bitboard board, int[] moves, int count, boolean isWhite) {
        long bishops = isWhite ? board.whiteBishops : board.blackBishops;
        long ownPieces = isWhite ? board.whitePieces : board.blackPieces;
        long enemyPieces = isWhite ? board.blackPieces : board.whitePieces;
        int pieceType = isWhite ? Bitboard.WHITE_BISHOP : Bitboard.BLACK_BISHOP;

        while (bishops != 0) {
            int from = Long.numberOfTrailingZeros(bishops);
            long attacks = AttackTables.bishopAttacks(from, board.allPieces) & ~ownPieces;

            while (attacks != 0) {
                int to = Long.numberOfTrailingZeros(attacks);
                int captured = ((enemyPieces & (1L << to)) != 0) ? getPieceAt(board, to) : Bitboard.EMPTY;
                moves[count++] = Move.create(from, to, pieceType, captured);
                attacks &= attacks - 1;
            }
            bishops &= bishops - 1;
        }
        return count;
    }

    // ============================================
    // ROOK MOVES
    // ============================================

    private static int generateRookMoves(Bitboard board, int[] moves, int count, boolean isWhite) {
        long rooks = isWhite ? board.whiteRooks : board.blackRooks;
        long ownPieces = isWhite ? board.whitePieces : board.blackPieces;
        long enemyPieces = isWhite ? board.blackPieces : board.whitePieces;
        int pieceType = isWhite ? Bitboard.WHITE_ROOK : Bitboard.BLACK_ROOK;

        while (rooks != 0) {
            int from = Long.numberOfTrailingZeros(rooks);
            long attacks = AttackTables.rookAttacks(from, board.allPieces) & ~ownPieces;

            while (attacks != 0) {
                int to = Long.numberOfTrailingZeros(attacks);
                int captured = ((enemyPieces & (1L << to)) != 0) ? getPieceAt(board, to) : Bitboard.EMPTY;
                moves[count++] = Move.create(from, to, pieceType, captured);
                attacks &= attacks - 1;
            }
            rooks &= rooks - 1;
        }
        return count;
    }

    // ============================================
    // QUEEN MOVES
    // ============================================

    private static int generateQueenMoves(Bitboard board, int[] moves, int count, boolean isWhite) {
        long queens = isWhite ? board.whiteQueens : board.blackQueens;
        long ownPieces = isWhite ? board.whitePieces : board.blackPieces;
        long enemyPieces = isWhite ? board.blackPieces : board.whitePieces;
        int pieceType = isWhite ? Bitboard.WHITE_QUEEN : Bitboard.BLACK_QUEEN;

        while (queens != 0) {
            int from = Long.numberOfTrailingZeros(queens);
            long attacks = AttackTables.queenAttacks(from, board.allPieces) & ~ownPieces;

            while (attacks != 0) {
                int to = Long.numberOfTrailingZeros(attacks);
                int captured = ((enemyPieces & (1L << to)) != 0) ? getPieceAt(board, to) : Bitboard.EMPTY;
                moves[count++] = Move.create(from, to, pieceType, captured);
                attacks &= attacks - 1;
            }
            queens &= queens - 1;
        }
        return count;
    }

    // ============================================
    // KING MOVES
    // ============================================

    private static int generateKingMoves(Bitboard board, int[] moves, int count, boolean isWhite) {
        long king = isWhite ? board.whiteKing : board.blackKing;
        long ownPieces = isWhite ? board.whitePieces : board.blackPieces;
        long enemyPieces = isWhite ? board.blackPieces : board.whitePieces;
        int pieceType = isWhite ? Bitboard.WHITE_KING : Bitboard.BLACK_KING;

        int from = Long.numberOfTrailingZeros(king);
        long attacks = AttackTables.kingAttacks(from) & ~ownPieces;

        while (attacks != 0) {
            int to = Long.numberOfTrailingZeros(attacks);
            int captured = ((enemyPieces & (1L << to)) != 0) ? getPieceAt(board, to) : Bitboard.EMPTY;
            moves[count++] = Move.create(from, to, pieceType, captured);
            attacks &= attacks - 1;
        }
        return count;
    }

    // ============================================
    // UTILITY
    // ============================================

    private static int getPieceAt(Bitboard board, int square) {
        long mask = 1L << square;
        if ((board.whitePawns & mask) != 0) return Bitboard.WHITE_PAWN;
        if ((board.whiteKnights & mask) != 0) return Bitboard.WHITE_KNIGHT;
        if ((board.whiteBishops & mask) != 0) return Bitboard.WHITE_BISHOP;
        if ((board.whiteRooks & mask) != 0) return Bitboard.WHITE_ROOK;
        if ((board.whiteQueens & mask) != 0) return Bitboard.WHITE_QUEEN;
        if ((board.whiteKing & mask) != 0) return Bitboard.WHITE_KING;
        if ((board.blackPawns & mask) != 0) return Bitboard.BLACK_PAWN;
        if ((board.blackKnights & mask) != 0) return Bitboard.BLACK_KNIGHT;
        if ((board.blackBishops & mask) != 0) return Bitboard.BLACK_BISHOP;
        if ((board.blackRooks & mask) != 0) return Bitboard.BLACK_ROOK;
        if ((board.blackQueens & mask) != 0) return Bitboard.BLACK_QUEEN;
        if ((board.blackKing & mask) != 0) return Bitboard.BLACK_KING;
        return Bitboard.EMPTY;
    }
}