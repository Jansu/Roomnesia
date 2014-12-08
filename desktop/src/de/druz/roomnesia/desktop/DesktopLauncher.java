package de.druz.roomnesia.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.druz.roomnesia.Roomnesia;

public class DesktopLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 480;
//		config.width = 1600;
//		config.height = 960;
//		config.fullscreen = true;
		new LwjglApplication(new Roomnesia(), config);
	}
}
