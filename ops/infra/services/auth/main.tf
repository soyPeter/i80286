module "auth_db" {
  source            = "../../modules/rds"
  allocated_storage = 20
  engine          = "postgres"
  engine_version  = "15.4"
  instance_class    = "db.t3.micro"
  db_name         = "authdb"
  username        = "admin"
  password        = var.db_password
  db_subnet_group_name = module.common.db_subnet_group_name
  vpc_security_group_ids = [module.common.common_security_group_id]
  storage_type = "gp2"
}

module "common" {
  source = "../../common"
}

resource "aws_db_user" "read_user" {
  db_instance_identifier = module.auth_db.db_resource_id
  name = "readuser"
  password = var.db_read_user_password
}

resource "aws_db_user" "write_user" {
  db_instance_identifier = module.auth_db.db_resource_id
  name = "writeuser"
  password = var.db_write_user_password
}

module "auth_sg" {
  source = "../../modules/security-group"
  name = "${var.project_name}-auth-sg"
  vpc_id = module.common.vpc_id
  ingress_rules = [
    {
      from_port = 8020
      to_port = 8020
      protocol = "tcp"
      cidr_blocks = [module.common.vpc_cidr]
      description = "Allow access to port 8020"
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

resource "aws_elasticache_cluster" "auth_redis" {
  cluster_id      = "${var.project_name}-auth-redis"
  engine          = "redis"
  node_type       = "cache.t3.micro"
  num_cache_nodes = 1
  port            = 6379
  subnet_group_names = [module.common.db_subnet_group_name]
  security_group_ids = [module.common.common_security_group_id, module.auth_sg.security_group_id]
}
