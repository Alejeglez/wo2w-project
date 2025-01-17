package org.wo2w.crawler;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class S3Storage implements Storage{

    private final S3Client s3Client;
    public  final String bucketName;

    public S3Storage(S3Client s3Client, String bucketName){
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }
    @Override
    public void write(String content, String filename) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .build();

        RequestBody requestBody = RequestBody.fromString(content, StandardCharsets.UTF_8);

        s3Client.putObject(putObjectRequest, requestBody);
        System.out.println("File written to S3 with key: " + filename);
    }
}
