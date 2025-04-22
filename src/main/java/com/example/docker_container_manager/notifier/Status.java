package com.example.docker_container_manager.notifier;

/**
 * A ordem de declaração do Enum é importante.
 */
public enum Status {
    /**
     * A aplicação está executando normalmente, sem erros.
     */
    RUNNING,
    /**
     * Um erro ocorreu e a aplicação está em execução, mas pode não funcionar corretamente.
     */
    ERROR

    /* Não é necessário reportar quando uma aplicação crasha, afinal não é possível reportar um erro se o programa não
     * está em execução. */
}
