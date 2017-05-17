package com.jacudibu.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Stefan Wolf (Jacudibu) on 07.05.2017.
 * Data Container for everything that needs to be rendered.
 */
public class ModelComponent implements Component {
    public Model model;
    public ModelInstance instance;
    private Entity entity;

    protected ModelComponent() {}

    public ModelComponent(Entity entity, Model model) {
        this(entity, model, new Vector3(), new Quaternion());
    }

    public ModelComponent(Entity entity, Model model, Vector3 position, Quaternion rotation) {
        this.entity = entity;
        this.model = model;
        this.instance = new ModelInstance(model);

        this.instance.transform.translate(position);
        this.instance.transform.rotate(rotation);
    }

    public void updateModel(Model model) {
        if (this.model != null)
            this.model.dispose();

        this.model = model;
    }

    public void updateTransform(Vector3 position, Quaternion rotation) {
        // TODO: Animate the hell out of that!
        instance.transform.set(position, rotation);

        MarkerComponent marker = Mappers.marker.get(entity);
        if (marker != null) {
            marker.handlePositionUpdate();
        }

        TrackerComponent tracker = Mappers.tracker.get(entity);
        if (tracker != null) {
            tracker.handlePositionUpdate();
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public boolean isMarker() {
        return (Mappers.marker.get(entity)) != null;
    }

    public boolean isTracker() {
        return (Mappers.tracker.get(entity)) != null;
    }
}
