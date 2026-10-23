# CasaEmOrdem

Aplicativo Android nativo em Java para organizar tarefas domésticas pessoais,
com armazenamento local e funcionamento offline.

## Estado atual (Entrega Parcial 3 — Desenvolvimento Intermediário)

Funcionalidades implementadas:

- **Banco de dados local em SQLite**, criado e versionado por `DatabaseHelper`,
  com carga inicial de tarefas de exemplo.
- **CRUD completo de tarefas**: cadastrar, listar, editar e excluir, com
  confirmação antes da exclusão.
- **Categorização por ambiente**, com tela que mostra quantas tarefas cada
  cômodo tem e quantas estão pendentes.
- **Frequência e recorrência**: pontual, diária, semanal, quinzenal e mensal.
  O intervalo de cada frequência alimenta o cálculo do que vence a cada dia.
- **Check-in com registro de data**: marcar a tarefa grava a data da execução
  no banco, e desmarcar restaura a execução anterior.
- **Painel do dia**, que filtra apenas as tarefas pendentes ou atrasadas,
  ordena as atrasadas no topo e exibe o progresso do dia.
- **Integração entre as telas** por `Intent`, com retorno de resultado para a
  tela de origem e recarga automática das listas.

## Estrutura do código

```
br.com.casaemordem
├── MainActivity           painel do dia e check-in
├── RoomsActivity          ambientes da casa
├── RoomTasksActivity      tarefas de um ambiente
├── NewTaskActivity        cadastro e edição de tarefa
├── data/                  DatabaseHelper e TarefaDao (SQLite)
├── model/                 Tarefa e Frequencia, com as regras de recorrência
├── ui/                    adaptadores de lista e barra de navegação
└── util/                  cálculo e formatação de datas
```

## Abrir no Android Studio

Abra esta pasta como projeto e aguarde a sincronização do Gradle. O projeto usa
Android Gradle Plugin 8.6.1, JDK 17, compile SDK 35 e min SDK 23. É necessário
ter o SDK Android 35 instalado pelo SDK Manager.

O ponto de entrada é `app/src/main/java/br/com/casaemordem/MainActivity.java`.

## Próximas etapas

- Notificações ou lembretes das tarefas do dia.
- Histórico de execuções por tarefa.
- Permitir que o usuário crie e edite os próprios ambientes.
