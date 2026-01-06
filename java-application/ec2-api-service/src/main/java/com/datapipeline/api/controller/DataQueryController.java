package com.datapipeline.api.controller;

import com.datapipeline.api.model.FileMetadata;
import com.datapipeline.api.service.RdsService;
import com.datapipeline.api.service.S3Service;
import com.datapipeline.config.EnvironmentConfig;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class DataQueryController {

    private final RdsService rdsService;
    private final S3Service s3Service;

    public DataQueryController(RdsService rdsService, S3Service s3Service) {
        this.rdsService = rdsService;
        this.s3Service = s3Service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getFileMetadata(@PathVariable Long id) {
        FileMetadata metadata = rdsService.getFileMetadata(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", metadata.getId());
        response.put("fileKey", metadata.getFileKey());
        response.put("fileName", metadata.getFileName());
        response.put("fileSize", metadata.getFileSize());
        response.put("contentType", metadata.getContentType());
        response.put("status", metadata.getStatus());
        response.put("uploadedAt", metadata.getUploadedAt());
        response.put("processedAt", metadata.getProcessedAt());
        response.put("processedFileKey", metadata.getProcessedFileKey());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FileMetadata>> getAllFiles() {
        List<FileMetadata> files = rdsService.getAllFiles();
        return ResponseEntity.ok(files);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadProcessedFile(@PathVariable Long id) {
        FileMetadata metadata = rdsService.getFileMetadata(id);
        
        if (metadata.getProcessedFileKey() == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] fileContent = s3Service.downloadFile(
                EnvironmentConfig.getS3BucketProcessed(),
                metadata.getProcessedFileKey()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", metadata.getFileName());

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileContent);
    }
}

