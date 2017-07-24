package com.jacudibu.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.jacudibu.Core;

/**
 * Created by Stefan Wolf (Jacudibu) on 07.05.2017.
 * Data Container for everything that needs to be rendered.
 */
public class ModelComponent implements Component {
    private static final ComponentMapper<ModelComponent> mapper = ComponentMapper.getFor(ModelComponent.class);

    public Model model;
    public ModelInstance modelInstance;
    protected Entity entity;

    public static ModelComponent get(Entity e) {
        return mapper.get(e);
    }

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
        AnimationComponent anim = AnimationComponent.get(entity);
        boolean newAnim = false;

        if (anim == null) {
            anim = new AnimationComponent(entity);
            newAnim = true;
        }

        anim.fromPos = getPosition();
        anim.toPos = position;
        anim.position = true;

        anim.fromRot = getRotation();
        anim.toRot = rotation;
        anim.rotation = true;

        anim.speed = 4f;

        if (newAnim) {
            entity.add(anim);
        } else {
            anim.resetProgress();
        }
    }

    public void updateTransform(Vector3 position, Quaternion rotation) {
        updateTransform(position, rotation, modelInstance.transform.getScale(new Vector3()));
    }

    public void updateTransform(Vector3 position, Quaternion rotation, Vector3 scale) {
        modelInstance.transform.set(position, rotation);

        if (NodeComponent.get(entity) != null) {
            NodeComponent.get(entity).handlePositionUpdate();
        }

        if (ColliderComponent.get(entity) != null) {
            ColliderComponent.get(entity).updateTransform(modelInstance.transform);
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public boolean isMarker() {
        if  (NodeComponent.get(entity) != null) {
            return NodeComponent.get(entity).isMarker;
        }
        return false;
    }
    public boolean isTracker() {
        if  (NodeComponent.get(entity) != null) {
            return NodeComponent.get(entity).isTracker;
        }
        return false;
    }

    public Vector3 getPosition() {
        return modelInstance.transform.getTranslation(new Vector3());
    }
    public Quaternion getRotation() {return modelInstance.transform.getRotation(new Quaternion()); }
    public Matrix4 getWorldTransform() {return modelInstance.transform; }

    public NodeComponent getNode() {
        return NodeComponent.get(entity);
    }

    public void setColorAttribute(Color color) {
        modelInstance.materials.get(0).set(ColorAttribute.createDiffuse(color));
    }

    public void setTextureAttribute(Texture texture) {
        modelInstance.materials.get(0).set(TextureAttribute.createDiffuse(texture));
    }

}
