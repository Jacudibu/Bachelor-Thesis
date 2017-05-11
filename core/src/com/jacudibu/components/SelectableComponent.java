package com.jacudibu.components;

import com.badlogic.ashley.core.Component;


/**
 * Created by Stefan Wolf (Jacudibu) on 08.05.2017.
 */
public class SelectableComponent implements Component {

    public float radius = 1f;

    public SelectableComponent() {}
    public SelectableComponent(float radius) {
        this.radius = radius;
    }
}
