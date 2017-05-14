package com.jacudibu.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Stefan on 11.05.2017.
 */
public class MarkerComponent implements Component {
    private Array<Entity> assignedTrackers;

    public MarkerComponent() {
        assignedTrackers = new Array<Entity>();
    }

    public void addTracker(Entity tracker) {
        assignedTrackers.add(tracker);
    }

    public void removeTracker(Entity tracker) {
        assignedTrackers.removeValue(tracker, false);
    }
}
