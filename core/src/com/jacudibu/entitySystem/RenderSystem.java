package com.jacudibu.entitySystem;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.jacudibu.MainCamera;
import com.jacudibu.components.ArrowComponent;
import com.jacudibu.components.ModelComponent;

/**
 * Created by Stefan Wolf (Jacudibu) on 07.05.2017.
 * System used for Rendering of every Entity having a ModelComponent.
 */
public class RenderSystem extends EntitySystem {
    private ImmutableArray<Entity> modelEntities;
    private ImmutableArray<Entity> arrowEntities;

    private ModelBatch modelBatch;
    private Environment environment;

    public RenderSystem() {
        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    }

    @Override
    public void addedToEngine(Engine engine) {
        modelEntities = engine.getEntitiesFor((Family.all(ModelComponent.class).get()));
        arrowEntities = engine.getEntitiesFor((Family.all(ArrowComponent.class).get()));
    }

    @Override
    public void update(float deltaTime) {
        modelBatch.begin(MainCamera.getCamera());

        for (int i = 0; i < modelEntities.size(); i++) {
            Entity entity = modelEntities.get(i);
            ModelComponent model = ModelComponent.mapper.get(entity);
            modelBatch.render(model.instance, environment);
        }

        // Ashley doesn't seem to take inheritance into account,
        // therefore modelEntitites won't contain entities with arrowComponents.
        for (int i = 0; i < arrowEntities.size(); i++) {
            Entity entity = arrowEntities.get(i);
            ArrowComponent arrow = ArrowComponent.mapper.get(entity);
            modelBatch.render(arrow.instance, environment);
        }

        modelBatch.end();
    }

}
