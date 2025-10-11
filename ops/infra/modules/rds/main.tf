resource "aws_db_instance" "default" {
  allocated_storage      = var.allocated_storage
  engine                 = var.engine
  engine_version         = var.engine_version
  instance_class         = var.instance_class
  db_name                = var.db_name
  username               = var.username
  password               = var.password
  db_subnet_group_name   = var.db_subnet_group_name
  vpc_security_group_ids = var.vpc_security_group_ids
  skip_final_snapshot    = true
  publicly_accessible    = false
  storage_type           = var.storage_type
}
