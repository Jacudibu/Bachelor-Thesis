package com.jacudibu.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jacudibu.Core;
import com.jacudibu.ubiWrap.UbiManager;

public class DesktopLauncher {
	private static boolean debugMode = false;
	private static boolean testMode = false;

	private static boolean useUbitrack = true;
	private static String ubiPath = "";

	private static String[] arguments;

	public static void main (String[] args) {
		arguments = args;

		searchUbitrackPathVariable();
		parseArguments();

		ubiPath = "C:\\Ubitrack\\" + ubiPath;
//		ubiPath = "C:\\Ubitrack\\bin\\ubitrack"; // How it should look like

		if (ubiPath.length() > 0 && useUbitrack) {
			if (testMode) {
				UbiManager.initTesting(ubiPath);
			} else {
				UbiManager.init(ubiPath);
			}
		}

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.samples = 3; // Anti Aliasing

		new LwjglApplication(new Core(debugMode), config);
	}

	private static void parseArguments() {
		int i = 0;
		while (i < arguments.length) {
			String currentArgument = arguments[i].toLowerCase();

			if (currentArgument.startsWith("-")) {
				currentArgument = currentArgument.substring(1);
			}

			i += parseArgument(currentArgument, i);
		}

	}

	private static void searchUbitrackPathVariable() {
		ubiPath = System.getenv("UBITRACK_COMPONENTS_PATH");
	}

	private static int parseArgument(String currentArgument, int index) {
		if (currentArgument.equals("help") || currentArgument.equals("teachmesenpai") || currentArgument.equals("?")) {
			printHelp();
			return 1;
		}

		if (currentArgument.equals("ubipath")) {
			ubiPath = arguments[index + 1];
			return 2;
		}

		if (currentArgument.equals("noubitrack")) {
			useUbitrack = false;
			return 1;
		}

		if (currentArgument.equals("debug")) {
			debugMode = true;
			return 1;
		}

		if (currentArgument.equals("test")) {
			testMode = true;
			return 1;
		}

		if (currentArgument.equals("upupdowndownleftrightleftrightba")){
			System.out.println("You are awesome!");
			return 1;
		}

		System.out.println("Unknown Argument: " + currentArgument + "\n" +
				"Enter help or ? for an overview of available arguments.");

		return 1;
	}

	private static void printHelp() {
		System.out.println("Fear not, young one: senpai noticed you! Available Commands:");

		System.out.println("-ubipath PATH \n" +
				"\tInitializes Ubitrack tracking. PATH needs to point to your ubitrack installation \n" +
				"\tTry this if you system's PATH variable is not found for some reason.");

		System.out.println("-noubitrack \n" +
				"\tStops Ubitrack from being initialized, even if a path variable is found. \n" +
				"\tUse this if your Ubitrack installation doesn't want to work no matter what you try.");

		System.out.println("-debug \n" +
				"\tDraws some funny colliders and other stuff. I don't know why you'd need that though.");
	}
}
