package com.jacudibu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.jacudibu.UI.InformationDrawer;

/**
 * Created by Stefan Wolf (Jacudibu) on 11.05.2017.
 * Our Camera used for rendering the 3D Scene.
 */
public class MainCamera {
    public static MainCamera instance;

    public PerspectiveCamera cam;
    public Vector3 camRightVector = new Vector3();

    private MainCamera() {
        cam = new PerspectiveCamera(60, Core.windowWidth, Core.windowHeight);
        reset();
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
        if (!InformationDrawer.isCurrentlyFocused()) {
            Vector3 movement = processMovementInput(deltaTime);

            Vector3 camRightVector = new Vector3().set(cam.direction).crs(cam.up).nor();
            cam.position.add(camRightVector.x * movement.x, camRightVector.y * movement.x, camRightVector.z * movement.x);
            cam.position.add(cam.up.x * movement.y, cam.up.y * movement.y, cam.up.z * movement.y);
            zoom(movement.z);
        }

        cam.update();
    }

    private Vector3 processMovementInput(float deltaTime) {
        float moveSpeed = 10f;

        Vector3 movement = new Vector3();

        // Forward
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            movement.z += moveSpeed * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            movement.z -= moveSpeed * deltaTime;
        }

        // Right
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            movement.x += moveSpeed * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            movement.x -= moveSpeed * deltaTime;
        }

        // Up
        if (Gdx.input.isKeyPressed(Input.Keys.Q) || Gdx.input.isKeyPressed(Input.Keys.PAGE_UP)) {
            movement.y += moveSpeed * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.E) || Gdx.input.isKeyPressed(Input.Keys.PAGE_DOWN)) {
            movement.y -= moveSpeed * deltaTime;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            movement.x *= 10f;
            movement.x *= 10f;
            movement.y *= 10f;
            movement.z *= 10f;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            movement.x *= 0.1f;
            movement.y *= 0.1f;
            movement.z *= 0.1f;
        }

        return movement;
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

    public void reset() {
        cam.position.set(0f,2f,5f);
        cam.near = 0.1f;
        cam.far = 300f;

        cam.direction.set(0, 0, -1);
        cam.up.set(0, 1, 0);
        cam.update();

        cam.lookAt(new Vector3(0f, 0f, 0f));
    }
}
