package com.jacudibu;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.jacudibu.UI.UIOverlay;
import com.jacudibu.ubiWrap.UbiManager;
import com.jacudibu.utility.Grid3d;
import com.jacudibu.components.NodeComponent;
import com.jacudibu.entitySystem.AnimationSystem;
import com.jacudibu.entitySystem.SelectionSystem;
import com.jacudibu.entitySystem.RenderSystem;
import com.jacudibu.fileSystem.JsonExporter;
import com.jacudibu.fileSystem.JsonImporter;

public class Core extends com.badlogic.gdx.Game {
	public static ModelBuilder modelBuilder;
	public static InputMultiplexer inputMultiplexer;
	public static Model markerModel;
	public static Model trackerModel;

	public static btCollisionWorld collisionWorld;
	btCollisionConfiguration collisionConfig;
	btBroadphaseInterface broadphase;
	btCollisionDispatcher dispatcher;

	public static Engine engine = new Engine();

	public static int windowHeight;
	public static int windowWidth;
	public static Grid3d grid;

	public static boolean usingUbitrack = false;
	public static boolean debugDraw = false;
	public static String startSRGPath;
	private DebugDrawer debugDrawer;

	public Core(boolean debug) {
		super();
		debugDraw = debug;
	}

	@Override
	public void create () {
		windowWidth = Gdx.graphics.getWidth();
		windowHeight = Gdx.graphics.getHeight();
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
		inputMultiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(inputMultiplexer);

		Bullet.init();
		collisionConfig = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfig);
		broadphase = new btDbvtBroadphase();
		collisionWorld = new btCollisionWorld(dispatcher, broadphase, collisionConfig);

		if (debugDraw) {
			debugDrawer = new DebugDrawer();
			debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
			collisionWorld.setDebugDrawer(debugDrawer);
		}

		MainCamera.initialize();
		engine.addSystem(new AnimationSystem());
		engine.addSystem(new SelectionSystem());
		engine.addSystem(new RenderSystem());

		setScreen(new UIOverlay());
		grid = new Grid3d(20, true);
		InputManager.initalize();

		initializeModels();

		if (startSRGPath != null && startSRGPath.length() > 1) {
			JsonImporter.importJson(startSRGPath);
		}
	}

	@Override
	public void render () {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT
				| (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		if (usingUbitrack) {
			try {
			UbiManager.update();
			} catch (Exception e) {
				return;
			}
		}

		MainCamera.instance.update(Gdx.graphics.getDeltaTime());
		grid.render();

		collisionWorld.updateAabbs();
		engine.update(Gdx.graphics.getDeltaTime());
		screen.render(Gdx.graphics.getDeltaTime());

		if (debugDraw) {
			debugDrawer.begin(MainCamera.instance.cam);
			collisionWorld.debugDrawWorld();
			debugDrawer.end();
		}
	}

	@Override
	public void dispose () {
		markerModel.dispose();
		trackerModel.dispose();
		collisionWorld.dispose();
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

	private void initializeModels() {
		modelBuilder = new ModelBuilder();
		markerModel = modelBuilder.createBox(0.2f, 0.2f, 0.000001f,
				new Material(ColorAttribute.createDiffuse(Color.WHITE)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);

		trackerModel = modelBuilder.createSphere(0.2f, 0.2f, 0.2f,50, 50,
				new Material(ColorAttribute.createDiffuse(Color.WHITE)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
	}

	public static void reset() {
		engine.removeAllEntities();
		JsonExporter.savePath = "";
		NodeComponent.resetCounter();
	}

}
