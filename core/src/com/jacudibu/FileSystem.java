package com.jacudibu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Stefan Wolf (Jacudibu) on 10.05.2017.
 */
public class FileSystem {
    public static final int TIMESTAMP = 5;

    public static final int QUATERNION_W = 10;
    public static final int QUATERNION_X = 11;
    public static final int QUATERNION_Y = 12;
    public static final int QUATERNION_Z = 13;

    public static final int POSITION_X = 16;
    public static final int POSITION_Y = 17;
    public static final int POSITION_Z = 18;

    public enum PathType {
        INTERNAL,
        EXTERNAL,
        ABSOLUTE,
        CLASSPATH,
    }

    public static void parseFile(String path) {
        parseFile(path, PathType.ABSOLUTE);
    }

    public static void parseFile(String path, PathType pathType) {
        FileHandle file = null;
        switch (pathType) {
            case INTERNAL:
                file = Gdx.files.internal(path);
                break;
            case EXTERNAL:
                file = Gdx.files.external(path);
                break;
            case ABSOLUTE:
                file = Gdx.files.absolute(path);
                break;
            case CLASSPATH:
                file = Gdx.files.classpath(path);
                break;
        }

        if (!file.exists()) {
            Gdx.app.error("ERROR", "Unable to parse file from " + path);
            return;
        }

        String data = file.readString();
        String[] dataPieces = data.split(" ");

        Vector3 position = getVector3(dataPieces);
        Quaternion rotation = getQuaternion(dataPieces);

        Entities.createMarker(position, rotation);
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



}
