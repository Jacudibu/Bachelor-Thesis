package com.jacudibu.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Stefan Wolf (Jacudibu) on 07.05.2017.
 */
public class ModelComponent implements Component {
    public Model model;
    public ModelInstance instance;

    public Material idle;
    public Material hover;
    public Material selected;

    public ModelComponent(Model model) {
        this(model, new Vector3(), new Quaternion());
    }

    public ModelComponent(Model model, Vector3 position, Quaternion rotation) {
        this.model = model;
        this.instance = new ModelInstance(model);

        this.instance.transform.translate(position);
        this.instance.transform.rotate(rotation);
    }
}
