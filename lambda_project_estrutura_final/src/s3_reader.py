import json
from src.model import ConsentStockFilePayload
from botocore.exceptions import ClientError

def read_consent_files_from_s3(s3_client, bucket, prefix):
    objects = []
    try:
        response = s3_client.list_objects_v2(Bucket=bucket, Prefix=prefix)
        if "Contents" not in response:
            return []

        for obj in response["Contents"]:
            key = obj["Key"]
            file_obj = s3_client.get_object(Bucket=bucket, Key=key)
            file_content = file_obj['Body'].read().decode('utf-8')
            json_data = json.loads(file_content)

            parsed = ConsentStockFilePayload(**json_data)
            objects.append(parsed)

    except ClientError as e:
        print(f"Erro ao acessar S3: {e}")
    except Exception as e:
        print(f"Erro ao processar JSON: {e}")

    return objects