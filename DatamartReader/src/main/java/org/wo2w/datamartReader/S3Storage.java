package org.wo2w.datamartReader;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class S3Storage implements Storage{

    private final S3Client s3Client;
    public  final String bucketName;

    public S3Storage(S3Client s3Client, String bucketName){
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public void write(String content, String filename) {
        long ttlExpirationTime = System.currentTimeMillis() + (60 * 1000);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .metadata(Map.of("expirationtime", String.valueOf(ttlExpirationTime)))
                .build();

        RequestBody requestBody = RequestBody.fromString(content, StandardCharsets.UTF_8);
        s3Client.putObject(putObjectRequest, requestBody);
    }

    @Override
    public boolean fileExists(String filename) {
        try {
            s3Client.headObject(builder -> builder.bucket(bucketName).key(filename).build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            throw new RuntimeException("Error checking file existence", e);
        }
    }

    @Override
    public boolean isTTLExpired(String filename) {
        HeadObjectRequest headRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .build();

        HeadObjectResponse response = s3Client.headObject(headRequest);

        System.out.println(response.metadata());
        String expirationTimeStr = response.metadata().get("expirationtime");
        System.out.println("TTL is :" + expirationTimeStr);
        if (expirationTimeStr != null) {
            long expirationTime = Long.parseLong(expirationTimeStr);
            return System.currentTimeMillis() > expirationTime;
        }

        return true;
    }
}
