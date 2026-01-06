package com.datapipeline.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvironmentConfig {

    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    public static String getAwsRegion() {
        return getEnv("AWS_REGION", "us-east-1");
    }

    public static String getS3BucketRaw() {
        return getEnv("S3_BUCKET_RAW", "data-pipeline-production-raw");
    }

    public static String getS3BucketProcessed() {
        return getEnv("S3_BUCKET_PROCESSED", "data-pipeline-production-processed");
    }

    public static String getSnsTopicArn() {
        return getEnv("SNS_TOPIC_ARN");
    }

    public static String getSqsQueueUrl() {
        return getEnv("SQS_QUEUE_URL");
    }

    public static String getRdsProxyEndpoint() {
        return getEnv("RDS_PROXY_ENDPOINT");
    }

    public static String getRdsEndpoint() {
        return getEnv("RDS_ENDPOINT");
    }

    public static String getDbName() {
        return getEnv("DB_NAME", "pipeline");
    }

    public static String getDbUsername() {
        return getEnv("DB_USERNAME", "admin");
    }

    public static int getDbPort() {
        return Integer.parseInt(getEnv("DB_PORT", "5432"));
    }

    private static String getEnv(String key) {
        String value = dotenv.get(key);
        if (value == null) {
            value = System.getenv(key);
        }
        if (value == null) {
            throw new IllegalStateException("Required environment variable not found: " + key);
        }
        return value;
    }

    private static String getEnv(String key, String defaultValue) {
        String value = dotenv.get(key);
        if (value == null) {
            value = System.getenv(key);
        }
        return value != null ? value : defaultValue;
    }
}

