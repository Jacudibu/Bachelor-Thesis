package com.jacudibu.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;


/**
 * Created by Stefan Wolf (Jacudibu) on 08.05.2017.
 * Data Container for everything that needs interaction via mouse clicks.
 */
public class SelectableComponent implements Component {
    public static final ComponentMapper<SelectableComponent> mapper = ComponentMapper.getFor(SelectableComponent.class);

    public float radius = 1f;

    public SelectableComponent() {}
    public SelectableComponent(float radius) {
        this.radius = radius;
    }
}
