package com.jacudibu.ubiWrap;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.jacudibu.utility.Entities;
import com.jacudibu.components.ModelComponent;
import com.jacudibu.components.NodeComponent;
import ubitrack.SimplePose;
import ubitrack.SimplePoseReceiver;

/**
 * Created by Stefan on 15.07.2017.
 */
public class PoseReceiver extends SimplePoseReceiver {
    public static final float scaleFactor = 0.01f;

    private Entity node;
    private String nodeID;

    private Vector3 position = new Vector3();
    private Quaternion rotation = new Quaternion();
    public boolean requestUpdate = false;

    public PoseReceiver(String id) {
        nodeID = id;
    }

    public void receivePose(SimplePose pose) {
        position.x = ((float) pose.getTx()) * scaleFactor;
        position.y = ((float) pose.getTy()) * scaleFactor;
        position.z = ((float) pose.getTz()) * scaleFactor;

        rotation.x = (float) pose.getRx();
        rotation.y = (float) pose.getRy();
        rotation.z = (float) pose.getRz();
        rotation.w = (float) pose.getRw();

        //Gdx.app.log("POSE", "Received " + position + " \t " + rotation);

        // Can't update here as this is most likely called by a non-GL thread
        requestUpdate = true;
    }

    public void update() {
        if (node == null) {
            node = Entities.createMarker(position, rotation);
            NodeComponent.get(node).setHex(nodeID);
        }
        else {
            ModelComponent.get(node).updateTransform(position, rotation);
        }

        requestUpdate = false;
    }
}
