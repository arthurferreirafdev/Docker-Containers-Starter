package com.example.docker_container_manager;

import com.example.docker_container_manager.model.GeneralData;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class FileService {
    public static void writeFile(String content, String fileName){
        String filePath = GeneralData.dockerLogPrefixPath + fileName;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true)){}) {
            writer.write(content);
            System.out.println("Successfully wrote content to " + filePath);
        }catch (Exception e){
            System.out.println("Error in file Write: "  + e.getMessage());
        }
    }
}
