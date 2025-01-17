#provider 
provider "aws" {
  region = var.region
}

# Crear VPC
resource "aws_vpc" "queryvpc" {
  cidr_block = "22.1.0.0/16"
  tags = {
    "name" = "query-VPC"
  }
  enable_dns_support   = true
  enable_dns_hostnames = true
}


# Subred pública 1
resource "aws_subnet" "public_subnet_1_query" {
    vpc_id = aws_vpc.queryvpc.id
    cidr_block = "22.1.1.0/24"
    map_public_ip_on_launch = true
    availability_zone = "us-east-1a"
    tags = {
      "name" = "public_subnet_1_query"
    }
}

resource "aws_internet_gateway" "internet_gateway_query" {
  vpc_id = aws_vpc.queryvpc.id

  tags = {
    Name = "Internet Gateway"
  }
}

# Routing Table para la subred pública
resource "aws_route_table" "Public_routing_for_tfvpc_publicSubnet_query" {
    vpc_id = aws_vpc.queryvpc.id
    route {
        cidr_block = "0.0.0.0/0"
        gateway_id = aws_internet_gateway.internet_gateway_query.id
    }
    tags = {
      "name" = "Public_routing_for_tfvpc_publicSubnet"
    }
}

resource "aws_route_table_association" "public_subnet_association_1_query" {
  subnet_id      = aws_subnet.public_subnet_1_query.id
  route_table_id = aws_route_table.Public_routing_for_tfvpc_publicSubnet_query.id
}

resource "aws_security_group" "alb_sg_query" {
  vpc_id = aws_vpc.queryvpc.id

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
    Name = "alb-sg-query"
  }
}

resource "aws_security_group" "ec2_sg_query" {
  vpc_id = aws_vpc.queryvpc.id

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # Rango de IP de la subred pública del ALB
  }

  ingress {
    from_port   = 8900
    to_port     = 8900
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
    Name = "ec2-sg-query"
  }
}


resource "aws_instance" "query_service" {
  ami           = "ami-0e2c8caa4b6378d8c"
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.public_subnet_1_query.id
  key_name      = "vockey"
  vpc_security_group_ids = [aws_security_group.ec2_sg_query.id]

  iam_instance_profile = "LabInstanceProfile"

  tags = {
    Name = "QueryService"
  }

  user_data = file("setup-script-query-service.sh")
}


