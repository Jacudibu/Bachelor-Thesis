package com.jacudibu.fileSystem;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.jacudibu.Entities;
import com.jacudibu.components.NodeComponent;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Stefan Wolf (Jacudibu) on 02.07.2017.
 */
public class JsonImporter {
    public static void importJson(String filename) {
        FileHandle file = Gdx.files.internal(filename + ".json");
        JSONObject json = new JSONObject(file.readString());

        Array<NodeComponent> nodes = createNodes((JSONArray)json.get("nodes"));
        createConnections((JSONArray)json.get("connections"), nodes);
    }

    private static Array<NodeComponent> createNodes(JSONArray nodeArray) {
        Array<NodeComponent> nodes = new Array<NodeComponent>();

        for (int i = 0; i < nodeArray.length(); i++) {
            nodes.add(createNode(nodeArray.getJSONObject(i)));
        }

        return nodes;
    }

    private static NodeComponent createNode(JSONObject json) {
        Vector3 pos = new Vector3();
        JSONObject posJson = json.getJSONObject("position");
        pos.x = (float) posJson.getDouble("x");
        pos.y = (float) posJson.getDouble("y");
        pos.z = (float) posJson.getDouble("z");

        Quaternion rot = new Quaternion();
        JSONObject rotJson = json.getJSONObject("rotation");
        rot.x = (float) rotJson.getDouble("x");
        rot.y = (float) rotJson.getDouble("y");
        rot.z = (float) rotJson.getDouble("z");
        rot.w = (float) rotJson.getDouble("w");

        int id = json.getInt("id");
        String name = json.getString("name");
        boolean isTracker = json.getBoolean("isTracker");
        boolean isMarker = json.getBoolean("isMarker");

        Entity e = Entities.createNode(pos, rot, id, name, isTracker, isMarker);
        return NodeComponent.mapper.get(e);
    }

    private static void createConnections(JSONArray connectionArray, Array<NodeComponent> nodes) {
        for (int i = 0; i < connectionArray.length(); i++) {
            JSONObject currentNode = connectionArray.getJSONObject(i);
            int nodeID = currentNode.getInt("from");
            JSONArray nodeConnections = currentNode.getJSONArray("to");

            NodeComponent node = getNodeWithID(nodeID, nodes);

            for (int j = 0; j < nodeConnections.length(); j++) {
                node.addOutgoing(getNodeWithID(nodeConnections.getInt(j), nodes));
            }
        }
    }

    private static NodeComponent getNodeWithID(int id, Array<NodeComponent> nodes) {
        for (int i = 0; i < nodes.size; i++) {
            if (nodes.get(i).ID == id) {
                return nodes.get(i);
            }
        }

        return null;
    }
}
