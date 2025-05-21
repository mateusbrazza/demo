
import logging
import boto3
from datetime import datetime, timezone, timedelta

logger = logging.getLogger(__name__)
s3 = boto3.client("s3")

def listar_arquivos(event):
    execution_date = (datetime.now(timezone.utc) - timedelta(days=1)).strftime("%Y-%m-%d")
    bucket = event["bucket"]
    prefix = f"consents/{execution_date}/"

    logger.info(f"Listando arquivos em: s3://{bucket}/{prefix}")
    response = s3.list_objects_v2(Bucket=bucket, Prefix=prefix)
    keys = [item["Key"] for item in response.get("Contents", [])]

    logger.info(f"Arquivos encontrados: {keys}")
    return {"status": "success", "files": keys, "execution_date": execution_date}
