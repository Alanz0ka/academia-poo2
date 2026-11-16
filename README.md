# Academia: cadastro de alunos e instrutores (POO II, NUP2)

Aplicação em Java 25 com tela em Swing e banco MySQL, para a apresentação individual de 07/12/2026
(prof. Luiz Marcelo Freitas da Silva, UNCISAL). Aluno: José Alan de Aquino Santos.

O sistema cadastra alunos (com plano e instrutor) e instrutores, e mostra um relatório com a
receita mensal prevista. Tem três abas: **Alunos**, **Instrutores** e **Relatório**.

## Como rodar

Precisa do Java 25 (JDK) e do Docker (para o MySQL). Os scripts escolhem o Java 25 sozinhos no Mac.

```bash
./iniciar-banco.sh   # sobe o MySQL 8.4 em Docker (porta 3307) e espera ficar pronto
./build.sh           # compila para a pasta out/
./run.sh             # abre a tela
```

O contêiner roda como usuário `mysql` (não root), sem nenhuma capability, com `no-new-privileges`, limite de memória e porta só em
`127.0.0.1`. Na primeira execução o programa cria as tabelas `instrutor` e `aluno` e cadastra 3 instrutores
e 5 alunos de exemplo. Para usar outro MySQL, defina as variáveis `ACADEMIA_DB_URL`,
`ACADEMIA_DB_USER` e `ACADEMIA_DB_PASS` antes de rodar. O `build.sh` baixa o driver JDBC do MySQL
(`mysql-connector-j` 9.7.0, Maven Central, hash SHA-1 conferido) na primeira vez.

Testes (sem JUnit, cada linha mostra OK ou FALHA; os de banco usam o MySQL e limpam o que criam):

```bash
java -cp "out:lib/*" TesteAcademia
```

## Onde está cada requisito do professor

| Requisito | Onde está |
|---|---|
| Palavra reservada `final` | `Validador` e `Conexao` são `final class`; `Pessoa.validar()` é `final`; atributos `final` em `Plano`; constantes `static final` (`TAMANHO_CPF`, `URL`) |
| Palavra reservada `static` | métodos `static` de `Validador` (`validarCpf`, `formatarCpf`...); `Conexao.getConexoesAbertas()` e o contador `static` de conexões; `BancoDeDados.preparar()` |
| Framework Collections | `List` (listas de alunos), `Map`/`HashMap` (id do instrutor para o nome), `EnumMap` e `TreeMap` e `Collections.sort` com `Comparator` no `PainelRelatorio` |
| Tratamento de exceções | `AcademiaException` e `DadosInvalidosException` (checadas); `try/catch/finally` nos painéis; `try-with-resources` nos DAOs; `SQLException` traduzida (CPF repetido, banco fora do ar) |
| Interface | `Cobravel` (com método `default`) implementada por `Aluno`; `Crud<T>` implementada por `AlunoDAO` e `InstrutorDAO` |
| Classe abstrata | `Pessoa` (método abstrato `getPapel()`), com filhos `Aluno` e `Instrutor` |
| Interface gráfica (Swing) | pacote `academia.tela`: `JFrame`, `JTabbedPane`, `JTable`, `JComboBox`, `JTextField`, `JButton`, `JOptionPane` |
| Banco (incluir, excluir, alterar, consultar) | `AlunoDAO` e `InstrutorDAO`: `inserir`, `excluir`, `atualizar`, `buscarPorId`, `listar`, `buscarPorNome`, todos com `PreparedStatement` |

## Estrutura

```
src/academia/
  Main.java                 abre o banco e a tela
  modelo/                   Pessoa (abstrata), Aluno, Instrutor, Plano (enum), Cobravel (interface)
  dao/                      Crud (interface), AlunoDAO, InstrutorDAO, Conexao, BancoDeDados
  excecao/                  AcademiaException, DadosInvalidosException
  util/Validador.java       validação de nome, CPF (com dígito verificador) e telefone
  tela/                     TelaPrincipal, PainelAlunos, PainelInstrutores, PainelRelatorio
teste/TesteAcademia.java    testes
prints/                     as três abas rodando
```

## Roteiro de demonstração (uns 5 minutos)

1. Mostrar o relatório e as abas já com os dados de exemplo.
2. Incluir um aluno com CPF errado (ex.: `123`): aparece a mensagem de `DadosInvalidosException`.
3. Incluir com CPF válido (ex.: `112.233.445-17`) e mostrar o aluno na tabela.
4. Tentar incluir o mesmo CPF de novo: "Já existe um aluno com esse CPF" (erro do MySQL traduzido).
5. Selecionar a linha, mudar o plano e clicar em Alterar; depois Excluir.
6. Cadastrar um instrutor novo e voltar à aba Alunos: ele já aparece no combo.
7. Abrir o código de `Pessoa`, `Cobravel` e `PainelRelatorio` para mostrar abstrata, interface e Collections.

CPFs válidos para teste (calculados com o dígito verificador): `12345678909`, `11144477735`, `52998224725`,
`39053344705`, `16899535009`, `98765432100`, `24681357928`, `13579246828`, `11223344517`, `99887766593`.
Os oito primeiros já estão nos dados de exemplo; `11223344517` e `99887766593` estão livres para a demonstração
(os testes usam o `99887766593` e o `11223344517` e apagam no fim).
