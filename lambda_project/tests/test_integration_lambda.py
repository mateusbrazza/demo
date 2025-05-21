import json
import os
import boto3
import pytest
from datetime import datetime, timedelta, timezone
from moto import mock_s3, mock_sns


from lambda_project.handler import lambda_handler

BUCKET_NAME = "fake-consent-bucket"
TOPIC_NAME = "fake-sns-topic"

@pytest.fixture
def aws_env():
    os.environ["BUCKET_NAME"] = BUCKET_NAME
    os.environ["SNS_TOPIC_ARN"] = f"arn:aws:sns:us-east-1:123456789012:{TOPIC_NAME}"

@mock_s3
@mock_sns
def test_lambda_handler_with_moto(aws_env):
    # Criar mocks
    s3 = boto3.client("s3", region_name="us-east-1")
    sns = boto3.client("sns", region_name="us-east-1")

    # Criar bucket S3 e tópico SNS
    s3.create_bucket(Bucket=BUCKET_NAME)
    sns.create_topic(Name=TOPIC_NAME)

    # Upload de arquivo de teste no S3
    test_date = (datetime.now(timezone.utc) - timedelta(days=1)).strftime("%Y-%m-%d")
    prefix = f"consents/{test_date}/"
    key = f"{prefix}arquivo1.json"

    payload = {
        "totalPF": 10,
        "totalPJ": 20,
        "organizations": [
            {
                "id": "org1",
                "name": "Org One",
                "initiators": [
                    {"id": "init1", "name": "Plataforma A", "total": 10}
                ]
            }
        ]
    }

    s3.put_object(
        Bucket=BUCKET_NAME,
        Key=key,
        Body=json.dumps(payload)
    )

    # Executar Lambda localmente
    response = lambda_handler({}, {})
    assert response["status"] == "success"
    assert response["file_count"] == 1
