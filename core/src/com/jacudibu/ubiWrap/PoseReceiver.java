package com.jacudibu.ubiWrap;

import com.badlogic.ashley.core.Entity;
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
    private String id;
    private String hex;

    private Vector3 position = new Vector3();
    private Quaternion rotation = new Quaternion();
    public boolean requestUpdate = false;

    public PoseReceiver(String id, String qr) {
        this.id = id;
        hex = qr;
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
            createNode();
        }
        else {
            ModelComponent.get(node).updateTransform(position, rotation);
        }

        requestUpdate = false;
    }

    private void createNode() {
        node = Entities.createNode(position, rotation, NodeComponent.total, id, false, true, hex);
        NodeComponent.get(node).setHex(hex);


    }
}
