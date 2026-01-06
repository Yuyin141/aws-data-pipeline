package com.datapipeline.lambda.service;

import java.util.Arrays;
import java.util.stream.Collectors;

public class DataTransformationService {

    public String transformData(String rawContent) {
        StringBuilder transformed = new StringBuilder();
        transformed.append("=== PROCESSED DATA ===\n");
        transformed.append("Processing timestamp: ").append(System.currentTimeMillis()).append("\n\n");

        String[] lines = rawContent.split("\n");
        long lineCount = Arrays.stream(lines).count();
        long wordCount = Arrays.stream(lines)
                .flatMap(line -> Arrays.stream(line.split("\\s+")))
                .count();

        transformed.append("Statistics:\n");
        transformed.append("  - Total lines: ").append(lineCount).append("\n");
        transformed.append("  - Total words: ").append(wordCount).append("\n");
        transformed.append("  - Total characters: ").append(rawContent.length()).append("\n\n");

        transformed.append("Transformed content (uppercase):\n");
        transformed.append(rawContent.toUpperCase());

        return transformed.toString();
    }
}

