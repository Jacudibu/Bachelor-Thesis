package com.jacudibu.utility;

import com.badlogic.gdx.math.Matrix4;

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

    public int resolutionX;
    public int resolutionY;

    public Matrix4 toProjectionMatrix() {
        float far  = 100f;
        float near = 0.1f;

        Matrix4 projectionMatrix = new Matrix4(new float[]
                {
                        focalX / principalX, 0                  , 0,                             0,
                        0,                   focalY / principalY, 0,                             0,
                        0,                   0,                   -(far + near) / (far  - near), (-2 * far * near) / (far - near),
                        0,                   0,                   -1,                            0
                });

        return projectionMatrix;
    }
}