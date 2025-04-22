package com.example.docker_container_manager.notifier;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.concurrent.atomic.AtomicInteger;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
class ExceptionInfo {
    /**
     * Exceções que a aplicação consegue corrigir por conta própria e continuar com seu funcionamento normalmente.
     */
    private final AtomicInteger light = new AtomicInteger(0);
    /**
     * Exceções que impedem que um recurso da aplicação funcione em sua totalidade, mas a aplicação ainda consegue
     * atender requisições parcialmente.
     */
    private final AtomicInteger moderate = new AtomicInteger(0);
    /**
     * Exceções que impedem completamente o funcionamento de um recurso importante da aplicação, e provavelmente será
     * necessária intervenção manual para corrigi-lo.
     * Não é garantido que a aplicação continue em execução após uma exceção crítica.
     */
    private final AtomicInteger critical = new AtomicInteger(0);

    public void incrementLight() {
        light.incrementAndGet();
    }

    public void incrementModerate() {
        moderate.incrementAndGet();
    }

    public void incrementCritical() {
        critical.incrementAndGet();
    }
}
