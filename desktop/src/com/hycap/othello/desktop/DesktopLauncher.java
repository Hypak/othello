package com.hycap.othello.desktop;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.hycap.othello.OthelloGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Othello";
		config.width = 1920;
		config.height = 1080;
		config.vSyncEnabled = true;
		config.backgroundFPS = 30;
		new LwjglApplication(new OthelloGame(), config);
	}
}
