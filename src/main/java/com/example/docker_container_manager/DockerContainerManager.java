package com.example.docker_container_manager;

import com.example.docker_container_manager.model.Container;
import com.example.docker_container_manager.model.GeneralData;
import com.example.docker_container_manager.model.InstanciaMonitoramento;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.InetAddress;
import java.util.UUID;
@Component
public class DockerContainerManager {
    static int dockerExitPort;
    @Value("${server.address}")
    String serverAddress;
    public final String systemPortFilePath = "resources/systemPortFile.txt";

    public DockerContainerManager() {
        dockerExitPort = initializeMonitoramentoPort();
    }

    private int initializeMonitoramentoPort(){
        int initialPort = 20000;

        try(BufferedReader reader = new BufferedReader(new FileReader(systemPortFilePath))){
            String line = reader.readLine();
            return Integer.parseInt(line);

        }catch (FileNotFoundException fnf){
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(systemPortFilePath, false))){
                writer.write(String.valueOf(initialPort));

            }catch(Exception f){
                System.out.println("Error writing systemPortFilePath: " + f.getMessage());
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
        }

        return initialPort;
    }

    public int removeContainer(String docker_container_id) {
        String dockerRmString = "docker rm " + docker_container_id + " -f";
        String error = null;
        int exitCode;
        int exitValue;

        Process process = null;
        try {
            process = Runtime.getRuntime().exec(dockerRmString);
            exitCode = process.waitFor();
            exitValue = process.exitValue();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return exitValue;
    }

    public InstanciaMonitoramento startNewContainers() throws IOException, InterruptedException {
        String serialNumber = generateUniqueSerialNumber();
        Container backend = new Container();

        //verifica se os container foram criados com sucesso, e manda inicialos
        while(backend.getError() == null){
            dockerExitPort = getNextPort();
            backend = startBack(serialNumber, dockerExitPort);
        }

        String dockerContainerHostName = backend.getDocker_container_ID().substring(0, 12);
        return new InstanciaMonitoramento(dockerContainerHostName, backend);
    }

    //inicia um container de backend
    private Container startBack(String serialNumber, int dockerExitPort) throws IOException {
        String dockerContainerId = null;
        String containerName = null;
        int exitValue = 0;
        String error = null;


        try {
            dockerExitPort--;

            containerName = "sae_monitoramento_BACK" + "_" + serialNumber + "_" + dockerExitPort ;

            String dockerRunString = "docker run -d -p " + dockerExitPort + ":60250 --name " + containerName + " -it " + "sae_monitoramento_back";
            Process process = Runtime.getRuntime().exec(dockerRunString);
            System.out.println("\n\n" + "container sae_monitoramento_BACK_" + serialNumber + "_" + dockerExitPort + " started \n\n");
            dockerContainerId = writeContainersUp(process, String.valueOf(dockerExitPort));
            error = writeError(process);

            int exitCode = process.waitFor();
            exitValue = process.exitValue();

        } catch (Exception e) {
            e.printStackTrace();
        }
        //TODO - GENERATE LOG FROM

        return new Container(dockerContainerId, containerName, String.valueOf(exitValue), String.valueOf(dockerExitPort), error);
    }


    //get next container port
    private synchronized int getNextPort() {
        int newPort = dockerExitPort += 2;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(systemPortFilePath, false))) {
            writer.write(String.valueOf(newPort));
        } catch (Exception e) {
            System.out.println("\nError writing portFile: " + e.getMessage());
        }
        return newPort;
    }

    //generate new container serial number
    private String generateUniqueSerialNumber() {
        // You can use a combination of timestamp and a random portion
        String timestamp = Long.toString(System.currentTimeMillis());
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 6); // Generate a random portion

        // Combine the timestamp and random portion to create a unique serial number
        return timestamp + random;
    }

    private static String writeContainersUp(Process process, String dockerExitPort){
        String dockerContainerId = "";

        try{
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedWriter write = new BufferedWriter(new FileWriter(GeneralData.dockerLogPrefixPath +  "output.txt", false));
            BufferedWriter writeContainerUp = new BufferedWriter(new FileWriter(GeneralData.dockerLogPrefixPath + "containers-up.txt", true));
            String stdoutLine;

            while ((stdoutLine = stdInput.readLine()) != null) {
                write.write(stdoutLine);
                write.newLine();
                dockerContainerId = stdoutLine;

                writeContainerUp.write(dockerExitPort + ";" + stdoutLine);
                writeContainerUp.newLine();
            }

            write.close();
            writeContainerUp.close();
            stdInput.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        return dockerContainerId;
    }

    private static String writeError(Process process){
        String logString = "";

        try{
            // Read the standard error
            BufferedWriter write = new BufferedWriter(new FileWriter(GeneralData.dockerLogPrefixPath + "error.txt", false));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String stderrLine;
            while ((stderrLine = stdError.readLine()) != null) {
                logString = stderrLine;
                System.out.println("error: " + stderrLine);
                write.write(stderrLine);
                write.newLine();
            }

            write.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return logString;
    }
}

