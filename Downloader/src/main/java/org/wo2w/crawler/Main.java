package org.wo2w.crawler;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.out.println("Usage: java -jar BookDownloader.jar <batchSize> <startId> <endId> <bucketName>");
            return;
        }

        try {
            int batchSize = Integer.parseInt(args[0]);
            int startId = Integer.parseInt(args[1]);
            int endId = Integer.parseInt(args[2]);
            String bucketName = args[3];

            if (batchSize <= 0 || startId < 0 || endId < startId || bucketName.isEmpty()) {
                System.out.println("Error: Ensure batchSize > 0, startId >= 0, endId >= startId, and bucketName is not empty.");
                return;
            }

            Controller controller = new Controller(batchSize, S3ClientFactory.createS3Client(), bucketName);

            controller.run(startId, endId);
        } catch (NumberFormatException e) {
            System.err.println("Error: Arguments <batchSize>, <startId>, and <endId> must be valid integers.");
        }
    }
}
