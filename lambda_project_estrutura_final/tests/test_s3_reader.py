import pytest
from src.s3_reader import read_consent_files_from_s3
from src.model import ConsentStockFilePayload
from tests.conftest import BUCKET_NAME


class TestS3Reader:
    """Testes para o módulo S3 Reader"""

    def test_read_consent_files_success(self, s3_client, setup_s3_test_files, get_test_date):
        """Testa leitura bem sucedida de arquivos do S3"""
        dates = get_test_date()
        prefix = f"consents/{dates['today_str']}/"

        setup_s3_test_files([
            {'path': 'daily_files/file_1.json', 'key': f"{prefix}valid.json"}
        ])

        result = read_consent_files_from_s3(s3_client, BUCKET_NAME, prefix)

        assert len(result) == 1
        assert isinstance(result[0], ConsentStockFilePayload)
        assert result[0].totalPF == 100
        assert result[0].totalPJ == 50

    def test_read_consent_files_multiple(self, s3_client, setup_s3_test_files, get_test_date):
        """Testa leitura de múltiplos arquivos"""
        dates = get_test_date()
        prefix = f"consents/{dates['today_str']}/"

        setup_s3_test_files([
            {'path': 'daily_files/file_1.json', 'key': f"{prefix}file_1.json"},
            {'path': 'daily_files/file_2.json', 'key': f"{prefix}file_2.json"},
            {'path': 'daily_files/file_3.json', 'key': f"{prefix}file_3.json"}
        ])

        result = read_consent_files_from_s3(s3_client, BUCKET_NAME, prefix)

        assert len(result) == 3
        assert sum(obj.totalPF for obj in result) == 450
        assert sum(obj.totalPJ for obj in result) == 225

    def test_read_consent_files_empty_bucket(self, s3_client):
        """Testa quando não há arquivos no bucket"""
        result = read_consent_files_from_s3(s3_client, BUCKET_NAME, "consents/non-existent/")
        assert result == []

    def test_read_consent_files_invalid_json(self, s3_client, get_test_date):
        """Testa quando há arquivo com JSON inválido"""
        dates = get_test_date()
        prefix = f"consents/{dates['today_str']}/"

        # Em vez de usar setup_s3_test_files, vamos colocar o conteúdo inválido diretamente
        s3_client.put_object(
            Bucket=BUCKET_NAME,
            Key=f"{prefix}invalid.json",
            Body="{invalid json content"
        )

        result = read_consent_files_from_s3(s3_client, BUCKET_NAME, prefix)
        assert result == []

    def test_read_consent_files_invalid_schema(self, s3_client, setup_s3_test_files, get_test_date):
        """Testa quando há arquivo com schema inválido"""
        dates = get_test_date()
        prefix = f"consents/{dates['today_str']}/"

        setup_s3_test_files([
            {'path': 'invalid/invalid_schema.json', 'key': f"{prefix}invalid_schema.json"}
        ])

        result = read_consent_files_from_s3(s3_client, BUCKET_NAME, prefix)
        assert result == []