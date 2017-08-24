package com.jacudibu.utility;

import com.badlogic.gdx.math.Matrix4;
import org.json.JSONObject;

/**
 * Created by Stefan Wolf (Jacudibu) on 20.08.2017.
 * A class which represents relevant values of an intrinsic matrix.
 */
public class Intrinsic{
    public float focalX;
    public float principalX;
    public float focalY;
    public float principalY;
    public float skew;

    public float far = 10f;
    public float near = 0.5f;

    public int resolutionX;
    public int resolutionY;
    public int nodeID;

    public Matrix4 toProjectionMatrix() {
        Matrix4 projectionMatrix = new Matrix4(new float[]
                {
                        focalX / principalX, 0                  , 0,                             0,
                        0,                   focalY / principalY, 0,                             0,
                        0,                   0,                   -(far + near) / (far  - near), (-2 * far * near) / (far - near),
                        0,                   0,                   -1,                            0
                });

        return projectionMatrix;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        json.put("node", nodeID);
        json.put("focalX", focalX);
        json.put("focalY", focalY);
        json.put("principalX", principalX);
        json.put("principalY", principalY);
        json.put("skew", skew);
        json.put("resolutionX", resolutionX);
        json.put("resolutionY", resolutionY);

        return json;
    }
}