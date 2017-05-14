package com.jacudibu;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.jacudibu.components.MarkerComponent;
import com.jacudibu.components.SelectableComponent;
import com.jacudibu.components.ModelComponent;
import com.jacudibu.components.TrackerComponent;

/**
 * Created by Stefan Wolf (Jacudibu) on 10.05.2017.
 * Static class that creates predefined Entities when needed.
 */
public final class Entities {
    private Entities() {}

    public static Entity createMarker(Vector3 position, Quaternion rotation)	{
        Entity marker = new Entity();

        marker.add(new ModelComponent(Core.testCube, position, rotation));
        marker.add(new SelectableComponent(1f));
        marker.add(new MarkerComponent());

        Core.engine.addEntity(marker);
        return marker;
    }

    public static Entity createTracker(Vector3 position, Quaternion rotation) {
        Entity tracker = new Entity();

        tracker.add(new ModelComponent(Core.testSphere, position, rotation));
        tracker.add(new SelectableComponent(1f));
        tracker.add(new TrackerComponent());

        Core.engine.addEntity(tracker);
        return tracker;
    }
}
