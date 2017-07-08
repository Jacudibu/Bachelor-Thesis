package com.jacudibu.fileSystem;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.jacudibu.Utility.Entities;
import com.jacudibu.components.NodeComponent;

/**
 * Created by Stefan Wolf (Jacudibu) on 10.05.2017.
 * Reads Files and creates Entites depending on their contents.
 */
public class FileListener {
    public static final int TIMESTAMP = 5;

    public static final int QUATERNION_W = 10;
    public static final int QUATERNION_X = 11;
    public static final int QUATERNION_Y = 12;
    public static final int QUATERNION_Z = 13;

    public static final int POSITION_X = 16;
    public static final int POSITION_Y = 17;
    public static final int POSITION_Z = 18;


    public static void parseFile(String path) {
        parseFile(path, PathType.ABSOLUTE);
    }

    public static void parseFile(String path, PathType pathType) {
        FileHandle file = getFileHandle(path, pathType);

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

    protected static FileHandle getFileHandle(String path, PathType pathType) {
        switch (pathType) {
            case INTERNAL:
                return Gdx.files.internal(path);
            case EXTERNAL:
                return Gdx.files.external(path);
            case ABSOLUTE:
                return Gdx.files.absolute(path);
            case CLASSPATH:
                return Gdx.files.classpath(path);
            default:
                return null;
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
        Entity newMarker = Entities.createMarker(position, rotation);
        Entity newTracker = Entities.createTracker(new Vector3(), new Quaternion());

        NodeComponent.mapper.get(newTracker).addOutgoing(newMarker);
    }



}
