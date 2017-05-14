package com.jacudibu.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.jacudibu.Core;
import com.jacudibu.Entities;

/**
 * Created by Stefan Wolf (Jacudibu) on 14.05.2017.
 */
public class UIOverlay implements Screen {
    private Stage stage;
    private Skin skin;

    private ButtonRow buttonRow;

    public UIOverlay() {
        stage = new Stage(new ScreenViewport());
        Core.inputMultiplexer.addProcessor(stage);
        setupSkin();

        buttonRow = new ButtonRow(stage, skin);

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
        buttonRow.updateUIPositions();
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
        buttonRow.dispose();
        stage.dispose();
    }

    private void setupSkin() {
        skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
    }




}
