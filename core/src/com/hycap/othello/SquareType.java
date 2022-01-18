package com.hycap.othello;

public enum SquareType {
    VOID("-"),
    WHITE("X"),
    BLACK("O");
    final String boardRepresentation;
    SquareType(String boardRepresentation) {
        this.boardRepresentation = boardRepresentation;
    }

    @Override
    public String toString() {
        return this.boardRepresentation;
    }
}
