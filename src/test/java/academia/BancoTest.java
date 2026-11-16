package academia;

import academia.dao.AlunoDAO;
import academia.dao.BancoDeDados;
import academia.dao.InstrutorDAO;
import academia.excecao.AcademiaException;
import academia.modelo.Aluno;
import academia.modelo.Instrutor;
import academia.modelo.Plano;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes que usam o MySQL de verdade. Se o banco não estiver rodando (iniciar-banco.sh),
 * eles são ignorados em vez de falhar. Cada teste apaga o que criou.
 */
class BancoTest {

    private final AlunoDAO alunoDAO = new AlunoDAO();
    private final InstrutorDAO instrutorDAO = new InstrutorDAO();

    @BeforeAll
    static void prepararBanco() {
        try {
            BancoDeDados.preparar();
        } catch (AcademiaException e) {
            Assumptions.abort("MySQL fora do ar, rode o iniciar-banco.sh: " + e.getMessage());
        }
    }

    @Test
    void dadosDeExemploNaoSaoDuplicados() throws AcademiaException {
        int antes = instrutorDAO.listar().size();
        BancoDeDados.preparar();
        assertEquals(antes, instrutorDAO.listar().size());
        assertTrue(antes >= 3);
    }

    @Test
    void incluirConsultarAlterarEExcluirAluno() throws AcademiaException {
        Aluno a = new Aluno(0, "Aluno Teste", "11223344517", "82933334444", Plano.TRIMESTRAL, 0, LocalDate.now());
        alunoDAO.inserir(a);
        try {
            assertTrue(a.getId() > 0);

            Aluno lido = alunoDAO.buscarPorId(a.getId());
            assertNotNull(lido);
            assertEquals("Aluno Teste", lido.getNome());
            assertEquals(Plano.TRIMESTRAL, lido.getPlano());

            lido.setNome("Aluno Alterado");
            lido.setPlano(Plano.ANUAL);
            alunoDAO.atualizar(lido);
            Aluno alterado = alunoDAO.buscarPorId(a.getId());
            assertEquals("Aluno Alterado", alterado.getNome());
            assertEquals(Plano.ANUAL, alterado.getPlano());

            List<Aluno> achados = alunoDAO.buscarPorNome("Alterado");
            assertEquals(1, achados.size());
        } finally {
            alunoDAO.excluir(a.getId());
        }
        assertNull(alunoDAO.buscarPorId(a.getId()));
    }

    @Test
    void cpfRepetidoGeraExcecao() throws AcademiaException {
        Aluno a = new Aluno(0, "Aluno Um", "99887766593", "82911112222", Plano.MENSAL, 0, LocalDate.now());
        alunoDAO.inserir(a);
        try {
            Aluno repetido = new Aluno(0, "Aluno Dois", "99887766593", "82900000000", Plano.MENSAL, 0, LocalDate.now());
            AcademiaException e = assertThrows(AcademiaException.class, () -> alunoDAO.inserir(repetido));
            assertTrue(e.getMessage().contains("CPF"));
        } finally {
            alunoDAO.excluir(a.getId());
        }
    }

    @Test
    void excluirInstrutorDeixaOAlunoSemInstrutor() throws AcademiaException {
        Instrutor ins = new Instrutor(0, "Instrutor Teste", "52998224726", "82911112222", "Yoga");
        // CPF acima so serve para este teste; o banco nao valida os digitos (quem valida e o Validador)
        instrutorDAO.inserir(ins);
        Aluno a = new Aluno(0, "Aluno do Instrutor", "16899535010", "82933334444", Plano.MENSAL, ins.getId(), LocalDate.now());
        alunoDAO.inserir(a);
        try {
            instrutorDAO.excluir(ins.getId());
            assertEquals(0, alunoDAO.buscarPorId(a.getId()).getInstrutorId());
        } finally {
            alunoDAO.excluir(a.getId());
        }
    }

    @Test
    void excluirIdInexistenteGeraExcecao() {
        assertThrows(AcademiaException.class, () -> alunoDAO.excluir(-1));
    }
}
