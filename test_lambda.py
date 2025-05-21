
from src.lambda_function import lambda_handler

def test_lambda_step_listar():
    event = {"etapa": "listar_arquivos", "bucket": "fake-bucket"}
    result = lambda_handler(event, {})
    assert "status" in result

def test_lambda_step_desconhecida():
    event = {"etapa": "etapa_invalida"}
    result = lambda_handler(event, {})
    assert result["status"] == "error"
