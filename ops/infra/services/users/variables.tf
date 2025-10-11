variable "db_password" {
  type = string
  description = "The password for the service database"
}
variable "image_tag" {
  type = string
  description = "The image tag of the docker image for this service"
}
