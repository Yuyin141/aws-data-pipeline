package com.datapipeline.lambda.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.datapipeline.lambda.model.ProcessingEvent;
import com.datapipeline.lambda.service.DataTransformationService;
import com.datapipeline.lambda.service.RdsProxyService;
import com.datapipeline.lambda.service.S3TransferService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FileProcessorHandler implements RequestHandler<SQSEvent, String> {

    private final S3TransferService s3Service;
    private final RdsProxyService rdsService;
    private final DataTransformationService transformationService;
    private final ObjectMapper objectMapper;

    public FileProcessorHandler() {
        this.s3Service = new S3TransferService();
        this.rdsService = new RdsProxyService();
        this.transformationService = new DataTransformationService();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage message : event.getRecords()) {
            try {
                ProcessingEvent processingEvent = objectMapper.readValue(
                        message.getBody(), 
                        ProcessingEvent.class
                );

                String rawContent = s3Service.downloadFileContent(processingEvent.getFileKey());
                String transformedContent = transformationService.transformData(rawContent);
                String processedKey = "processed/" + processingEvent.getFileKey().replace("raw/", "");
                
                s3Service.uploadProcessedFile(processedKey, transformedContent);
                rdsService.updateFileMetadata(processingEvent.getFileKey(), processedKey);

            } catch (Exception e) {
                context.getLogger().log("Error processing message: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }

        return "Processing completed";
    }
}

