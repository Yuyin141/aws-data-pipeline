package com.datapipeline.config;

public class Constants {

    public static final String PROCESSED_PREFIX = "processed/";
    public static final String RAW_PREFIX = "raw/";
    
    public static final int MAX_UPLOAD_SIZE_MB = 100;
    public static final long MAX_UPLOAD_SIZE_BYTES = MAX_UPLOAD_SIZE_MB * 1024L * 1024L;
    
    public static final String FILE_UPLOAD_EVENT_TYPE = "FileUploadEvent";
    public static final String FILE_PROCESSED_EVENT_TYPE = "FileProcessedEvent";
    
    public static final int CONNECTION_POOL_SIZE = 10;
    public static final int CONNECTION_TIMEOUT_MS = 30000;
    
    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
}

