package com.jacudibu.fileSystem;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.jacudibu.components.FrustumComponent;
import com.jacudibu.utility.Intrinsic;

/**
 * Created by Stefan Wolf (Jacudibu) on 19.08.2017.
 */
public class IntrinsicParser {

    private static final int TIMESTAMP = 5;
    private static final int FOCAL_X = 8;
    private static final int SKEW = 9;
    private static final int PRINCIPAL_X = 10;
    private static final int FOCAL_Y = 12;
    private static final int PRINCIPAL_Y = 13;
    private static final int CAM_RESOLUTION_X = 17;
    private static final int CAM_RESOLUTION_Y = 18;
    private static final int RADIAL_DISTORTION_AMOUNT = 19;
    private static final int TANGENTIAL_DISTORTION_AMOUNT = 20;

    public static void parse(String path, Entity assignedEntity) {
        parse(path, PathType.INTERNAL, assignedEntity);
    }

    public static void parse(String path, PathType pathType, Entity assignedEntity) {
        FileHandle file = FileSystem.getFileHandle(path, pathType);

        if (!file.exists()) {
            Gdx.app.error("ERROR", "Unable to parse file from " + path);
            return;
        }

        String data = file.readString();
        String[] dataPieces = data.split(" ");

        Intrinsic intrinsic = new Intrinsic();
        intrinsic.focalX = Float.parseFloat(dataPieces[FOCAL_X]);
        intrinsic.focalY = Float.parseFloat(dataPieces[FOCAL_Y]);
        intrinsic.principalX = Float.parseFloat(dataPieces[PRINCIPAL_X]);
        intrinsic.principalY = Float.parseFloat(dataPieces[PRINCIPAL_Y]);
        intrinsic.skew = Float.parseFloat(dataPieces[SKEW]);
        intrinsic.resolutionX = Integer.parseInt(dataPieces[CAM_RESOLUTION_X]);
        intrinsic.resolutionY = Integer.parseInt(dataPieces[CAM_RESOLUTION_Y]);

        assignedEntity.add(new FrustumComponent(intrinsic));
    }
}
