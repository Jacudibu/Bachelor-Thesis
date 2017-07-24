package com.jacudibu.entitySystem;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.utils.Array;
import com.jacudibu.Core;
import com.jacudibu.InputManager;
import com.jacudibu.MainCamera;
import com.jacudibu.UI.InformationDrawer;
import com.jacudibu.components.ArrowComponent;
import com.jacudibu.components.ModelComponent;

/**
 * Created by Stefan Wolf (Jacudibu) on 08.05.2017.
 * System managing interaction with SelectableComponents.
 */
public class SelectionSystem extends EntitySystem {
    private Camera camera;

    public Entity currentlyHovered = null;
    public Entity currentlySelected = null;
    public Array<Entity> multiSelection = new Array<>();

    private static Color unselectedColor = Color.WHITE;

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
            if (!multiSelection.contains(currentlyHovered, true)) {
                setEntityColor(currentlyHovered, Color.WHITE);
            }
        }

        if (entity == null) {
            if (!multiSelection.contains(currentlyHovered, true)) {
                setEntityColor(currentlyHovered, Color.WHITE);
            }
            currentlyHovered = null;
            return;
        }

        currentlyHovered = entity;

        if (!multiSelection.contains(currentlyHovered, true)) {
            setEntityColor(entity, Color.YELLOW);
        }
    }

    public void select () {
        if (currentlyHovered == currentlySelected) {
            return;
        }

        Entity lastSelected = this.currentlySelected;
        if (currentlySelected != null) {
            unselectAll();
        }

        if (currentlyHovered == null) {
            unselectAll();
            return;
        }

        if (lastSelected != null) {
            select(InputManager.twoEntitiesSelected(lastSelected, currentlyHovered));
        }
        else {
            select(currentlyHovered);
        }
    }

    public void multiSelect() {
        if (currentlyHovered == null) {
            return;
        }

        if (multiSelection.contains(currentlyHovered, true)) {
            multiSelection.removeValue(currentlyHovered, true);
            return;
        }

        select(currentlyHovered);
    }

    private void select(Entity entity) {
        if (entity == null) {
            return;
        }

        currentlySelected = entity;
        multiSelection.add(entity);

        setEntityColor(this.currentlySelected, Color.BLUE);
        InformationDrawer.setCurrentlySelectedObject(ModelComponent.get(this.currentlySelected));
    }

    private void unselect(Entity entity) {
        if (multiSelection.contains(entity, true)) {
            multiSelection.removeValue(entity, true);
            setEntityColor(entity, unselectedColor);
        }

        if (multiSelection.size > 0) {
            currentlySelected = multiSelection.get(multiSelection.size - 1);
            InformationDrawer.setCurrentlySelectedObject(ModelComponent.get(currentlySelected));
        }
        else {
            currentlySelected = null;
            InformationDrawer.setCurrentlySelectedObject(null);
        }
    }

    private void unselectAll() {
        for (int i = 0; i < multiSelection.size; i++) {
            setEntityColor(multiSelection.get(i), unselectedColor);
        }

        multiSelection.clear();
        currentlySelected = null;
        InformationDrawer.setCurrentlySelectedObject(null);
    }

    private void setEntityColor(Entity entity, Color color) {
        if (ModelComponent.get(entity) != null) {
            ModelComponent.get(entity).setColorAttribute(color);
        }
        else if (ArrowComponent.get(entity) != null) {
            ArrowComponent.get(entity).setColorAttribute(color);
        }

    }
}
