package com.jacudibu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by Stefan Wolf (Jacudibu) on 14.05.2017.
 */
public class UI implements Screen {
    private Stage stage;
    private Skin skin;

    private Texture addTrackerTexture;
    private Texture addTrackerPressedTexture;
    private Texture addMarkerTexture;
    private Texture addMarkerPressedTexture;

    ImageButton createMarker;
    ImageButton createTracker;

    public UI() {
        stage = new Stage(new ScreenViewport());
        Core.inputMultiplexer.addProcessor(stage);
        setupSkin();


        createUpperButtonRow();

        TextField test = new TextField("", skin);
        test.setPosition(Core.windowWidth - 10, Core.windowHeight - 10, Align.topRight);
        stage.addActor(test);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        updateUIPositions();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        addTrackerTexture.dispose();
        addTrackerPressedTexture.dispose();
        addMarkerTexture.dispose();
        addMarkerPressedTexture.dispose();
        stage.dispose();
    }

    private void setupSkin() {
        skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
    }

    private void createUpperButtonRow() {
        addMarkerTexture = new Texture(Gdx.files.internal("addMarker.png"));
        addMarkerPressedTexture = new Texture(Gdx.files.internal("addMarkerPressed.png"));

        createMarker = new ImageButton(new TextureRegionDrawable(new TextureRegion(addMarkerTexture)),
                new TextureRegionDrawable(new TextureRegion(addMarkerPressedTexture)));

        createMarker.addListener(new ActorGestureListener() {
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                Entities.createMarker(new Vector3(), new Quaternion());
            }
        });
        stage.addActor(createMarker);

        addTrackerTexture = new Texture(Gdx.files.internal("addTracker.png"));
        addTrackerPressedTexture = new Texture(Gdx.files.internal("addTrackerPressed.png"));

        createTracker = new ImageButton(new TextureRegionDrawable(new TextureRegion(addTrackerTexture)),
                new TextureRegionDrawable(new TextureRegion(addTrackerPressedTexture)));

        createTracker.addListener(new ActorGestureListener() {
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                Entities.createTracker(new Vector3(), new Quaternion());
            }
        });

        stage.addActor(createTracker);

        updateUIPositions();
    }

    private void updateUIPositions() {
        int upperRowHeight = Gdx.graphics.getHeight() - 10;

        // Button Row
        createMarker.setPosition(10, upperRowHeight, Align.topLeft);
        createTracker.setPosition(20 + 64,upperRowHeight, Align.topLeft);
    }
}
