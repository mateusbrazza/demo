
import logging
from src.s3_reader import listar_arquivos
from src.processor import processar_arquivos
from src.consolidator import consolidar_arquivos
from src.updater import atualizar_consolidado

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
    logger.info(f"Executando a etapa: {etapa}")

    if not etapa:
        logger.error("Nenhuma etapa especificada no evento")
        return {"status": "error", "message": "Nenhuma etapa especificada no evento"}

    handler = step_handlers.get(etapa)
    if not handler:
        logger.error(f"Etapa desconhecida: {etapa}")
        return {"status": "error", "message": f"Etapa desconhecida: {etapa}"}

    return handler(event)
