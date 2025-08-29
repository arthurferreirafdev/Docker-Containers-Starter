package com.example.docker_container_manager;

import com.example.docker_container_manager.model.Container;
import com.example.docker_container_manager.model.InstanciaMonitoramento;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class ContainerService {
    private static int dockerExitPort;
    @Value("${server.address}")
    String serverAddress;
    public final String systemPortFilePath = "resources/systemPortFile.txt";

    public ContainerService() {
        dockerExitPort = initializeMonitoramentoPort();
    }

    /**
     * Inicializa a porta de monitoramento a partir de um arquivo.
     * Trata de forma robusta o caso de o arquivo não existir ou não poder ser lido.
     *
     * @return A porta inicial.
     * @throws ContainerServiceException se ocorrer um erro irrecuperável de I/O.
     */
    private int initializeMonitoramentoPort() {
        int initialPort = 20000;
        try (BufferedReader reader = new BufferedReader(new FileReader(systemPortFilePath))) {
            String line = reader.readLine();
            if (line == null || line.trim().isEmpty()) {
                System.err.println("Arquivo de porta está vazio. Usando a porta inicial padrão.");
                return initialPort;
            }
            return Integer.parseInt(line.trim());
        } catch (FileNotFoundException fnf) {
            System.out.println("Arquivo de porta não encontrado. Criando novo com a porta inicial " + initialPort);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(systemPortFilePath, false))) {
                writer.write(String.valueOf(initialPort));
                return initialPort;
            } catch (IOException ioe) {
                // Erro ao escrever, lança uma exceção para o chamador.
                throw new ContainerServiceException("Erro ao escrever no arquivo de porta: " + systemPortFilePath, ioe);
            }
        } catch (IOException ioe) {
            // Erro ao ler o arquivo, lança uma exceção.
            throw new ContainerServiceException("Erro ao ler o arquivo de porta: " + systemPortFilePath, ioe);
        } catch (NumberFormatException nfe) {
            // Erro de formato, lança uma exceção.
            throw new ContainerServiceException("Conteúdo do arquivo de porta inválido. Esperado um número.", nfe);
        }
    }

    /**
     * Remove um container Docker e trata o resultado da operação.
     *
     * @param docker_container_id O ID do container a ser removido.
     * @return O código de saída do processo (0 para sucesso).
     * @throws ContainerServiceException se a operação de remoção falhar.
     */
    public int removeContainer(String docker_container_id) {
        String dockerRmString = "docker rm " + docker_container_id + " -f";
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(dockerRmString);
            int exitCode = process.waitFor();

            // Verifica o código de saída do processo.
            if (exitCode != 0) {
                String stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()))
                        .lines().collect(Collectors.joining("\n"));
                throw new ContainerServiceException("Falha ao remover o container " + docker_container_id + ". Código de saída: " + exitCode + ". Erro: " + stderr);
            }
            return exitCode;
        } catch (IOException | InterruptedException e) {
            throw new ContainerServiceException("Erro durante a execução do comando docker rm", e);
        } finally {
            if (process != null) {
                process.destroy();
                // Opcional: espera o processo terminar para liberar recursos completamente
                try {
                    process.waitFor(5, TimeUnit.SECONDS);
                } catch (InterruptedException ignored) {}
            }
        }
    }

    /**
     * Inicia novos containers e retorna a instância de monitoramento.
     *
     * @param eventId O ID do evento.
     * @return A instância de monitoramento criada.
     * @throws ContainerServiceException se a inicialização falhar.
     */
    public InstanciaMonitoramento startNewContainers(int eventId) {
        String serialNumber = generateUniqueSerialNumber();

        try {
            dockerExitPort = getNextPort();
            Container backend = startBack(serialNumber, dockerExitPort, eventId);
            String dockerContainerHostName = backend.getDocker_container_ID().substring(0, 12);
            return new InstanciaMonitoramento(dockerContainerHostName, backend);
        } catch (Exception e) {
            // Captura qualquer erro de inicialização e o encapsula
            throw new ContainerServiceException("Erro ao iniciar novos containers.", e);
        }
    }

    /**
     * Inicia um container de backend.
     *
     * @param serialNumber O número de série único do container.
     * @param dockerExitPort A porta de saída do Docker.
     * @param eventId O ID do evento.
     * @return O objeto Container representando o container iniciado.
     * @throws ContainerServiceException se a inicialização falhar.
     */
    private Container startBack(String serialNumber, int dockerExitPort, int eventId) {
        String containerName = null;
        String dockerContainerId = null;

        try {
            containerName = "sae_monitoramento_BACK" + "_" + serialNumber + "_" + dockerExitPort;
            String envVariables = "-e KAFKA_BROKER_1=10.3.192.19:9092,10.3.192.17:9092 -e PHYSIS_MONGO_DB=mongodb://general:RvrHbGKizj69yKJF@10.3.192.99:27017/?authSource=admin -e PHYSIS_ENV=dev";
            String dockerRunString = "docker run " + envVariables + " -d -p " + dockerExitPort + ":60250 --name " + containerName + " -it " + "sae_monitoramento_back " + eventId;

            String response = CLIService.execCommand(dockerRunString);

            // Assumindo que a primeira linha da resposta é o ID do container
            dockerContainerId = response.split("\n")[0].trim();
            if (dockerContainerId.isEmpty()) {
                throw new ContainerServiceException("Nenhum ID de container retornado pelo comando Docker.");
            }

            System.out.println("Container " + containerName + " iniciado com sucesso.");
            FileService.writeFile(response, "container.txt");

            return new Container(dockerContainerId, containerName, "running", String.valueOf(dockerExitPort), null);
        } catch (IOException e) {
            // Em caso de erro de I/O, propaga uma exceção mais significativa.
            throw new ContainerServiceException("Erro de I/O ao iniciar o container: " + containerName, e);
        } catch (Exception e) {
            // Para outros erros genéricos, propaga uma exceção.
            throw new ContainerServiceException("Erro inesperado ao iniciar o container: " + containerName, e);
        }
    }


    /**
     * Obtém a próxima porta disponível de forma síncrona.
     *
     * @return A próxima porta disponível.
     */
    private synchronized int getNextPort() {
        int newPort = dockerExitPort;
        while (portIsBusy(newPort)) {
            newPort++;
        }
        return newPort;
    }

    /**
     * Gera um número de série único para o container.
     *
     * @return Uma string de número de série única.
     */
    private String generateUniqueSerialNumber() {
        String timestamp = Long.toString(System.currentTimeMillis());
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        return timestamp + random;
    }

    /**
     * Verifica a saúde de uma lista de containers.
     *
     * @param containerIds A lista de IDs de containers para verificar.
     * @return Um mapa com o ID de cada container e seu status de saúde.
     * @throws ContainerServiceException se a verificação falhar.
     */
    public Map<String, String> checkContainerHealth(List<String> containerIds) {
        Map<String, String> containersHealthMap = new HashMap<>();
        try {
            for (String containerId : containerIds) {
                String command = "docker inspect -f '{{.State.Status}}' " + containerId;
                String containerStatus = CLIService.execCommand(command).trim();
                System.out.println("Status do container " + containerId + ": " + containerStatus);
                containersHealthMap.put(containerId, containerStatus);
            }
        } catch (IOException e) {
            throw new ContainerServiceException("Erro de I/O ao verificar a saúde dos containers.", e);
        } catch (Exception e) {
            throw new ContainerServiceException("Erro inesperado ao verificar a saúde dos containers.", e);
        }
        return containersHealthMap;
    }

    /**
     * Verifica se uma porta específica já está em uso por um container Docker.
     *
     * @param port A porta a ser verificada.
     * @return true se a porta estiver em uso, false caso contrário.
     */
    public boolean portIsBusy(int port) {
        try {
            String command = "docker ps --filter publish=" + port + " --format {{.Names}}";
            String response = CLIService.execCommand(command);
            // Se o comando retornar qualquer coisa, significa que a porta está em uso.
            return response != null && !response.trim().isEmpty();
        } catch (IOException e) {
            System.err.println("Erro de I/O ao verificar a porta " + port + ": " + e.getMessage());
            return false;
        }
    }
}
