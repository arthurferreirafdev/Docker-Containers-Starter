pipeline {
    agent any

    tools {
        maven 'Maven_3'
    }

    environment {
        JAR_NAME = 'docker_container_manager-0.0.1-SNAPSHOT.jar'
    }

    stages {
        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('Stop Old App') {
            steps {
                sh '''
                PID=$(pgrep -f $JAR_NAME)
                if [ ! -z "$PID" ]; then
                  echo "Stopping process $PID"
                  sudo kill $PID
                  sleep 5
                else
                  echo "No process running"
                fi
                '''
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