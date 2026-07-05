package engine;

public class Main {
    public static void main(String[] args) {
       
        // Create a new board in starting position
        Bitboard board = new Bitboard();
        board.printBoard();

        // checking some function here (getPiece form Bitboard class)
        for(int i = 56; i < 64; i++) {
            System.out.println("Square " + i + ": " + board.getPiece(i));
        }

    }
}