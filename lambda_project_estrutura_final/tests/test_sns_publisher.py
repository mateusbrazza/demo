import os
import pytest
from src.sns_publisher import publish_to_sns


class TestSNSPublisher:
    """Testes para o módulo SNS Publisher"""

    def test_publish_success(self, setup_test_env):
        """Testa publicação bem sucedida no SNS"""
        summary = {
            "totalPF": 100,
            "totalPJ": 50,
            "organizations": [
                {
                    "id": "org1",
                    "name": "Banco A",
                    "initiators": [
                        {"id": "init1", "name": "App Mobile", "total": 60}
                    ]
                }
            ]
        }

        message_id = publish_to_sns(setup_test_env["sns"], summary)
        assert message_id is not None
        assert isinstance(message_id, str)

    def test_publish_missing_topic_arn(self, setup_test_env):
        """Testa erro quando SNS_TOPIC_ARN não está definido"""
        if "SNS_TOPIC_ARN" in os.environ:
            del os.environ["SNS_TOPIC_ARN"]

        with pytest.raises(ValueError) as exc_info:
            publish_to_sns(setup_test_env["sns"], {"test": "data"})

        assert "SNS_TOPIC_ARN não definido" in str(exc_info.value)

    def test_publish_invalid_topic(self, setup_test_env):
        """Testa erro quando o tópico SNS é inválido"""
        os.environ["SNS_TOPIC_ARN"] = "arn:aws:sns:us-east-1:123456789012:non-existent-topic"

        with pytest.raises(Exception):  # Ajustado para capturar qualquer exceção do SNS
            publish_to_sns(setup_test_env["sns"], {"test": "data"})

    def test_publish_invalid_message(self, setup_test_env):
        """Testa erro quando a mensagem é inválida"""

        class UnserializableObject:
            pass

        with pytest.raises(TypeError):
            publish_to_sns(setup_test_env["sns"], {"invalid": UnserializableObject()})