import os
import json
import boto3
import pytest
from datetime import datetime, timezone, timedelta
from moto import mock_aws

BUCKET_NAME = "fake-consent-bucket"
SNS_TOPIC_ARN = "arn:aws:sns:us-east-1:123456789012:fake-topic"
MOCKS_DIR = os.path.join(os.path.dirname(__file__), "mocks")


@pytest.fixture
def aws_env():
    """Configura variáveis de ambiente para AWS"""
    original_env = dict(os.environ)
    os.environ["BUCKET_NAME"] = BUCKET_NAME
    os.environ["AWS_DEFAULT_REGION"] = "us-east-1"
    os.environ["AWS_ACCESS_KEY_ID"] = "testing"
    os.environ["AWS_SECRET_ACCESS_KEY"] = "testing"
    os.environ["SNS_TOPIC_ARN"] = SNS_TOPIC_ARN

    yield

    os.environ.clear()
    os.environ.update(original_env)


@pytest.fixture
def setup_test_env(aws_env, s3_client, sns_client):
    """Configura todo o ambiente de teste"""
    return {
        "s3": s3_client,
        "sns": sns_client
    }


@pytest.fixture
def s3_client():
    """Cria um cliente S3 mockado"""
    with mock_aws():
        s3 = boto3.client("s3", region_name="us-east-1")
        s3.create_bucket(Bucket=BUCKET_NAME)
        yield s3

@pytest.fixture
def sns_client():
    """Cria um cliente SNS mockado"""
    with mock_aws():
        sns = boto3.client("sns", region_name="us-east-1")
        topic = sns.create_topic(Name="fake-topic")
        os.environ["SNS_TOPIC_ARN"] = topic["TopicArn"]
        yield sns

@pytest.fixture
def mock_date():
    """Retorna uma data fixa para testes"""
    return datetime.now(timezone.utc)

@pytest.fixture
def load_mock_file():
    """Carrega arquivo mock do diretório de mocks"""
    def _load_mock_file(filepath):
        with open(os.path.join(MOCKS_DIR, filepath), 'r') as f:
            return json.load(f)
    return _load_mock_file

@pytest.fixture
def setup_s3_test_files(s3_client, mock_date, load_mock_file):
    """Configura arquivos de teste no S3"""
    def _setup_files(mock_files):
        results = []
        for mock_file in mock_files:
            content = load_mock_file(mock_file['path'])
            key = mock_file['key']
            s3_client.put_object(
                Bucket=BUCKET_NAME,
                Key=key,
                Body=json.dumps(content)
            )
            results.append({"key": key, "content": content})
        return results
    return _setup_files

@pytest.fixture
def get_test_date():
    """Retorna datas de teste (hoje e ontem)"""
    def _get_dates():
        today = datetime.now(timezone.utc)
        yesterday = today - timedelta(days=1)
        return {
            "today": today,
            "today_str": today.strftime("%Y-%m-%d"),
            "yesterday": yesterday,
            "yesterday_str": yesterday.strftime("%Y-%m-%d")
        }
    return _get_dates