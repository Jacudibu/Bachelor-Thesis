package com.jacudibu.UI;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.jacudibu.components.ArrowComponent;
import com.jacudibu.components.FrustumComponent;
import com.jacudibu.components.ModelComponent;
import com.jacudibu.components.NodeComponent;
import com.jacudibu.entitySystem.SelectionSystem;
import com.jacudibu.fileSystem.IntrinsicParser;

/**
 * Created by Stefan Wolf (Jacudibu) on 14.05.2017.
 * Draws various information regarding the currently selected Object which can also be manipulated by the user.
 */
public class InformationDrawer implements Disposable {
    private Stage stage;
    private Skin skin;

    private Group informationParent;
    private Group nameGroup;
    private Group positionGroup;
    private Group rotationGroup;
    private Group manipulateIntrinsicGroup;
    private Group loadIntrinsicGroup;
    private TextField.TextFieldListener textFieldListener;

    private Label positionLabel;

    private TextField name;
    private TextField hex;
    private TextField xPos, yPos, zPos;
    private TextField xRot, yRot, zRot;
    private TextField near, far;
    private FloatFilter floatFilter = new FloatFilter();

    private TextButton loadIntrinsicButton;

    private static InformationDrawer instance;
    private Entity currentlySelectedEntity;
    private ModelComponent currentlySelectedModel;
    private ArrowComponent currentlySelectedArrow;

    private final float backgroundBaseHeight = 200f;
    private final float backgroundBaseWidth = 215f;

    protected InformationDrawer (Stage stage, Skin skin) {
        instance = this;

        this.stage = stage;
        this.skin = skin;
        setupTextFieldListener();

        informationParent = new Group();
        stage.addActor(informationParent);

        informationParent.setWidth(backgroundBaseWidth);
        informationParent.setHeight(backgroundBaseHeight);

        Image image = new Image(skin.getDrawable("textfield"));
        image.setFillParent(true);
        informationParent.addActor(image);

        generateNameDrawers();
        generatePositionDrawer();
        generateRotationDrawer();
        generateIntrinsicManipulationDrawer();
        generateIntrinsicLoaderDrawer();

        updateUIPositions();
    }

    public static void updateTextFields(Entity entity) {
        instance.currentlySelectedEntity = entity;
        instance.currentlySelectedModel = null;
        instance.currentlySelectedArrow = null;

        if (entity == null) {
            instance.disableInput();
        }
        else if (NodeComponent.get(entity) != null) {
                instance.currentlySelectedModel = ModelComponent.get(entity);
                instance.drawNodeInformation(ModelComponent.get(entity), NodeComponent.get(entity));
        } else if (ArrowComponent.get(entity) != null) {
                instance.currentlySelectedArrow = ArrowComponent.get(entity);
                instance.drawPoseInformation(ArrowComponent.get(entity));
        }
        instance.updateUIPositions();
    }

    public static boolean isCurrentlyFocused() {
        return instance.stage.getKeyboardFocus() != null;
    }

    private void drawNodeInformation(ModelComponent selectedObject, NodeComponent node) {
        setupFieldsForNodeDisplay();

        setName(node.name, false);
        if (node.isMarker) {
            setHex(node.getHex());
        }
        else {
            hideHex();
        }

        setPositionValues(selectedObject.getPosition(), false);
        setRotationValues(selectedObject.getRotation(), false);
        setIntrinsicValues(FrustumComponent.get(currentlySelectedEntity));
    }

    private void drawPoseInformation(ArrowComponent arrow) {
        setupFieldsForPoseDisplay();

        NodeComponent from = NodeComponent.get(arrow.from);
        NodeComponent to = NodeComponent.get(arrow.to);

        ModelComponent fromModel = ModelComponent.get(arrow.from);
        ModelComponent toModel = ModelComponent.get(arrow.to);

        setName(from.name + " -> " + to.name, true);

        setPositionValues(fromModel.getPosition().sub(toModel.getPosition()), true);

        Vector3 fromRot = getRotationAsVector(fromModel.getRotation());
        Vector3 toRot = getRotationAsVector(toModel.getRotation());

        setRotationValues(fromRot.add(toRot), true);
    }

