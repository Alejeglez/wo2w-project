package org.wo2w.datamartUpdater;

import java.util.Map;
import java.util.Set;

public interface GraphDatabaseWriter {

    void insertOrUpdateNode(Node node, String primaryKey);
    void createRelationship(Node node, Node nodeTarget, String relationshipType, String keyField, Map<String, Object> propertiesRelationship);
    void close();
    Set<Map<String, Object>> getAllNodesProperties();
}
