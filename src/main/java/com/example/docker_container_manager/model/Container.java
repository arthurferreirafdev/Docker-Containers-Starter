package com.example.docker_container_manager.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
@Data
public class Container {
    private String docker_container_ID;
    private String container_name;
    private String status;
    private String port;
    private String uptime;
    private String error;


    public Container(String container_ID, String container_name, String status, String port, String error) {
        this.docker_container_ID = container_ID;
        this.container_name = container_name;
        this.status = status;
        this.port = port;
        this.uptime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.error = error;
    }

    public Container(){
        error = null;
    }

    public String getError() {
        return error;
    }

    public String getPort() {
        return port;
    }

    public String getDocker_container_ID() {
        return docker_container_ID;
    }
}


