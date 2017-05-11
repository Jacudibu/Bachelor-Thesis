package com.jacudibu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.jacudibu.entitySystem.SelectionSystem;

/**
 * Created by Stefan Wolf (Jacudibu) on 11.05.2017.
 */
public class InputManager implements InputProcessor {
    public static InputManager instance;

    public boolean invertY = true;
    public boolean invertX = true;

    private int lastDragX;
    private int lastDragY;

    public InputManager() {
        enable();
    }

    public static void initalize() {
        if (instance != null) {
            Gdx.app.log("WARNING", "InputManager is already initialized!");
            return;
        }

        instance = new InputManager();
    }

    public void enable() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
    }

    @Override
    public boolean keyTyped(char character) {
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        lastDragX = screenX;
        lastDragY = screenY;

        if (button == Input.Buttons.LEFT) {
            Core.engine.getSystem(SelectionSystem.class).select();
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        float distanceX = screenX - lastDragX;
        float distanceY = screenY - lastDragY;

        lastDragX = screenX;
        lastDragY = screenY;

        Vector3 camRightVector = new Vector3().set(Core.mainCamera.direction).crs(Core.mainCamera.up).nor();
        Core.mainCamera.rotate(camRightVector, invertY ? -distanceY : distanceY);
        Core.mainCamera.rotate(Vector3.Y, invertX ? -distanceX : distanceX);
        Core.mainCamera.update();
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
