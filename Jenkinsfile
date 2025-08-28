pipeline {
    agent any
    tools{
        maven 'Maven_3'
    }

    stages {
        stage('Checkout do Código') {
            steps {
                // Comando para clonar o repositório do Bitbucket
                // O 'credentialsId' deve ser o ID da sua credencial no Jenkins
                git branch: 'master',
                    credentialsId: '6d342202-b7ee-44ab-9973-b00448f9b968',
                    url: 'https://alanpp@bitbucket.org/alanpp/sae_cep_container_manager.git'
            }
        }

        stage('Build e Empacotamento') {
            steps {
                // O Jenkins já estará no diretório do projeto clonado
                // Comando para construir sua aplicação Java e gerar o .jar
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

        stage('Execução em Background') {
            steps {
                script {
                    def jarName = 'sae_container_manager.jar'
                    def localPath = '/opt/sae_container_manager'

                    // Primeiro, copia o .jar que foi gerado no passo de build
                    sh "cp target/${jarName} ${localPath}"

                    // Agora, executa a aplicação em background a partir do diretório de destino
                    sh """
                        cd ${localPath}

                        PIDS=\$(ps -ef | grep ${jarName} | grep -v grep | awk '{print \$2}')
                        if [ -n "\$PIDS" ]; then
                            echo "Aplicação já está rodando. Matando o processo: \$PIDS"
                            kill -9 \$PIDS
                        fi

                        nohup java -Dspring.profiles.active=dev -jar ${jarName} > application.log 2>&1 &
                    """
                }
            }
        }
    }
}