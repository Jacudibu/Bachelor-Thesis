package com.jacudibu.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;

/**
 * Created by Stefan Wolf (Jacudibu) on 08.07.2017.
 */
public class ColliderComponent implements Component {
    public btCollisionObject collider;

    public ColliderComponent(btCollisionObject collider, Matrix4 transform) {
        this.collider = collider;
        updateTransform(transform);
    }

    public void updateTransform(Matrix4 transform) {
        collider.setWorldTransform(transform);
    }
}
