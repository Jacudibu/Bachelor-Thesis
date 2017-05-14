package com.jacudibu.components;

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
 */
public class ArrowComponent extends ModelComponent {
    public ArrowComponent(Vector3 from, Vector3 to) {
        updateModel(from, to);
    }

    public void updateModel(Vector3 from, Vector3 to) {
        if (model != null) {
            model.dispose();
        }

        model = Core.modelBuilder.createArrow(from.x, from.y, from.z, to.x, to.y, to.z,
                0.1f, 0.2f, 10, GL20.GL_TRIANGLES,
                new Material(ColorAttribute.createDiffuse(Color.CYAN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        instance = new ModelInstance(model);
    }

}
