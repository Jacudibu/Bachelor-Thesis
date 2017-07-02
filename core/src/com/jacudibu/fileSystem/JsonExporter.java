package com.jacudibu.fileSystem;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.jacudibu.Core;
import com.jacudibu.components.NodeComponent;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Stefan Wolf (Jacudibu) on 02.07.2017.
 */
public class JsonExporter {
    public static void export(String fileName) {
        FileHandle file = Gdx.files.local(fileName + ".json");
        file.writeString(createJson().toString(), false);
    }

    private static JSONObject createJson() {
        JSONArray nodeArray = new JSONArray();
        JSONArray connectionArray = new JSONArray();

        ImmutableArray<Entity> nodeEntities = Core.engine.getEntitiesFor((Family.all(NodeComponent.class).get()));
        for (int i = 0; i < nodeEntities.size(); i++) {
            NodeComponent node = NodeComponent.mapper.get(nodeEntities.get(i));

            nodeArray.put(node.toJson());
            connectionArray.put(node.getOutgoingConnectionJson());
        }

        JSONObject result = new JSONObject();
        result.put("nodes", nodeArray);
        result.put("connections", connectionArray);
        return result;
    }
}
