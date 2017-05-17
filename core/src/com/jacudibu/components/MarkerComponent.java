package com.jacudibu.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Stefan on 11.05.2017.
 * Data Container for Marker Relevant Stuff.
 */
public class MarkerComponent implements Component {
    private Array<TrackerComponent> assignedTrackers;
    private Entity entity;

    public MarkerComponent(Entity entity) {
        this.entity = entity;
        assignedTrackers = new Array<TrackerComponent>();
    }

    public void addTracker(TrackerComponent tracker) {
        assignedTrackers.add(tracker);
    }

    public void addTracker(Entity entity) {
        addTracker(Mappers.tracker.get(entity));
    }

    public void removeTracker(Entity tracker) {
        removeTracker(Mappers.tracker.get(tracker));
    }
    public void removeTracker(TrackerComponent tracker) {
        assignedTrackers.removeValue(tracker, false);
    }

    public void handlePositionUpdate() {
        for (int i = 0; i < assignedTrackers.size; i++) {
            assignedTrackers.get(i).handlePositionUpdate();
        }
    }

    public Entity getEntity() {
        return entity;
    }
}
