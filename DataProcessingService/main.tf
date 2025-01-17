provider "aws" {
  region = "us-east-1"  # Cambia a tu región preferida
}

# Definir el ARN del rol IAM existente
variable "lambda_role_arn" {
  default = "arn:aws:iam::901526470281:role/LabRole"
}

# Crear un tópico de SNS
resource "aws_sns_topic" "sns_topic" {
  name = "mi_tema_sns"
}

# Crear la función Lambda
resource "aws_lambda_function" "my_lambda" {
  function_name = "mi_lambda_function"
  role          = var.lambda_role_arn   # Usamos el ARN del rol IAM existente
  runtime       = "python3.10"
  handler       = "lambda_function.lambda_handler"
  memory_size   = 256                    # Aumentar la memoria
  timeout       = 120                    # Aumentar el tiempo de ejecución (120 segundos)

  # Subir el código desde el archivo ZIP local
  filename      = "${path.module}/lambda_function.zip"

  # Adjuntar la capa a la Lambda
  layers        = [aws_lambda_layer_version.my_layer.arn]
}

# Crear la capa Lambda
resource "aws_lambda_layer_version" "my_layer" {
  filename         = "${path.module}/my_package.zip"    # Ruta al archivo ZIP de la capa
  layer_name       = "mi_capa_lambda"
  compatible_runtimes = ["python3.10"]             # Define las versiones de Python compatibles
}

# Dar permiso a SNS para invocar la Lambda
resource "aws_lambda_permission" "allow_sns_to_invoke_lambda" {
  statement_id  = "AllowExecutionFromSNS"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.my_lambda.function_name
  principal     = "sns.amazonaws.com"
  source_arn    = aws_sns_topic.sns_topic.arn
}

# Crear la suscripción SNS -> Lambda
resource "aws_sns_topic_subscription" "sns_lambda_subscription" {
  topic_arn = aws_sns_topic.sns_topic.arn
  protocol  = "lambda"
  endpoint  = aws_lambda_function.my_lambda.arn
}
