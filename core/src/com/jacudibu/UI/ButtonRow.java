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
import com.badlogic.gdx.utils.Disposable;
import com.jacudibu.Entities;

/**
 * Created by Stefan Wolf (Jacudibu) on 14.05.2017.
 */
public class ButtonRow implements Disposable {
    private Stage stage;
    private Skin skin;
    private Texture addTrackerTexture;
    private Texture addTrackerPressedTexture;
    private Texture addMarkerTexture;
    private Texture addMarkerPressedTexture;

    Group buttonGroup;

    public ButtonRow(Stage stage, Skin skin) {
        buttonGroup = new Group();
        stage.addActor(buttonGroup);

        this.stage = stage;
        this.skin = skin;

        createMarkerButton();
        createTrackerButton();

        updateUIPositions();
    }

    private void createMarkerButton() {
        addMarkerTexture = new Texture(Gdx.files.internal("addMarker.png"));
        addMarkerPressedTexture = new Texture(Gdx.files.internal("addMarkerPressed.png"));

        ImageButton createMarker = new ImageButton(new TextureRegionDrawable(new TextureRegion(addMarkerTexture)),
                new TextureRegionDrawable(new TextureRegion(addMarkerPressedTexture)));

        createMarker.addListener(new ActorGestureListener() {
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                Entities.createMarker(new Vector3(), new Quaternion());
            }
        });
        createMarker.setPosition(0, 0, Align.topLeft);
        buttonGroup.addActor(createMarker);
    }

    private void createTrackerButton() {
        addTrackerTexture = new Texture(Gdx.files.internal("addTracker.png"));
        addTrackerPressedTexture = new Texture(Gdx.files.internal("addTrackerPressed.png"));

        ImageButton createTracker = new ImageButton(new TextureRegionDrawable(new TextureRegion(addTrackerTexture)),
                new TextureRegionDrawable(new TextureRegion(addTrackerPressedTexture)));

        createTracker.addListener(new ActorGestureListener() {
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                Entities.createTracker(new Vector3(), new Quaternion());
            }
        });
        createTracker.setPosition(64 + 10,0, Align.topLeft);
        buttonGroup.addActor(createTracker);
    }

    public void updateUIPositions() {
        buttonGroup.setPosition(10, Gdx.graphics.getHeight() - 10);
    }

    @Override
    public void dispose() {
        addTrackerTexture.dispose();
        addTrackerPressedTexture.dispose();
        addMarkerTexture.dispose();
        addMarkerPressedTexture.dispose();
    }
}
