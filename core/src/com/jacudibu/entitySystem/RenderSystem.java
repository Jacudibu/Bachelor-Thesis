package com.jacudibu.entitySystem;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.jacudibu.Core;
import com.jacudibu.MainCamera;
import com.jacudibu.components.ArrowComponent;
import com.jacudibu.components.FrustumComponent;
import com.jacudibu.components.GridLineComponent;
import com.jacudibu.components.ModelComponent;

/**
 * Created by Stefan Wolf (Jacudibu) on 07.05.2017.
 * System used for Rendering of every Entity having a ModelComponent.
 */
public class RenderSystem extends EntitySystem {
    private ImmutableArray<Entity> models;
    private ImmutableArray<Entity> arrows;
    private ImmutableArray<Entity> gridLines;
    private ImmutableArray<Entity> frustums;

    private ModelBatch modelBatch;
    private Environment environment;

    public RenderSystem() {
        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    }

    @Override
    public void addedToEngine(Engine engine) {
        models = engine.getEntitiesFor((Family.all(ModelComponent.class).get()));
        arrows = engine.getEntitiesFor((Family.all(ArrowComponent.class).get()));
        gridLines = engine.getEntitiesFor((Family.all(GridLineComponent.class).get()));
        frustums = engine.getEntitiesFor((Family.all(FrustumComponent.class).get()));
    }

    @Override
    public void update(float deltaTime) {
        modelBatch.begin(MainCamera.getCamera());

        drawFrustums();
        drawModels();

        if (Core.grid.isDrawaing()) {
            drawGrid();
        }

        modelBatch.end();
    }

    private void drawFrustums() {
        for (int i = 0; i < frustums.size(); i++) {
            Entity entity = frustums.get(i);
            FrustumComponent frustum = FrustumComponent.get(entity);
            modelBatch.render(frustum.modelInstance);
        }
    }

    private void drawModels() {
        for (int i = 0; i < models.size(); i++) {
            Entity entity = models.get(i);
            ModelComponent model = ModelComponent.get(entity);
            modelBatch.render(model.modelInstance, environment);
        }

        // Ashley doesn't seem to take inheritance into account,
        // therefore modelEntitites won't contain entities with arrowComponents.
        for (int i = 0; i < arrows.size(); i++) {
            Entity entity = arrows.get(i);
            ArrowComponent arrow = ArrowComponent.get(entity);
            if (arrow.modelInstance != null) {
                modelBatch.render(arrow.modelInstance, environment);
            }
        }
    }

    private void drawGrid() {
        for (int i = 0; i < gridLines.size(); i++) {
            Entity entity = gridLines.get(i);
            GridLineComponent gridLine = GridLineComponent.get(entity);
            modelBatch.render(gridLine.modelInstance, environment);
        }
    }
}
