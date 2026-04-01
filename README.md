# Comunicacao API
## Descricao

API Responsavel pelo gerenciamento de comunicacoes, permitindo agendamento, consulta de status e cancelamento de envios.

## Endpoints da Aplicacao
### Agendar comunicacao
- **POST /comunicacao/agendar**
Responsavel por agendar uma comunicacao.
### Buscar status da comunicacao
- **GET /comunicacao?emailDestinatario={email}**
Retorna o status da comunicacao com base no email do destinatario.
### Cancelar comunicacao
- **PATCH /comunicacao/cancelar?emailDestinatario={email}**
Altera o status da comunicacao para cancelado.