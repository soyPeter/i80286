variable "name" {
  type = string
  description = "The name for the security group"
}
variable "vpc_id" {
  type = string
  description = "The ID of the VPC"
}
variable "ingress_rules" {
  type = list(
    object({
      from_port = number
      to_port = number
      protocol = string
      cidr_blocks = list(string)
      description = string
    })
  )
  default = []
  description = "A list of ingress rules"
}
variable "egress_rules" {
  type = list(
    object({
      from_port = number
      to_port = number
      protocol = string
      cidr_blocks = list(string)
      description = string
    })
  )
  default = []
  description = "A list of egress rules"
}
