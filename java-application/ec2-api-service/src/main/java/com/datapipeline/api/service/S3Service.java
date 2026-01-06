package com.datapipeline.api.service;

import com.datapipeline.config.AwsClientConfig;
import com.datapipeline.config.EnvironmentConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileUpload;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final S3TransferManager transferManager;
    private final String rawBucket;
    private final String processedBucket;

    public S3Service() {
        this.s3Client = AwsClientConfig.getS3Client();
        this.transferManager = AwsClientConfig.getTransferManager();
        this.rawBucket = EnvironmentConfig.getS3BucketRaw();
        this.processedBucket = EnvironmentConfig.getS3BucketProcessed();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileKey = generateFileKey(file.getOriginalFilename());
        File tempFile = convertMultipartFileToFile(file);

        try {
            UploadFileRequest uploadRequest = UploadFileRequest.builder()
                    .putObjectRequest(req -> req
                            .bucket(rawBucket)
                            .key(fileKey)
                            .contentType(file.getContentType()))
                    .source(Paths.get(tempFile.getAbsolutePath()))
                    .build();

            FileUpload upload = transferManager.uploadFile(uploadRequest);
            CompletedFileUpload completed = upload.completionFuture().join();

            return fileKey;
        } finally {
            tempFile.delete();
        }
    }

    public byte[] downloadFile(String bucket, String key) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        return s3Client.getObjectAsBytes(getRequest).asByteArray();
    }

    public String getPresignedUrl(String bucket, String key) {
        return String.format("https://%s.s3.amazonaws.com/%s", bucket, key);
    }

    private String generateFileKey(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return "raw/" + uuid + extension;
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        }
        return tempFile;
    }
}

