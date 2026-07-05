package engine;

public class Main {
    public static void main(String[] args) {
       
        // Create a new board in starting position
        Bitboard board = new Bitboard();
        board.printBoard();
        
        for(int i = 56; i < 64; i++) {
            System.out.println("Square " + i + ": " + board.getPiece(i));
        }

    }
}