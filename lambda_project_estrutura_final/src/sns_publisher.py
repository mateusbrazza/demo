import json
import os

def publish_to_sns(sns_client, message):
    """
    Publica mensagem no SNS
    """
    topic_arn = os.environ.get("SNS_TOPIC_ARN")
    if not topic_arn:
        raise ValueError("SNS_TOPIC_ARN não definido nas variáveis de ambiente")

    response = sns_client.publish(
        TopicArn=topic_arn,
        Message=json.dumps(message),
        Subject="Consentimento Resumido"
    )
    return response['MessageId']