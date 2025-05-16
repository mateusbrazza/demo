
from collections import defaultdict

def resumir_arquivos_do_dia(registros):
    ativos = []
    revogados = []

    for r in registros:
        status = r.get("status", "").upper()
        if status == "ATIVO":
            ativos.append(r)
        elif status in ["REVOGADO", "EXPIRADO"]:
            revogados.append(r)

    return gerar_resumo_731(ativos), gerar_resumo_723(revogados)

def gerar_resumo_731(consentimentos):
    por_fase = defaultdict(int)
    por_marca = defaultdict(int)

    for c in consentimentos:
        por_fase[c.get("fase", "DESCONHECIDO")] += 1
        por_marca[c.get("marca", "DESCONHECIDO")] += 1

    return {
        "tipo": "731",
        "total_consentimentos_ativos": len(consentimentos),
        "por_fase": dict(por_fase),
        "por_marca": dict(por_marca)
    }

def gerar_resumo_723(consentimentos):
    motivos = defaultdict(int)
    for c in consentimentos:
        motivos[c.get("status", "DESCONHECIDO").lower()] += 1

    return {
        "tipo": "723",
        "total_consentimentos_retirados": len(consentimentos),
        "motivos": dict(motivos)
    }
