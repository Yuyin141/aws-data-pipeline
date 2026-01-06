package com.datapipeline.api.service;

import com.datapipeline.config.AwsClientConfig;
import com.datapipeline.config.EnvironmentConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.HashMap;
import java.util.Map;

@Service
public class SnsService {

    private final SnsClient snsClient;
    private final String topicArn;
    private final ObjectMapper objectMapper;

    public SnsService() {
        this.snsClient = AwsClientConfig.getSnsClient();
        this.topicArn = EnvironmentConfig.getSnsTopicArn();
        this.objectMapper = new ObjectMapper();
    }

    public void publishFileUploadEvent(String fileKey, String fileName, Long fileSize) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "FileUploadEvent");
        event.put("fileKey", fileKey);
        event.put("fileName", fileName);
        event.put("fileSize", fileSize);
        event.put("timestamp", System.currentTimeMillis());

        try {
            String message = objectMapper.writeValueAsString(event);
            PublishRequest publishRequest = PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(message)
                    .subject("File Upload Event")
                    .build();

            snsClient.publish(publishRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }
}

