package com.jacudibu.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Stefan Wolf (Jacudibu) on 08.07.2017.
 */
public class ColliderComponent implements Component, Disposable{
    public btCollisionObject collisionObject;
    private boolean isArrow;

    public static ColliderComponent createTrackerCollider(Entity entity) {
        ColliderComponent collider = new ColliderComponent();
        collider.collisionObject.setCollisionShape(new btSphereShape(0.2f));
        collider.collisionObject.setWorldTransform(ModelComponent.mapper.get(entity).getWorldTransform());
        return collider;
    }

    public static ColliderComponent createMarkerCollider(Entity entity) {
        ColliderComponent collider = new ColliderComponent();
        collider.collisionObject.setCollisionShape(new btBoxShape(new Vector3(0.2f, 0.2f, 0.2f)));
        collider.collisionObject.setWorldTransform(ModelComponent.mapper.get(entity).getWorldTransform());
        return collider;
    }

    private ColliderComponent() {
        this.collisionObject = new btCollisionObject();
    }

    public void updateTransform(Matrix4 transform) {
        collisionObject.setWorldTransform(transform);
    }

    @Override
    public void dispose() {
        collisionObject.dispose();
    }
}
