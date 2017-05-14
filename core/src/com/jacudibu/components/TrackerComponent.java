package com.jacudibu.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Stefan on 11.05.2017.
 */
public class TrackerComponent implements Component {
    public Array<Entity> observerdMarkers;
}
