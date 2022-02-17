package com.hycap.othello;

public class AICompare {
    private static IntPair getScores(Player black, Player white) {
        Board board = new Board();
        boolean whiteMoved = true;
        boolean blackMoved = true;
        for (int i = 0; i < 32; ++i) {
            blackMoved = black.PlayMove(board);
            if (!whiteMoved && !blackMoved) {
                break;
            }
            whiteMoved = white.PlayMove(board);
            if (!whiteMoved && !blackMoved) {
                break;
            }
        }
        int score = board.getWhiteAdvantage();
        int whiteCount = (64 - board.getFreeSquares()) / 2 + score;
        int blackCount = 64 - board.getFreeSquares() - whiteCount;
        return new IntPair(blackCount, whiteCount);
    }


    public static void main(String[] args) {
        AIPlayer1 playerA = new AIPlayer1(false);
        playerA.setEvaluator(new BestEvaluator());
        AIPlayer1 playerB = new AIPlayer1(true);
        playerB.setEvaluator(new BestEvaluatorTest());
        int totalA = 0;
        int totalB = 0;
        for (float time = 0.001f; time < 3; time *= 1.5) {
            playerA.setThinkTimeNS(Math.round(time * 1000000000));
            playerB.setThinkTimeNS(Math.round(time * 1000000000));
            IntPair resultA = getScores(playerA, playerB);
            totalA += resultA.getX();;
            totalB += resultA.getY();
            playerA.setWhite(true);
            playerB.setWhite(false);
            IntPair resultB = getScores(playerB, playerA);
            resultB = new IntPair(resultB.getY(), resultB.getX());
            totalA += resultB.getX();
            totalB += resultB.getY();
            System.out.println("Time: " + time + " Score: " + resultA + ", " + resultB);
            System.out.println("Total score: " + new IntPair(totalA, totalB));
            playerA.setWhite(false);
            playerB.setWhite(true);

        }
    }
}
