import os
import json
import boto3

sns_client = boto3.client('sns')

def publish_to_sns(summary):
    topic_arn = os.environ.get("SNS_TOPIC_ARN")
    if not topic_arn:
        raise ValueError("SNS_TOPIC_ARN não definido nas variáveis de ambiente")

    response = sns_client.publish(
        TopicArn=topic_arn,
        Message=json.dumps(summary),
        Subject="Consentimento Resumido",
    )

    print("Mensagem publicada no SNS:", response['MessageId'])
