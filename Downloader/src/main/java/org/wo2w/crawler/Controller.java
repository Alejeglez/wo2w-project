package org.wo2w.crawler;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.io.IOException;
import java.util.List;

public class Controller {

    private int batchSize;
    private S3Client s3Client;
    private String bucketName;

    public Controller(int batchSize, S3Client s3Client, String bucketName) {
        this.batchSize = batchSize;
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public void run(int startId, int endId) {
        GutenbergBookReader bookReader = new GutenbergBookReader();
        Storage storage = new S3Storage(s3Client, bucketName);

        System.out.println("Batch size: " + batchSize);
        System.out.println("Bucket name: " + bucketName);
        System.out.println("Downloading from ID " + startId + " to ID " + endId);

        for (int currentId = startId; currentId <= endId; currentId += batchSize) {
            int batchEnd = Math.min(currentId + batchSize - 1, endId);
            System.out.println("Processing batch: " + currentId + " to " + batchEnd);
        }

        BatchDownloader batchDownloader = new BatchDownloader(startId, endId, batchSize, bookReader, storage);

        batchDownloader.downloadBatches("English");
    }
}

