package org.wo2w.datamartReader;

import software.amazon.awssdk.services.s3.S3Client;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class Controller {

    private final Neo4jDatabaseReader neo4jDatabaseReader;
    private final S3Client s3Client;
    private final String bucketName;

    public Controller(Neo4jDatabaseReader neo4jDatabaseReader, S3Client s3Client, String bucketName) {
        this.neo4jDatabaseReader = neo4jDatabaseReader;
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public void findPaths(String startValue, String endValue) throws IOException {
        Storage storage = new S3Storage(s3Client, bucketName);
        String key = startValue + "_" + endValue + ".json";

        if (FileManager.checkFile(storage, key)) return;

        HashMap<String, Object> paths = neo4jDatabaseReader.findAllPaths("value", "averageOccurrences", startValue, endValue, "Word", "SIMILAR");
        FileManager.writeFile(paths, storage, key);
    }


    public void findMaxDistancePath() throws IOException {

        Storage storage = new S3Storage(s3Client, bucketName);
        String key = "findMaxDistancePath.json";

        if (FileManager.checkFile(storage, key)) return;

        HashMap<String, Object> maxDistancePath = neo4jDatabaseReader.getMaxDistanceGraph("value", "averageOccurrences", "Word", "SIMILAR");
        FileManager.writeFile(maxDistancePath, storage, key);
    }

    public void findNodesWithConnectivityDegree(int connectivity) throws IOException {
        Storage storage = new S3Storage(s3Client, bucketName);
        String key = "findNodesWithConnectivityDegree_" + connectivity + ".json";

        if (FileManager.checkFile(storage, key)) return;

        HashMap<String, Object> nodesWithHighestConnections = neo4jDatabaseReader.getNodesByConnectivityDegree(connectivity, "value", "Word", "SIMILAR");
        FileManager.writeFile(nodesWithHighestConnections, storage, key);
    }

    public void findIsolatedNodes() throws IOException {
        Storage storage = new S3Storage(s3Client, bucketName);
        String key = "findIsolatedNodes.json";

        if (FileManager.checkFile(storage, key)) return;

        HashMap<String, Object> nodesWithHighestConnections = neo4jDatabaseReader.getIsolatedNodes("value", "Word", "SIMILAR");
        FileManager.writeFile(nodesWithHighestConnections, storage, key);
    }

    public void findNodesWithHighestConnections() throws IOException {
        Storage storage = new S3Storage(s3Client, bucketName);
        String key = "findNodesWithHighestConnections.json";

        if (FileManager.checkFile(storage, key)) return;

        HashMap<String, Object> nodesWithHighestConnections = neo4jDatabaseReader.getNodesWithMaxConnections("value", "Word", "SIMILAR");
        FileManager.writeFile(nodesWithHighestConnections, storage, key);
    }

    public void findNodesCluster() throws IOException {
        Storage storage = new S3Storage(s3Client, bucketName);
        String key = "findNodesCluster.json";

        if (FileManager.checkFile(storage, key)) return;

        HashMap<String, Object> nodesCluster = neo4jDatabaseReader.getNodesForCluster("value", "Word", "SIMILAR");
        FileManager.writeFile(nodesCluster, storage, key);
    }
    

}
