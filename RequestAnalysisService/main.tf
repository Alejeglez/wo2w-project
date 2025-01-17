#provider 
provider "aws" {
  region = var.region
}

# Crear VPC
resource "aws_vpc" "requestsvpc" {
  cidr_block = "23.1.0.0/16"
  tags = {
    "name" = "requests-VPC"
  }
  enable_dns_support   = true
  enable_dns_hostnames = true
}


# Subred pública 1
resource "aws_subnet" "public_subnet_1_requests" {
    vpc_id = aws_vpc.requestsvpc.id
    cidr_block = "23.1.1.0/24"
    map_public_ip_on_launch = true
    availability_zone = "us-east-1a"
    tags = {
      "name" = "public_subnet_1_requests"
    }
}

# Subred pública 2
resource "aws_subnet" "public_subnet_2_requests" {
    vpc_id = aws_vpc.requestsvpc.id
    cidr_block = "23.1.2.0/24"
    map_public_ip_on_launch = true
    availability_zone = "us-east-1b"
    tags = {
      "name" = "public_subnet_2_requests"
    }
}

resource "aws_internet_gateway" "internet_gateway_requests" {
  vpc_id = aws_vpc.requestsvpc.id

  tags = {
    Name = "Internet Gateway"
  }
}

# Routing Table para la subred pública
resource "aws_route_table" "Public_routing_for_tfvpc_publicSubnet_requests" {
    vpc_id = aws_vpc.requestsvpc.id
    route {
        cidr_block = "0.0.0.0/0"
        gateway_id = aws_internet_gateway.internet_gateway_requests.id
    }
    tags = {
      "name" = "Public_routing_for_tfvpc_publicSubnet"
    }
}

resource "aws_route_table_association" "public_subnet_association_1_requests" {
  subnet_id      = aws_subnet.public_subnet_1_requests.id
  route_table_id = aws_route_table.Public_routing_for_tfvpc_publicSubnet_requests.id
}


resource "aws_route_table_association" "public_subnet_association_2_requests" {
  subnet_id      = aws_subnet.public_subnet_2_requests.id
  route_table_id = aws_route_table.Public_routing_for_tfvpc_publicSubnet_requests.id
}

resource "aws_security_group" "alb_sg_requests" {
  vpc_id = aws_vpc.requestsvpc.id

  ingress {
    from_port   = 80                   # Puerto en el ALB
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]        # Permitir tráfico externo
  }

  ingress{
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "alb-sg-requests"
  }
}

resource "aws_security_group" "ec2_sg_requests" {
  vpc_id = aws_vpc.requestsvpc.id

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # Rango de IP de la subred pública del ALB
  }

  ingress {
    from_port   = 9090
    to_port     = 9090
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # Rango de IP de la subred pública del ALB
  }

  # Permitir tráfico SSH (puerto 22) desde tu IP
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]  # Reemplaza <tu-ip> con tu dirección IP
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "ec2-sg-requests"
  }
}


resource "aws_instance" "request_analysis_service" {
  ami           = "ami-0e2c8caa4b6378d8c"
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.public_subnet_1_requests.id
  key_name      = "vockey"
  vpc_security_group_ids = [aws_security_group.ec2_sg_requests.id]

  iam_instance_profile = "LabInstanceProfile"

  tags = {
    Name = "RequestAnalysisService"
  }

  user_data = file("setup-script-request-analysis.sh")
}