
from collections import defaultdict

def processar_evento(consentimentos):
    resumo_731 = []
    resumo_723 = []

    for c in consentimentos:
        status = c.get("status", "").upper()
        if status == "ATIVO":
            resumo_731.append(c)
        elif status in ("REVOGADO", "EXPIRADO"):
            resumo_723.append(c)

    return {
        "informe_731": {
            "total": len(resumo_731),
            "detalhes": resumo_731
        },
        "informe_723": {
            "total": len(resumo_723),
            "detalhes": resumo_723
        }
    }
