package com.jacudibu.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Stefan Wolf (Jacudibu) on 07.05.2017.
 * Data Container for everything that needs to be rendered.
 */
public class ModelComponent implements Component {
    public static final ComponentMapper<ModelComponent> mapper = ComponentMapper.getFor(ModelComponent.class);

    public Model model;
    public ModelInstance modelInstance;
    private Entity entity;

    protected ModelComponent() {}

    public ModelComponent(Entity entity, Model model) {
        this(entity, model, new Vector3(), new Quaternion());
    }

    public ModelComponent(Entity entity, Model model, Vector3 position, Quaternion rotation) {
        this.entity = entity;
        this.model = model;
        this.modelInstance = new ModelInstance(model);

        this.modelInstance.transform.translate(position);
        this.modelInstance.transform.rotate(rotation);
    }

    public void updateModel(Model model) {
        if (this.model != null)
            this.model.dispose();

        this.model = model;
    }

    public void updateTransform(Vector3 position, Quaternion rotation) {
        // TODO: Animate the hell out of that!
        modelInstance.transform.set(position, rotation);

        MarkerComponent marker = MarkerComponent.mapper.get(entity);
        if (marker != null) {
            marker.handlePositionUpdate();
        }

        TrackerComponent tracker = TrackerComponent.mapper.get(entity);
        if (tracker != null) {
            tracker.handlePositionUpdate();
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public boolean isMarker() {
        return (MarkerComponent.mapper.get(entity)) != null;
    }
    public MarkerComponent getMarker() {
        return MarkerComponent.mapper.get(entity);
    }

    public boolean isTracker() {
        return (TrackerComponent.mapper.get(entity)) != null;
    }
    public TrackerComponent getTracker() {
        return TrackerComponent.mapper.get(entity);
    }


}
