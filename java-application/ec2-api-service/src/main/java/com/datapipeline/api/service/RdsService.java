package com.datapipeline.api.service;

import com.datapipeline.api.model.FileMetadata;
import com.datapipeline.api.repository.FileMetadataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RdsService {

    private final FileMetadataRepository repository;

    public RdsService(FileMetadataRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public FileMetadata saveFileMetadata(String fileKey, String fileName, Long fileSize, String contentType) {
        FileMetadata metadata = new FileMetadata();
        metadata.setFileKey(fileKey);
        metadata.setFileName(fileName);
        metadata.setFileSize(fileSize);
        metadata.setContentType(contentType);
        return repository.save(metadata);
    }

    @Transactional(readOnly = true)
    public FileMetadata getFileMetadata(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("File metadata not found"));
    }

    @Transactional(readOnly = true)
    public List<FileMetadata> getAllFiles() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public FileMetadata getFileMetadataByKey(String fileKey) {
        return repository.findByFileKey(fileKey)
                .orElseThrow(() -> new RuntimeException("File metadata not found"));
    }
}

