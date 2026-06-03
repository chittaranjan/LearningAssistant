package com.juleswhite.module1;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class FileTokenManager implements TokenManager {
    private final String filename;
    private long totalTokensUsed;
    private final long maxTokensPerSession;

    public FileTokenManager(String filename, long maxTokensPerSession) {
        this.filename = filename;
        this.maxTokensPerSession = maxTokensPerSession;
        this.totalTokensUsed = loadTotalTokensUsed();
    }

    @Override
    public long getTotalTokensUsed() {
        return totalTokensUsed;
    }

    @Override
    public void addTokens(long tokens) {
        totalTokensUsed += tokens;
        if (totalTokensUsed > maxTokensPerSession) {
            throw new RuntimeException("Session token limit exceeded: " + totalTokensUsed + " tokens used.");
        }
        saveTotalTokensUsed(totalTokensUsed);
    }

    private long loadTotalTokensUsed() {
        try {
            File file = new File(filename);
            if (file.exists()) {
                try (Scanner scanner = new Scanner(file)) {
                    if (scanner.hasNextLong()) {
                        return scanner.nextLong();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load token usage from file: " + e.getMessage());
        }
        return 0;
    }

    private void saveTotalTokensUsed(long tokens) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.print(tokens);
        } catch (Exception e) {
            System.err.println("Warning: Could not save token usage to file: " + e.getMessage());
        }
    }
}
