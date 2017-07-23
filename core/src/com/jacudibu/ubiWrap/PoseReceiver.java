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

    private Entity marker;
    private Entity tracker;
    private String id;
    private String hex;

    private Vector3 position = new Vector3();
    private Quaternion rotation = new Quaternion();
    public boolean requestUpdate = false;

    public PoseReceiver(String id, String qr) {
        this.id = id;
        hex = qr;
    }

    public void setTracker(Entity entity) {
        tracker = entity;
    }

    public Entity getTracker() {
        return tracker;
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
        if (marker == null) {
            createNode();
        }
        else {
            // Add the position of the camera which is tracking our Marker as an offset.
            if (tracker != null) {
                position.add(ModelComponent.get(tracker).getPosition());
            }

            ModelComponent.get(marker).animateTo(position, rotation);
        }

        requestUpdate = false;
    }

    private void createNode() {
        marker = Entities.createNode(position, rotation, NodeComponent.total, id, false, true, hex);
        NodeComponent markerComponent = NodeComponent.get(marker);
        markerComponent.setHex(hex);
        markerComponent.setPoseReceiver(this);

        tracker = Entities.createTracker(new Vector3(), new Quaternion());
        NodeComponent trackerComponent = NodeComponent.get(tracker);

        trackerComponent.addTracked(markerComponent);
    }
}
