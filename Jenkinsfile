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
    }



    stages {
        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('Stop Old App') { //stopping last app
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
        //running app
        //-Dspring.profiles.active=ENV sets spring profile variable
            steps {
                sh '''
                java -Dspring.profiles.active=$ENV -jar target/$JAR_NAME &
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
