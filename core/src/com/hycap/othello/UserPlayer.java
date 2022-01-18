package com.hycap.othello;

import java.util.*;

public class UserPlayer implements Player{
    private static final Scanner scanner;
    private static final List<Character> letters;

    static {
        scanner = new Scanner(System.in);

        Character[] letterArray = new Character[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        letters = new ArrayList<>(Arrays.asList(letterArray));
    }

    private final boolean isWhite;

    public UserPlayer(boolean isWhite) {
        this.isWhite = isWhite;
    }

    @Override
    public boolean PlayMove(Board board) {
        if (board.GetAllNextTurnBoards(isWhite).size() == 0) {
            return false;
        }
        while (true) {
            String move = scanner.nextLine();
            move = move.trim().toLowerCase(Locale.ROOT);
            if (move.length() != 2) {
                System.out.println("Invalid input (should range from a1 to h8)");
                continue;  // Query again
            }
            if (!letters.contains(move.charAt(0))) {
                System.out.println("Invalid input (should range from a1 to h8)");
                continue;  // Query again
            }
            int x = letters.indexOf(move.charAt(0));
            int y;
            try {
                y = Short.parseShort(String.valueOf(move.charAt(1)));
            } catch (NumberFormatException e) {
                System.out.println("Invalid input (should range from a1 to h8)");
                continue;  // Query again
            }
            y = 8 - y;  // Converts to 'chess-like' notation
            // Try move, return if valid move
            if (board.TryMove(x, y, isWhite, false)) {
                return true;
            } else {
                System.out.println("Invalid square (all moves should flip an opponent's piece)");
            }
        }
    }
}
