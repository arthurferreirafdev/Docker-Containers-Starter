package com.example.docker_container_manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class CheckContainerStatusTest {

    @Test
    void shouldReturnAStringFromCLICommand(){
        DockerContainerManager containerManager = new DockerContainerManager();
        List<String> commands = new ArrayList<>();
        commands.add("25cb0184eaa3");

        try {
            Map<String, String> map = containerManager.checkContainerHealth(commands);

            assert(map != null || map.isEmpty());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
