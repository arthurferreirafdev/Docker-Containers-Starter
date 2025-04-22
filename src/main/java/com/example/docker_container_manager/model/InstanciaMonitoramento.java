package com.example.docker_container_manager.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class InstanciaMonitoramento {
    String dockerContainerHostName; //hashmap key
    Long monitoramentoId;
    Long idEvento;
    Long idFluxo;
    Long idBarragem;
    Long idArvoreDecisao;
    String uptime;
    Container backend;
    Container frontend;
    String route;

    public InstanciaMonitoramento(String dockerContainerHostName, Container backend, Container frontend, String route) {
        this.dockerContainerHostName = dockerContainerHostName;
        this.monitoramentoId = (new java.util.Random().nextLong());
        this.uptime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.backend = backend;
        this.frontend = frontend;
        this.route = route;
    }
}
