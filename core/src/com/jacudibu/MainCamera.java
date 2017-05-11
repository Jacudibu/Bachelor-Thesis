package com.jacudibu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Stefan Wolf (Jacudibu) on 11.05.2017.
 */
public class MainCamera {
    public static MainCamera instance;

    private PerspectiveCamera cam;
    public Vector3 camRightVector = new Vector3();

    private MainCamera() {
        cam = new PerspectiveCamera(67, Core.windowWidth, Core.windowHeight);
        cam.position.set(0f,0f,10f);
        cam.near = 1f;
        cam.far = 300f;
    }

    public static MainCamera initialize() {
        if (instance != null) {
            return instance;
        }

        instance = new MainCamera();
        return instance;
    }

    public static Camera getCamera() {
        if (instance != null) {
            return instance.cam;
        } else {
            return initialize().cam;
        }

    }

    public void update(float deltaTime) {
        float moveSpeed = 10f;

        // Move
        float forward = 0f;
        float right = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            forward += moveSpeed * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            forward -= moveSpeed * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            right += moveSpeed * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            right -= moveSpeed * deltaTime;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            forward *= 10f;
            right *= 10f;
        }

        zoom(forward);

        Vector3 camRightVector = new Vector3().set(cam.direction).crs(cam.up).nor();
        cam.position.add(camRightVector.x * right, camRightVector.y * right, camRightVector.z * right);
        cam.update();
    }

    public void dragRotation(float xRot, float yRot) {
        camRightVector.set(cam.direction).crs(cam.up).nor();
        cam.rotate(camRightVector, InputManager.invertY ? -yRot : yRot);
        cam.rotate(Vector3.Y, InputManager.invertX ? -xRot : xRot);
        cam.update();
    }

    public void zoom(float distance) {
        cam.position.add(cam.direction.x * distance, cam.direction.y * distance, cam.direction.z * distance);
    }
}
