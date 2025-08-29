package com.example.docker_container_manager;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class CLIService {
    public static String execCommand(String command) throws IOException {
        Process process = null;
        BufferedReader stdInput = null;
        StringBuilder output = new StringBuilder();

        try {
            process = Runtime.getRuntime().exec(command);

            stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String stdoutLine;

            while ((stdoutLine = stdInput.readLine()) != null) {
                output.append(stdoutLine).append("\n");
            }
        } finally {
            if (stdInput != null) {
                try {
                    stdInput.close();
                } catch (IOException e) {
                    System.err.println("Error closing BufferedReader: " + e.getMessage());
                }
            }
            if (process != null) {
                process.destroy();
            }
        }

        if (output.length() == 0) {
            return null;
        }

        return output.toString();
    }
}