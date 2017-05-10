package com.jacudibu;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.jacudibu.components.InteractableComponent;
import com.jacudibu.components.ModelComponent;
import com.jacudibu.entitySystem.InteractionSystem;
import com.jacudibu.entitySystem.RenderSystem;

public class Core extends com.badlogic.gdx.Game {

	public static Model model;

	public static Engine engine = new Engine();

	@Override
	public void create () {
		ModelBuilder modelBuilder = new ModelBuilder();
		model = modelBuilder.createBox(5f, 5f, 5f,
				new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);


	//	addMarker(new Vector3(), new Quaternion());
	//	addMarker(new Vector3(10f, 0f, 0f), new Quaternion());
	//	addMarker(new Vector3(-10f, 0f, 0f), new Quaternion());

		FileSystem.parseFile("HMDCam2IDS.txt", FileSystem.PathType.INTERNAL);

		PerspectiveCamera mainCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		mainCamera.position.set(10f,10f,10f);
		mainCamera.lookAt(0,0,0);
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
		model.dispose();
	}

	public static void addUser(Vector3 position, Quaternion rotation) {
		Entity user = new Entity();

		user.add(new ModelComponent(model, position, rotation));

		engine.addEntity(user);
	}

	public static void addMarker(Vector3 position, Quaternion rotation)	{
		Entity marker = new Entity();

		marker.add(new ModelComponent(model, position, rotation));
		marker.add(new InteractableComponent());

		engine.addEntity(marker);
	}
}
