package com.jacudibu.entitySystem;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.jacudibu.components.AnimationComponent;

/**
 * Created by Stefan Wolf (Jacudibu) on 16.06.2017.
 * Handles animation of Entities.
 */
public class AnimationSystem extends EntitySystem {

    private ImmutableArray<Entity> animatedEntities;


    @Override
    public void addedToEngine(Engine engine) {
        animatedEntities = engine.getEntitiesFor((Family.all(AnimationComponent.class).get()));
    }

    @Override
    public void update(float deltaTime) {
        for (int i = 0; i < animatedEntities.size(); i++) {
            Entity entity = animatedEntities.get(i);
            AnimationComponent animation = AnimationComponent.get(entity);
            animation.update(deltaTime);
        }
    }
}
