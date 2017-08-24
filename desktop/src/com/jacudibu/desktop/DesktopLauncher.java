package com.jacudibu.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jacudibu.Core;
import com.jacudibu.ubiWrap.UbiManager;

public class DesktopLauncher {
	private static boolean initUbitrack = true;

	public static void main (String[] arg) {
		parseArguments(arg);

		if (initUbitrack) {
			UbiManager.init();
		}

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.samples = 3; // Anti Aliasing

		new LwjglApplication(new Core(), config);
	}

	private static void parseArguments(String[] args) {
		for (int i = 0; i < args.length; i++) {
			String current = args[i].toLowerCase();

			if (current.startsWith("-")) {
				current = current.substring(1);
			}

			parseArgument(current);
		}

	}

	private static void parseArgument(String arg) {
		if (arg.equals("help") || arg.equals("teachmesenpai")) {
			printHelp();
		}

		if (arg.equals("noubitrack")) {
			initUbitrack = false;
		}

	}

	private static void printHelp() {
		// STUB
	}
}
