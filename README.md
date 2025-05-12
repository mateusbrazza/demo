✅ POST /consents – Criação de consentimento
🔵 FASE 2
🌐 Sucesso
 RESOURCES_READ + permissão de pessoa física (ex: CUSTOMERS_PERSONAL_IDENTIFICATIONS_READ)

 RESOURCES_READ + permissão de pessoa jurídica com businessEntity (ex: CUSTOMERS_BUSINESS_IDENTIFICATIONS_READ)

 Permissões múltiplas da mesma categoria (ex: CUSTOMERS_PERSONAL_IDENTIFICATIONS_READ, CUSTOMERS_PERSONAL_ADDITIONALINFO_READ)

 Permissões parcialmente suportadas → 201 com subconjunto aceito

❌ Falhas
 Apenas RESOURCES_READ → 400

 Permissão de PJ sem businessEntity → 400 ou 422

 Mistura Fase 2 + Fase 3 → 422

 Pessoa jurídica com CPF → 422

 Permissão de PJ sem RESOURCES_READ → 400

 Permissões de PF com businessEntity → 422

 loggedUser ausente ou rel ≠ CPF → 400

 expirationDateTime mal formatado → 400

🟣 FASE 3
🌐 Sucesso
 Agrupamento completo de uma permissão Fase 3 (ex: ENDORSEMENT_REQUEST_CREATE com endorsementInformation)

 Permissões parcialmente suportadas → 201 com subset funcional

 Consentimento de resgate com objeto obrigatório (withdrawalLifePensionInformation, etc.)

❌ Falhas
 Agrupamentos múltiplos de Fase 3 → 422

 Permissões de Fase 3 sem permissão obrigatória do agrupamento → 400

 Permissão Fase 3 sem seu objeto obrigatório → 422

 Consentimento Fase 3 para si mesmo (SPOC) → 422

 Permissões de Fase 3 com agrupamento incompleto (ex: só QUOTE_AUTO_LEAD_UPDATE) → 400

 withdrawalReasonOthers ausente com withdrawalReason = OUTROS → 400

 desiredTotalAmount ausente com withdrawalType = 2_PARCIAL → 400

🟢 COMUM A AMBAS
❌ Falhas
 Falta de campo obrigatório (expirationDateTime, permissions, loggedUser) → 400

 Permissões inexistentes → 400

 businessEntity ausente quando exigido → 400 ou 422

 Idempotency com divergência de payload → 422

✅ GET /consents/{consentId} – Consulta de consentimento
🌐 Sucesso
 Consulta de status AWAITING_AUTHORISATION

 Consulta de status AUTHORISED, CONSUMED, REJECTED, REVOKED

 Consentimento expirado → status REJECTED, motivo CONSENT_EXPIRED

 Consentimento revogado → status REVOKED, motivo CONSENT_MAX_DATE_REACHED

❌ Falhas
 Consentimento inexistente → 404

 Token ausente ou inválido → 401 ou 403

 Consulta após expiração → status REJECTED com motivo

 Consulta após revogação → status REVOKED com motivo

 Campo rejection ausente em status REJECTED ou REVOKED → inválido

 Permissões não suportadas → não devem aparecer na resposta

✅ DELETE /consents/{consentId} – Revogação de consentimento
🌐 Sucesso
 Consentimento AUTHORISED → status REVOKED

 Consentimento AWAITING_AUTHORISATION → status REJECTED, motivo CUSTOMER_MANUALLY_REJECTED

❌ Falhas
 Consentimento REVOKED, REJECTED, CONSUMED → 422

 Consentimento inexistente → 404

 Token inválido ou ausente → 401 ou 403

 Revogação de consentimento expirado → status já é REJECTED, não permite DELETE

 Header x-fapi-interaction-id ausente → 400

✅ Extras para todos os métodos
 Headers obrigatórios presentes (Authorization, x-fapi-interaction-id, x-v, x-min-v, etc.)

 Campos de resposta: data, links, meta presentes e válidos

 Datas em formato RFC3339 + Z (UTC)

💡 Agora sim, com essa divisão por método e fase, você tem um mapeamento completo e auditável com todos os casos que a SUSEP exige ou implica.
Q1: Quais campos da resposta devem ser validados em cada status (AWAITING_AUTHORISATION, REJECTED, REVOKED)?
→ Para cada status, os campos obrigatórios mudam:

REJECTED e REVOKED devem conter rejection com rejectedBy e reason.code.

AWAITING_AUTHORISATION não deve ter rejection.

Campos como permissions, creationDateTime, status, expirationDateTime são sempre obrigatórios.

Q2: O que acontece se eu enviar permissões duplicadas no POST? A SUSEP trata isso?
→ Não há menção direta na documentação, mas a prática correta é:

Remover duplicatas no backend.

Responder apenas com o conjunto distinto de permissões.

Ideal incluir um teste que envia permissões duplicadas e validar se a resposta é normalizada.

Q3: É permitido atualizar um consentimento após criado? Existe PATCH/PUT?
→ Não. A SUSEP não define nenhum endpoint de atualização de consentimento.

Consentimentos são imutáveis após criados.

Qualquer mudança requer revogação e criação de novo consentimento.

Tentar implementar PATCH ou PUT seria fora do escopo da regulação e deve ser evitado.
