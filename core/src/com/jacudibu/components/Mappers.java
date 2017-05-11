package com.jacudibu.components;

import com.badlogic.ashley.core.ComponentMapper;

/**
 * Created by Stefan Wolf (Jacudibu) on 07.05.2017.
 */
public class Mappers {
    public static final ComponentMapper<ModelComponent> model = ComponentMapper.getFor(ModelComponent.class);
    public static final ComponentMapper<SelectableComponent> selectable = ComponentMapper.getFor(SelectableComponent.class);


}
