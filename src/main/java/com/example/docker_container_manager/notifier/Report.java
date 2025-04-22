package com.example.docker_container_manager.notifier;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Singleton utilizado para reportar erros ao notificador. Injete-o como dependência nas classes utilizando
 * {@link Autowired} do Spring.
 * <br>O programa começa com o status {@link Status#RUNNING}, e o status só tende a piorar. Não é possível voltar ao
 * estado anterior após ele ter sido alterado (a não ser que o programa seja reiniciado).
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class Report implements Cloneable {
    /**
     * Início do monitoramento da aplicação
     */
    @NonNull
    @JsonSerialize(using = DateUnixMillisSerializer.class)
    final Date start = new Date();
    /**
     * Última busca por estatísticas da aplicação.
     */
    @NonNull
    @JsonSerialize(using = DateUnixMillisSerializer.class)
    Date last = new Date();
    @Getter
    @NonNull
    Status status = Status.RUNNING;
    @NonNull
    ExceptionInfo exceptionInfo = new ExceptionInfo();

    /**
     * @return Shallow copy
     */
    @Override
    public Report clone() {
        try {
            return (Report) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Erro ao clonar report");
        }
    }

    /**
     * Registra uma exceção crítica.
     *
     * @param status Status do programa após a exceção. Caso o estado anterior do programa seja melhor (seguindo a ordem
     *               de declaração em {@link Status}) do que o aqui especificado, ele não será alterado.
     * @see ExceptionInfo
     */
    public void criticalException(Status status) {
        if (this.status.compareTo(status) < 0)
            this.status = status;
        this.last = new Date();
        this.exceptionInfo.incrementCritical();
    }

    /**
     * Registra uma exceção moderada.
     *
     * @param status Status do programa após a exceção. Caso o estado anterior do programa seja melhor (seguindo a ordem
     *               de declaração em {@link Status}) do que o aqui especificado, ele não será alterado.
     * @see ExceptionInfo
     */
    public void moderateException(Status status) {
        if (this.status.compareTo(status) < 0)
            this.status = status;
        this.last = new Date();
        this.exceptionInfo.incrementModerate();
    }

    /**
     * Registra uma exceção leve. O status do programa não é alterado.
     *
     * @see ExceptionInfo
     */
    public void lightException() {
        this.last = new Date();
        this.exceptionInfo.incrementLight();
    }

    void reset() {
        this.last = new Date();
        this.exceptionInfo = new ExceptionInfo();
    }

    public Status getStatus() {
        return status;
    }
}
