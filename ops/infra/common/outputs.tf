output "vpc_id" {
  description = "The ID of the VPC"
  value = aws_vpc.main.id
}
output "public_subnet_ids" {
  description = "List of IDs for public subnets"
  value = aws_subnet.public[*].id
}
output "private_subnet_ids" {
  description = "List of IDs for private subnets"
  value = aws_subnet.private[*].id
}
output "db_subnet_ids" {
  description = "List of IDs for db subnets"
  value = aws_subnet.db[*].id
}
output "common_security_group_id" {
  description = "The ID of the common security group"
  value = module.common_security_group.security_group_id
}
output "admin_group_arn" {
  description = "The ARN of the administrator IAM group"
  value = aws_iam_group.admin_group.arn
}
output "developer_group_arn" {
  description = "The ARN of the developer IAM group"
  value = aws_iam_group.developer_group.arn
}
