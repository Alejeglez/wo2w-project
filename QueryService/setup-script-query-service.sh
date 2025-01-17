#!/bin/bash
# Redirigir toda la salida estándar y errores a user-log.txt
exec > /home/ubuntu/user-log.txt 2>&1

# Instalar Java 17
sudo apt update -y
sudo apt install -y openjdk-17-jdk

# Verificar instalación de Java
java -version

# Instalar unzip y AWS CLI
sudo apt install -y unzip
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install

# Comprobar instalación de AWS CLI
aws --version

# Descargar Downloader.jar y script Python desde S3
aws s3 cp s3://wo2w-artifacts-22/query-service-api.py /home/ubuntu/query-service-api.py

# Instalar Python y configurar entorno virtual
sudo apt install python3 -y
sudo apt install python3-pip -y
sudo apt install -y python3-venv
python3 -m venv ~/myvirtualenv
source ~/myvirtualenv/bin/activate
pip3 install flask
pip3 install requests
pip3 install boto3
pip3 install prometheus_client

# Ejecutar script Python en segundo plano
python3 /home/ubuntu/query-service-api.py &