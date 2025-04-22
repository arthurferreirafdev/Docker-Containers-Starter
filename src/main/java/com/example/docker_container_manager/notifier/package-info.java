/**
 * Este pacote contém classes responsáveis pela comunicação com o µServiço Notifier, que checa periodicamente o estado
 * da aplicação referente a erros de execução. A própria aplicação é responsável por reportar erros ocorridos utilizando
 * {@link lri.notifier.Report}.
 * <br>Este pacote precisa estar em um diretório visível para o scan de classes do Spring.
 * <br>Este pacote é o mesmo para vários µServiços e, idealmente, não precisa ser alterado.
 *
 * @see lri.notifier.HealthController
 */
package com.example.docker_container_manager.notifier;
