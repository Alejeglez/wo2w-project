package org.wo2w.crawler;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class S3ClientFactory {
    public static S3Client createS3Client() {
        //final String ACCESS_KEY = "test";
        //final String SECRET_KEY = "test";

        Region region = Region.US_EAST_1;

        S3Client s3Client = S3Client.builder()
                //.endpointOverride(URI.create("https://s3.localhost.localstack.cloud:4566"))
                //.credentialsProvider(StaticCredentialsProvider.create(
                //AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)))
                .region(region)
                .build();

        return s3Client;
    }
}
