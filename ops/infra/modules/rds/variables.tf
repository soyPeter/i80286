variable "allocated_storage" {
  type = number
  description = "The allocated storage for the RDS instance"
}
variable "engine" {
  type = string
  description = "The database engine to use"
}
variable "engine_version" {
  type = string
  description = "The database engine version"
}
variable "instance_class" {
  type = string
  description = "The instance class for the RDS instance"
}
variable "db_name" {
  type = string
  description = "The name for the database"
}
variable "username" {
  type = string
  description = "The username to access the database"
}
variable "password" {
  type = string
  description = "The password for the database"
}
variable "db_subnet_group_name" {
  type = string
  description = "The name of the subnet group for the database"
}
variable "vpc_security_group_ids" {
  type = list(string)
  description = "The security groups to be used by the database"
}
variable "storage_type"{
  type = string
  description = "The storage type to be used for the database"
}
