package com.datapipeline.api.controller;

import com.datapipeline.api.model.FileMetadata;
import com.datapipeline.api.service.RdsService;
import com.datapipeline.api.service.S3Service;
import com.datapipeline.api.service.SnsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final S3Service s3Service;
    private final SnsService snsService;
    private final RdsService rdsService;

    public FileUploadController(S3Service s3Service, SnsService snsService, RdsService rdsService) {
        this.s3Service = s3Service;
        this.snsService = snsService;
        this.rdsService = rdsService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileKey = s3Service.uploadFile(file);
            
            FileMetadata metadata = rdsService.saveFileMetadata(
                    fileKey,
                    file.getOriginalFilename(),
                    file.getSize(),
                    file.getContentType()
            );

            snsService.publishFileUploadEvent(
                    fileKey,
                    file.getOriginalFilename(),
                    file.getSize()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("id", metadata.getId());
            response.put("fileKey", fileKey);
            response.put("fileName", file.getOriginalFilename());
            response.put("status", "uploaded");
            response.put("message", "File uploaded successfully");

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to upload file");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}

