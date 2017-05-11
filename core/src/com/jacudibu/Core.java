package com.jacudibu;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.jacudibu.entitySystem.InteractionSystem;
import com.jacudibu.entitySystem.RenderSystem;

public class Core extends com.badlogic.gdx.Game {

	public static Model testModel;
	public static PerspectiveCamera mainCamera;
	public static Engine engine = new Engine();

	@Override
	public void create () {
		ModelBuilder modelBuilder = new ModelBuilder();
		testModel = modelBuilder.createBox(5f, 5f, 5f,
				new Material(ColorAttribute.createDiffuse(Color.WHITE)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

		FileSystem.parseFile("HMDCam2IDS.txt", FileSystem.PathType.INTERNAL);

		// Some more markers, for fun.
		Entities.createMarker(new Vector3(10f, 0f, -10f), new Quaternion());
		Entities.createMarker(new Vector3(-20f, 0f, -10f), new Quaternion());
		Entities.createMarker(new Vector3(0f, 0f, -20f), new Quaternion());

		mainCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		mainCamera.position.set(0f,0f,10f);
		mainCamera.near = 1f;
		mainCamera.far = 300f;

		engine.addSystem(new RenderSystem(mainCamera));
		engine.addSystem(new InteractionSystem(mainCamera));
	}

	@Override
	public void render () {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		engine.update(Gdx.graphics.getDeltaTime());
	}
	
	@Override
	public void dispose () {
		testModel.dispose();
	}

	@Override
	public void resize (int width, int height) {
		if (screen != null) {
			screen.resize(width, height);
		}
		if (mainCamera != null) {
			mainCamera.viewportWidth = width;
			mainCamera.viewportHeight = height;
		}
	}
}
