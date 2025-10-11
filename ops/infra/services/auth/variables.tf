variable "db_password" {
  type = string
  description = "The password for the service database"
}
variable "image_tag" {
  type = string
  description = "The image tag of the docker image for this service"
}
variable "db_read_user_password" {
  type = string
  description = "The password for the read user"
}
variable "db_write_user_password" {
  type = string
  description = "The password for the read and write user"
}
