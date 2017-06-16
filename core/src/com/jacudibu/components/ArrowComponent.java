package com.jacudibu.components;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.jacudibu.Core;

/**
 * Created by Stefan Wolf (Jacudibu) on 14.05.2017.
 * Data Container used to visualized relations between Trackers and Markers.
 */
public class ArrowComponent extends ModelComponent {
    public static final ComponentMapper<ArrowComponent> mapper = ComponentMapper.getFor(ArrowComponent.class);

    private static Model head;
    public ModelInstance headInstance;

    private Entity from;
    private Entity to;

    public ArrowComponent(Entity from, Entity to) {
        this.from = from;
        this.to = to;

        updateModel();
    }

    public void updateModel() {
        if (model != null) {
            model.dispose();
            model = null;
            modelInstance = null;
        }

        Vector3 fromPos = ModelComponent.mapper.get(from).modelInstance.transform.getTranslation(new Vector3());
        Vector3 toPos = ModelComponent.mapper.get(to).modelInstance.transform.getTranslation(new Vector3());

        if (fromPos.equals(toPos)) {
            Gdx.app.log("Warning", "Can't draw Arrow, fromPos == toPos!");
            return;
        }

        float distance = fromPos.dst(toPos);
        float sizeFactor = 1f / distance; // Needed since libGDX makes arrow thickness depending on it's length, which just looks ugly

        // Gdx.app.log("Arrow Info", fromPos + " -> " + toPos + "\nDistance: " + distance + ", Factor: " + sizeFactor);

        model = Core.modelBuilder.createArrow(fromPos.x, fromPos.y, fromPos.z, toPos.x, toPos.y, toPos.z,
                0.1f  * sizeFactor, 0.2f, 10, GL20.GL_TRIANGLES,
                new Material(ColorAttribute.createDiffuse(Color.CYAN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        modelInstance = new ModelInstance(model);
    }
}
