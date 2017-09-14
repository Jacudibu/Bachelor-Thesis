package com.jacudibu.ubiWrap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import javax.swing.*;

/**
 * Created by Stefan on 16.07.2017.
 * Parses .dfg files and creates PoseReceivers.
 */
public class DFGParser {

    public static void openLoadDialogue() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load DFG...");
        fileChooser.setApproveButtonText("Load");

        JFrame frame = new JFrame();
        frame.setVisible(true);
        frame.toFront();
        frame.setVisible(false);
        int result = fileChooser.showOpenDialog(frame);
        frame.dispose();
        if (result == JFileChooser.APPROVE_OPTION) {
            parse(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    public static void parse(String path) {
        FileHandle file = Gdx.files.absolute(path);
        JSONObject json = XML.toJSONObject(file.readString());
        UbiManager.loadDataflow(path);
        parseJson(json);
    }

    private static void parseJson (JSONObject json) {
        //System.out.println(json.toString(4));
        JSONObject UTQLResponse = json.getJSONObject("UTQLResponse");
        JSONArray patterns = UTQLResponse.getJSONArray("Pattern");
        parsePatternArray(patterns);
    }

    private static void parsePatternArray(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            parsePattern(array.getJSONObject(i));
        }
    }

    private static void parsePattern(JSONObject pattern) {
        String id = pattern.getString("id");

        if (pattern.has("Input")) {
            JSONObject input = pattern.getJSONObject("Input");
            parseInputObject(input, id);
        }
    }

    private static void parseInputObject(JSONObject json, String id) {
        if (json.has("Node")) {
            JSONArray node = json.getJSONArray("Node");
            parseNode(node, id);
        }
    }

    private static void parseNode(JSONArray array, String id) {
        for (int i = 0; i < array.length(); i++) {
            if (array.getJSONObject(i).has("Attribute")) {
                JSONArray attributeArray = array.getJSONObject(i).getJSONArray("Attribute");
                parseNodeAttributeArray(attributeArray, id);
            }
        }
    }

    private static void parseNodeAttributeArray(JSONArray array, String id) {
        for (int i = 0; i < array.length(); i++) {
            parseNodeAttribute(array.getJSONObject(i), id);
        }
    }

    private static void parseNodeAttribute(JSONObject json, String id) {
        String name = json.getString("name");

        if (!name.equals("markerId")) {
            return;
        }

        String value = json.getString("value");
        Gdx.app.log("Ubitrack", "Found " + id + " with value " + value + "!");

        value = value.substring(2);
        UbiManager.receivePose(id, value);
    }

}
