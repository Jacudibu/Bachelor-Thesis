package com.jacudibu.entitySystem;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.jacudibu.components.ClickableComponent;
import com.jacudibu.components.Mappers;
import com.jacudibu.components.ModelComponent;

/**
 * Created by Stefan Wolf (Jacudibu) on 08.05.2017.
 */
public class InteractionSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;
    private PerspectiveCamera camera;

    private Entity currentlyHovered = null;
    private Entity currentlySelected = null;

    public InteractionSystem(PerspectiveCamera camera) {
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        Ray ray = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());

        float distance = Float.MAX_VALUE;
        Entity closestEntity = null;

        for (int i = 0; i < entities.size(); i++) {
            Entity currentlyCheckedEntity = entities.get(i);

            ClickableComponent clickable = Mappers.clickable.get(currentlyCheckedEntity);
            ModelComponent model = Mappers.model.get(currentlyCheckedEntity);

            Vector3 position = model.instance.transform.getTranslation(Vector3.Zero);
            float currentDistance = ray.origin.dst2(position);

            if (currentDistance > distance) {
                continue;
            }

            if (Intersector.intersectRaySphere(ray, position, clickable.radius, null)) {
                closestEntity = currentlyCheckedEntity;
                distance = currentDistance;

                Gdx.app.log("test", "found sumfing");
            }
        }
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor((Family.all(ModelComponent.class, ClickableComponent.class).get()));
    }
}
