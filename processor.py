
import logging
import boto3
import json

logger = logging.getLogger(__name__)
s3 = boto3.client("s3")

def processar_arquivos(event):
    bucket = event["bucket"]
    keys = event.get("files", [])
    payloads = []

    for key in keys:
        obj = s3.get_object(Bucket=bucket, Key=key)
        content = obj["Body"].read().decode("utf-8")
        payload = json.loads(content)
        payloads.append(payload)

    logger.info(f"{len(payloads)} arquivos processados.")
    return {"status": "success", "payloads": payloads}
