
import logging

logger = logging.getLogger(__name__)

def consolidar_arquivos(event):
    payloads = event.get("payloads", [])
    resumo = {}

    for payload in payloads:
        for org in payload.get("organizations", []):
            org_id = org["id"]
            total = org.get("totalPF", 0)
            resumo[org_id] = resumo.get(org_id, 0) + total

    logger.info(f"Resumo consolidado: {resumo}")
    return {"status": "success", "summary": resumo}
