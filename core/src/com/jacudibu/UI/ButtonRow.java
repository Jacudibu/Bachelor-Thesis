package com.jacudibu.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.jacudibu.Core;
import com.jacudibu.fileSystem.PoseParser;
import com.jacudibu.ubiWrap.DFGParser;
import com.jacudibu.utility.Entities;
import com.jacudibu.InputManager;
import com.jacudibu.fileSystem.JsonExporter;
import com.jacudibu.fileSystem.JsonImporter;

/**
 * Created by Stefan Wolf (Jacudibu) on 14.05.2017.
 * The Upper UI Row on screen.
 */
public class ButtonRow implements Disposable {
    private Stage stage;
    private Skin skin;
    Array<Texture> textureArray = new Array<Texture>();

    Group buttonGroup;

    public ButtonRow(Stage stage, Skin skin) {
        buttonGroup = new Group();
        stage.addActor(buttonGroup);

        this.stage = stage;
        this.skin = skin;

        int column1 = 0;
        int column2 = 69;
        int y = 0;
        int yStep = 69;

        createSaveButton(column1, 0);
        createLoadButton(column2, 0);
        y -= yStep;

        createMarkerButton(column1, y);
        createTrackerButton(column2, y);
        y -= yStep;

        createConnectButton(column1, y);
        createMergeButton(column2, y);
        y -= yStep;

        createToggleGridButton(column1, y);

        y -= yStep;
        createLoadPoseButton(column1, y);
        createLoadDFGButton(column2, y);

        updateUIPositions();
    }

    private void createButton(int x, int y, String image, String imagePressed, ActorGestureListener actionListener) {
        Texture normalTexture = new Texture(Gdx.files.internal(image));
        Texture pressedTexture = new Texture(Gdx.files.internal(imagePressed));

        ImageButton button = new ImageButton(new TextureRegionDrawable(new TextureRegion(normalTexture)),
                new TextureRegionDrawable(new TextureRegion(pressedTexture)));

        button.addListener(actionListener);
        button.setPosition(x, y, Align.topLeft);
        buttonGroup.addActor(button);

        textureArray.add(normalTexture);
        textureArray.add(pressedTexture);
    }

    private void createSaveButton(int x, int y) {
        createButton(x, y, "buttons/save.png", "buttons/savePressed.png",
                new ActorGestureListener() {
                    public void tap(InputEvent event, float x, float y, int count, int button) {
                        super.tap(event, x, y, count, button);
                        JsonExporter.openSaveDialogue();
                    }
                });
    }

    private void createLoadButton(int x, int y) {
        createButton(x, y, "buttons/load.png", "buttons/loadPressed.png",
                new ActorGestureListener() {
                    public void tap(InputEvent event, float x, float y, int count, int button) {
                        super.tap(event, x, y, count, button);
                        JsonImporter.openLoadDialogue();
                    }
                });
    }
    private void createMarkerButton(int x, int y) {
        createButton(x, y, "buttons/addMarker.png", "buttons/addMarkerPressed.png",
                new ActorGestureListener() {
                    public void tap(InputEvent event, float x, float y, int count, int button) {
                        super.tap(event, x, y, count, button);
                        Entities.createMarker(new Vector3(), new Quaternion());
                    }
                });
    }

    private void createTrackerButton(int x, int y) {
        createButton(x, y, "buttons/addTracker.png", "buttons/addTrackerPressed.png",
            new ActorGestureListener() {
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                Entities.createTracker(new Vector3(), new Quaternion());
                }
            });
    }

    private void createConnectButton(int x, int y) {
        createButton(x, y, "buttons/connect.png", "buttons/connectPressed.png",
                new ActorGestureListener() {
                    public void tap(InputEvent event, float x, float y, int count, int button) {
                        super.tap(event, x, y, count, button);
                        InputManager.instance.setConnectMode();
                    }
                });
    }

    private void createMergeButton(int x, int y) {
        createButton(x, y, "buttons/merge.png", "buttons/mergePressed.png",
                new ActorGestureListener() {
                    public void tap(InputEvent event, float x, float y, int count, int button) {
                        super.tap(event, x, y, count, button);
                        InputManager.instance.setMergeMode();
                    }
                });
    }



    private void createToggleGridButton(int x, int y) {
        createButton(x, y, "buttons/toggleGrid.png", "buttons/toggleGridPressed.png",
                new ActorGestureListener() {
                    public void tap(InputEvent event, float x, float y, int count, int button) {
                        super.tap(event, x, y, count, button);
                        Core.grid.toggle();
                    }
                });
    }

    private void createLoadDFGButton(int x, int y) {
        if (!Core.usingUbitrack) {
            return;
        }

        createButton(x, y, "buttons/loadDFG.png", "buttons/loadDFGPressed.png",
                new ActorGestureListener() {
                    public void tap(InputEvent event, float x, float y, int count, int button) {
                        super.tap(event, x, y, count, button);
                        DFGParser.openLoadDialogue();
                    }
                });
    }

    private void createLoadPoseButton(int x, int y) {
        createButton(x, y, "buttons/loadPose.png", "buttons/loadPosePressed.png",
                new ActorGestureListener() {
                    public void tap(InputEvent event, float x, float y, int count, int button) {
                        super.tap(event, x, y, count, button);
                        PoseParser.openLoadDialogue();
                    }
                });
    }


    public void updateUIPositions() {
        buttonGroup.setPosition(10, Gdx.graphics.getHeight() - 10);
    }

    @Override
    public void dispose() {
        for (int i = textureArray.size -1; i >= 0; i--) {
            textureArray.get(i).dispose();
        }
    }
}
