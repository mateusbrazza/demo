import os
import json
from datetime import datetime, timedelta, timezone
import boto3
from botocore.exceptions import ClientError
from src.model import ConsentStockFilePayload
from src.processor import consolidate_payloads
from src.s3_reader import read_consent_files_from_s3
from src.sns_publisher import publish_to_sns

s3_client = boto3.client('s3')

def get_execution_date():
    return (datetime.now(timezone.utc) - timedelta(days=1)).strftime("%Y-%m-%d")

def listar_arquivos(event):
    execution_date = get_execution_date()
    bucket = os.environ["BUCKET_NAME"]
    prefix = f"consents/{execution_date}/"
    files = []
    try:
        response = s3_client.list_objects_v2(Bucket=bucket, Prefix=prefix)
        files = [obj["Key"] for obj in response.get("Contents", [])]
    except ClientError as e:
        return {"status": "error", "message": str(e)}

    return {"status": "success", "files": files, "execution_date": execution_date}

def processar_arquivos(event):
    bucket = os.environ["BUCKET_NAME"]
    files = event.get("files", [])
    results = []
    for key in files:
        obj = s3_client.get_object(Bucket=bucket, Key=key)
        content = obj["Body"].read().decode("utf-8")
        data = json.loads(content)
        payload = ConsentStockFilePayload(**data)
        results.append(payload)

    return {
        "status": "success",
        "payloads": [r.dict() for r in results],
        "execution_date": event.get("execution_date")
    }

def consolidar_arquivos(event):
    bucket = os.environ["BUCKET_NAME"]
    execution_date = event["execution_date"]
    previous_date = (datetime.strptime(execution_date, "%Y-%m-%d") - timedelta(days=1)).strftime("%Y-%m-%d")
    previous_key = f"consolidated/{previous_date}.json"
    payloads = [ConsentStockFilePayload(**p) for p in event.get("payloads", [])]

    try:
        prev_obj = s3_client.get_object(Bucket=bucket, Key=previous_key)
        prev_content = prev_obj["Body"].read().decode("utf-8")
        prev_payload = ConsentStockFilePayload(**json.loads(prev_content))
        payloads.append(prev_payload)
    except s3_client.exceptions.NoSuchKey:
        pass

    consolidated = consolidate_payloads(payloads)
    return {
        "status": "success",
        "consolidated": consolidated,
        "execution_date": execution_date
    }

def atualizar_consolidado(event):
    bucket = os.environ["BUCKET_NAME"]
    execution_date = event["execution_date"]
    key = f"consolidated/{execution_date}.json"
    data = json.dumps(event["consolidated"])
    s3_client.put_object(Bucket=bucket, Key=key, Body=data.encode("utf-8"))
    return {"status": "success", "message": f"Arquivo consolidado salvo em {key}"}
