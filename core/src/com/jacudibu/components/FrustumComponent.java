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
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.jacudibu.utility.Intrinsic;

/**
 * Created by Stefan Wolf (Jacudibu) on 19.08.2017.
 */
public class FrustumComponent implements Component {
    private static final ComponentMapper<FrustumComponent> mapper = ComponentMapper.getFor(FrustumComponent.class);

    public Vector3[] planePoints;
    public ModelInstance modelInstance;

    private static Color lineColor = Color.BLACK;
    private Model model;

    public static FrustumComponent get(Entity e) {
        return mapper.get(e);
    }

    private FrustumComponent(Vector3[] planePoints) {
        this.planePoints = planePoints;

        createFrustumModel();
    }

    public static FrustumComponent fromIntrinsic(Intrinsic intrinsic) {
        return fromProjectionMatrix(intrinsic.toProjectionMatrix());
    }

    public static FrustumComponent fromProjectionMatrix(Matrix4 projection) {
        projection = projection.inv();

        Frustum frustum = new Frustum();
        frustum.update(projection.inv());

        //Vector3 a = new Vector3(-1, -1, 1).mul(projection);
        //Vector3 b = new Vector3(-1, 1, 1).mul(projection);
        //Vector3 c = new Vector3(1, 1, 1).mul(projection);
        //Vector3 d = new Vector3(1, -1, 1).mul(projection);

        return new FrustumComponent(frustum.planePoints);
    }

    private void createFrustumModel() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        Material material = new Material();
        material.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));

        MeshPartBuilder builder = modelBuilder.part("grid", GL20.GL_LINES, VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, material);
        builder.setColor(lineColor);

        Vector3 position = new Vector3();

        // Near Plane
        builder.line(planePoints[0], planePoints[1]);
        builder.line(planePoints[1], planePoints[2]);
        builder.line(planePoints[2], planePoints[3]);
        builder.line(planePoints[3], planePoints[0]);

        // Far Plane
        builder.line(planePoints[4], planePoints[5]);
        builder.line(planePoints[5], planePoints[6]);
        builder.line(planePoints[6], planePoints[7]);
        builder.line(planePoints[7], planePoints[4]);

        // Connections between near & far plane
        builder.line(planePoints[0], planePoints[6]);
        builder.line(planePoints[1], planePoints[7]);
        builder.line(planePoints[2], planePoints[4]);
        builder.line(planePoints[3], planePoints[5]);

        model = modelBuilder.end();
        modelInstance = new ModelInstance(model);
    }
}
