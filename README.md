# CasaEmOrdem

Aplicativo Android nativo em Java para organizar tarefas domésticas pessoais.

## Entrega parcial

- Estrutura inicial de um projeto Android Studio com Gradle.
- Painel inicial com tarefas do dia e checklist demonstrativo.
- Tela de ambientes com atalho para cadastrar uma tarefa vinculada ao cômodo.
- Formulário com nome, ambiente e frequência.
- Navegação entre Hoje, Ambientes e Nova tarefa.

Nesta etapa, as tarefas exibidas são exemplos estáticos. O formulário valida o nome e demonstra o fluxo, mas ainda não persiste os dados; banco local, CRUD completo e atualização real do painel ficam para as próximas etapas.

## Abrir no Android Studio

Abra esta pasta como projeto no Android Studio e aguarde a sincronização do Gradle. O projeto usa Android Gradle Plugin 8.6.1, JDK 17, compile SDK 35 e min SDK 23. É necessário ter o SDK Android 35 instalado pelo SDK Manager.

O ponto de entrada do aplicativo é `app/src/main/java/br/com/casaemordem/MainActivity.java`.