package com.jacudibu.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Stefan Wolf (Jacudibu) on 19.08.2017.
 */
public class FrustumComponent implements Component {
    public Vector3 a, b, c, d;
    public ModelInstance modelInstance;

    private static Color lineColor = Color.BLACK;
    private Model model;

    public FrustumComponent(Vector3 a, Vector3 b, Vector3 c, Vector3 d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;

        createFrustumModel();
    }

    private void createFrustumModel() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        Material material = new Material();
        material.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));

        MeshPartBuilder builder = modelBuilder.part("grid", GL20.GL_LINES, VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, material);
        builder.setColor(lineColor);

        Vector3 position = new Vector3();

        builder.line(position, a);
        builder.line(position, b);
        builder.line(position, c);
        builder.line(position, d);

        builder.line(a, b);
        builder.line(b, c);
        builder.line(c, d);
        builder.line(d, a);

        model = modelBuilder.end();
        modelInstance = new ModelInstance(model);
    }
}
