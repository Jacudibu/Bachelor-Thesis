package com.jacudibu.entitySystem;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.jacudibu.Core;
import com.jacudibu.components.InteractableComponent;
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

    public InteractionSystem() {
        InteractionSystem(Core.mainCamera);
    }

    public InteractionSystem(PerspectiveCamera camera) {
        this.camera = camera;

        // TODO: Extract into InputManager
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override public boolean touchDown (int x, int y, int pointer, int button) {
                select();
                return true;
            }
        });
    }

    @Override
    public void update(float deltaTime) {
        Ray ray = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());

        float distance = Float.MAX_VALUE;
        Entity closestEntity = null;

        for (int i = 0; i < entities.size(); i++) {
            Entity currentlyCheckedEntity = entities.get(i);

            InteractableComponent clickable = Mappers.interactable.get(currentlyCheckedEntity);
            ModelComponent model = Mappers.model.get(currentlyCheckedEntity);

            Vector3 position = model.instance.transform.getTranslation(Vector3.Zero);
            float currentDistance = ray.origin.dst2(position);

            if (currentDistance > distance) {
                continue;
            }

            if (Intersector.intersectRaySphere(ray, position, clickable.radius, null)) {
                closestEntity = currentlyCheckedEntity;
                distance = currentDistance;
            }
        }

        hover(closestEntity);
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor((Family.all(ModelComponent.class, InteractableComponent.class).get()));
    }


    // Interaction Stuff
    // TODO: Refactor, create SelectableComponent and query there
    private void hover (Entity entity) {
        if (currentlyHovered == entity) {
            return;
        }

        if (currentlyHovered != null) {
            if (currentlyHovered != currentlySelected) {
                setEntityColor(currentlyHovered, Color.WHITE);
            }
        }

        if (entity == null) {
            if (currentlyHovered != currentlySelected) {
                setEntityColor(currentlyHovered, Color.WHITE);
            }
            currentlyHovered = null;
            return;
        }

        currentlyHovered = entity;

        if (entity != currentlySelected) {
            setEntityColor(entity, Color.YELLOW);
        }
    }

    private void select () {
        if (currentlyHovered == currentlySelected) {
            return;
        }

        if (currentlySelected != null) {
            unselect();
        }

        if (currentlyHovered == null) {
            return;
        }

        currentlySelected = currentlyHovered;
        setEntityColor(currentlySelected, Color.BLUE);
    }

    private void unselect() {
        setEntityColor(currentlySelected, Color.WHITE);
        currentlySelected = null;
    }

    private void setEntityColor(Entity entity, Color color) {
        Mappers.model.get(entity).instance.materials.get(0).set(ColorAttribute.createDiffuse(color));
    }
}
