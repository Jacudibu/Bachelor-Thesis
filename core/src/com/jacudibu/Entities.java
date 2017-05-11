package com.jacudibu;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.jacudibu.components.SelectableComponent;
import com.jacudibu.components.ModelComponent;

/**
 * Created by Stefan Wolf (Jacudibu) on 10.05.2017.
 */
public final class Entities {
    private Entities() {}

    public static Entity createMarker(Vector3 position, Quaternion rotation)	{
        Entity marker = new Entity();

        marker.add(new ModelComponent(Core.testModel, position, rotation));
        marker.add(new SelectableComponent(3f));

        Core.engine.addEntity(marker);
        return marker;
    }

    public static Entity createUser(Vector3 position, Quaternion rotation) {
        Entity user = new Entity();

        user.add(new ModelComponent(Core.testModel, position, rotation));

        Core.engine.addEntity(user);
        return user;
    }
}
