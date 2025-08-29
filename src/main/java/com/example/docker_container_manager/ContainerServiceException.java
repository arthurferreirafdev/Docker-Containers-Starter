package com.example.docker_container_manager;

public class ContainerServiceException extends RuntimeException {
    public ContainerServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContainerServiceException(String message) {
        super(message);
    }
}
