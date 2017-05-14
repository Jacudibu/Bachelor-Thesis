package com.jacudibu.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.jacudibu.Core;

/**
 * Created by Stefan Wolf (Jacudibu) on 14.05.2017.
 */
public class UIOverlay extends ScreenAdapter {
    private Stage stage;
    private Skin skin;

    private ButtonRow buttonRow;
    private InformationDrawer informationDrawer;

    public UIOverlay() {
        stage = new Stage(new ScreenViewport());
        Core.inputMultiplexer.addProcessor(stage);
        skin = new Skin(Gdx.files.internal("skins/uiskin.json"));

        buttonRow = new ButtonRow(stage, skin);
        informationDrawer = new InformationDrawer(stage, skin);
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
        informationDrawer.updateUIPositions();
    }

    @Override
    public void dispose() {
        buttonRow.dispose();
        informationDrawer.dispose();
        stage.dispose();
    }
}
