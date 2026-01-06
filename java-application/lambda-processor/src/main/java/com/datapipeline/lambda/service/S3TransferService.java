package com.datapipeline.lambda.service;

import com.datapipeline.config.AwsClientConfig;
import com.datapipeline.config.EnvironmentConfig;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class S3TransferService {

    private final S3Client s3Client;
    private final String rawBucket;
    private final String processedBucket;

    public S3TransferService() {
        this.s3Client = AwsClientConfig.getS3Client();
        this.rawBucket = EnvironmentConfig.getS3BucketRaw();
        this.processedBucket = EnvironmentConfig.getS3BucketProcessed();
    }

    public String downloadFileContent(String key) throws IOException {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(rawBucket)
                .key(key)
                .build();

        try (ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getRequest)) {
            return new String(response.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public void uploadProcessedFile(String key, String content) {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(processedBucket)
                .key(key)
                .build();

        s3Client.putObject(putRequest, RequestBody.fromString(content));
    }
}

