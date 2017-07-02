package com.jacudibu.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

/**
 * Draws a line between this Entities position and the XZ Axis.
 * Created by Stefan Wolf (Jacudibu) on 02.07.2017.
 */
public class GridLineComponent implements Component {
    public static final ComponentMapper<GridLineComponent> mapper = ComponentMapper.getFor(GridLineComponent.class);
    private static boolean drawIntersections = true;

    private Model model;
    private Entity entity;
    public ModelInstance modelInstance;

    public GridLineComponent(Entity entity) {
        this.entity = entity;
        updatePosition();
    }

    public void updatePosition() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        Material material = new Material();
        material.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));

        MeshPartBuilder builder = modelBuilder.part("grid", GL20.GL_LINES, VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, material);
        builder.setColor(new Color(1f,1f,1f,0.2f));

        Vector3 position = ModelComponent.mapper.get(entity).getPosition();
        Vector3 target = position.cpy();
        target.y = 0;

        builder.line(position, target);

        if (drawIntersections) {
            generateIntersections(builder, position);
        }


        model = modelBuilder.end();
        modelInstance = new ModelInstance(model);
    }

    private void generateIntersections(MeshPartBuilder builder, Vector3 position) {
        float sign = position.y > 0 ? 1 : -1;
        Vector3 x1 = position.cpy();
        x1.x -= 0.05f;
        Vector3 x2 = position.cpy();
        x2.x += 0.05f;
        Vector3 z1 = position.cpy();
        z1.z -= 0.05f;
        Vector3 z2 = position.cpy();
        z2.z += 0.05f;
        for (float i = 1; i < Math.abs(position.y); i++) {
            x1.y = i * sign;
            x2.y = i * sign;
            z1.y = i * sign;
            z2.y = i * sign;

            builder.line(x1, x2);
            builder.line(z1, z2);

            //builder.line(x1, z1);
            //builder.line(z1, x2);
            //builder.line(x2, z2);
            //builder.line(z2, x1);
        }
    }

}
