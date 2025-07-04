pipeline {
    agent any

    tools {
        maven 'Maven_3' //setting maven for build project
    }

    parameters { //(build with parameters in jenkins)
            string(name: 'Environment', defaultValue: '', description: 'Ambiente de configuração')
        }

    environment {
        JAR_NAME = 'docker_container_manager-0.0.1-SNAPSHOT.jar'
        PID_FILE = 'app.pid'
        ENV = "${params.Environment}" //defininco variavel de ambiente para que o spring selecione o perfil desejado (build with parameters)
        BUILD_ID = 'dontKillMe' //evitar que o jenkins mate o processo em background resultante
        SERVER = 'deploy@10.3.192.100'
        REMOTE_HOST = '10.3.192.100'
    }



    stages {
        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }

        stages {
        stage('Prepare SSH') {
            steps {
                sshagent(['ssh-credential-id']) {
                    // Adiciona o host remoto no known_hosts para evitar erro de verificação
                    sh '''
                    mkdir -p ~/.ssh
                    touch ~/.ssh/known_hosts
                    ssh-keyscan -H ${REMOTE_HOST} >> ~/.ssh/known_hosts
                    '''
                }
            }
        }

        stage('Run New App') {
        //running app
        //-Dspring.profiles.active=ENV | sets spring profile variable
            steps {
                sshagent(['ssh-credential-id']){
                sh """
                scp target/${JAR_NAME} ${SERVER}:/opt/sae_container_manager
                ssh ${SERVER} 'systemctl restart sae_container_manager'
                """
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
