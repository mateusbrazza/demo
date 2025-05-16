
import json
import boto3
import datetime
from utils.relatorios import processar_evento

s3 = boto3.client('s3')
BUCKET_DESTINO = "seu-bucket-openfinance-relatorios"  # Altere para o nome real

def lambda_handler(event, context):
    try:
        registros = [json.loads(r['Sns']['Message']) for r in event['Records']]
        data_referencia = datetime.datetime.utcnow().strftime('%Y-%m-%d')

        relatorios = processar_evento(registros)

        for tipo, conteudo in relatorios.items():
            nome_arquivo = f"{tipo}_{data_referencia}.json"
            caminho = f"{data_referencia}/{nome_arquivo}"
            s3.put_object(
                Bucket=BUCKET_DESTINO,
                Key=caminho,
                Body=json.dumps(conteudo).encode("utf-8")
            )

        return {
            "statusCode": 200,
            "body": json.dumps({"mensagem": "Relatórios gerados e salvos no S3."})
        }
    except Exception as e:
        return {
            "statusCode": 500,
            "body": json.dumps({"erro": str(e)})
        }
