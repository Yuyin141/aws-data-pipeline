output "vpc_id" {
  description = "VPC ID"
  value       = module.networking.vpc_id
}

output "s3_bucket_raw" {
  description = "S3 bucket for raw files"
  value       = module.storage.bucket_raw_name
}

output "s3_bucket_processed" {
  description = "S3 bucket for processed files"
  value       = module.storage.bucket_processed_name
}

output "sns_topic_arn" {
  description = "SNS topic ARN"
  value       = module.messaging.sns_topic_arn
}

output "sqs_queue_url" {
  description = "SQS queue URL"
  value       = module.messaging.sqs_queue_url
}

output "rds_proxy_endpoint" {
  description = "RDS Proxy endpoint"
  value       = module.database.rds_proxy_endpoint
}

output "rds_endpoint" {
  description = "RDS instance endpoint"
  value       = module.database.rds_endpoint
}

output "lambda_function_arn" {
  description = "Lambda function ARN"
  value       = module.serverless.lambda_function_arn
}

output "alb_dns_name" {
  description = "Application Load Balancer DNS name"
  value       = module.compute.alb_dns_name
}

output "ec2_security_group_id" {
  description = "EC2 security group ID"
  value       = module.compute.ec2_security_group_id
}

