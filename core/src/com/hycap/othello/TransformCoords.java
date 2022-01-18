package com.hycap.othello;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class TransformCoords {
    public static float viewWidth = 1920;
    public static float viewHeight = 1080;
    public static float squareSize = 32 * 4;
    public static float boardSize = 68 * 4 * 4;

    public static Vector2 GetWorldCoords(int boardX, int boardY) {
        float x = viewWidth / 2 + (boardX - 3.5f) * squareSize;
        float y = viewHeight / 2 + (7 - boardY - 3.5f) * squareSize;
        return new Vector2(x, y);
    }
    public static Pair<Integer, Integer> GetBoardCoords(float worldX, float worldY) {
        int x = Math.round((worldX - viewWidth / 2) / squareSize + 3.5f);
        int y = Math.round((worldY - viewHeight / 2) / squareSize + 3.5f);
        return new Pair<>(x, y);
    }
}
