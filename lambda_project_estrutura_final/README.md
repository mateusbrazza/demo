# Lambda de Consolidação de Consentimentos
## Descrição
Lambda responsável por consolidar arquivos de consentimento, processando dados diários e gerando um arquivo consolidado.
## Arquitetura
### Componentes AWS
- **S3**: Armazenamento dos arquivos de consentimento
- **Lambda**: Processamento e consolidação
- **SNS**: Notificação de eventos e resultados

### Estrutura de Diretórios no S3
``` plaintext
bucket-name/
├── consents/
│   └── YYYY-MM-DD/
│       ├── file_1.json
│       └── file_2.json
└── consolidated/
    └── YYYY-MM-DD.json
```
## Fluxo de Funcionamento
1. **Leitura de Arquivos**
    - Lambda é acionada
    - Lê arquivos do diretório do S3 `consents/YYYY-MM-DD/`
    - Processa cada arquivo individualmente

2. **Consolidação**
    - Unifica os dados dos arquivos
    - Aplica validações necessárias
    - Gera arquivo consolidado

3. **Resultado**
    - Salva arquivo consolidado em `consolidated/YYYY-MM-DD.json`
    - Publica mensagem no SNS informando conclusão

### Formato das Mensagens SNS
#### Sucesso
``` json
{
   
}
```

## Configuração
### Variáveis de Ambiente

| Variável | Descrição | Exemplo |
| --- | --- | --- |
| `BUCKET_NAME` | Nome do bucket S3 | `my-consents-bucket` |
| `ENVIRONMENT` | Ambiente de execução | `development`, `staging`, `production` |
| `SNS_TOPIC_ARN` | ARN do tópico SNS | `arn:aws:sns:region:account:topic` |


