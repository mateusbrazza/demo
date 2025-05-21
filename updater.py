
import logging
import boto3
import json
from datetime import datetime, timezone

logger = logging.getLogger(__name__)
s3 = boto3.client("s3")

def atualizar_consolidado(event):
    bucket = event["bucket"]
    summary = event["summary"]
    today = datetime.now(timezone.utc).strftime("%Y-%m-%d")
    key = f"consolidated/{today}/summary.json"

    s3.put_object(
        Bucket=bucket,
        Key=key,
        Body=json.dumps(summary).encode("utf-8")
    )
    logger.info(f"Arquivo consolidado salvo em: s3://{bucket}/{key}")
    return {"status": "success", "message": "Arquivo consolidado salvo com sucesso", "key": key}
