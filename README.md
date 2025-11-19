# dock-access-counter-system

(EM CONSTRUÇÃO)

Este projeto tem como objetivo realizar o desenvolvimento de um desafio da equipe de Auth da Dock.

## Requisitos
*	Criar o sistema que rode contabilize votos/acessos.
*	Deve rodar em cluster
*	API gRPC
*	Dockerizado
*	Deve receber 1.000.000 de requisições em paralelo de múltiplos usuários e o contador deve bater exatamente 1.000.000.

## Arquitetura

### Diagrama da arquitetura no modelo C4 Level

![Diagrama da arquitetura no modelo C4 Level](docs/access-counter.drawio.png)

### access-api

#### Contrato do método AddAccess

```json
{
  "clientId": 58217,
  "clientName": "Amanda Segundo",
  "device": {
    "ipAddress": "192.168.12.45",
    "os": "iOS",
    "type": "MOBILE",
    "version": "26"
  },
  "geolocation": {
    "latitude": -26.9189,
    "longitude": -49.0661
  },
  "requestId": "9f722bc0-5b8a-4e52-95e6-182df45fbe4e",
  "timestamp": 1763559532
}

```


