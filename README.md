# Documentação do Projeto: Sistema de Monitoramento Agrícola com Drones

# Autor
- Guilherme Vecchi Bonnotti Freitas Silveira

## Sumário
1. [Introdução](#introdução)
2. [Objetivos](#objetivos)
3. [Funcionalidades Mínimas](#funcionalidades-mínimas)
4. [Requisitos de Segurança](#requisitos-de-segurança)
5. [Metodologia de Desenvolvimento](#metodologia-de-desenvolvimento)
6. [Diagramas UML](#diagramas-em-uml)
   - [Diagrama de Classes](#diagrama-de-classes)
   - [Diagrama de Sequência](#diagrama-de-sequência)
   - [Diagrama de Colaboração](#diagrama-de-colaboração)
   - [Diagrama de Estados](#diagrama-de-estados)
7. [Princípios de Orientação a Objetos e CRC](#princípios-de-orientação-a-objetos-e-crc)
8. [Resumo do SistemaDeMonitoramentoDeDrones.java](#resumo-do-sistemademonitoramentodedronesjava)
9. [Resumo do Banco.sql](#resumo-do-bancosql)
10. [Conclusão](#conclusão)
11. [Referências](#referências)

## Introdução
Esse é um projeto sobre um sistema de monitoramento agrícola utilizando drones. O projeto aborda desafios reais enfrentados por cooperativas rurais, onde o monitoramento manual de plantações é ineficiente, demorado e suscetível a erros. Utilizando drones para sobrevoos periódicos, o sistema coleta dados ambientais (como temperatura, umidade e detecção de pragas) e imagens, facilitando análises e decisões agronômicas.


## Objetivos
### Geral
Desenvolver um protótipo de software orientado a objeto seguro para monitoramento agrícola com drones, integrando modelagem UML, implementação Java e persistência de dados.

## Funcionalidades Mínimas
- **Cadastro de Áreas Agrícolas**: Tamanho, localização e tipo de cultivo.
- **Cadastro de Drones**: ID, sensores disponíveis e status.
- **Agendamento de Missões de Voo**: Data, área e sensores, com validação de sobreposições.
- **Registro de Dados Coletados**: Imagens e valores simulados .
- **Relatórios Básicos**: Últimas medições e voos realizados.

## Requisitos de Segurança
- **Controle de Acesso**: Diferenciado .
- **Validações**: Impedir missões sobrepostas; checklist antes de voos .
- **Tratamento de Dados**: Evitar entradas inválidas/corrompidas via métodos com validação.
- **Persistência Segura**: Uso de PreparedStatements para prevenir SQL Injection.
- **Estados Seguros**: Guardas em transições para evitar estados inválidos (ex.: executar missão sem autenticação).

## Metodologia de Desenvolvimento
Adotamos uma abordagem incremental:
1. Modelagem estática (classes) e dinâmica (sequência, colaboração, estados).
2. Definição de responsabilidades via CRC (classes com propósitos coesos, colaborações seguras).
3. Integração com dados relacionais.
4. Implementação em Java com abstrações e validações.
5. Testes simulados no método Main.

## Diagramas em UML

### Diagrama de Classes
Representa a estrutura estática, com classes, atributos, operações e relacionamentos. Usa abstrações e interfaces para contratos restritos e validação de parâmetros, prevenindo falhas.

<img width="2613" height="1614" alt="Diagrama_de_classe" src="https://github.com/user-attachments/assets/18b4b6ce-d67a-47b4-92b3-9b9277e6326c" />

### Diagrama de Sequência
Mostra interações temporais entre objetos. Inclui atores, objetos e mensagens com validações de segurança.

<img width="2613" height="1614" alt="Diagrama_de_sequencia" src="https://github.com/user-attachments/assets/bfadba0b-8d2c-47f0-bb35-afc732847814" />

### Diagrama de Colaboração
Complementa o diagrama de sequência, focando em estrutura de links e mensagens numeradas. Enfatizando a segurança na troca de mensagens e validando dados para evitar exposições.

<img width="2613" height="1614" alt="Diagrama_Integração_ Sequencia_Colaboração" src="https://github.com/user-attachments/assets/69def96e-db48-478b-98aa-207a2651b955" />


### Diagrama de Estados
Modela o ciclo de vida com estados, transições e guardas para segurança, além de Prevêr estados inválidos (ex.: Executando sem checklist).

<img width="2613" height="1614" alt="Diagrama_de_estados" src="https://github.com/user-attachments/assets/c5c63393-8e15-44b8-8059-81cef509c87e" />

## Princípios de Orientação a Objetos e CRC
Usando CRC , cada classe tem responsabilidades coesas:
- **Usuario**: Saber credenciais; fazer autenticação (controlador com validação).
- **MissaoVoo**: Saber dados da missão; fazer validação e execução (colabora com Drone e DadosColetados).
Papéis: Entidades (AreaAgricola), Controladores (DAOs com sanitização), Fronteiras (Main para interações).

## Funcionamento do SistemaDeMonitoramentoDeDrones.java

### Interface Autenticavel
- Define um contrato para autenticação de usuários, garantindo que apenas credenciais válidas permitam acesso ao sistema.

### Interface Validavel
- Estabelece um padrão para validação de objetos, ajudando a assegurar que dados e estados sejam consistentes antes de operações críticas.

### Classes

#### Classe Usuario (abstrata)
- Representa usuários genéricos do sistema, fornecendo mecanismos básicos para gerenciamento de credenciais e autenticação segura.

#### Classe Administrador (herda de Usuario)
- Permite que administradores cadastrem áreas agrícolas e drones, além de gerarem relatórios, facilitando a administração e análise de dados.

#### Classe OperadorDrone (herda de Usuario)
- Habilita operadores a agendarem missões de voo, com verificações para evitar conflitos, promovendo eficiência operacional.

#### Classe AreaAgricola
- Modela áreas agrícolas, armazenando informações essenciais para planejamento e monitoramento de cultivos.

#### Classe Drone
- Representa drones, incluindo verificações de prontidão para voos, garantindo que equipamentos estejam aptos para missões.

#### Classe MissaoVoo 
- Gerencia missões de voo, validando condições e executando coletas de dados, otimizando o processo de monitoramento.

#### Classe DadosColetados 
- Armazena dados ambientais coletados durante missões, com validações para manter a integridade das informações.

#### Classe Relatorio
- Gera resumos de medições e voos, auxiliando na tomada de decisões agronômicas baseadas em dados recentes.

#### Classe ConnectionFactory
- Fornece conexões seguras ao banco de dados, facilitando a integração entre a aplicação e o armazenamento persistente.

#### Classe UsuarioDAO
- Lida com autenticação de usuários no banco de dados, promovendo acesso controlado e seguro.

#### Classe AreaAgricolaDAO
- Gerencia o cadastro de áreas agrícolas no banco, assegurando persistência de dados geográficos e de cultivo.

#### Classe DroneDAO
- Cuida do registro e recuperação de drones e seus sensores, suportando o gerenciamento de frota.

#### Classe MissaoVooDAO
- Administra o agendamento de missões e verificações de sobreposições, evitando conflitos operacionais.

#### Classe DadosColetadosDAO
- Registra dados coletados e imagens associadas, preservando evidências de monitoramentos.

#### Classe RelatorioDAO
- Extrai dados para relatórios, fornecendo insights sobre áreas específicas.

#### Classe Main
- Demonstra o fluxo do sistema através de simulações, ilustrando como componentes interagem em cenários reais.

## Funçionamento do Banco.sql

### Criação do Banco de Dados
- Estabelece o banco de dados principal, servindo como repositório central para todos os dados do sistema.

### Criação das Tabelas
- Define estruturas para usuários, áreas agrícolas, drones, sensores, missões de voo, dados coletados e imagens, garantindo organização e relacionamentos lógicos entre entidades.

### Concessão de Permissões
- Atribui acessos mínimos a um usuário de aplicação, promovendo segurança ao limitar operações a leitura, inserção e atualização essenciais.

## Conclusão
O projeto integra modelagem UML com implementação com foco em seguraça, voltado a monitoramento agrícola baseado em drones.
