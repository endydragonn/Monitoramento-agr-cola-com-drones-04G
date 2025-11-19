# Documentação do Projeto: Sistema de Monitoramento Agrícola com Drones

# Autor
- Guilherme Vecchi Bonnotti Freitas Silveira

## Sumário
1. [Introdução](#introdução)
2. [Proposta do Projeto](#proposta-do-projeto)
3. [Objetivos](#objetivos)
4. [Funcionalidades Mínimas](#funcionalidades-mínimas)
5. [Requisitos de Segurança](#requisitos-de-segurança)
6. [Metodologia de Desenvolvimento](#metodologia-de-desenvolvimento)
7. [Modelagem UML](#modelagem-uml)
   - [Diagrama de Classes](#diagrama-de-classes)
   - [Diagrama de Sequência](#diagrama-de-sequência)
   - [Diagrama de Colaboração](#diagrama-de-colaboração)
   - [Diagrama de Estados](#diagrama-de-estados)
   - [Integração com Banco de Dados (Modelo ER)](#integração-com-banco-de-dados-modelo-er)
8. [Princípios de Orientação a Objetos e CRC](#princípios-de-orientação-a-objetos-e-crc)
9. [Implementação em Java](#implementação-em-java)
10. [Banco de Dados Relacional](#banco-de-dados-relacional)
11. [Segurança by Design](#segurança-by-design)
12. [Testes e Simulações](#testes-e-simulações)
13. [Conclusão](#conclusão)
14. [Referências](#referências)

## Introdução
Este documento detalha o projeto de software para um sistema de monitoramento agrícola utilizando drones, desenvolvido como parte da disciplina de Projeto de Software na Universidade Presbiteriana Mackenzie, sob orientação do Prof. Dr. Rodrigo Silva. O projeto aborda desafios reais enfrentados por cooperativas rurais no Brasil, onde o monitoramento manual de plantações é ineficiente, demorado e suscetível a erros. Utilizando drones para sobrevoos periódicos, o sistema coleta dados ambientais (como temperatura, umidade e detecção de pragas) e imagens, facilitando análises e decisões agronômicas.

Inspirado nos materiais da disciplina, como as apostilas sobre UML (ex.: "APOSTILA - UML - SEQUENCIAL.pdf"), integração de diagramas ("Aula_07_Integração Inicial- Classes + Sequência + Dados .pdf"), classes abstratas e interfaces ("Aula_10_Classes_Abstratas_Interfaces.pdf"), diagramas de estados ("Aula_13_Diagrama de Estados.pdf"), responsabilidades OO e CRC ("Responsabilidade_OO_CRC_Segurança.pdf"), e diagramas de colaboração ("Aula_12_Diagrama_Colaboracao.pdf"), o projeto adota uma abordagem orientada a objetos (OO) com ênfase em segurança by design, conforme a atividade prática ("Atividade_Java_ALICE3_SEC.pdf"). Embora a atividade mencione Alice 3 para prototipação interativa (ex.: cena de herói protegendo uma fortaleza), adaptamos os conceitos OO e de segurança para o contexto agrícola, simulando "proteção" como monitoramento contra pragas.

O sistema é modelado em UML, implementado em Java com integração a banco de dados relacional, e prioriza modularidade, reuso de código e validações para prevenir vulnerabilidades.

## Proposta do Projeto
Uma cooperativa rural deseja monitorar plantações usando drones para coletar dados e imagens. O sistema permite cadastro de entidades, agendamento de missões, registro de dados e geração de relatórios, com controles de segurança para evitar acessos indevidos, dados corrompidos ou operações arriscadas. Isso alinha com os princípios de DevSecOps e Security by Design, pensando em segurança desde a modelagem (como recomendado na "Atividade_Java_ALICE3_SEC.pdf").

## Objetivos
### Geral
Desenvolver um protótipo de software OO seguro para monitoramento agrícola com drones, integrando modelagem UML, implementação Java e persistência de dados.

### Específicos
- Aplicar conceitos de OO: encapsulamento, polimorfismo, herança, abstrações e interfaces ("Aula_10_Classes_Abstratas_Interfaces.pdf").
- Modelar interações com diagramas de sequência e colaboração ("APOSTILA - UML - SEQUENCIAL.pdf" e "Aula_12_Diagrama_Colaboracao.pdf").
- Representar ciclos de vida com diagramas de estados, incluindo guardas de segurança ("Aula_13_Diagrama de Estados.pdf").
- Integrar diagramas com banco de dados relacional, prevenindo SQL Injection ("Aula_07_Integração Inicial- Classes + Sequência + Dados .pdf").
- Definir responsabilidades via CRC, com foco em controladores seguros ("Responsabilidade_OO_CRC_Segurança.pdf").
- Simular interações seguras, adaptando a prototipação de Alice 3 para código Java.

## Funcionalidades Mínimas
- **Cadastro de Áreas Agrícolas**: Tamanho, localização e tipo de cultivo.
- **Cadastro de Drones**: ID, sensores disponíveis e status.
- **Agendamento de Missões de Voo**: Data, área e sensores, com validação de sobreposições.
- **Registro de Dados Coletados**: Imagens e valores simulados (temperatura, umidade, pragas).
- **Relatórios Básicos**: Últimas medições e voos realizados.

## Requisitos de Segurança
- **Controle de Acesso**: Diferenciado (administrador para cadastros/relatórios; operador para agendamentos).
- **Validações**: Impedir missões sobrepostas; checklist antes de voos (bateria ≥20%, sensores funcionando).
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

## Modelagem em UML
Os diagramas feito em uml se complementam para servir como guia para o sistema.

### Diagrama de Classes
Representa a estrutura estática, com classes, atributos, operações e relacionamentos. Usa abstrações (classes abstratas como Usuario) e interfaces (Autenticavel, Validavel) para contratos restritos e validação de parâmetros, prevenindo falhas ("Aula_10_Classes_Abstratas_Interfaces.pdf"). Exemplos: herança para polimorfismo em usuários; associações para missões.

<img width="2613" height="1614" alt="Diagrama_de_classe" src="https://github.com/user-attachments/assets/18b4b6ce-d67a-47b4-92b3-9b9277e6326c" />

### Diagrama de Sequência
Mostra interações temporais entre objetos para agendamento e execução de missões ("APOSTILA - UML - SEQUENCIAL.pdf"). Inclui atores (OperadorDrone), objetos e mensagens com alternativas para validações de segurança.

<img width="2613" height="1614" alt="Diagrama_de_sequencia" src="https://github.com/user-attachments/assets/bfadba0b-8d2c-47f0-bb35-afc732847814" />

### Diagrama de Sequência + Colaboração
Complementa a sequência, focando em estrutura de links e mensagens numeradas ("Aula_12_Diagrama_Colaboracao.pdf"). Enfatiza segurança na troca de mensagens, validando dados para evitar exposições.

<img width="2613" height="1614" alt="Diagrama_Integração_ Sequencia_Colaboração" src="https://github.com/user-attachments/assets/69def96e-db48-478b-98aa-207a2651b955" />


### Diagrama de Estados
Modela o ciclo de vida de MissaoVoo, com estados, transições e guardas para segurança ("Aula_13_Diagrama de Estados.pdf"). Prevê estados inválidos (ex.: Executando sem checklist) usando UMLsec.

<img width="2613" height="1614" alt="Diagrama_de_estados" src="https://github.com/user-attachments/assets/296a1626-4bd3-4338-88ce-37c528081daa" />

## Princípios de Orientação a Objetos e CRC
Usando CRC ("Responsabilidade_OO_CRC_Segurança.pdf"), cada classe tem responsabilidades coesas:
- **Usuario**: Saber credenciais; fazer autenticação (controlador com validação).
- **MissaoVoo**: Saber dados da missão; fazer validação e execução (colabora com Drone e DadosColetados).
Papéis: Entidades (AreaAgricola), Controladores (DAOs com sanitização), Fronteiras (Main para interações).



## Medidas de segurança no codigo
- Encapsulamento: Senhas privadas.
- Validações: Nos setters e métodos.
- Prevenção de Injeções: PreparedStatements.
- Estados Seguros: Checagens em transições (ex.: checklist).

## Testes e Simulações
No Main, simule autenticação, agendamento e execução. : Imagine uma "cena" onde drone (herói) protege plantação (fortaleza) de pragas.

## Conclusão
O projeto integra modelagem UML com implementação com foco em seguraça, voltado a monitoramento agrícola baseado em drones.
