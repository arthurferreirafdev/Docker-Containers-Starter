# 🐳 Docker Container Manager API

API para gerenciar dinamicamente containers Docker, com funcionalidades de criação, remoção e monitoramento de instâncias.

---

## 📦 Visão Geral do Projeto

- **Framework:** Spring Boot
- **Funcionalidade:**  
  ➝ Criação dinâmica de containers Docker  
  ➝ Remoção de containers ativos  
  ➝ Gerenciamento de portas para evitar conflitos  
  ➝ Armazenamento de logs de containers iniciados e erros
- **Arquitetura:**
    - Controller: `HttpController`
    - Service: `DockerContainerManager`
    - Models: `Container`, `InstanciaMonitoramento`, `GeneralData`

---

## 🚀 Endpoints HTTP

| Método | Endpoint                   | Descrição                                   | Parâmetros Query | Status de Resposta                      |
|--------|-----------------------------|---------------------------------------------|------------------|------------------------------------------|
| **GET**  | `/container/create`          | Cria um novo container Docker               | Nenhum           | 200 ✅ Sucesso<br>500 ❌ Erro             |
| **GET**  | `/container/remove`          | Remove um container Docker existente        | `containerId`    | 200 ✅ Sucesso<br>500 ❌ Erro             |

---

## 🔧 Funcionamento dos Endpoints

### 🔹 **Criar Container**
**`GET /container/create`**
- Cria uma nova instância de container com porta dinâmica.
- Gera um nome de container no padrão:  
  `sae_monitoramento_BACK_{serial}_{porta}`
- Retorna os dados da instância criada em formato JSON.

**Exemplo de resposta:**
```json
{
  "dockerContainerHostName": "7a6e4b1d2fbc",
  "monitoramentoId": 432123123,
  "uptime": "2025-06-17 14:23:01",
  "backend": {
    "docker_container_ID": "7a6e4b1d2fbc",
    "container_name": "sae_monitoramento_BACK_1728929392312_20002",
    "status": "0",
    "port": "20002",
    "uptime": "2025-06-17 14:23:01",
    "error": ""
  }
}
```
# 🗂️ Modelos de Dados - Docker Container Manager

Documentação dos modelos utilizados na aplicação de gerenciamento de containers Docker.

---

## 📦 Container

### Descrição
Representa um container Docker criado pela API. Armazena informações essenciais sobre o estado do container.

### Atributos

| Atributo             | Tipo    | Descrição                                          |
|----------------------|---------|----------------------------------------------------|
| `docker_container_ID`| String  | ID do container Docker gerado na criação.          |
| `container_name`     | String  | Nome do container no padrão: `sae_monitoramento_BACK_{serial}_{porta}` |
| `status`             | String  | Status da criação (0 = sucesso, diferente de 0 = erro). |
| `port`               | String  | Porta externa mapeada no host.                     |
| `uptime`             | String  | Data e hora de criação no formato `yyyy-MM-dd HH:mm:ss`. |
| `error`              | String  | Mensagem de erro durante a criação (vazio se OK).  |

### 🔧 Exemplo de Objeto
```json
{
  "docker_container_ID": "7a6e4b1d2fbc",
  "container_name": "sae_monitoramento_BACK_1728929392312_20002",
  "status": "0",
  "port": "20002",
  "uptime": "2025-06-17 14:23:01",
  "error": ""
}
```

## Instancia Monitoramento
### Atributos
| Atributo                  | Tipo      | Descrição                                                       |
| ------------------------- | --------- | --------------------------------------------------------------- |
| `dockerContainerHostName` | String    | Hash ou nome base do container (primeiros 12 caracteres do ID). |
| `monitoramentoId`         | Long      | ID gerado aleatoriamente para a instância de monitoramento.     |
| `idEvento`                | Long      | ID opcional para vincular a um evento externo.                  |
| `idFluxo`                 | Long      | ID opcional de fluxo (para uso futuro).                         |
| `idBarragem`              | Long      | ID opcional de uma barragem associada.                          |
| `idArvoreDecisao`         | Long      | ID opcional da árvore de decisão associada.                     |
| `uptime`                  | String    | Data e hora de criação no formato `yyyy-MM-dd HH:mm:ss`.        |
| `backend`                 | Container | Objeto `Container` com os dados do container backend associado. |




