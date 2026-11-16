import academia.dao.AlunoDAO;
import academia.dao.BancoDeDados;
import academia.dao.InstrutorDAO;
import academia.excecao.AcademiaException;
import academia.excecao.DadosInvalidosException;
import academia.modelo.Aluno;
import academia.modelo.Instrutor;
import academia.modelo.Plano;
import academia.util.Validador;

import java.time.LocalDate;
import java.util.List;

/** Testes simples sem JUnit: cada verificacao imprime OK ou FALHA. Rodar: java -cp "out:lib/*" TesteAcademia */
public class TesteAcademia {

    static int falhas = 0;

    static void verifica(String descricao, boolean condicao) {
        System.out.println((condicao ? "OK     " : "FALHA  ") + descricao);
        if (!condicao) {
            falhas++;
        }
    }

    static boolean lancaDadosInvalidos(Runnable2 r) {
        try {
            r.executar();
            return false;
        } catch (DadosInvalidosException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    interface Runnable2 {
        void executar() throws Exception;
    }

    public static void main(String[] args) throws Exception {
        // ---- Validador ----
        verifica("CPF valido passa", !lancaDadosInvalidos(() -> Validador.validarCpf("123.456.789-09")));
        verifica("CPF com digito errado e rejeitado", lancaDadosInvalidos(() -> Validador.validarCpf("12345678900")));
        verifica("CPF com todos os digitos iguais e rejeitado", lancaDadosInvalidos(() -> Validador.validarCpf("11111111111")));
        verifica("CPF curto e rejeitado", lancaDadosInvalidos(() -> Validador.validarCpf("123")));
        verifica("Nome vazio e rejeitado", lancaDadosInvalidos(() -> Validador.validarNome("  ")));
        verifica("Telefone curto e rejeitado", lancaDadosInvalidos(() -> Validador.validarTelefone("9999")));
        verifica("formatarCpf", "123.456.789-09".equals(Validador.formatarCpf("12345678909")));
        verifica("formatarTelefone 11 digitos", "(82) 99111-0001".equals(Validador.formatarTelefone("82991110001")));

        // ---- Modelo ----
        Aluno modelo = new Aluno(0, "Teste Modelo", "39053344705", "82900000000", Plano.ANUAL, 0, LocalDate.now());
        verifica("Mensalidade do plano anual = 90", modelo.calcularMensalidade() == 90.00);
        verifica("Papel do aluno", "Aluno".equals(modelo.getPapel()));
        verifica("resumoCobranca (default da interface)", modelo.resumoCobranca().contains("90,00") || modelo.resumoCobranca().contains("90.00"));
        verifica("validar() em aluno com CPF errado lanca excecao",
                lancaDadosInvalidos(() -> new Aluno(0, "Fulano", "123", "82900000000", Plano.MENSAL, 0, LocalDate.now()).validar()));

        // ---- Banco (CRUD de verdade no MySQL) ----
        BancoDeDados.preparar();
        BancoDeDados.preparar(); // segunda chamada nao pode duplicar os dados de exemplo
        InstrutorDAO instrutorDAO = new InstrutorDAO();
        AlunoDAO alunoDAO = new AlunoDAO();
        verifica("Dados de exemplo existem (3 instrutores)", instrutorDAO.listar().size() >= 3);

        Instrutor ins = new Instrutor(0, "Instrutor Teste", "99887766593", "82911112222", "Yoga");
        instrutorDAO.inserir(ins);
        verifica("Instrutor inserido recebeu id", ins.getId() > 0);

        Aluno a = new Aluno(0, "Aluno Teste", "11223344517", "82933334444", Plano.TRIMESTRAL, ins.getId(), LocalDate.now());
        alunoDAO.inserir(a);
        verifica("Aluno inserido recebeu id", a.getId() > 0);

        Aluno lido = alunoDAO.buscarPorId(a.getId());
        verifica("Consulta por id devolve os mesmos dados",
                lido != null && lido.getNome().equals("Aluno Teste") && lido.getPlano() == Plano.TRIMESTRAL
                        && lido.getInstrutorId() == ins.getId());

        boolean duplicado = false;
        try {
            alunoDAO.inserir(new Aluno(0, "Outro", "11223344517", "82900000000", Plano.MENSAL, 0, LocalDate.now()));
        } catch (AcademiaException e) {
            duplicado = e.getMessage().contains("CPF");
        }
        verifica("CPF repetido gera AcademiaException", duplicado);

        lido.setNome("Aluno Alterado");
        lido.setPlano(Plano.ANUAL);
        alunoDAO.atualizar(lido);
        Aluno alterado = alunoDAO.buscarPorId(a.getId());
        verifica("Alteracao gravada", alterado.getNome().equals("Aluno Alterado") && alterado.getPlano() == Plano.ANUAL);

        List<Aluno> achados = alunoDAO.buscarPorNome("Alterado");
        verifica("Busca por nome acha o aluno", achados.size() == 1);

        // excluir o instrutor deixa o aluno sem instrutor (ON DELETE SET NULL)
        instrutorDAO.excluir(ins.getId());
        verifica("Aluno fica sem instrutor quando o instrutor e excluido", alunoDAO.buscarPorId(a.getId()).getInstrutorId() == 0);

        alunoDAO.excluir(a.getId());
        verifica("Aluno excluido nao e mais encontrado", alunoDAO.buscarPorId(a.getId()) == null);

        boolean naoAchou = false;
        try {
            alunoDAO.excluir(a.getId());
        } catch (AcademiaException e) {
            naoAchou = true;
        }
        verifica("Excluir id inexistente lanca excecao", naoAchou);

        System.out.println(falhas == 0 ? "\nTodos os testes passaram." : "\n" + falhas + " teste(s) falharam.");
        System.exit(falhas == 0 ? 0 : 1);
    }
}
