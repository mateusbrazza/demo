import pytest
from lambda_project.processor import consolidate_payloads
from lambda_project.model import ConsentStockFilePayload, ConsentStockOrganization, ConsentStockInitiator

def test_consolidate_payloads_basic():
    payload = ConsentStockFilePayload(
        totalPF=10,
        totalPJ=5,
        organizations=[
            ConsentStockOrganization(
                id="org1",
                name="Org One",
                initiators=[
                    ConsentStockInitiator(id="init1", name="Initiator One", total=5),
                    ConsentStockInitiator(id="init2", name="Initiator Two", total=5)
                ]
            )
        ]
    )

    result = consolidate_payloads([payload])

    assert result["totalPF"] == 10
    assert result["totalPJ"] == 5
    assert result["organizationCount"] == 1
    assert len(result["organizations"][0]["initiators"]) == 2
    assert result["organizations"][0]["initiators"][0]["total"] == 5
