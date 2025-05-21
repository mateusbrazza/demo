import pytest
from lambda_project.model import ConsentStockFilePayload, ConsentStockOrganization, ConsentStockInitiator
from pydantic import ValidationError

def test_valid_payload():
    data = {
        "totalPF": 100,
        "totalPJ": 50,
        "organizations": [
            {
                "id": "org123",
                "name": "Bank XPTO",
                "initiators": [
                    {"id": "init1", "name": "App1", "total": 30},
                    {"id": "init2", "name": "App2", "total": 70}
                ]
            }
        ]
    }

    payload = ConsentStockFilePayload(**data)
    assert payload.totalPF == 100
    assert payload.organizations[0].initiators[0].name == "App1"

def test_invalid_payload_missing_fields():
    data = {
        "totalPF": 100,
        "organizations": []
    }
    with pytest.raises(ValidationError):
        ConsentStockFilePayload(**data)
