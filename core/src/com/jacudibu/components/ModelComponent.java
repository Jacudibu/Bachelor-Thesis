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
    protected Entity entity;

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

    public void animateTo(Vector3 position, Quaternion rotation) {
        AnimationComponent anim = new AnimationComponent(entity);

        anim.fromPos = modelInstance.transform.getTranslation(new Vector3());
        anim.toPos = position;
        anim.position = true;

        anim.fromRot = modelInstance.transform.getRotation(new Quaternion());
        anim.toRot = rotation;
        anim.rotation = true;

        anim.speed = 4f;

        entity.add(anim);
    }

    public void updateTransform(Vector3 position, Quaternion rotation) {
        updateTransform(position, rotation, modelInstance.transform.getScale(new Vector3()));
    }

    public void updateTransform(Vector3 position, Quaternion rotation, Vector3 scale) {
        modelInstance.transform.set(position, rotation);

        if (NodeComponent.mapper.get(entity) != null) {
            NodeComponent.mapper.get(entity).handlePositionUpdate();
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public boolean isMarker() {
        if  (NodeComponent.mapper.get(entity) != null) {
            return NodeComponent.mapper.get(entity).isMarker;
        }
        return false;
    }
    public boolean isTracker() {
        if  (NodeComponent.mapper.get(entity) != null) {
            return NodeComponent.mapper.get(entity).isTracker;
        }
        return false;
    }

    public Vector3 getPosition() {
        return modelInstance.transform.getTranslation(new Vector3());
    }

    public NodeComponent getNode() {
        return NodeComponent.mapper.get(entity);
    }

}
