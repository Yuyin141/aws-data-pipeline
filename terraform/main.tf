module "networking" {
  source = "./modules/networking"

  project_name       = var.project_name
  environment        = var.environment
  vpc_cidr           = var.vpc_cidr
  availability_zones = var.availability_zones
}

module "storage" {
  source = "./modules/storage"

  project_name = var.project_name
  environment  = var.environment
}

module "database" {
  source = "./modules/database"

  project_name         = var.project_name
  environment          = var.environment
  vpc_id               = module.networking.vpc_id
  private_subnet_ids   = module.networking.private_subnet_ids
  db_instance_class    = var.db_instance_class
  db_name              = var.db_name
  db_username          = var.db_username
  lambda_sg_id         = module.serverless.lambda_security_group_id
}

module "messaging" {
  source = "./modules/messaging"

  project_name        = var.project_name
  environment         = var.environment
  lambda_function_arn = module.serverless.lambda_function_arn
}

module "serverless" {
  source = "./modules/serverless"

  project_name          = var.project_name
  environment           = var.environment
  vpc_id                = module.networking.vpc_id
  private_subnet_ids    = module.networking.private_subnet_ids
  lambda_memory_size    = var.lambda_memory_size
  lambda_timeout        = var.lambda_timeout
  s3_bucket_raw         = module.storage.bucket_raw_name
  s3_bucket_processed   = module.storage.bucket_processed_name
  rds_proxy_endpoint    = module.database.rds_proxy_endpoint
  db_name               = var.db_name
  sqs_queue_arn         = module.messaging.sqs_queue_arn
}

module "compute" {
  source = "./modules/compute"

  project_name        = var.project_name
  environment         = var.environment
  vpc_id              = module.networking.vpc_id
  public_subnet_ids   = module.networking.public_subnet_ids
  private_subnet_ids  = module.networking.private_subnet_ids
  ec2_instance_type   = var.ec2_instance_type
  s3_bucket_raw       = module.storage.bucket_raw_name
  s3_bucket_processed = module.storage.bucket_processed_name
  sns_topic_arn       = module.messaging.sns_topic_arn
  rds_endpoint        = module.database.rds_endpoint
  db_name             = var.db_name
  db_username         = var.db_username
}

