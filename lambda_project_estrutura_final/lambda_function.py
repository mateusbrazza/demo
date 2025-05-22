import logging
from src.step_functions import (
    listar_arquivos,
    processar_arquivos,
    consolidar_arquivos,
    atualizar_consolidado
)

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

step_handlers = {
    "listar_arquivos": listar_arquivos,
    "processar_arquivos": processar_arquivos,
    "consolidar_arquivos": consolidar_arquivos,
    "atualizar_consolidado": atualizar_consolidado,
}

def lambda_handler(event, _context):
    etapa = event.get("etapa")
    if not etapa:
        logger.error("Nenhuma etapa especificada no evento")
        return {"status": "error", "message": "Nenhuma etapa especificada no evento"}

    handler = step_handlers.get(etapa)
    if handler:
        logger.info(f"Executando a etapa: {etapa}")
        return handler(event)

    logger.error(f"Etapa desconhecida: {etapa}")
    return {"status": "error", "message": f"Etapa desconhecida: {etapa}"}
