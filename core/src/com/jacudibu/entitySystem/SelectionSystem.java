package com.jacudibu.entitySystem;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.jacudibu.InputManager;
import com.jacudibu.MainCamera;
import com.jacudibu.UI.InformationDrawer;
import com.jacudibu.components.SelectableComponent;
import com.jacudibu.components.ModelComponent;

/**
 * Created by Stefan Wolf (Jacudibu) on 08.05.2017.
 * System managing interaction with SelectableComponents.
 */
public class SelectionSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;
    private Camera camera;

    public Entity currentlyHovered = null;
    public Entity currentlySelected = null;

    private Vector3 currentlySelectedPosition;

    public SelectionSystem() {
        this(MainCamera.getCamera());
    }

    public SelectionSystem(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        Ray ray = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());

        float distance = Float.MAX_VALUE;
        Entity closestEntity = null;

        for (int i = 0; i < entities.size(); i++) {
            Entity currentlyCheckedEntity = entities.get(i);

            SelectableComponent clickable = SelectableComponent.mapper.get(currentlyCheckedEntity);
            ModelComponent model = ModelComponent.mapper.get(currentlyCheckedEntity);

            Vector3 position = model.modelInstance.transform.getTranslation(Vector3.Zero);
            float currentDistance = ray.origin.dst2(position);

            if (currentDistance > distance) {
                continue;
            }

            if (Intersector.intersectRaySphere(ray, position, clickable.radius, null)) {
                closestEntity = currentlyCheckedEntity;
                distance = currentDistance;
                currentlySelectedPosition = position;
            }
        }

        hover(closestEntity);
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor((Family.all(ModelComponent.class, SelectableComponent.class).get()));
    }


    // Interaction Stuff
    // TODO: Refactor
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

    public void select () {
        if (currentlyHovered == currentlySelected) {
            return;
        }

        Entity lastSelected = currentlySelected;
        if (currentlySelected != null) {
            unselect();
        }

        if (currentlyHovered == null) {
            return;
        }

        if (lastSelected != null) {
            currentlySelected = InputManager.TwoEntitesSelected(lastSelected, currentlyHovered);

            if (currentlySelected == null) {
                return;
            }
        }
        else {
            currentlySelected = currentlyHovered;
        }

        setEntityColor(currentlySelected, Color.BLUE);

        InformationDrawer.setCurrentlySelectedObject(ModelComponent.mapper.get(currentlySelected));
    }

    private void unselect() {
        setEntityColor(currentlySelected, Color.WHITE);
        currentlySelected = null;
        InformationDrawer.setCurrentlySelectedObject(null);
    }

    private void setEntityColor(Entity entity, Color color) {
        ModelComponent.mapper.get(entity).modelInstance.materials.get(0).set(ColorAttribute.createDiffuse(color));
    }
}
