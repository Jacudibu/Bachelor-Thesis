package com.jacudibu;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.jacudibu.components.ArrowComponent;
import com.jacudibu.components.GridLineComponent;
import com.jacudibu.components.NodeComponent;
import com.jacudibu.entitySystem.SelectionSystem;
import com.jacudibu.fileSystem.JsonExporter;
import com.jacudibu.fileSystem.JsonImporter;

/**
 * Created by Stefan Wolf (Jacudibu) on 11.05.2017.
 * Manages all types of User Input.
 */
public class InputManager implements InputProcessor {
    public static InputManager instance;

    public static boolean invertY = true;
    public static boolean invertX = true;

    private enum SelectionAction {
        NONE,
        MERGE,
        CONNECT,
    }

    private static SelectionAction currentAction = SelectionAction.NONE;

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
        Core.inputMultiplexer.addProcessor(this);
    }

    /* Called by Selection System whenever a second entity is selected.
       Depending on pending acitons it will return the Entity that should be chosen as selected.
     */
    public static Entity twoEntitiesSelected(Entity first, Entity second) {
        // Gdx.app.log("Double Selection", first + " <----> " + second);

        switch (currentAction) {
            case MERGE:
                NodeComponent.mapper.get(first).merge(second);

                currentAction = SelectionAction.NONE;
                return first;

            case CONNECT:
                NodeComponent.mapper.get(first).addOutgoing(second);
                currentAction = SelectionAction.NONE;
                return second;

            default:
                currentAction = SelectionAction.NONE;
                return second;
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT))
        {
            return keyDownCTRL(keycode);
        }

        switch (keycode) {
            case Input.Keys.G:
                Core.grid.toggle();
                return true;

            case Input.Keys.R:
                MainCamera.instance.reset();
                return true;
        }

        return HandleSelectionActions(keycode);
    }

    private boolean keyDownCTRL(int keycode) {
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
        {
            return keyDownCTRLSHIFT(keycode);
        }

        switch (keycode) {
            case Input.Keys.S:
                JsonExporter.export();
                return true;

            case Input.Keys.L:
                JsonImporter.openLoadDialogue();
                return true;

            case Input.Keys.N:
                Core.reset();
                return true;
        }

        return false;
    }

    private boolean keyDownCTRLSHIFT(int keycode) {
        switch (keycode) {
            case Input.Keys.S:
                JsonExporter.openSaveDialogue();
                return true;
        }

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

    private boolean HandleSelectionActions(int keycode) {
        SelectionSystem selectionSystem = Core.engine.getSystem(SelectionSystem.class);

        if (selectionSystem.currentlySelected == null) {
            currentAction = SelectionAction.NONE;
            return false;
        }

        switch (keycode) {
            case Input.Keys.M:
                if(setMergeMode()) {
                    return true;
                }
                break;

            case Input.Keys.C:
                if(setConnectMode()) {
                    return true;
                }
                break;

            case Input.Keys.FORWARD_DEL:
                if(setDeleteMode()) {
                    return true;
                }
                break;
        }

        currentAction = SelectionAction.NONE;
        return false;
    }

    public boolean setMergeMode() {
        SelectionSystem selectionSystem = Core.engine.getSystem(SelectionSystem.class);

        if (NodeComponent.mapper.get(selectionSystem.currentlySelected) != null) {
            currentAction = SelectionAction.MERGE;
            return true;
        }

        return false;
    }

    public boolean setConnectMode() {
        SelectionSystem selectionSystem = Core.engine.getSystem(SelectionSystem.class);

        if (NodeComponent.mapper.get(selectionSystem.currentlySelected) != null) {
            currentAction = SelectionAction.CONNECT;
            return true;
        }

        return false;
    }

    public boolean setDeleteMode() {
        SelectionSystem selectionSystem = Core.engine.getSystem(SelectionSystem.class);

        if (NodeComponent.mapper.get(selectionSystem.currentlySelected) != null) {
            currentAction = SelectionAction.NONE;
            NodeComponent.mapper.get(selectionSystem.currentlySelected).delete();
            return true;
        }
        else if (ArrowComponent.mapper.get(selectionSystem.currentlySelected) != null) {
            currentAction = SelectionAction.NONE;
            ArrowComponent.mapper.get(selectionSystem.currentlySelected).delete();
            return true;
        }


        return false;
    }
}
