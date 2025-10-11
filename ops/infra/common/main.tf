terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
  profile = var.aws_profile
}

resource "aws_vpc" "main" {
  cidr_block = var.vpc_cidr
  tags = {
    Name = "${var.project_name}-vpc"
  }
}

resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id
  tags = {
    Name = "${var.project_name}-igw"
  }
}

resource "aws_default_route_table" "default" {
  default_route_table_id = aws_vpc.main.default_route_table_id

  tags = {
    Name = "${var.project_name}-default-rt"
  }

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }
}


resource "aws_subnet" "public" {
  count = length(var.public_subnet_cidrs)
  vpc_id = aws_vpc.main.id
  cidr_block = var.public_subnet_cidrs[count.index]
  map_public_ip_on_launch = true
  availability_zone = data.aws_availability_zones.available.names[count.index]
  tags = {
    Name = "${var.project_name}-public-subnet-${count.index + 1}"
  }
}

resource "aws_subnet" "private" {
  count = length(var.private_subnet_cidrs)
  vpc_id = aws_vpc.main.id
  cidr_block = var.private_subnet_cidrs[count.index]
  availability_zone = data.aws_availability_zones.available.names[count.index]
  tags = {
    Name = "${var.project_name}-private-subnet-${count.index + 1}"
  }
}

resource "aws_subnet" "db" {
  count = length(var.db_subnet_cidrs)
  vpc_id = aws_vpc.main.id
  cidr_block = var.db_subnet_cidrs[count.index]
  availability_zone = data.aws_availability_zones.available.names[count.index]
  tags = {
    Name = "${var.project_name}-db-subnet-${count.index + 1}"
  }
}

data "aws_availability_zones" "available" {
  state = "available"
}

module "common_security_group" {
  source = "../modules/security-group"
  name = "${var.project_name}-common-sg"
  vpc_id = aws_vpc.main.id
  ingress_rules = [
    {
      from_port = 80
      to_port = 80
      protocol = "tcp"
      cidr_blocks = ["0.0.0.0/0"]
      description = "Allow HTTP access"
    },
    {
      from_port = 443
      to_port = 443
      protocol = "tcp"
      cidr_blocks = ["0.0.0.0/0"]
      description = "Allow HTTPS access"
    }
  ]
  egress_rules = [
    {
      from_port = 0
      to_port = 0
      protocol = "-1"
      cidr_blocks = ["0.0.0.0/0"]
      description = "Allow all outbound traffic"
    }
  ]
}

resource "aws_iam_group" "admin_group" {
  name = var.admin_group_name
}

resource "aws_iam_group" "developer_group" {
  name = var.developer_group_name
}

resource "aws_iam_user" "admin_users" {
  count = length(var.admin_user_names)
  name = var.admin_user_names[count.index]
}
resource "aws_iam_user" "developer_users" {
  count = length(var.developer_user_names)
  name = var.developer_user_names[count.index]
}

resource "aws_iam_group_membership" "admin_membership" {
  count = length(aws_iam_user.admin_users)
  name = "admin_membership-${count.index}"
  users = [aws_iam_user.admin_users[count.index].name]
  group = aws_iam_group.admin_group.name
}


resource "aws_iam_group_membership" "developer_membership" {
  count = length(aws_iam_user.developer_users)
  name = "developer_membership-${count.index}"
  users = [aws_iam_user.developer_users[count.index].name]
  group = aws_iam_group.developer_group.name
}
