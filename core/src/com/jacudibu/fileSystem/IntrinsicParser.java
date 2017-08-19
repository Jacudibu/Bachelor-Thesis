package com.jacudibu.fileSystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.jacudibu.components.FrustumComponent;

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

    public static FrustumComponent parse(String path) {
        return parse(path, PathType.INTERNAL);
    }

    public static FrustumComponent parse(String path, PathType pathType) {
        FileHandle file = FileSystem.getFileHandle(path, pathType);

        if (!file.exists()) {
            Gdx.app.error("ERROR", "Unable to parse file from " + path);
            return null;
        }

        String data = file.readString();
        String[] dataPieces = data.split(" ");

        float focalX = Float.parseFloat(dataPieces[FOCAL_X]);
        float focalY = Float.parseFloat(dataPieces[FOCAL_Y]);
        float principalX = Float.parseFloat(dataPieces[PRINCIPAL_X]);
        float principalY = Float.parseFloat(dataPieces[PRINCIPAL_Y]);

        float far  = 100f;
        float near = 0.1f;

        Matrix4 projectionMatrix = new Matrix4(new float[]
                {
                        focalX / principalX, 0                  , 0,                             0,
                        0,                   focalY / principalY, 0,                             0,
                        0,                   0,                   -(far + near) / (far  - near), (-2 * far * near) / (far - near),
                        0,                   0,                   -1,                            0
                });

        projectionMatrix = projectionMatrix.inv();

        Vector3 a = new Vector3(-1, -1, 1).mul(projectionMatrix);
        Vector3 b = new Vector3(-1, 1, 1).mul(projectionMatrix);
        Vector3 c = new Vector3(1, 1, 1).mul(projectionMatrix);
        Vector3 d = new Vector3(1, -1, 1).mul(projectionMatrix);

        return new FrustumComponent(a, b, c, d);
    }
}
