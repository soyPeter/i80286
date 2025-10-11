output "service_created_auth" {
  value = true
  description = "Indicates if the service was created"
}
output "db_address" {
  value = module.auth_db.address
  description = "The address of the database instance"
}
output "db_port" {
  value = module.auth_db.port
  description = "The database port"
}

output "auth_security_group_id" {
  value = module.auth_sg.security_group_id
  description = "The auth service security group id"
}
