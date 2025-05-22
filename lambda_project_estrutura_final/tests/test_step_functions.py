import json
import os
from datetime import datetime, timedelta

from src.step_functions import listar_arquivos, processar_arquivos, consolidar_arquivos, atualizar_consolidado


class TestStepFunctions:
    """Testes para as Step Functions"""

    def test_step_list_files(self, setup_test_env, setup_s3_test_files):
        """Testa etapa de listagem de arquivos"""
        # Primeiro, vamos fazer uma chamada para descobrir qual data a função está usando
        initial_response = listar_arquivos({})
        execution_date = initial_response["execution_date"]

        # Agora configuramos os arquivos de teste usando a data que a função está esperando
        setup_s3_test_files([
            {'path': 'daily_files/file_1.json', 'key': f"consents/{execution_date}/file_1.json"},
            {'path': 'daily_files/file_2.json', 'key': f"consents/{execution_date}/file_2.json"}
        ])

        # Executamos a função novamente agora que os arquivos existem
        response = listar_arquivos({})

        # Verificações
        assert response["status"] == "success"
        assert len(response["files"]) == 2
        assert all(f"consents/{execution_date}" in f for f in response["files"])

    def test_step_process_files(self, setup_test_env, setup_s3_test_files, get_test_date):
        """Testa etapa de processamento de arquivos"""
        dates = get_test_date()

        # Setup dos arquivos
        files = setup_s3_test_files([
            {'path': 'daily_files/file_1.json', 'key': f"consents/{dates['today_str']}/file_1.json"},
            {'path': 'daily_files/file_2.json', 'key': f"consents/{dates['today_str']}/file_2.json"}
        ])

        # Executa processamento
        response = processar_arquivos({
            "files": [f["key"] for f in files],
            "execution_date": dates['today_str']
        })

        # Verificações
        assert response["status"] == "success"
        assert len(response["payloads"]) == 2
        assert all(p.get("totalPF") is not None for p in response["payloads"])

    def test_step_consolidate_files(self, setup_test_env, setup_s3_test_files, get_test_date):
        """Testa etapa de consolidação"""
        dates = get_test_date()

        # Setup dos arquivos diários e consolidado anterior
        setup_s3_test_files([
            {'path': 'consolidated/previous_consolidated.json',
             'key': f"consolidated/{dates['yesterday_str']}.json"}
        ])

        # Simula payloads processados
        payloads = [
            {
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
        ]

        # Executa consolidação
        response = consolidar_arquivos({
            "payloads": payloads,
            "execution_date": dates['today_str']
        })

        # Verificações
        assert response["status"] == "success"
        assert response["consolidated"] is not None
        assert response["consolidated"]["totalPF"] > 0

    def test_step_update_consolidated(self, setup_test_env, get_test_date):
        """Testa etapa de atualização do consolidado"""
        dates = get_test_date()

        consolidated = {
            "totalPF": 1250,
            "totalPJ": 625,
            "organizations": [
                {
                    "id": "org1",
                    "name": "Banco A",
                    "initiators": [
                        {"id": "init1", "name": "App Mobile", "total": 440}
                    ]
                }
            ]
        }

        response = atualizar_consolidado({
            "consolidated": consolidated,
            "execution_date": dates['today_str']
        })

        assert response["status"] == "success"
        assert "message" in response

    def test_integration_full_flow(self, setup_test_env, setup_s3_test_files):
        """
        Teste de integração que simula o fluxo completo:
        1. Lista arquivos
        2. Processa arquivos
        3. Consolida dados
        4. Atualiza arquivo consolidado
        """
        # Primeira etapa: Listar arquivos
        initial_response = listar_arquivos({})
        execution_date = initial_response["execution_date"]
        yesterday = (datetime.strptime(execution_date, "%Y-%m-%d") - timedelta(days=1)).strftime("%Y-%m-%d")

        # Configura os arquivos de teste
        setup_s3_test_files([
            {
                'path': 'daily_files/file_1.json',
                'key': f"consents/{execution_date}/file_1.json"
            },
            {
                'path': 'daily_files/file_2.json',
                'key': f"consents/{execution_date}/file_2.json"
            },
            {
                'path': 'consolidated/previous_consolidated.json',
                'key': f"consolidated/{yesterday}.json"
            }
        ])

        # 1. Lista arquivos
        list_response = listar_arquivos({})
        assert list_response["status"] == "success"
        assert len(list_response["files"]) == 2

        # 2. Processa arquivos
        process_response = processar_arquivos({
            "files": list_response["files"],
            "execution_date": execution_date
        })
        assert process_response["status"] == "success"
        assert len(process_response["payloads"]) == 2

        # Verifica a soma dos arquivos do dia
        assert sum(p["totalPF"] for p in process_response["payloads"]) == 250, "Soma do dia para PF incorreta"
        assert sum(p["totalPJ"] for p in process_response["payloads"]) == 125, "Soma do dia para PJ incorreta"

        # 3. Consolida arquivos
        consolidate_response = consolidar_arquivos({
            "payloads": process_response["payloads"],
            "execution_date": execution_date
        })
        assert consolidate_response["status"] == "success"
        assert consolidate_response["consolidated"] is not None

        # Validações detalhadas do consolidado
        consolidated = consolidate_response["consolidated"]
        assert consolidated["totalPF"] == 1250, "Total PF consolidado deve ser 1000 (anterior) + 250 (dia)"
        assert consolidated["totalPJ"] == 625, "Total PJ consolidado deve ser 500 (anterior) + 125 (dia)"

        # Validação das organizações e iniciadores
        # Banco A (org1)
        org1 = next(org for org in consolidated["organizations"] if org["id"] == "org1")
        init1 = next(init for init in org1["initiators"] if init["id"] == "init1")
        init2 = next(init for init in org1["initiators"] if init["id"] == "init2")
        assert init1["total"] == 440, "Total do init1 deve ser 300 (anterior) + 60 + 80 (dia)"
        assert init2["total"] == 310, "Total do init2 deve ser 200 (anterior) + 40 + 70 (dia)"

        # Banco B (org2)
        org2 = next(org for org in consolidated["organizations"] if org["id"] == "org2")
        init3 = next(init for init in org2["initiators"] if init["id"] == "init3")
        assert init3["total"] == 550, "Total do init3 deve ser 500 (anterior) + 50 (dia)"

        # Banco C (org3)
        org3 = next(org for org in consolidated["organizations"] if org["id"] == "org3")
        init4 = next(init for init in org3["initiators"] if init["id"] == "init4")
        assert init4["total"] == 75, "Total do init4 deve ser 75 (somente do dia)"

        # 4. Atualiza consolidado
        update_response = atualizar_consolidado({
            "consolidated": consolidate_response["consolidated"],
            "execution_date": execution_date
        })
        assert update_response["status"] == "success"

        # Validação final: Verifica se o arquivo foi salvo no S3
        s3_client = setup_test_env['s3']
        try:
            final_obj = s3_client.get_object(
                Bucket=os.environ["BUCKET_NAME"],
                Key=f"consolidated/{execution_date}.json"
            )
            final_content = json.loads(final_obj["Body"].read().decode("utf-8"))
            assert isinstance(final_content, dict)
            assert final_content["totalPF"] == 1250, "Arquivo final com valor PF incorreto"
            assert final_content["totalPJ"] == 625, "Arquivo final com valor PJ incorreto"

            # Validação final das organizations no arquivo salvo
            org1_final = next(org for org in final_content["organizations"] if org["id"] == "org1")
            assert next(i["total"] for i in org1_final["initiators"] if i["id"] == "init1") == 440
            assert next(i["total"] for i in org1_final["initiators"] if i["id"] == "init2") == 310

            org2_final = next(org for org in final_content["organizations"] if org["id"] == "org2")
            assert next(i["total"] for i in org2_final["initiators"] if i["id"] == "init3") == 550

            org3_final = next(org for org in final_content["organizations"] if org["id"] == "org3")
            assert next(i["total"] for i in org3_final["initiators"] if i["id"] == "init4") == 75

        except Exception as e:
            raise Exception(f"Falha ao verificar arquivo consolidado final: {str(e)}")