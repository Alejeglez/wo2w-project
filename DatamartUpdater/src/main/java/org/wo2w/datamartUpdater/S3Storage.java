package org.wo2w.datamartUpdater;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class S3Storage implements Storage{

    private final S3Client s3Client;
    public  final String bucketName;

    public S3Storage(S3Client s3Client, String bucketName){
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }


    @Override
    public String read(String key) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .key(key)
                .bucket(bucketName)
                .build();

        try (ResponseInputStream<GetObjectResponse> s3ObjectStream = s3Client.getObject(objectRequest)) {
            return new BufferedReader(new InputStreamReader(s3ObjectStream))
                    .lines()
                    .reduce("", (acc, line) -> acc + line + "\n");
        } catch (IOException e) {
            throw new RuntimeException("Failed to read the object from S3: " + key, e);
        }
    }
}
