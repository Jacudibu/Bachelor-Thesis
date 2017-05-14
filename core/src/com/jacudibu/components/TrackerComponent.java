package com.jacudibu.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.jacudibu.Entities;

/**
 * Created by Stefan on 11.05.2017.
 */
public class TrackerComponent implements Component {
    private Entity entity;
    private Array<Entity> observerdMarkers;
    private Array<Entity> outgoingArrows;

    public TrackerComponent(Entity entity) {
        this.entity = entity;
        observerdMarkers = new Array<Entity>();
        outgoingArrows = new Array<Entity>();
    }

    public void addMarker(Entity entity) {
        observerdMarkers.add(entity);
        outgoingArrows.add(Entities.createArrow(this.entity, entity));
    }

    public void removeMarker(Entity entity) {
        observerdMarkers.removeValue(entity, false);

        // TODO: Remove arrow from existence.
    }
}
