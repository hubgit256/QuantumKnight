package engine;

public class Bitboard {
    // ===== PIECE BITBOARDS =====
    long whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKing;
    long blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKing;
    long whitePieces, blackPieces, allPieces, emptySquares;
    
    // ===== GAME STATE =====
    boolean whiteToMove = true;
    
    // ===== CONSTANTS =====
    public static final int EMPTY = 0;
    public static final int WHITE_PAWN = 1;
    public static final int WHITE_KNIGHT = 2;
    public static final int WHITE_BISHOP = 3;
    public static final int WHITE_ROOK = 4;
    public static final int WHITE_QUEEN = 5;
    public static final int WHITE_KING = 6;
    public static final int BLACK_PAWN = 7;
    public static final int BLACK_KNIGHT = 8;
    public static final int BLACK_BISHOP = 9;
    public static final int BLACK_ROOK = 10;
    public static final int BLACK_QUEEN = 11;
    public static final int BLACK_KING = 12;
    
    // ===== CONSTRUCTOR =====
    public Bitboard() {
        setupStartPosition();
    }
    
    // ===== SETUP =====
    public void setupStartPosition() {
        whitePawns   = 0xFF00L;
        whiteKnights = (1L << 1) | (1L << 6);
        whiteBishops = (1L << 2) | (1L << 5);
        whiteRooks   = (1L << 0) | (1L << 7);
        whiteQueens  = (1L << 3);
        whiteKing    = (1L << 4);
        
        blackPawns   = 0xFF000000000000L;
        blackKnights = (1L << 57) | (1L << 62);
        blackBishops = (1L << 58) | (1L << 61);
        blackRooks   = (1L << 56) | (1L << 63);
        blackQueens  = (1L << 59);
        blackKing    = (1L << 60);
        
        updateAll();
    }
    
    // ===== UPDATE COMBINED BITBOARDS =====
    public void updateAll() {
        whitePieces = whitePawns | whiteKnights | whiteBishops | whiteRooks | whiteQueens | whiteKing;
        blackPieces = blackPawns | blackKnights | blackBishops | blackRooks | blackQueens | blackKing;
        allPieces = whitePieces | blackPieces;
        emptySquares = ~allPieces;
    }
    
    // ===== MAKE MOVE =====
    public void makeMove(Move move) {
        long fromMask = 1L << move.fromSquare;
        long toMask = 1L << move.toSquare;
        long combinedMask = fromMask | toMask;
        
        // Move the piece (XOR removes from 'from', adds to 'to')
        switch (move.pieceType) {
            case WHITE_PAWN:   whitePawns   ^= combinedMask; break;
            case WHITE_KNIGHT: whiteKnights ^= combinedMask; break;
            case WHITE_BISHOP: whiteBishops ^= combinedMask; break;
            case WHITE_ROOK:   whiteRooks   ^= combinedMask; break;
            case WHITE_QUEEN:  whiteQueens  ^= combinedMask; break;
            case WHITE_KING:   whiteKing    ^= combinedMask; break;
            case BLACK_PAWN:   blackPawns   ^= combinedMask; break;
            case BLACK_KNIGHT: blackKnights ^= combinedMask; break;
            case BLACK_BISHOP: blackBishops ^= combinedMask; break;
            case BLACK_ROOK:   blackRooks   ^= combinedMask; break;
            case BLACK_QUEEN:  blackQueens  ^= combinedMask; break;
            case BLACK_KING:   blackKing    ^= combinedMask; break;
        }
        
        // Remove captured piece if any
        if (move.capturedPiece != EMPTY) {
            long captureMask = 1L << move.toSquare;
            switch (move.capturedPiece) {
                case WHITE_PAWN:   whitePawns   ^= captureMask; break;
                case WHITE_KNIGHT: whiteKnights ^= captureMask; break;
                case WHITE_BISHOP: whiteBishops ^= captureMask; break;
                case WHITE_ROOK:   whiteRooks   ^= captureMask; break;
                case WHITE_QUEEN:  whiteQueens  ^= captureMask; break;
                case WHITE_KING:   whiteKing    ^= captureMask; break;
                case BLACK_PAWN:   blackPawns   ^= captureMask; break;
                case BLACK_KNIGHT: blackKnights ^= captureMask; break;
                case BLACK_BISHOP: blackBishops ^= captureMask; break;
                case BLACK_ROOK:   blackRooks   ^= captureMask; break;
                case BLACK_QUEEN:  blackQueens  ^= captureMask; break;
                case BLACK_KING:   blackKing    ^= captureMask; break;
            }
        }
        
        whiteToMove = !whiteToMove;
        updateAll();
    }
    
    // ===== DISPLAY =====
    public char getPiece(int square) {
        long mask = 1L << square;
        if ((whitePawns & mask) != 0) return 'P';
        if ((whiteKnights & mask) != 0) return 'N';
        if ((whiteBishops & mask) != 0) return 'B';
        if ((whiteRooks & mask) != 0) return 'R';
        if ((whiteQueens & mask) != 0) return 'Q';
        if ((whiteKing & mask) != 0) return 'K';
        if ((blackPawns & mask) != 0) return 'p';
        if ((blackKnights & mask) != 0) return 'n';
        if ((blackBishops & mask) != 0) return 'b';
        if ((blackRooks & mask) != 0) return 'r';
        if ((blackQueens & mask) != 0) return 'q';
        if ((blackKing & mask) != 0) return 'k';
        return '.';
    }
    
    public void printBoard() {
        System.out.println("\n  +-----------------+");
        for (int rank = 7; rank >= 0; rank--) {
            System.out.print((rank + 1) + " | ");
            for (int file = 0; file < 8; file++) {
                System.out.print(getPiece(rank * 8 + file) + " ");
            }
            System.out.println("| " + (rank + 1));
        }
        System.out.println("  +-----------------+");
        System.out.println("    a b c d e f g h");
        System.out.println("White to move: " + whiteToMove);
    }
}