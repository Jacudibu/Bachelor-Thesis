package com.jacudibu.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jacudibu.Core;

public class DesktopLauncher {
	public static void main (String[] arg) {
		System.loadLibrary("ubitrack_java");

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.samples = 3;
		new LwjglApplication(new Core(), config);
	}
}
