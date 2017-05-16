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
 * Draws various information to the currently selected Object.
 */
public class InformationDrawer implements Disposable {
    private Stage stage;
    private Skin skin;

    private Group positionGroup;
    private TextField.TextFieldListener textFieldListener;

    private TextField xPos, yPos, zPos;

    private static InformationDrawer instance;
    private ModelComponent currentlySelected;

    protected InformationDrawer (Stage stage, Skin skin) {
        instance = this;

        this.stage = stage;
        this.skin = skin;
        setupTextFieldListener();

        generatePositionDrawer();
        generateRotationDrawer();

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
        textFieldListener = (textField, c) -> {
            if (c == '\r' || c == '\n') {
                applyValues();
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
        positionGroup = new Group();
        stage.addActor(positionGroup);

        setupLabel("Position", 100, 0, Align.top, positionGroup);

        // X
        xPos = setupTextField(20, -20, Align.topLeft, positionGroup);
        setupLabel("x ", 20, -20, Align.topRight, positionGroup);

        // Y
        yPos = setupTextField(85, -20, Align.topLeft, positionGroup);
        setupLabel("y ", 85, -20, Align.topRight, positionGroup);

        // Z
        zPos = setupTextField(150, -20,  Align.topLeft, positionGroup);
        setupLabel("z ", 150,-20, Align.topRight, positionGroup);
    }

    private void generateRotationDrawer() {

    }

    private TextField setupTextField(float x, float y, int align, Group group) {
        TextField textField = new TextField("", skin);

        textField.setPosition(x, y, align);
        textField.setWidth(50f);
        textField.setTextFieldListener(textFieldListener);
        textField.setTextFieldFilter(new FloatFilter());
        textField.setDisabled(true);
        group.addActor(textField);

        return textField;
    }

    private Label setupLabel(String text, float x, float y, int align, Group group) {
        Label label = new Label(text, skin);

        label.setPosition(x, y, align);
        group.addActor(label);

        return label;
    }

    protected void updateUIPositions() {
        positionGroup.setPosition(Gdx.graphics.getWidth() - 225, Gdx.graphics.getHeight() - 10);
    }

    @Override
    public void dispose() {
    }

}
