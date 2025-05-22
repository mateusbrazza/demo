def consolidate_payloads(payloads):
    total_pf = 0
    total_pj = 0
    organization_map = {}

    for payload in payloads:
        total_pf += payload.totalPF
        total_pj += payload.totalPJ

        for org in payload.organizations:
            if org.id not in organization_map:
                organization_map[org.id] = {
                    "id": org.id,
                    "name": org.name,
                    "initiators": {}
                }

            for init in org.initiators:
                if init.id not in organization_map[org.id]["initiators"]:
                    organization_map[org.id]["initiators"][init.id] = {
                        "id": init.id,
                        "name": init.name,
                        "total": 0
                    }
                organization_map[org.id]["initiators"][init.id]["total"] += init.total

    summarized_orgs = []
    for org in organization_map.values():
        summarized_orgs.append({
            "id": org["id"],
            "name": org["name"],
            "initiators": list(org["initiators"].values())
        })

    return {
        "totalPF": total_pf,
        "totalPJ": total_pj,
        "organizationCount": len(summarized_orgs),
        "organizations": summarized_orgs
    }