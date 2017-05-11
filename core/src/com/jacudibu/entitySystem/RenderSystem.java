package com.jacudibu.entitySystem;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.jacudibu.Core;
import com.jacudibu.MainCamera;
import com.jacudibu.components.Mappers;
import com.jacudibu.components.ModelComponent;

/**
 * Created by Stefan Wolf (Jacudibu) on 07.05.2017.
 */
public class RenderSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;
    private ModelBatch modelBatch;
    private Environment environment;
    private Camera camera;

    public RenderSystem() {
        this(MainCamera.getCamera());
    }

    public RenderSystem(Camera cam) {
        camera = cam;
        camera.update();

        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor((Family.all(ModelComponent.class).get()));
    }

    @Override
    public void update(float deltaTime) {
        updateCamera(deltaTime);
        modelBatch.begin(camera);

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            ModelComponent model = Mappers.model.get(entity);
            modelBatch.render(model.instance, environment);
        }

        modelBatch.end();
    }

    private void updateCamera(float deltaTime) {

    }


}
