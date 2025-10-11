variable "aws_region" {
  type = string
  description = "The AWS region to deploy to"
  default = "eu-west-1"
}

variable "project_name" {
  type = string
  description = "The name of the project"
}

variable "vpc_cidr" {
  type = string
  description = "CIDR block for the VPC"
  default = "10.0.0.0/16"
}
variable "public_subnet_cidrs" {
  type = list(string)
  description = "List of CIDR blocks for public subnets"
}
variable "private_subnet_cidrs" {
  type = list(string)
  description = "List of CIDR blocks for private subnets"
}
variable "db_subnet_cidrs" {
  type = list(string)
  description = "List of CIDR blocks for database subnets"
}
variable "admin_group_name" {
  type = string
  description = "The name of the admin IAM group"
  default = "administrators"
}
variable "developer_group_name" {
  type = string
  description = "The name of the developer IAM group"
  default = "developers"
}
variable "admin_user_names" {
  type = list(string)
  description = "The list of admin usernames"
}
variable "developer_user_names" {
  type = list(string)
  description = "The list of developer usernames"
}

variable "aws_profile" {
  type        = string
  description = "The AWS CLI profile to use"
  default = "peter"
}
