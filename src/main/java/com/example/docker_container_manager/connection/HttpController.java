package com.example.docker_container_manager.connection;

import com.example.docker_container_manager.DockerContainerManager;
import com.example.docker_container_manager.model.InstanciaMonitoramento;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/container")
public class HttpController {
    @Autowired
    private DockerContainerManager dockerContainerManager;

    @GetMapping("/create/{eventId}")
    public ResponseEntity<Object> createContainer(@PathVariable("eventId") int eventId){
        try {
            InstanciaMonitoramento instanciaMonitoramento = dockerContainerManager.startNewContainers(eventId);
            System.out.println(instanciaMonitoramento);
            System.out.println("");
            String response = new Gson().toJson(instanciaMonitoramento);

            return ResponseEntity.ok(response);

        } catch (InterruptedException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error starting new containers: " + e.getMessage());
        }
    }

    @GetMapping("/remove")
    public ResponseEntity<Object> removeContainer(@RequestParam String containerId){
        try {
            int exitValue = dockerContainerManager.removeContainer(containerId);
            String response;

            if(exitValue == 0){
                response = "status: 200";
            }else{
                response = "status: 500";
            }

            ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error removing container: " + e.getMessage());
        }
        return null;
    }

    @GetMapping("/health")
    public ResponseEntity<Object> checkContainersHealth(@RequestParam List<String> containerIds){
        try {
            Map<String, String> containersHealthMap = dockerContainerManager.checkContainerHealth(containerIds);
            return ResponseEntity.ok(containersHealthMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
