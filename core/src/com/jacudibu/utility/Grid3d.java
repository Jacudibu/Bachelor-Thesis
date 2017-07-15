package com.jacudibu.utility;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.jacudibu.MainCamera;

/**
 * Created by Stefan Wolf (Jacudibu) on 15.06.2017.
 */
public class Grid3d implements Disposable{
    private int size;

    private Model axesModel;
    private ModelInstance axesInstance;
    private ModelBatch modelBatch;

    private boolean drawing = true;

    public boolean isDrawaing() {
        return drawing;
    }

    public Grid3d(int size, boolean circular) {
        this.size = size;
        modelBatch = new ModelBatch();

        createAxes(circular);
    }

    public void enable() {
        drawing = true;
    }

    public void disable() {
        drawing = false;
    }

    private void createAxes (boolean useCircles) {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        Material material = new Material();
        material.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));

        MeshPartBuilder builder = modelBuilder.part("grid", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked, material);
        builder.setColor(new Color(1f,1f,1f,0.2f));

        if (useCircles) {
            for (float i = 1; i <= size; i++) {
                createCircle(i, 100, builder);
            }
        } else {
            for (float i = -size; i <= size; i++) {
                builder.line(i, 0, -size, i, 0, size);
                builder.line(-size, 0, i, size, 0, i);
            }
        }

        axesModel = modelBuilder.end();
        axesInstance = new ModelInstance(axesModel);
    }

    private void createCircle(float radius, int divisions, MeshPartBuilder builder) {
        float step = (MathUtils.degreesToRadians * 360) / divisions;
        float angle = step;
        Vector3 currentPos = new Vector3();
        Vector3 lastPos = new Vector3(MathUtils.cos(0) * radius, 0, MathUtils.sin(0) * radius);
        for (int i = 0; i < divisions; i++) {
            currentPos.x = MathUtils.cos(angle) * radius;
            currentPos.z = MathUtils.sin(angle) * radius;

            builder.line(lastPos, currentPos);
            lastPos = currentPos.cpy();
            angle += step;
        }
    }

    public void render() {
        if (drawing) {
            modelBatch.begin(MainCamera.instance.cam);
            modelBatch.render(axesInstance);
            modelBatch.end();
        }
    }

    @Override
    public void dispose() {
        axesModel.dispose();
        modelBatch.dispose();
    }

    public void toggle() {
        drawing = !drawing;
    }
}
