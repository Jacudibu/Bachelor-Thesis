package com.jacudibu.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.softbody.btSoftBody;
import com.badlogic.gdx.utils.Array;
import com.jacudibu.ubiWrap.PoseReceiver;
import com.jacudibu.utility.Entities;
import com.jacudibu.utility.QRGenerator;
import org.json.JSONObject;

import javax.xml.soap.Node;
import java.util.Stack;

/**
 * Created by Stefan Wolf (Jacudibu) on 26.06.2017.
 */
public class NodeComponent implements Component {
    private static final ComponentMapper<NodeComponent> mapper = ComponentMapper.getFor(NodeComponent.class);
    public static int total = 0;

    public static NodeComponent get(Entity e) {
        return mapper.get(e);
    }

    private class Connection {
        public NodeComponent node;
        public ArrowComponent arrow;
    }

    private Entity entity;
    private Array<Connection> outgoingConnections;
    private Array<Connection> incomingConnections;
    private Array<NodeComponent> trackedNodes;
    private PoseReceiver poseReceiver;

    public boolean isMarker;
    public boolean isTracker;

    public String name = "";
    public int ID;
    private String hex = "";

    public NodeComponent(Entity entity, boolean isMarker, boolean isTracker) {
        this(entity, isMarker, isTracker, total, "Node " + total);
    }

    public NodeComponent(Entity entity, boolean isMarker, boolean isTracker, int ID, String name) {
        this.entity = entity;

        this.isMarker = isMarker;
        this.isTracker = isTracker;

        outgoingConnections = new Array<Connection>();
        incomingConnections = new Array<Connection>();
        trackedNodes = new Array<NodeComponent>();

        this.name = name;
        this.ID = ID;
        if (total <= ID) {
            total = ID + 1;
        }
    }

    public void setPoseReceiver(PoseReceiver poseReceiver) {
        this.poseReceiver = poseReceiver;
    }

    public void addTracked(NodeComponent node) {
        trackedNodes.add(node);
        node.poseReceiver.setTracker(entity);
        addOutgoing(node);
    }

    public void addOutgoing(Entity entity) {
        addOutgoing(mapper.get(entity));
    }

    public void addOutgoing(NodeComponent node) {
        Connection connection = new Connection();
        connection.node = node;

        Entity arrow = Entities.createArrow(node.getEntity(), entity);
        connection.arrow = ArrowComponent.get(arrow);

        outgoingConnections.add(connection);
        node.addIncoming(this, connection.arrow);
    }

    public void addIncoming(Entity entity, ArrowComponent arrow) {
        addIncoming(mapper.get(entity), arrow);
    }

    public void addIncoming(NodeComponent node, ArrowComponent arrow) {
        Connection connection = new Connection();
        connection.node = node;
        connection.arrow = arrow;

        incomingConnections.add(connection);
    }

    public void removeConnectionTo(Entity entity) {
        removeConnectionTo(mapper.get(entity));
    }

    public void removeConnectionTo(NodeComponent node) {
        Connection connection = null;

        for (int i = 0; i < outgoingConnections.size; i++) {
            if (outgoingConnections.get(i).node == node) {
                connection = outgoingConnections.get(i);
                break;
            }
        }

        if (connection != null) {
            Entities.destroyEntity(connection.arrow.getEntity());
            outgoingConnections.removeValue(connection, true);
            connection.node.removeIncomingConnection(this);
        }
    }

    // Only called by removeConnectionTo. So if you want to remove connections by yourself, only call removeConnectionTo!
    private void removeIncomingConnection(NodeComponent node) {
        Connection connection = null;

        for (int i = 0; i < incomingConnections.size; i++) {
            if (incomingConnections.get(i).node == node) {
                connection = incomingConnections.get(i);
                break;
            }
        }

        if (connection != null) {
            incomingConnections.removeValue(connection, true);
        }
    }

    public void handlePositionUpdate() {
        updateArrows();
    }

