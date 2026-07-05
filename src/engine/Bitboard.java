package engine;

public class Bitboard {
    long whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKing;
    long blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKing;
    long whitePieces, blackPieces, allPieces, emptySquares;
    
    boolean whiteToMove = true;
    
    public Bitboard() {
        setupStartPosition();
    }
    
    public void setupStartPosition() {
        // White: rank 1 = bits 0-7, rank 2 = bits 8-15
        whitePawns   = 0xFF00L;                    // rank 2: bits 8-15
        whiteRooks   = (1L << 0) | (1L << 7);      // a1, h1
        whiteKnights = (1L << 1) | (1L << 6);      // b1, g1
        whiteBishops = (1L << 2) | (1L << 5);      // c1, f1
        whiteQueens  = (1L << 3);                   // d1
        whiteKing    = (1L << 4);                   // e1
        
        // Black: rank 7 = bits 48-55, rank 8 = bits 56-63
        blackPawns   = 0xFF000000000000L;           // rank 7: bits 48-55
        blackRooks   = (1L << 56) | (1L << 63);    // a8, h8
        blackKnights = (1L << 57) | (1L << 62);    // b8, g8
        blackBishops = (1L << 58) | (1L << 61);    // c8, f8
        blackQueens  = (1L << 59);                  // d8
        blackKing    = (1L << 60);                  // e8
        
        updateAll();
    }
    
    public void updateAll() {
        whitePieces = whitePawns | whiteKnights | whiteBishops | whiteRooks | whiteQueens | whiteKing;
        blackPieces = blackPawns | blackKnights | blackBishops | blackRooks | blackQueens | blackKing;
        allPieces = whitePieces | blackPieces;
        emptySquares = ~allPieces;
    }
    
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