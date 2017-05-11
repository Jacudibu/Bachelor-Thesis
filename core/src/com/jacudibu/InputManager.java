package com.jacudibu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.jacudibu.entitySystem.SelectionSystem;

/**
 * Created by Stefan Wolf (Jacudibu) on 11.05.2017.
 */
public class InputManager implements InputProcessor {
    public static InputManager instance;

    public static boolean invertY = true;
    public static boolean invertX = true;

    private int lastDragX;
    private int lastDragY;

    private InputManager() {
        enable();
    }

    public static InputManager initalize() {
        if (instance != null) {
            Gdx.app.log("WARNING", "InputManager is already initialized!");
            return instance;
        }

        instance = new InputManager();
        return instance;
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
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
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

        MainCamera.instance.dragRotation(distanceX, distanceY);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        MainCamera.instance.zoom(-amount);
        return true;
    }
}
