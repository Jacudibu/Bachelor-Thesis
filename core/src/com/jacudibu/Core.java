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
import com.jacudibu.components.FrustumComponent;
import com.jacudibu.fileSystem.IntrinsicParser;
import com.jacudibu.fileSystem.PathType;
import com.jacudibu.ubiWrap.DFGParser;
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

	public final static boolean DEBUG_DRAW = false;
	private DebugDrawer debugDrawer;

	float testTimer = 0f;

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

		if (DEBUG_DRAW) {
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

		initDebugStuff();
	}

	@Override
	public void render () {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT
				| (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		try {
			UbiManager.update();
		} catch (Exception e) {
			return;
		}

		MainCamera.instance.update(Gdx.graphics.getDeltaTime());
		grid.render();

		collisionWorld.updateAabbs();
		engine.update(Gdx.graphics.getDeltaTime());
		screen.render(Gdx.graphics.getDeltaTime());

		if (DEBUG_DRAW) {
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

	private void initDebugStuff() {
		modelBuilder = new ModelBuilder();
		markerModel = modelBuilder.createBox(0.2f, 0.2f, 0.000001f,
				new Material(ColorAttribute.createDiffuse(Color.WHITE)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);

		trackerModel = modelBuilder.createSphere(0.2f, 0.2f, 0.2f,50, 50,
				new Material(ColorAttribute.createDiffuse(Color.WHITE)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);

		// PoseParser.parseFile("samples/samplePose.txt", PoseParser.PathType.INTERNAL);
		JsonImporter.importJson("test-nodes");
		DFGParser.parse(UbiManager.dfgPath);

		//IntrinsicParser.parse("samples/sampleIntrinsic.txt", engine.getEntities().first());
	}

	public static void reset() {
		engine.removeAllEntities();
		JsonExporter.savePath = "";
		NodeComponent.resetCounter();
	}

}
