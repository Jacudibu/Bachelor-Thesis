package com.jacudibu.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.utils.Disposable;
import com.jacudibu.Core;

/**
 * Created by Stefan Wolf (Jacudibu) on 08.07.2017.
 */
public class ColliderComponent implements Component, Disposable{
    public static final ComponentMapper<ColliderComponent> mapper = ComponentMapper.getFor(ColliderComponent.class);

    public btCollisionObject collisionObject;
    private boolean isArrow;

    public static ColliderComponent createTrackerCollider(Entity entity) {
        ColliderComponent collider = new ColliderComponent(entity);
        collider.collisionObject.setCollisionShape(new btSphereShape(0.1f));
        collider.collisionObject.setWorldTransform(ModelComponent.mapper.get(entity).getWorldTransform());
        Core.collisionWorld.addCollisionObject(collider.collisionObject);

        return collider;
    }

    public static ColliderComponent createMarkerCollider(Entity entity) {
        ColliderComponent collider = new ColliderComponent(entity);
        collider.collisionObject.setCollisionShape(new btBoxShape(new Vector3(0.1f, 0.1f, 0.01f)));
        collider.collisionObject.setWorldTransform(ModelComponent.mapper.get(entity).getWorldTransform());
        Core.collisionWorld.addCollisionObject(collider.collisionObject);

        return collider;
    }

    public static ColliderComponent createArrowCollider(Entity entity) {
        ColliderComponent collider = new ColliderComponent(entity);

        collider.updateArrowCollider(ArrowComponent.mapper.get(entity));
        Core.collisionWorld.addCollisionObject(collider.collisionObject);

        return collider;
    }

    private ColliderComponent(Entity entity) {
        this.collisionObject = new btCollisionObject();
        this.collisionObject.userData = entity;
    }

    public void updateTransform(Matrix4 transform) {
        collisionObject.setWorldTransform(transform);
    }

    public void updateArrowCollider(ArrowComponent arrowComponent) {
        collisionObject.setCollisionShape(Bullet.obtainStaticNodeShape(arrowComponent.model.nodes));
        collisionObject.setWorldTransform(arrowComponent.getWorldTransform());
    }

    @Override
    public void dispose() {
        Core.collisionWorld.removeCollisionObject(collisionObject);
        collisionObject.dispose();
    }
}
