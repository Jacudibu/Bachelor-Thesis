package com.jacudibu.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.jacudibu.components.FrustumComponent;
import com.jacudibu.components.ModelComponent;
import com.jacudibu.components.NodeComponent;
import com.jacudibu.entitySystem.SelectionSystem;

/**
 * Created by Stefan Wolf (Jacudibu) on 14.05.2017.
 * Draws various information to the currently selected Object, can also be manipulated by the user.
 */
public class InformationDrawer implements Disposable {
    private Stage stage;
    private Skin skin;

    private Group informationParent;
    private Group nameGroup;
    private Group positionGroup;
    private Group rotationGroup;
    private Group intrinsicGroup;
    private TextField.TextFieldListener textFieldListener;

    private TextField name;
    private TextField hex;
    private TextField xPos, yPos, zPos;
    private TextField xRot, yRot, zRot;
    private TextField near, far;
    private FloatFilter floatFilter = new FloatFilter();

    private static InformationDrawer instance;
    private ModelComponent currentlySelected;

    private final float backgroundBaseHeight = 200f;

    protected InformationDrawer (Stage stage, Skin skin) {
        instance = this;

        this.stage = stage;
        this.skin = skin;
        setupTextFieldListener();

        informationParent = new Group();
        stage.addActor(informationParent);

        informationParent.setWidth(215);
        informationParent.setHeight(backgroundBaseHeight);

        Image image = new Image(skin.getDrawable("textfield"));
        image.setFillParent(true);
        informationParent.addActor(image);

        generateNameDrawers();
        generatePositionDrawer();
        generateRotationDrawer();
        generateIntrinsicDrawer();

        updateUIPositions();
    }

    public static void setCurrentlySelectedObject(ModelComponent selectedObject) {
        instance.currentlySelected = selectedObject;

        if (selectedObject != null) {
            NodeComponent node = NodeComponent.get(selectedObject.getEntity());

            instance.setName(node.name);
            if (node.isMarker) {
                instance.setHex(node.getHex());
            }
            else {
                instance.hideHex();
            }

            instance.setPositionValues(selectedObject.getPosition());
            instance.setRotationValues(selectedObject.getRotation());
            instance.setIntrinsicValues(FrustumComponent.get(selectedObject.getEntity()));

        }
        else {
            instance.disableInput();
        }

        instance.updateUIPositions();
    }

    public static boolean isCurrentlyFocused() {
        return instance.stage.getKeyboardFocus() != null;
    }

    private void setName(String newName) {
        name.setDisabled(false);
        name.setText(newName);
    }

    private void setHex(String newHex) {
        hex.setDisabled(false);
        hex.setText(newHex);
        hex.setVisible(true);
    }

