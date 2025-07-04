pipeline {
    agent any

    tools {
        maven 'Maven_3' // setting maven for build project
    }

    parameters {
        string(name: 'Environment', defaultValue: '', description: 'Ambiente de configuração')
    }

    environment {
        JAR_NAME = 'docker_container_manager-0.0.1-SNAPSHOT.jar'
        PID_FILE = 'app.pid'
        ENV = "${params.Environment}" // variável de ambiente para perfil Spring
        BUILD_ID = 'dontKillMe'       // impede o Jenkins de matar o processo background
        SERVER = 'deploy@10.3.192.100'
        REMOTE_HOST = '10.3.192.100'
    }

    stages {
        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('Prepare SSH') {
            steps {
                sshagent(['ssh-credential-id']) {
                    // Adiciona o host remoto ao known_hosts
                    sh '''
                    mkdir -p ~/.ssh
                    touch ~/.ssh/known_hosts
                    ssh-keyscan -H ${REMOTE_HOST} >> ~/.ssh/known_hosts
                    '''
                }
            }
        }

        stage('Run New App') {
            steps {
                sshagent(['ssh-credential-id']) {
                    sh """
                    scp target/${JAR_NAME} ${SERVER}:/opt/sae_container_manager
                    ssh ${SERVER} 'systemctl restart sae_container_manager'
                    """
                }
            }
        }
    }

    post {
        success {
            echo '✅ Deploy realizado com sucesso!'
        }
        failure {
            echo '❌ Ocorreu uma falha no pipeline.'
        }
    }
}
