pipeline {
    agent any
    tools {
        maven 'Maven_3'
    }

    stages {
        stage('Checkout do Código') {
            steps {
                git branch: 'master',
                    credentialsId: 'b0743c51-8ac0-4dd5-b78e-b65abe58de0b',
                    url: 'https://alanpp@bitbucket.org/alanpp/sae_cep_container_manager.git'
            }
        }

        stage('Build e Empacotamento') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Preparar Diretório de Deploy') {
            steps {
                echo 'Garantindo que o diretório de deploy exista e tenha permissões corretas...'
                sh 'sudo mkdir -p /opt/sae_container_manager'
                sh 'sudo chown -R jenkins:jenkins /opt/sae_container_manager'
            }
        }

        stage('Deploy via systemd') {
            steps {
                script {
                    def jarName = 'docker_container_manager-0.0.1-SNAPSHOT.jar'
                    def localPath = '/opt/sae_container_manager'

                    // copia o .jar
                    sh "cp target/${jarName} ${localPath}"

                    // reinicia o serviço systemd
                    sh "sudo systemctl restart sae_container_manager"

                    // checa status para garantir que subiu
                    sh "sudo systemctl status sae_container_manager --no-pager"
                }
            }
        }
    }
}