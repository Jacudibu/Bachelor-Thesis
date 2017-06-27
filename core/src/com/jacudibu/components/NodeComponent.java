package com.jacudibu.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.jacudibu.Core;
import com.jacudibu.Entities;

/**
 * Created by Stefan Wolf (Jacudibu) on 26.06.2017.
 */
public class NodeComponent implements Component {
    public static final ComponentMapper<NodeComponent> mapper = ComponentMapper.getFor(NodeComponent.class);

    private class Connection {
        public NodeComponent node;
        public ArrowComponent arrow;
    }

    private Entity entity;
    private Array<Connection> outgoingConnections;
    private Array<Connection> incomingConnections;

    public boolean isMarker;
    public boolean isTracker;

    public NodeComponent(Entity entity, boolean isMarker, boolean isTracker) {
        this.entity = entity;

        this.isMarker = isMarker;
        this.isTracker = isTracker;

        outgoingConnections = new Array<Connection>();
        incomingConnections = new Array<Connection>();
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

    public void merge(Entity entity) {
        if (mapper.get(entity) != null) {
            merge(mapper.get(entity));
        }
    }

    /* Merges this node with another node.
       It will copy all its connections and afterwards remove the other node.
     */
    public void merge(NodeComponent node) {
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
}
