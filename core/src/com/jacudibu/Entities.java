package com.jacudibu;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.jacudibu.components.*;

/**
 * Created by Stefan Wolf (Jacudibu) on 10.05.2017.
 * Static class that creates predefined Entities when needed.
 */
public final class Entities {
    private Entities() {}

    public static Entity createMarker(Vector3 position, Quaternion rotation)	{
        Entity entity = new Entity();

        entity.add(new ModelComponent(entity, Core.markerModel, position, rotation));
        entity.add(new NodeComponent(entity, true, false));
        entity.add(new GridLineComponent(entity));
        entity.add(ColliderComponent.createMarkerCollider(entity));

        entity.add(AnimationComponent.scale01(entity));


        Core.engine.addEntity(entity);
        return entity;
    }

    public static Entity createTracker(Vector3 position, Quaternion rotation) {
        Entity entity = new Entity();

        entity.add(new ModelComponent(entity, Core.trackerModel, position, rotation));
        entity.add(new NodeComponent(entity, false, true));
        entity.add(new GridLineComponent(entity));
        entity.add(ColliderComponent.createTrackerCollider(entity));

        entity.add(AnimationComponent.scale01(entity));

        Core.engine.addEntity(entity);
        return entity;
    }

    public static Entity createNode(Vector3 position, Quaternion rotation, int ID, String name, boolean isTracker, boolean isMarker) {
        Entity entity = new Entity();

        if (isTracker) {
            entity.add(new ModelComponent(entity, Core.trackerModel, position, rotation));
            entity.add(ColliderComponent.createTrackerCollider(entity));
        }
        else
        {
            entity.add(new ModelComponent(entity, Core.markerModel, position, rotation));
            entity.add(ColliderComponent.createMarkerCollider(entity));
        }
        entity.add(new NodeComponent(entity, isMarker, isTracker, ID, name));
        entity.add(new GridLineComponent(entity));

        entity.add(AnimationComponent.scale01(entity));

        Core.engine.addEntity(entity);
        return entity;
    }

    public static Entity createArrow(Entity from, Entity to) {
        Entity entity = new Entity();

        entity.add(new ArrowComponent(entity, from, to));
        entity.add(ColliderComponent.createArrowCollider(entity));

        Core.engine.addEntity(entity);
        return entity;
    }

    public static void destroyEntity(Entity e) {
        if (ColliderComponent.mapper.get(e) != null) {
            ColliderComponent.mapper.get(e).dispose();
        }

        if (ArrowComponent.mapper.get(e) != null) {
            ArrowComponent.mapper.get(e).dispose();
        }

        Core.engine.removeEntity(e);
    }
}
