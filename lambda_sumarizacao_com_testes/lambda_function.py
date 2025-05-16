
import boto3
import json
import os
from datetime import datetime
from utils.processor import resumir_arquivos_do_dia

s3 = boto3.client('s3')
sns = boto3.client('sns')

BUCKET_NAME = os.environ.get('BUCKET_RELATORIO')
SNS_TOPIC_ARN = os.environ.get('SNS_TOPIC_ARN')

def lambda_handler(event, context):
    hoje = datetime.utcnow().strftime('%Y-%m-%d')
    prefixo = f"consentimentos/{hoje}/"
    
    arquivos = s3.list_objects_v2(Bucket=BUCKET_NAME, Prefix=prefixo)
    chaves = [item['Key'] for item in arquivos.get('Contents', []) if item['Key'].endswith('.json')]

    registros = []
    for chave in chaves:
        obj = s3.get_object(Bucket=BUCKET_NAME, Key=chave)
        conteudo = json.loads(obj['Body'].read())
        registros.extend(conteudo)

    resumo_731, resumo_723 = resumir_arquivos_do_dia(registros)

    sns.publish(
        TopicArn=SNS_TOPIC_ARN,
        Message=json.dumps(resumo_731),
        Subject="Resumo 731"
    )
    sns.publish(
        TopicArn=SNS_TOPIC_ARN,
        Message=json.dumps(resumo_723),
        Subject="Resumo 723"
    )

    return {
        "statusCode": 200,
        "body": json.dumps({
            "arquivos_processados": len(chaves),
            "total_registros": len(registros)
        })
    }
