package com.jacudibu;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.jacudibu.components.InteractableComponent;
import com.jacudibu.components.ModelComponent;

/**
 * Created by Stefan Wolf (Jacudibu) on 10.05.2017.
 */
public final class Entities {
    private Entities() {}

    public static void createMarker(Vector3 position, Quaternion rotation)	{
        Entity marker = new Entity();

        marker.add(new ModelComponent(Core.testModel, position, rotation));
        marker.add(new InteractableComponent());

        Core.engine.addEntity(marker);
    }

    public static void createUser(Vector3 position, Quaternion rotation) {
        Entity user = new Entity();

        user.add(new ModelComponent(Core.testModel, position, rotation));

        Core.engine.addEntity(user);
    }
}
