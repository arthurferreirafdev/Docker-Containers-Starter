pipeline {
    agent any

    tools {
        maven 'Maven_3'
    }

    environment {
        JAR_NAME = 'docker_container_manager-0.0.1-SNAPSHOT.jar'
        PID_FILE = 'app.pid'
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
                if [ -f $PID_FILE ]; then
                  PID=$(cat $PID_FILE)
                  if kill -0 $PID > /dev/null 2>&1; then
                    echo "Stopping process $PID"
                    kill $PID
                    sleep 5
                  else
                    echo "Process $PID not running"
                  fi
                  rm -f $PID_FILE
                else
                  echo "No PID file found. No process to stop."
                fi
                '''
            }
        }

        stage('Run New App') {
            steps {
                sh '''
                nohup java -jar target/$JAR_NAME > app.log 2>&1 &
                echo $! > $PID_FILE
                echo "Application started with PID $(cat $PID_FILE)"
                '''
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
