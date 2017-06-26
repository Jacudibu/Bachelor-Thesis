package com.jacudibu.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.jacudibu.Core;
import com.jacudibu.Entities;

/**
 * Created by Stefan on 11.05.2017.
 * Data Container for a Tracker (most likely some sort of camera)
 */
public class TrackerComponent implements Component {
    public static final ComponentMapper<TrackerComponent> mapper = ComponentMapper.getFor(TrackerComponent.class);

    private Entity entity;
    private Array<MarkerComponent> observedMarkers;
    private Array<ArrowComponent> outgoingArrows;

    public TrackerComponent(Entity entity) {
        this.entity = entity;
        observedMarkers = new Array<MarkerComponent>();
        outgoingArrows = new Array<ArrowComponent>();
    }

    public void addMarker(Entity entity) {
        addMarker(MarkerComponent.mapper.get(entity));
    }

    public void addMarker(MarkerComponent marker) {
        observedMarkers.add(marker);

        Entity arrow = Entities.createArrow(marker.getEntity(), entity);
        outgoingArrows.add(ArrowComponent.mapper.get(arrow));
    }

    public void removeMarker(Entity entity) {
        removeMarker(MarkerComponent.mapper.get(entity));
    }

    public void removeMarker(MarkerComponent marker) {
        observedMarkers.removeValue(marker, false);
        // TODO: Remove arrow from existence.

    }

    public void handlePositionUpdate() {
        updateArrows();
    }

    public void updateArrows() {
        for (int i = 0; i < outgoingArrows.size; i++) {
            outgoingArrows.get(i).updateModel();
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public void Merge(MarkerComponent marker) {
        Core.engine.removeEntity(marker.getEntity());
        entity.add(marker);
    }

    public void Merge(TrackerComponent tracker) {
        for (MarkerComponent marker : tracker.observedMarkers) {
            tracker.removeMarker(marker);
            this.addMarker(marker);
        }

        Core.engine.removeEntity(tracker.entity);
    }
}
