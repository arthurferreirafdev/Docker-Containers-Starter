package com.example.docker_container_manager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;

@Component
public class FileService {
    static String dockerLogPrefixPath;

    public static void writeFile(String content, String fileName){
        String filePath = dockerLogPrefixPath + fileName;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true)){}) {
            writer.write(content);
            System.out.println("Successfully wrote content to " + filePath);
        }catch (Exception e){
            System.out.println("Error in file Write: "  + e.getMessage());
        }
    }

    @Value("${docker.file.output}")
    public void setDockerLogPrefixPath(String path) {
        FileService.dockerLogPrefixPath = path;
    }
}
