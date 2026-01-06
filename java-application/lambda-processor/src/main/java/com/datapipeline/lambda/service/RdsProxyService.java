package com.datapipeline.lambda.service;

import com.datapipeline.config.AwsClientConfig;
import com.datapipeline.config.EnvironmentConfig;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsUtilities;
import software.amazon.awssdk.services.rds.model.GenerateAuthenticationTokenRequest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class RdsProxyService {

    private final RdsUtilities rdsUtilities;
    private final String proxyEndpoint;
    private final String dbName;
    private final String username;
    private final int port;

    public RdsProxyService() {
        this.rdsUtilities = AwsClientConfig.getRdsUtilities();
        this.proxyEndpoint = EnvironmentConfig.getRdsProxyEndpoint();
        this.dbName = EnvironmentConfig.getDbName();
        this.username = EnvironmentConfig.getDbUsername();
        this.port = EnvironmentConfig.getDbPort();
    }

    public void updateFileMetadata(String fileKey, String processedFileKey) throws Exception {
        String authToken = generateAuthToken();
        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", proxyEndpoint, port, dbName);

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, authToken)) {
            String sql = "UPDATE file_metadata SET status = ?, processed_file_key = ?, processed_at = ? WHERE file_key = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "COMPLETED");
                stmt.setString(2, processedFileKey);
                stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setString(4, fileKey);
                stmt.executeUpdate();
            }
        }
    }

    private String generateAuthToken() {
        GenerateAuthenticationTokenRequest tokenRequest = GenerateAuthenticationTokenRequest.builder()
                .hostname(proxyEndpoint)
                .port(port)
                .username(username)
                .build();

        return rdsUtilities.generateAuthenticationToken(tokenRequest);
    }
}

