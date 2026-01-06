resource "aws_sns_topic" "file_events" {
  name = "${var.project_name}-${var.environment}-file-events"

  tags = {
    Name = "${var.project_name}-${var.environment}-file-events"
  }
}

resource "aws_sqs_queue" "processing_dlq" {
  name                      = "${var.project_name}-${var.environment}-processing-dlq"
  message_retention_seconds = 1209600

  tags = {
    Name = "${var.project_name}-${var.environment}-processing-dlq"
    Type = "dead-letter-queue"
  }
}

resource "aws_sqs_queue" "processing" {
  name                       = "${var.project_name}-${var.environment}-processing"
  visibility_timeout_seconds = 360
  message_retention_seconds  = 345600
  receive_wait_time_seconds  = 20

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.processing_dlq.arn
    maxReceiveCount     = 3
  })

  tags = {
    Name = "${var.project_name}-${var.environment}-processing-queue"
  }
}

resource "aws_sqs_queue_policy" "processing" {
  queue_url = aws_sqs_queue.processing.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Principal = { Service = "sns.amazonaws.com" }
      Action    = "sqs:SendMessage"
      Resource  = aws_sqs_queue.processing.arn
      Condition = {
        ArnEquals = {
          "aws:SourceArn" = aws_sns_topic.file_events.arn
        }
      }
    }]
  })
}

resource "aws_sns_topic_subscription" "sqs" {
  topic_arn = aws_sns_topic.file_events.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.processing.arn
}

resource "aws_lambda_event_source_mapping" "sqs_trigger" {
  event_source_arn = aws_sqs_queue.processing.arn
  function_name    = var.lambda_function_arn
  batch_size       = 10
  enabled          = true

  scaling_config {
    maximum_concurrency = 10
  }
}

