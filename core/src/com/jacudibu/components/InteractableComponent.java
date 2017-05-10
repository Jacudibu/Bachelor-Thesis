package com.jacudibu.components;

import com.badlogic.ashley.core.Component;


/**
 * Created by Stefan Wolf (Jacudibu) on 08.05.2017.
 */
public class InteractableComponent implements Component {

    public float radius = 3f;

    public InteractableComponent() {}
    public InteractableComponent(float radius) {
        this.radius = radius;
    }
}