    public void updateArrows() {
        for (int i = 0; i < outgoingConnections.size; i++) {
            outgoingConnections.get(i).arrow.updateModel();
        }
        for (int i = 0; i < incomingConnections.size; i++) {
            incomingConnections.get(i).arrow.updateModel();
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public Array<NodeComponent> getAllConnectedNodes() {
        Stack<NodeComponent> frontier = new Stack<NodeComponent>();
        Stack<NodeComponent> visited = new Stack<NodeComponent>();
        frontier.push(this);

        while (!frontier.empty()) {
            NodeComponent current = frontier.pop();
            visited.push(current);

            for (int i = 0; i < current.incomingConnections.size; i++) {
                NodeComponent next = current.incomingConnections.get(i).node;
                if (!visited.contains(next) && !frontier.contains(next)) {
                    frontier.push(next);
                }
            }

            for (int i = 0; i < current.outgoingConnections.size; i++) {
                NodeComponent next = current.outgoingConnections.get(i).node;
                if (!visited.contains(next) && !frontier.contains(next)) {
                    frontier.push(next);
                }
            }
        }

        Array<NodeComponent> result = new Array<NodeComponent>();
        while (!visited.isEmpty()) {
            result.add(visited.pop());
        }

        return result;
    }

    public void merge(Entity entity) {
        if (mapper.get(entity) != null) {
            merge(mapper.get(entity));
        }
    }

    /* Merges this node with another node.
       It will copy all its connections and afterwards remove the other node.
     */
    public void merge(NodeComponent node) {
        Array<NodeComponent> subtree = node.getAllConnectedNodes();
        for (int i = 0; i < subtree.size; i++) {
            if (subtree.get(i) == this) {
                // Nodes within the same subtree can't be merged!
                // TODO: ERROR HANDLING
                return;
            }
        }

        // Move Subtree
        Vector3 start = ModelComponent.get(this.entity).getPosition();
        Vector3 end = ModelComponent.get(node.entity).getPosition();
        Vector3 movement = start.cpy().sub(end);
        for (int i = 0; i < subtree.size; i++) {
            subtree.get(i).entity.add(AnimationComponent.lerpMovement(subtree.get(i).entity, movement));
        }

        // Apply Data
        if (node.isTracker) {
            isTracker = true;
        }
        if (node.isMarker) {
            isMarker = true;
        }

        // Incoming
        for (int i = 0; i < node.incomingConnections.size; i++) {
            node.incomingConnections.get(i).node.addOutgoing(this);
        }

        // Outgoing
        for (int i = 0; i < node.outgoingConnections.size; i++) {
            // Make sure tracked nodes do not count as outgoing
            if (!node.trackedNodes.contains(node.outgoingConnections.get(i).node, true)) {
                addOutgoing(node.outgoingConnections.get(i).node);
            }
        }

        // Tracked
        for (int i = 0; i < node.trackedNodes.size; i++) {
            addTracked(node.trackedNodes.get(i));
        }

        node.delete();
    }

    public int getOutgoingCount() {
        return outgoingConnections.size;
    }

    public void delete() {
        // Incoming
        for (int i = incomingConnections.size - 1; i >= 0; i--) {
            incomingConnections.get(i).node.removeConnectionTo(this);
        }

        // Outgoing
        for (int i = outgoingConnections.size - 1; i >= 0; i--) {
            removeConnectionTo(outgoingConnections.get(i).node);
        }

        Entities.destroyEntity(entity);
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        if (this.hex == hex) {
            return;
        }

        this.hex = hex;
        if (isMarker && QRGenerator.isValidCode(hex)) {
            ModelComponent.get(entity).setTextureAttribute(QRGenerator.generate(hex));
        }
    }

    public static void resetCounter() {
        total = 0;
    }

    //----------
    //-- JSON --
    //----------

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        json.put("id", ID);
        json.put("name", name);
        json.put("hex", hex);
        json.put("isTracker", isTracker);
        json.put("isMarker", isMarker);

        Vector3 pos = ModelComponent.get(entity).getPosition();
        JSONObject posJson = new JSONObject();
        posJson.put("x", pos.x);
        posJson.put("y", pos.y);
        posJson.put("z", pos.z);
        json.put("position", posJson);

        Quaternion rot = ModelComponent.get(entity).getRotation();
        JSONObject rotJson = new JSONObject();
        rotJson.put("x", rot.x);
        rotJson.put("y", rot.y);
        rotJson.put("z", rot.z);
        rotJson.put("w", rot.w);
        json.put("rotation", rotJson);

        return json;
    }

    public JSONObject getOutgoingConnectionJson() {
        JSONObject json = new JSONObject();

        json.put("from", ID);

        for (int i = 0; i < outgoingConnections.size; i++) {
            json.append("to", outgoingConnections.get(i).node.ID);
        }

        return json;
    }
}