    private void setName(String newName, boolean disabled) {
        name.setDisabled(disabled);
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

    private void setPositionValues(Vector3 pos, boolean disabled) {
        xPos.setDisabled(disabled);
        yPos.setDisabled(disabled);
        zPos.setDisabled(disabled);

        xPos.setText(Float.toString(pos.x));
        yPos.setText(Float.toString(pos.y));
        zPos.setText(Float.toString(pos.z));
    }

    private Vector3 getRotationAsVector(Quaternion rot) {
        Vector3 rotationVector = new Vector3();
        rotationVector.x = rot.getYaw();
        rotationVector.y = rot.getPitch();
        rotationVector.z = rot.getRoll();

        return rotationVector;
    }

    private void setRotationValues(Quaternion rot, boolean disabled) {
        setRotationValues(getRotationAsVector(rot), disabled);
    }

    private void setRotationValues(Vector3 rotationVector, boolean disabled) {
        xRot.setDisabled(disabled);
        yRot.setDisabled(disabled);
        zRot.setDisabled(disabled);

        xRot.setText(Float.toString(rotationVector.x));
        yRot.setText(Float.toString(rotationVector.y));
        zRot.setText(Float.toString(rotationVector.z));
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

        loadIntrinsicButton.setDisabled(true);

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
        applyNameChanges(NodeComponent.get(currentlySelectedEntity));
        applyTransformChanges();
        applyIntrinsicChanges(FrustumComponent.get(currentlySelectedEntity));

        stage.setKeyboardFocus(null);
    }

    private void applyNameChanges(NodeComponent node) {
        node.name = name.getText();
        node.setHex(hex.getText());
    }

    private void applyTransformChanges() {
        Vector3 posOffset = new Vector3();
        posOffset.x = parseFloat(xPos.getText()) - currentlySelectedModel.getPosition().x;
        posOffset.y = parseFloat(yPos.getText()) - currentlySelectedModel.getPosition().y;
        posOffset.z = parseFloat(zPos.getText()) - currentlySelectedModel.getPosition().z;

        Vector3 rotOffset = new Vector3();
        rotOffset.x = parseFloat(xRot.getText()) - currentlySelectedModel.getRotation().getYaw();
        rotOffset.y = parseFloat(yRot.getText()) - currentlySelectedModel.getRotation().getPitch();
        rotOffset.z = parseFloat(zRot.getText()) - currentlySelectedModel.getRotation().getRoll();


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

        positionLabel = setupLabel("Position", backgroundBaseWidth * 0.5f, 0, Align.top, positionGroup);

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

        setupLabel("Rotation", backgroundBaseWidth * 0.5f, 0, Align.top, rotationGroup);

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

    private void generateIntrinsicManipulationDrawer() {
        manipulateIntrinsicGroup = new Group();
        informationParent.addActor(manipulateIntrinsicGroup);

        setupLabel("Intrinsic", backgroundBaseWidth * 0.5f, 0, Align.top, manipulateIntrinsicGroup);

        // Near
        near = setupTextField(40, -20, Align.topLeft, manipulateIntrinsicGroup);
        setupLabel("near", 40, -20, Align.topRight, manipulateIntrinsicGroup);

        // Far
        far = setupTextField(120, -20, Align.topLeft, manipulateIntrinsicGroup);
        setupLabel("far", 120, -20, Align.topRight, manipulateIntrinsicGroup);
    }

    private void generateIntrinsicLoaderDrawer() {
        loadIntrinsicGroup = new Group();
        informationParent.addActor(loadIntrinsicGroup);

        // setupLabel("Intrinsic", backgroundBaseWidth * 0.5f, 0, Align.top, loadIntrinsicGroup);

        loadIntrinsicButton = new TextButton("Load Intrinsic", skin);
        loadIntrinsicGroup.addActor(loadIntrinsicButton);
        loadIntrinsicButton.setPosition(backgroundBaseWidth * 0.5f, -20, Align.top);

        loadIntrinsicButton.addListener(new ActorGestureListener() {
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                IntrinsicParser.openLoadDialogue(currentlySelectedEntity);
                setIntrinsicValues(FrustumComponent.get(currentlySelectedEntity));
            }
        });
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

    private void setupFieldsForNodeDisplay() {
        name.setWidth(115);
        positionLabel.setText("Position");
    }

    private void setupFieldsForPoseDisplay() {
        hideHex();
        setIntrinsicValues(null);

        name.setWidth(180f);
        positionLabel.setText("Direction");
    }

    protected void updateUIPositions() {
        if (currentlySelectedModel == null && currentlySelectedArrow == null) {
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

        updateFrustum(currentlySelectedEntity, x, y);
    }

    protected void updateFrustum(Entity entity, float x, float y) {
        if (FrustumComponent.get(entity) != null) {
            manipulateIntrinsicGroup.setVisible(true);
            loadIntrinsicGroup.setVisible(false);

            manipulateIntrinsicGroup.setPosition(x, y, Align.topLeft);
        } else {
            manipulateIntrinsicGroup.setVisible(false);

            if (NodeComponent.get(entity) != null) {
                if (NodeComponent.get(entity).isTracker) {
                    loadIntrinsicGroup.setVisible(true);
                    loadIntrinsicGroup.setPosition(x, y, Align.topLeft);
                } else {
                    loadIntrinsicGroup.setVisible(false);
                }
            }
        }
    }

    @Override
    public void dispose() {
    }

}
