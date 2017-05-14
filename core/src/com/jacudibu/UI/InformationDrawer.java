package com.jacudibu.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.jacudibu.components.ModelComponent;

/**
 * Created by Stefan Wolf (Jacudibu) on 14.05.2017.
 */
public class InformationDrawer implements Disposable {
    private Stage stage;
    private Skin skin;

    private Group positionGroup;
    private TextField.TextFieldListener textFieldListener;

    private TextField xPos, yPos, zPos;

    private static InformationDrawer instance;
    private ModelComponent currentlySelected;

    public InformationDrawer (Stage stage, Skin skin) {
        instance = this;

        this.stage = stage;
        this.skin = skin;
        setupTextFieldListener();

        positionGroup = new Group();
        stage.addActor(positionGroup);

        generatePositionDrawer();

        updateUIPositions();
    }

    public static void setCurrentlySelectedObject(ModelComponent selectedObject) {
        instance.currentlySelected = selectedObject;

        if (selectedObject != null) {
            instance.setPositionValues(selectedObject.instance.transform.getTranslation(new Vector3()));
        }
        else {
            instance.disableInput();
        }

    }

    private void setPositionValues(Vector3 pos) {
        xPos.setDisabled(false);
        yPos.setDisabled(false);
        zPos.setDisabled(false);

        xPos.setText(Float.toString(pos.x));
        yPos.setText(Float.toString(pos.y));
        zPos.setText(Float.toString(pos.z));
    }

    private void disableInput() {
        xPos.setText("");
        yPos.setText("");
        zPos.setText("");

        xPos.setDisabled(true);
        yPos.setDisabled(true);
        zPos.setDisabled(true);
    }

    private void setupTextFieldListener() {
        textFieldListener = new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if (c == '\r' || c == '\n') {
                    applyValues();
                }
            }
        };

    }

    private void applyValues() {
        Vector3 pos = new Vector3();
        pos.x = parseFloat(xPos.getText());
        pos.y = parseFloat(yPos.getText());
        pos.z = parseFloat(zPos.getText());

        currentlySelected.instance.transform.set(pos, new Quaternion());
    }

    private float parseFloat(String string) {
        // That this isn't part of the basic Float.parse function is ridiculous.
        if (string.isEmpty())
            return 0f;

        string = string.replace(',','.');

        // TODO: More error handling.

        try {
            return Float.parseFloat(string);
        } catch (java.lang.NumberFormatException e) {
            return 0;
        }
    }

    private void generatePositionDrawer() {
        // X
        xPos = new TextField("", skin);
        setupTextField(xPos, 0, 0, Align.topLeft);
        positionGroup.addActor(xPos);

        Label xText = new Label("x ", skin);
        xText.setPosition(0,0,Align.topRight);
        positionGroup.addActor(xText);

        // Y
        yPos = new TextField("", skin);
        setupTextField(yPos, 65, 0, Align.topLeft);
        positionGroup.addActor(yPos);


        Label yText = new Label("y ", skin);
        yText.setPosition(65,0,Align.topRight);
        positionGroup.addActor(yText);

        // Z
        zPos = new TextField("", skin);
        setupTextField(zPos, 130, 0,  Align.topLeft);
        positionGroup.addActor(zPos);

        Label zText = new Label("z ", skin);
        zText.setPosition(130,0,Align.topRight);
        positionGroup.addActor(zText);
    }

    private void setupTextField(TextField textField, float x, float y, int align) {
        textField.setPosition(x, y, align);
        textField.setWidth(50f);
        textField.setTextFieldListener(textFieldListener);
        textField.setTextFieldFilter(new FloatFilter());
        textField.setDisabled(true);
    }

    public void updateUIPositions() {
        positionGroup.setPosition(Gdx.graphics.getWidth() - 200, Gdx.graphics.getHeight() - 10);
    }

    @Override
    public void dispose() {

    }

}
