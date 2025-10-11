output "address" {
  value = aws_db_instance.default.address
  description = "The address of the database"
}
output "port" {
  value = aws_db_instance.default.port
  description = "The database port"
}
output "db_resource_id" {
  value = aws_db_instance.default.id
  description = "The resource id of the database"
}
