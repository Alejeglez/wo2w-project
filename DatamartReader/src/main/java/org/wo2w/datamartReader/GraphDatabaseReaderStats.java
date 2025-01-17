package org.wo2w.datamartReader;

import java.util.HashMap;

public interface GraphDatabaseReaderStats {
    HashMap<String, Object> getTotalNodesCount(String nodeType);

    HashMap<String, Object> getTotalConnectionsCount(String nodeType, String relationshipType);

    HashMap<String, Object> getAverageNodeDegreeCount(String nodeType);

    HashMap<String, Object> getIsolatedNodesCount(String nodeType);

    HashMap<String, Object> getConnectedNodesCount(String nodeType);
}
