package com.jacudibu.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.jacudibu.Core;
import com.jacudibu.Entities;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Stack;

/**
 * Created by Stefan Wolf (Jacudibu) on 26.06.2017.
 */
public class NodeComponent implements Component {
    public static final ComponentMapper<NodeComponent> mapper = ComponentMapper.getFor(NodeComponent.class);
    public static int total = 0;

    private class Connection {
        public NodeComponent node;
        public ArrowComponent arrow;
    }

    private Entity entity;
    private Array<Connection> outgoingConnections;
    private Array<Connection> incomingConnections;

    public boolean isMarker;
    public boolean isTracker;

    public String name = "";
    public final int ID;

    public NodeComponent(Entity entity, boolean isMarker, boolean isTracker) {
        this.entity = entity;

        this.isMarker = isMarker;
        this.isTracker = isTracker;

        outgoingConnections = new Array<Connection>();
        incomingConnections = new Array<Connection>();

        ID = total;
        total++;
    }

    public void addOutgoing(Entity entity) {
        addOutgoing(mapper.get(entity));
    }

    public void addOutgoing(NodeComponent node) {
        Connection connection = new Connection();
        connection.node = node;

        Entity arrow = Entities.createArrow(node.getEntity(), entity);
        connection.arrow = ArrowComponent.mapper.get(arrow);

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
            Core.engine.removeEntity(connection.arrow.getEntity());
            outgoingConnections.removeValue(connection, true);
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
        Vector3 start = ModelComponent.mapper.get(this.entity).getPosition();
        Vector3 end = ModelComponent.mapper.get(node.entity).getPosition();
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
            node.incomingConnections.get(i).node.removeConnectionTo(node);
        }

        // Outgoing
        for (int i = 0; i < node.outgoingConnections.size; i++) {
            addOutgoing(node.outgoingConnections.get(i).node);
            node.removeConnectionTo(node.outgoingConnections.get(i).node);
        }

        Core.engine.removeEntity(node.entity);
    }

    //----------
    //-- JSON --
    //----------

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        json.put("id", ID);
        json.put("name", name);
        json.put("isTracker", isTracker);
        json.put("isMarker", isMarker);

        Vector3 pos = ModelComponent.mapper.get(entity).getPosition();
        JSONObject posJson = new JSONObject();
        posJson.put("x", pos.x);
        posJson.put("y", pos.y);
        posJson.put("z", pos.z);
        json.put("position", posJson);

        Quaternion rot = ModelComponent.mapper.get(entity).getRotation();
        JSONObject rotJson = new JSONObject();
        rotJson.put("x", rot.x);
        rotJson.put("y", rot.y);
        rotJson.put("z", rot.z);
        rotJson.put("w", rot.w);
        json.put("rotation", rotJson);

        System.out.println(json.toString());
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
