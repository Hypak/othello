package com.hycap.othello;

import java.util.*;

public class Main {
    private static Board board;


    public static void main(String[] args) {
        board = new Board();
        Player blackPlayer = new AIPlayer1(false);
        Player whitePlayer = new UserPlayer(true);

        boolean whiteMoved = true;
        boolean blackMoved = true;
        for (int i = 0; i < 32; ++i) {
            System.out.println(board.toString());
            System.out.println("Black's turn");
            blackMoved = blackPlayer.PlayMove(board);
            if (!whiteMoved && !blackMoved) {
                break;
            }
            System.out.println(board.toString());
            System.out.println("White's turn");
            whiteMoved = whitePlayer.PlayMove(board);
            if (!whiteMoved && !blackMoved) {
                break;
            }
        }
        int score = board.getWhiteAdvantage();
        int whiteCount = (64 - board.getFreeSquares()) / 2 + score;
        int blackCount = 64 - board.getFreeSquares() - whiteCount;
        if (whiteCount > blackCount) {
            System.out.println("White won " + whiteCount + "-" + blackCount);
        } else if (blackCount > whiteCount) {
            System.out.println("Black won " +  blackCount + "-" + whiteCount);
        } else {
            System.out.println("Tie " +  whiteCount + "-" + blackCount);
        }
    }
}
