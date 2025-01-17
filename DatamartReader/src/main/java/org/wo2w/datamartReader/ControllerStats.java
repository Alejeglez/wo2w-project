package org.wo2w.datamartReader;

import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.util.HashMap;

public class ControllerStats {
    private final Neo4jDatabaseReaderStats neo4jDatabaseReaderStats;
    private final S3Client s3Client;
    private final String bucketName;

    public ControllerStats(Neo4jDatabaseReaderStats neo4jDatabaseReaderStats, S3Client s3Client, String bucketName) {
        this.neo4jDatabaseReaderStats = neo4jDatabaseReaderStats;
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public void findTotalNodesCount() throws IOException {
        Storage storage = new S3Storage(s3Client, bucketName);
        String key = "findTotalNodesCount.json";

        if (FileManager.checkFile(storage, key)) return;

        HashMap<String, Object> nodesCount = neo4jDatabaseReaderStats.getTotalNodesCount("Word");
        FileManager.writeFile(nodesCount, storage, key);
    }

    public void findTotalConnectionsCount() throws IOException {
        Storage storage = new S3Storage(s3Client, bucketName);
        String key = "findTotalConnectionsCount.json";

        if (FileManager.checkFile(storage, key)) return;

        HashMap<String, Object> connectionsCount = neo4jDatabaseReaderStats.getTotalConnectionsCount("Word", "SIMILAR");
        FileManager.writeFile(connectionsCount, storage, key);
    }

    public void findAverageNodeDegreeCount() throws IOException {
        Storage storage = new S3Storage(s3Client, bucketName);
        String key = "findAverageNodeDegreeCount.json";

        if (FileManager.checkFile(storage, key)) return;

        HashMap<String, Object> averageNodeDegreeCount = neo4jDatabaseReaderStats.getAverageNodeDegreeCount("Word");
        FileManager.writeFile(averageNodeDegreeCount, storage, key);
    }

    public void findIsolatedNodesCount() throws IOException {
        Storage storage = new S3Storage(s3Client, bucketName);
        String key = "findIsolatedNodesCount.json";

        if (FileManager.checkFile(storage, key)) return;

        HashMap<String, Object> isolatedNodesCount = neo4jDatabaseReaderStats.getIsolatedNodesCount("Word");
        FileManager.writeFile(isolatedNodesCount, storage, key);
    }

    public void findConnectedNodesCount() throws IOException {
        Storage storage = new S3Storage(s3Client, bucketName);
        String key = "findConnectedNodesCount.json";

        if (FileManager.checkFile(storage, key)) return;

        HashMap<String, Object> isolatedNodesCount = neo4jDatabaseReaderStats.getConnectedNodesCount("Word");
        FileManager.writeFile(isolatedNodesCount, storage, key);
    }
}
