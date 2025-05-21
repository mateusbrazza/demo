import os
import json
import boto3
from datetime import datetime, timedelta, timezone
from lambda_project.s3_reader import read_consent_files_from_s3
from lambda_project.processor import consolidate_payloads
from lambda_project.sns_publisher import publish_to_sns
s3_client = boto3.client('s3')

def lambda_handler(event, context):
    print("Lambda acionada pelo EventBridge")

    execution_date = (datetime.now(timezone.utc) - timedelta(days=1)).strftime("%Y-%m-%d")
    bucket = os.environ.get("BUCKET_NAME")
    prefix = f"consents/{execution_date}/"

    print(f"Buscando arquivos em: s3://{bucket}/{prefix}")

    consent_payloads = read_consent_files_from_s3(s3_client, bucket, prefix)

    if not consent_payloads:
        print("Nenhum arquivo encontrado para processar.")
        return {"status": "empty", "execution_date": execution_date}

    summary = consolidate_payloads(consent_payloads)

    print("Resumo consolidado:", summary)

    publish_to_sns(summary)

    return {
        "status": "success",
        "execution_date": execution_date,
        "file_count": len(consent_payloads)
    }
