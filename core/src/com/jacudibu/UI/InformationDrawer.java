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
 * Draws various information to the currently selected Object, can also be manipulated by the user.
 */
public class InformationDrawer implements Disposable {
    private Stage stage;
    private Skin skin;

    private Group informationParent;
    private Group positionGroup;
    private Group rotationGroup;
    private TextField.TextFieldListener textFieldListener;

    private TextField xPos, yPos, zPos;
    private TextField xRot, yRot, zRot;

    private static InformationDrawer instance;
    private ModelComponent currentlySelected;

    protected InformationDrawer (Stage stage, Skin skin) {
        instance = this;

        this.stage = stage;
        this.skin = skin;
        setupTextFieldListener();

        informationParent = new Group();
        stage.addActor(informationParent);

        generatePositionDrawer();
        generateRotationDrawer();

        updateUIPositions();
    }

    public static void setCurrentlySelectedObject(ModelComponent selectedObject) {
        instance.currentlySelected = selectedObject;

        if (selectedObject != null) {
            instance.setPositionValues(selectedObject.modelInstance.transform.getTranslation(new Vector3()));
            instance.setRotationValues(selectedObject.modelInstance.transform.getRotation(new Quaternion()));
        }
        else {
            instance.disableInput();
        }

        instance.updateUIPositions();
    }

    private void setPositionValues(Vector3 pos) {
        xPos.setDisabled(false);
        yPos.setDisabled(false);
        zPos.setDisabled(false);

        xPos.setText(Float.toString(pos.x));
        yPos.setText(Float.toString(pos.y));
        zPos.setText(Float.toString(pos.z));
    }

    private void setRotationValues(Quaternion rot) {
        xRot.setDisabled(false);
        yRot.setDisabled(false);
        zRot.setDisabled(false);

        xRot.setText(Float.toString(rot.getYaw()));
        yRot.setText(Float.toString(rot.getPitch()));
        zRot.setText(Float.toString(rot.getRoll()));
    }

    private void disableInput() {
        xPos.setText("");
        yPos.setText("");
        zPos.setText("");

        xPos.setDisabled(true);
        yPos.setDisabled(true);
        zPos.setDisabled(true);

        xRot.setText("");
        yRot.setText("");
        zRot.setText("");

        xRot.setDisabled(true);
        yRot.setDisabled(true);
        zRot.setDisabled(true);
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

        float pitch, roll, yaw;
        pitch = parseFloat(yRot.getText());
        roll  = parseFloat(zRot.getText());
        yaw   = parseFloat(xRot.getText());

        Quaternion rot =  new Quaternion().setEulerAngles(yaw, pitch, roll);

        currentlySelected.updateTransform(pos, rot);
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
        informationParent.addActor(positionGroup);

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
        rotationGroup = new Group();
        informationParent.addActor(rotationGroup);

        setupLabel("Rotation", 100, 0, Align.top, rotationGroup);

        // X
        xRot = setupTextField(20, -20, Align.topLeft, rotationGroup);
        setupLabel("x ", 20, -20, Align.topRight, rotationGroup);

        // Y
        yRot = setupTextField(85, -20, Align.topLeft, rotationGroup);
        setupLabel("y ", 85, -20, Align.topRight, rotationGroup);

        // Z
        zRot = setupTextField(150, -20,  Align.topLeft, rotationGroup);
        setupLabel("z ", 150,-20, Align.topRight, rotationGroup);

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
        if (currentlySelected == null) {
            informationParent.setPosition(-1000, - 1000);
            return;
        } else {
            informationParent.setPosition(Gdx.graphics.getWidth() - 225, Gdx.graphics.getHeight() - 10);
        }

        float x = 0;
        float y = 0;

        positionGroup.setPosition(x, y, Align.topLeft);
        y -= 50;
        rotationGroup.setPosition(x, y, Align.topLeft);
        y -= 50;

        if (currentlySelected.isMarker()) {
            // TODO: Show Marker ID in a Text field
        }

    }

    @Override
    public void dispose() {
    }

}