    private void hideHex() {
        hex.setDisabled(true);
        hex.setVisible(false);
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

    private void setIntrinsicValues(FrustumComponent frustum) {
        if (frustum == null) {
            return;
        }

        near.setDisabled(false);
        far.setDisabled(false);

        near.setText(Float.toString(frustum.getIntrinsic().near));
        far.setText(Float.toString(frustum.getIntrinsic().far));
    }

    private void disableInput() {
        name.setText("");
        name.setDisabled(true);

        hex.setText("");
        hex.setDisabled(true);

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

        near.setText("");
        far.setText("");

        near.setDisabled(true);
        far.setDisabled(true);

        stage.setKeyboardFocus(null);
    }

    private void setupTextFieldListener() {
        textFieldListener = (textField, c) -> {
            if (c == '\r' || c == '\n') {
                applyValues();
            }
        };

    }

    private void applyValues() {

        applyNameChanges(NodeComponent.get(currentlySelected.getEntity()));
        applyTransformChanges();
        applyIntrinsicChanges(FrustumComponent.get(currentlySelected.getEntity()));

        stage.setKeyboardFocus(null);
    }

    private void applyNameChanges(NodeComponent node) {
        node.name = name.getText();
        node.setHex(hex.getText());
    }

    private void applyTransformChanges() {
        Vector3 posOffset = new Vector3();
        posOffset.x = parseFloat(xPos.getText()) - currentlySelected.getPosition().x;
        posOffset.y = parseFloat(yPos.getText()) - currentlySelected.getPosition().y;
        posOffset.z = parseFloat(zPos.getText()) - currentlySelected.getPosition().z;

        Vector3 rotOffset = new Vector3();
        rotOffset.x = parseFloat(xRot.getText()) - currentlySelected.getRotation().getYaw();
        rotOffset.y = parseFloat(yRot.getText()) - currentlySelected.getRotation().getPitch();
        rotOffset.z = parseFloat(zRot.getText()) - currentlySelected.getRotation().getRoll();


        for (int i = 0; i < SelectionSystem.multiSelection.size; i++) {
            if (NodeComponent.get(SelectionSystem.multiSelection.get(i)) == null) {
                continue;
            }

            ModelComponent current = ModelComponent.get(SelectionSystem.multiSelection.get(i));
            Vector3 targetPos = posOffset.cpy().add(current.getPosition());
            Vector3 targetRot = rotOffset.cpy();
            targetRot.x += current.getRotation().getYaw();
            targetRot.y += current.getRotation().getPitch();
            targetRot.z += current.getRotation().getRoll();

            Quaternion quaternion = new Quaternion().setEulerAngles(targetRot.x, targetRot.y, targetRot.z);

            current.animateTo(targetPos, quaternion);
        }
    }

    private void applyIntrinsicChanges(FrustumComponent frustum) {
        if (frustum == null) {
            return;
        }

        float nearValue = parseFloat(near.getText());
        float farValue = parseFloat(far.getText());

        frustum.updateNearFar(nearValue, farValue);
    }

    private float parseFloat(String string) {
        // That this isn't part of the basic Float.parse function is ridiculous.
        if (string.isEmpty())
            return 0f;

        string = string.replace(',','.');

        // TODO: More error handling?

        try {
            return Float.parseFloat(string);
        } catch (java.lang.NumberFormatException e) {
            return 0;
        }
    }

    private void generateNameDrawers() {
        nameGroup = new Group();
        informationParent.addActor(nameGroup);

        name = setupTextField(20, 0, Align.topLeft, nameGroup);
        name.setWidth(115);
        name.setTextFieldFilter(null);
        name.setAlignment(Align.center);

        hex = setupTextField(150, 0, Align.topLeft, nameGroup);
        hex.setTextFieldFilter(new HexFilter());
        hex.setAlignment(Align.center);
        hex.setMaxLength(4);
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

    private void generateIntrinsicDrawer() {
        intrinsicGroup = new Group();
        informationParent.addActor(intrinsicGroup);

        setupLabel("Intrinsic", 100, 0, Align.top, intrinsicGroup);

        // Near
        near = setupTextField(45, -20, Align.topLeft, intrinsicGroup);
        setupLabel("near", 45, -20, Align.topRight, intrinsicGroup);

        // Far
        far = setupTextField(125, -20, Align.topLeft, intrinsicGroup);
        setupLabel("far", 125, -20, Align.topRight, intrinsicGroup);
    }

    private TextField setupTextField(float x, float y, int align, Group group) {
        TextField textField = new TextField("", skin);

        textField.setPosition(x, y, align);
        textField.setWidth(50f);
        textField.setTextFieldListener(textFieldListener);
        textField.setTextFieldFilter(floatFilter);
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
            informationParent.setVisible(false);
            return;
        }

        informationParent.setVisible(true);
        informationParent.setPosition(Gdx.graphics.getWidth() - informationParent.getWidth() - 10,
                                      Gdx.graphics.getHeight() - informationParent.getHeight() - 10);

        float x = 0;
        float y = informationParent.getHeight() - 10;

        nameGroup.setPosition(x, y, Align.topLeft);
        y -= 30;
        positionGroup.setPosition(x, y, Align.topLeft);
        y -= 50;
        rotationGroup.setPosition(x, y, Align.topLeft);
        y -= 50;

        if (FrustumComponent.get(currentlySelected.getEntity()) != null) {
            intrinsicGroup.setVisible(true);
            intrinsicGroup.setPosition(x, y, Align.topLeft);
        } else {
            intrinsicGroup.setVisible(false);

            if (NodeComponent.get(currentlySelected.getEntity()) != null) {
                if (NodeComponent.get(currentlySelected.getEntity()).isTracker) {
                    // TODO: Draw Load Intrinsic button
                }
            }
        }
    }

    @Override
    public void dispose() {
    }

}
