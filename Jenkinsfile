pipeline {
    agent any

    environment {
        JAR_NAME = 'docker_container_manager-0.0.1-SNAPSHOT.jar'
    }

    stages {
//         stage('Checkout') {
//             steps {
//                 git branch: 'master',
//                     credentialsId: 'd493f0b8-ca71-4e05-8abc-8b1a0e255d93',
//                     url: 'https://bitbucket.org/alanpp/sae_cep_container_manager.git'
//             }
//         }

        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('Stop Old App') {
            steps {
                script {
                    sh '''
                    PID=$(pgrep -f $JAR_NAME)
                    if [ ! -z "$PID" ]; then
                      echo "Stopping process $PID"
                      kill $PID
                      sleep 5
                    else
                      echo "No process running"
                    fi
                    '''
                }
            }
        }

        stage('Run New App') {
            steps {
                script {
                    sh '''
                    nohup java -jar target/*.jar > app.log 2>&1 &
                    echo "Application started"
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'Deploy realizado com sucesso!'
        }
        failure {
            echo 'Ocorreu uma falha no pipeline.'
        }
    }
}