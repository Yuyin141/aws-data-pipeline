output "bucket_raw_name" {
  description = "Raw data bucket name"
  value       = aws_s3_bucket.raw.id
}

output "bucket_raw_arn" {
  description = "Raw data bucket ARN"
  value       = aws_s3_bucket.raw.arn
}

output "bucket_processed_name" {
  description = "Processed data bucket name"
  value       = aws_s3_bucket.processed.id
}

output "bucket_processed_arn" {
  description = "Processed data bucket ARN"
  value       = aws_s3_bucket.processed.arn
}

