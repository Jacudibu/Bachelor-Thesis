package com.jacudibu;

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
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Stefan Wolf (Jacudibu) on 15.06.2017.
 */
public class Grid3d implements Disposable{
    private int size;

    private Model axesModel;
    private ModelInstance axesInstance;
    private ModelBatch modelBatch;

    private boolean drawing = true;

    public Grid3d(int size) {
        this.size = size;
        modelBatch = new ModelBatch();

        createAxes();
    }

    public void enable() {
        drawing = true;
    }

    public void disable() {
        drawing = false;
    }

    private void createAxes () {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        Material material = new Material();
        material.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));

        MeshPartBuilder builder = modelBuilder.part("grid", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked, material);
        builder.setColor(new Color(1f,1f,1f,0.1f));

        for (float i = -size; i <= size; i++) {
            builder.line(i, 0, -size, i, 0, size);
            builder.line(-size, 0, i, size, 0, i);
        }

        axesModel = modelBuilder.end();
        axesInstance = new ModelInstance(axesModel);
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
}
