package com.jacudibu.entitySystem;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.jacudibu.Core;
import com.jacudibu.InputManager;
import com.jacudibu.MainCamera;
import com.jacudibu.UI.InformationDrawer;
import com.jacudibu.components.ModelComponent;

/**
 * Created by Stefan Wolf (Jacudibu) on 08.05.2017.
 * System managing interaction with SelectableComponents.
 */
public class SelectionSystem extends EntitySystem {
    private Camera camera;

    public Entity currentlyHovered = null;
    public Entity currentlySelected = null;

    private static final float MAX_RAY_DISTANCE = 100f;

    public SelectionSystem() {
        this(MainCamera.getCamera());
    }

    public SelectionSystem(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        Ray ray = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());

        Vector3 rayFrom = ray.origin;
        Vector3 rayTo = ray.direction.scl(MAX_RAY_DISTANCE).add(ray.origin);

        ClosestRayResultCallback raycast = new ClosestRayResultCallback(rayFrom, rayTo);

        Core.collisionWorld.rayTest(rayFrom, rayTo, raycast);
        Entity hitEntity = null;


        if (raycast.hasHit()) {
            final btCollisionObject hit = raycast.getCollisionObject();
            hitEntity = (Entity) hit.userData;
            //Gdx.app.log("Raycast", "hit: " + hitEntity.toString());
        }
        else {
            //Gdx.app.log("Raycast", "nohit");
        }

        hover(hitEntity);
    }

    @Override
    public void addedToEngine(Engine engine) {
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
            currentlySelected = InputManager.twoEntitiesSelected(lastSelected, currentlyHovered);

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
