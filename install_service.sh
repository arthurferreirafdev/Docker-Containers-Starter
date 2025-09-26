#!/bin/bash

SERVICE_NAME="sae_container_manager"
SERVICE_FILE="/etc/systemd/system/${SERVICE_NAME}.service"
APP_DIR="/opt/sae_container_manager"
JAR_NAME="docker_container_manager-0.0.1-SNAPSHOT.jar"
JAVA_BIN=$(which java) # detecta o caminho do java

echo "==> Criando diretório da aplicação em ${APP_DIR}..."
sudo mkdir -p $APP_DIR
sudo chown -R jenkins:jenkins $APP_DIR

echo "==> Gerando arquivo de serviço em ${SERVICE_FILE}..."
cat <<EOF | sudo tee $SERVICE_FILE
[Unit]
Description=SAE CEP Container Manager
After=network.target

[Service]
User=jenkins
Group=jenkins
WorkingDirectory=${APP_DIR}

ExecStart=${JAVA_BIN} -Dspring.profiles.active=hml -jar ${APP_DIR}/${JAR_NAME}

Restart=always
RestartSec=5

StandardOutput=append:${APP_DIR}/application.log
StandardError=append:${APP_DIR}/application.log

[Install]
WantedBy=multi-user.target
EOF

echo "==> Recarregando systemd..."
sudo systemctl daemon-reload

echo "==> Habilitando serviço para iniciar no boot..."
sudo systemctl enable $SERVICE_NAME

echo "==> Iniciando serviço..."
sudo systemctl restart $SERVICE_NAME

echo "==> Status do serviço:"
sudo systemctl status $SERVICE_NAME --no-pager -l
