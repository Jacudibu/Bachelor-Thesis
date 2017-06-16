package com.jacudibu;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.jacudibu.UI.UIOverlay;
import com.jacudibu.entitySystem.AnimationSystem;
import com.jacudibu.entitySystem.SelectionSystem;
import com.jacudibu.entitySystem.RenderSystem;

public class Core extends com.badlogic.gdx.Game {
	public static ModelBuilder modelBuilder;
	public static InputMultiplexer inputMultiplexer;
	public static Model testCube;
	public static Model testSphere;
	public static Engine engine = new Engine();

	public static int windowHeight;
	public static int windowWidth;
	private Grid3d grid;

	@Override
	public void create () {
		windowWidth = Gdx.graphics.getWidth();
		windowHeight = Gdx.graphics.getHeight();
		inputMultiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(inputMultiplexer);

		MainCamera.initialize();
		engine.addSystem(new AnimationSystem());
		engine.addSystem(new SelectionSystem());
		engine.addSystem(new RenderSystem());

		setScreen(new UIOverlay());
		grid = new Grid3d(20);
		InputManager.initalize();

		initDebugStuff();
	}

	@Override
	public void render () {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		MainCamera.instance.update(Gdx.graphics.getDeltaTime());
		grid.render();

		engine.update(Gdx.graphics.getDeltaTime());
		screen.render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void dispose () {
		testCube.dispose();
		testSphere.dispose();
		screen.dispose();
		grid.dispose();
	}

	@Override
	public void resize (int width, int height) {
		if (screen != null) {
			screen.resize(width, height);
		}
		if (MainCamera.getCamera() != null) {
			MainCamera.getCamera().viewportWidth = width;
			MainCamera.getCamera().viewportHeight = height;
		}

		windowHeight = width;
		windowHeight = height;
	}

	private void initDebugStuff() {
		modelBuilder = new ModelBuilder();
		testCube = modelBuilder.createBox(0.2f, 0.2f, 0.2f,
				new Material(ColorAttribute.createDiffuse(Color.WHITE)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

		testSphere = modelBuilder.createSphere(0.2f, 0.2f, 0.2f,50, 50,
				new Material(ColorAttribute.createDiffuse(Color.WHITE)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

		FileSystem.parseFile("HMDCam2IDS.txt", FileSystem.PathType.INTERNAL);

		// spawnDebugEntities();
	}

	private void spawnDebugEntities() {
		//// Some random trackers
		Entities.createTracker(new Vector3(0f, 0f, 0f), new Quaternion());
		Entities.createTracker(new Vector3(-20f, 20f, 0f), new Quaternion());

		// Some more markers, for fun.
		Entities.createMarker(new Vector3(10f, 0f, -10f), new Quaternion());
		Entities.createMarker(new Vector3(-20f, 0f, -10f), new Quaternion());
		Entities.createMarker(new Vector3(0f, 0f, -20f), new Quaternion());
	}

}
