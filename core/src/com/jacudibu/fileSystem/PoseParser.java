package com.jacudibu.fileSystem;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.jacudibu.components.ModelComponent;
import com.jacudibu.entitySystem.SelectionSystem;
import com.jacudibu.utility.Entities;
import com.jacudibu.components.NodeComponent;

import javax.swing.*;

/**
 * Created by Stefan Wolf (Jacudibu) on 10.05.2017.
 * Reads Pose Files and creates Entites depending on their contents.
 */
public class PoseParser {
    public static final int TIMESTAMP = 5;

    private static final int QUATERNION_W = 10;
    private static final int QUATERNION_X = 11;
    private static final int QUATERNION_Y = 12;
    private static final int QUATERNION_Z = 13;

    private static final int POSITION_X = 16;
    private static final int POSITION_Y = 17;
    private static final int POSITION_Z = 18;


    public static void parseFile(String path) {
        parseFile(path, PathType.ABSOLUTE);
    }

    public static void parseFile(String path, PathType pathType) {
        FileHandle file = FileSystem.getFileHandle(path, pathType);

        if (!file.exists()) {
            Gdx.app.error("ERROR", "Unable to parse file from " + path);
            return;
        }

        String data = file.readString();
        String[] dataPieces = data.split(" ");

        Vector3 position = getVector3(dataPieces);
        Quaternion rotation = getQuaternion(dataPieces);

        createPair(position, rotation);
    }

    public static void openLoadDialogue() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Pose...");
        fileChooser.setApproveButtonText("Load");

        JFrame frame = new JFrame();
        frame.setVisible(true);
        frame.toFront();
        frame.setVisible(false);
        int result = fileChooser.showOpenDialog(frame);
        frame.dispose();
        if (result == JFileChooser.APPROVE_OPTION) {
            parseFile(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private static Vector3 getVector3(String[] dataPieces) {
        Vector3 position = new Vector3();
        position.x = Float.parseFloat(dataPieces[POSITION_X]);
        position.y = Float.parseFloat(dataPieces[POSITION_Y]);
        position.z = Float.parseFloat(dataPieces[POSITION_Z]);
        return position;
    }

    private static Quaternion getQuaternion(String[] dataPieces) {
        Quaternion rotation = new Quaternion();
        rotation.w = Float.parseFloat(dataPieces[QUATERNION_W]);
        rotation.x = Float.parseFloat(dataPieces[QUATERNION_X]);
        rotation.y = Float.parseFloat(dataPieces[QUATERNION_Y]);
        rotation.z = Float.parseFloat(dataPieces[QUATERNION_Z]);
        return rotation;
    }

    private static void createPair(Vector3 position, Quaternion rotation) {
        if (SelectionSystem.currentlySelected == null) {
            Entity newTracker = Entities.createTracker(new Vector3(), new Quaternion());
            spawnNewMarkerForEntity(newTracker, position, rotation);
        }
        else {
            spawnNewMarkerForEntity(SelectionSystem.currentlySelected, position, rotation);
        }
    }

    // Spawns a new marker with relative position to it's parent entity
    private static void spawnNewMarkerForEntity(Entity entity, Vector3 position, Quaternion rotation) {
        position.add(ModelComponent.get(entity).getPosition());

        Entity newMarker = Entities.createMarker(position, rotation);

        NodeComponent.get(entity).addOutgoing(newMarker);
    }

}
