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
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Created by Stefan Wolf (Jacudibu) on 14.05.2017.
 */
public class UI implements Screen {
    public static Stage stage;

    private Texture addTrackerTexture;
    private Texture addTrackerPressedTexture;
    private Texture addMarkerTexture;
    private Texture addMarkerPressedTexture;

    public UI() {
        stage = new Stage(new FitViewport(Core.windowWidth, Core.windowHeight));
        Core.inputMultiplexer.addProcessor(stage);

        createUpperButtonRow();
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

    private void createUpperButtonRow() {
        addMarkerTexture = new Texture(Gdx.files.internal("addMarker.png"));
        addMarkerPressedTexture = new Texture(Gdx.files.internal("addMarkerPressed.png"));

        ImageButton createMarker = new ImageButton(new TextureRegionDrawable(new TextureRegion(addMarkerTexture)),
                new TextureRegionDrawable(new TextureRegion(addMarkerPressedTexture)));
        createMarker.setPosition(10, Core.windowHeight - 10, Align.topLeft);

        createMarker.addListener(new ActorGestureListener() {
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                Entities.createMarker(new Vector3(), new Quaternion());
            }
        });
        stage.addActor(createMarker);

        addTrackerTexture = new Texture(Gdx.files.internal("addTracker.png"));
        addTrackerPressedTexture = new Texture(Gdx.files.internal("addTrackerPressed.png"));

        ImageButton createTracker = new ImageButton(new TextureRegionDrawable(new TextureRegion(addTrackerTexture)),
                new TextureRegionDrawable(new TextureRegion(addTrackerPressedTexture)));
        createTracker.setPosition(20 + 64,Core.windowHeight - 10, Align.topLeft);

        createTracker.addListener(new ActorGestureListener() {
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                Entities.createTracker(new Vector3(), new Quaternion());
            }
        });

        stage.addActor(createTracker);
    }
}
