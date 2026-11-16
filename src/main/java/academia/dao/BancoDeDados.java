package academia.dao;

import academia.excecao.AcademiaException;
import academia.modelo.Aluno;
import academia.modelo.Instrutor;
import academia.modelo.Plano;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

/** Cria as tabelas na primeira execucao e cadastra alguns dados de exemplo. */
public final class BancoDeDados {

    private BancoDeDados() {
    }

    public static void preparar() throws AcademiaException {
        criarTabelas();
        popularSeVazio();
    }

    private static void criarTabelas() throws AcademiaException {
        String instrutor = "CREATE TABLE IF NOT EXISTS instrutor ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "nome VARCHAR(100) NOT NULL, "
                + "cpf VARCHAR(11) NOT NULL UNIQUE, "
                + "telefone VARCHAR(11) NOT NULL, "
                + "especialidade VARCHAR(60))";
        String aluno = "CREATE TABLE IF NOT EXISTS aluno ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "nome VARCHAR(100) NOT NULL, "
                + "cpf VARCHAR(11) NOT NULL UNIQUE, "
                + "telefone VARCHAR(11) NOT NULL, "
                + "plano VARCHAR(20) NOT NULL, "
                + "instrutor_id INT NULL, "
                + "data_matricula DATE NOT NULL, "
                + "FOREIGN KEY (instrutor_id) REFERENCES instrutor(id) ON DELETE SET NULL)";
        try (Connection c = Conexao.getConexao(); Statement st = c.createStatement()) {
            st.executeUpdate(instrutor);
            st.executeUpdate(aluno);
        } catch (SQLException e) {
            throw new AcademiaException("Erro ao criar as tabelas: " + e.getMessage(), e);
        }
    }

    private static void popularSeVazio() throws AcademiaException {
        try (Connection c = Conexao.getConexao();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM instrutor")) {
            rs.next();
            if (rs.getInt(1) > 0) {
                return;
            }
        } catch (SQLException e) {
            throw new AcademiaException("Erro ao consultar o banco: " + e.getMessage(), e);
        }

        InstrutorDAO instrutorDAO = new InstrutorDAO();
        AlunoDAO alunoDAO = new AlunoDAO();

        Instrutor carlos = new Instrutor(0, "Carlos Mendes", "12345678909", "82991110001", "Musculação");
        Instrutor fernanda = new Instrutor(0, "Fernanda Lima", "11144477735", "82991110002", "Funcional");
        Instrutor rafael = new Instrutor(0, "Rafael Souza", "52998224725", "82991110003", "Spinning");
        instrutorDAO.inserir(carlos);
        instrutorDAO.inserir(fernanda);
        instrutorDAO.inserir(rafael);

        LocalDate hoje = LocalDate.now();
        alunoDAO.inserir(new Aluno(0, "Ana Paula Ribeiro", "39053344705", "82988880001",
                Plano.MENSAL, carlos.getId(), hoje.minusMonths(1)));
        alunoDAO.inserir(new Aluno(0, "Bruno Tavares", "16899535009", "82988880002",
                Plano.TRIMESTRAL, carlos.getId(), hoje.minusMonths(2)));
        alunoDAO.inserir(new Aluno(0, "Camila Duarte", "98765432100", "82988880003",
                Plano.ANUAL, fernanda.getId(), hoje.minusMonths(5)));
        alunoDAO.inserir(new Aluno(0, "Diego Albuquerque", "24681357928", "82988880004",
                Plano.MENSAL, rafael.getId(), hoje.minusDays(10)));
        alunoDAO.inserir(new Aluno(0, "Elisa Monteiro", "13579246828", "82988880005",
                Plano.TRIMESTRAL, 0, hoje.minusDays(3)));
    }
}
