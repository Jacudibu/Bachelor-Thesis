package com.jacudibu.components;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.jacudibu.Core;

/**
 * Created by Stefan Wolf (Jacudibu) on 14.05.2017.
 * Data Container used to visualized relations between Trackers and Markers.
 */
public class ArrowComponent extends ModelComponent {
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
        }

        Vector3 fromPos = Mappers.model.get(from).instance.transform.getTranslation(new Vector3());
        Vector3 toPos = Mappers.model.get(to).instance.transform.getTranslation(new Vector3());
        Gdx.app.log("", fromPos + " -> " + toPos);
        // TODO: Create model by yourself and just update its vertices.
        model = Core.modelBuilder.createArrow(fromPos.x, fromPos.y, fromPos.z, toPos.x, toPos.y, toPos.z,
                0.1f, 0.2f, 10, GL20.GL_TRIANGLES,
                new Material(ColorAttribute.createDiffuse(Color.CYAN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        instance = new ModelInstance(model);
    }
}
