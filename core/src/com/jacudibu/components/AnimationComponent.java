package com.jacudibu.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.jacudibu.entitySystem.AnimationSystem;

/**
 * Created by Stefan Wolf (Jacudibu) on 16.06.2017.
 * Data container for Entities that need some sort of Animation.
 */
public class AnimationComponent implements Component {
    public static final ComponentMapper<AnimationComponent> mapper = ComponentMapper.getFor(AnimationComponent.class);

    public boolean position;
    public Vector3 fromPos;
    public Vector3 toPos;

    public boolean scale;
    public Vector3 fromScale;
    public Vector3 toScale;

    public boolean rotation;
    public Quaternion fromRot;
    public Quaternion toRot;

    public float speed = 1f;

    private float currentAnimationProgress = 0f;
    private Entity entity;
    private ModelComponent modelComponent;

    public static AnimationComponent scale01(Entity entity) {
        AnimationComponent anim = new AnimationComponent(entity);

        anim.fromScale = Vector3.Zero;
        anim.toScale = new Vector3(1,1,1);
        anim.scale = true;

        return anim;
    }

    public AnimationComponent(Entity entity) {
        this.entity = entity;
        modelComponent = ModelComponent.mapper.get(entity);
    }

    public void update(float deltaTime) {
        currentAnimationProgress = MathUtils.clamp(currentAnimationProgress + deltaTime * speed, 0, 1);

        Vector3 currentPos;
        Vector3 currentScale;
        Quaternion currentRot;

        if (position) {
            currentPos = fromPos.cpy();
            currentPos.lerp(toPos, currentAnimationProgress);
        } else {
            currentPos = modelComponent.modelInstance.transform.getTranslation(new Vector3());
        }

        if (scale) {
            currentScale = fromScale.cpy();
            currentScale.lerp(toScale, currentAnimationProgress);
        } else {
            currentScale = modelComponent.modelInstance.transform.getScale(new Vector3());
        }

        if (rotation) {
            currentRot = fromRot.cpy();
            currentRot.slerp(toRot, currentAnimationProgress);
        } else {
            currentRot = modelComponent.modelInstance.transform.getRotation(new Quaternion());
        }

        modelComponent.updateTransform(currentPos, currentRot, currentScale);

        if (currentAnimationProgress == 1) {
            entity.remove(AnimationComponent.class);
        }
    }
}
