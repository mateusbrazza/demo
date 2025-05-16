
import json
import os
import boto3
from moto import mock_s3, mock_sns
import pytest
from datetime import datetime
from lambda_function import lambda_handler

@pytest.fixture
def setup_env(monkeypatch):
    monkeypatch.setenv("BUCKET_RELATORIO", "bucket-relatorios-openfinance")
    monkeypatch.setenv("SNS_TOPIC_ARN", "arn:aws:sns:us-east-1:123456789012:relatorio-sns")

@mock_s3
@mock_sns
def test_lambda_processa_arquivos_s3(setup_env):
    s3 = boto3.client("s3", region_name="us-east-1")
    sns = boto3.client("sns", region_name="us-east-1")

    bucket = "bucket-relatorios-openfinance"
    s3.create_bucket(Bucket=bucket)
    topic = sns.create_topic(Name="relatorio-sns")

    hoje = datetime.utcnow().strftime('%Y-%m-%d')
    key = f"consentimentos/{hoje}/consentimentos1.json"

    consentimentos = [
        {"id": "1", "status": "ATIVO", "fase": "FASE_2", "marca": "ITAU"},
        {"id": "2", "status": "REVOGADO", "fase": "FASE_2", "marca": "BRADESCO"},
        {"id": "3", "status": "EXPIRADO", "fase": "FASE_3", "marca": "SANTANDER"},
        {"id": "4", "status": "ATIVO", "fase": "FASE_3", "marca": "ITAU"}
    ]

    s3.put_object(
        Bucket=bucket,
        Key=key,
        Body=json.dumps(consentimentos)
    )

    event = {}  # EventBridge cron event (vazio)

    response = lambda_handler(event, None)
    assert response["statusCode"] == 200
    body = json.loads(response["body"])
    assert body["arquivos_processados"] == 1
    assert body["total_registros"] == 4
