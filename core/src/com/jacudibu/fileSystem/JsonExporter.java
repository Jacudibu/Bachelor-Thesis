package com.jacudibu.fileSystem;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.files.FileHandle;
import com.jacudibu.Core;
import com.jacudibu.components.FrustumComponent;
import com.jacudibu.components.NodeComponent;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;

/**
 * Created by Stefan Wolf (Jacudibu) on 02.07.2017.
 * Exports everything within the current SRG to a file.
 */
public class JsonExporter {
    public static String savePath;

    public static void openSaveDialogue() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save as...");
        fileChooser.setApproveButtonText("Save");

        JFrame frame = new JFrame();
        frame.setVisible(true);
        frame.toFront();
        frame.setVisible(false);
        int result = fileChooser.showSaveDialog(frame);
        frame.dispose();
        if (result == JFileChooser.APPROVE_OPTION) {
            export(fileChooser.getSelectedFile().getAbsolutePath(), PathType.ABSOLUTE);
        }
    }

    public static void export() {
        if (savePath != null && !savePath.isEmpty()) {
            export(savePath, PathType.ABSOLUTE);
        }
        else {
            openSaveDialogue();
        }
    }

    public static void export(String path) {
        export(path, PathType.INTERNAL);
    }

    public static void export(String path, PathType pathType) {
        savePath = path;
        FileHandle file = FileSystem.getFileHandle(path, pathType);
        file.writeString(createJson().toString(), false);
    }

    private static JSONObject createJson() {
        JSONArray nodeArray = new JSONArray();
        JSONArray connectionArray = new JSONArray();
        JSONArray intrinsicArray = new JSONArray();

        ImmutableArray<Entity> nodeEntities = Core.engine.getEntitiesFor((Family.all(NodeComponent.class).get()));
        for (int i = 0; i < nodeEntities.size(); i++) {
            NodeComponent node = NodeComponent.get(nodeEntities.get(i));

            nodeArray.put(node.toJson());
            if (node.getOutgoingCount() > 0) {
                connectionArray.put(node.getOutgoingConnectionJson());
            }

            FrustumComponent frustum = FrustumComponent.get(node.getEntity());
            if (frustum != null) {
                intrinsicArray.put(frustum.getIntrinsic().toJson());
            }
        }

        JSONObject result = new JSONObject();
        result.put("nodes", nodeArray);
        result.put("connections", connectionArray);
        result.put("intrinsics", intrinsicArray);
        return result;
    }
}
