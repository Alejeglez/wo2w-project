package org.wo2w.datamartReader;

import java.util.HashMap;

public interface GraphDatabaseReader {
    HashMap<String, Object> findAllPaths(String property, String cost, String startValue, String endValue, String nodeType, String relationshipType);

    HashMap<String, Object> getMaxDistanceGraph(String property, String cost, String nodeType, String relationshipType);

    HashMap<String, Object> getNodesWithMaxConnections(String property, String nodeType, String relationshipType);

    HashMap<String, Object> getNodesByConnectivityDegree(int connections, String property, String nodeType, String relationshipType);

    HashMap<String, Object> getIsolatedNodes(String property, String nodeType, String relationshipType);

    HashMap<String, Object> getNodesForCluster(String property, String nodeType, String relationshipType);
}
