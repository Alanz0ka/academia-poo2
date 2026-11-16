package academia.dao;

import academia.excecao.AcademiaException;
import academia.modelo.Aluno;
import academia.modelo.Plano;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AlunoDAO implements Crud<Aluno> {

    private static final int ERRO_CHAVE_DUPLICADA = 1062;

    @Override
    public void inserir(Aluno a) throws AcademiaException {
        String sql = "INSERT INTO aluno (nome, cpf, telefone, plano, instrutor_id, data_matricula) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(ps, a);
            ps.executeUpdate();
            ResultSet chaves = ps.getGeneratedKeys();
            if (chaves.next()) {
                a.setId(chaves.getInt(1));
            }
        } catch (SQLException e) {
            throw traduzir(e, "incluir");
        }
    }

    @Override
    public void atualizar(Aluno a) throws AcademiaException {
        String sql = "UPDATE aluno SET nome = ?, cpf = ?, telefone = ?, plano = ?, instrutor_id = ?, "
                + "data_matricula = ? WHERE id = ?";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            preencher(ps, a);
            ps.setInt(7, a.getId());
            if (ps.executeUpdate() == 0) {
                throw new AcademiaException("Aluno não encontrado (id " + a.getId() + ").");
            }
        } catch (SQLException e) {
            throw traduzir(e, "alterar");
        }
    }

    @Override
    public void excluir(int id) throws AcademiaException {
        String sql = "DELETE FROM aluno WHERE id = ?";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new AcademiaException("Aluno não encontrado (id " + id + ").");
            }
        } catch (SQLException e) {
            throw traduzir(e, "excluir");
        }
    }

    @Override
    public Aluno buscarPorId(int id) throws AcademiaException {
        String sql = "SELECT * FROM aluno WHERE id = ?";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return montar(rs);
            }
            return null;
        } catch (SQLException e) {
            throw traduzir(e, "consultar");
        }
    }

    @Override
    public List<Aluno> listar() throws AcademiaException {
        String sql = "SELECT * FROM aluno ORDER BY nome";
        List<Aluno> lista = new ArrayList<>();
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
        } catch (SQLException e) {
            throw traduzir(e, "consultar");
        }
        return lista;
    }

    public List<Aluno> buscarPorNome(String trecho) throws AcademiaException {
        String sql = "SELECT * FROM aluno WHERE nome LIKE ? ORDER BY nome";
        List<Aluno> lista = new ArrayList<>();
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + trecho.trim() + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(montar(rs));
            }
        } catch (SQLException e) {
            throw traduzir(e, "consultar");
        }
        return lista;
    }

    // o mesmo preenchimento serve para INSERT e UPDATE (os 6 primeiros parametros sao iguais)
    private void preencher(PreparedStatement ps, Aluno a) throws SQLException {
        ps.setString(1, a.getNome().trim());
        ps.setString(2, a.getCpf());
        ps.setString(3, a.getTelefone());
        ps.setString(4, a.getPlano().name());
        if (a.getInstrutorId() == 0) {
            ps.setNull(5, Types.INTEGER);
        } else {
            ps.setInt(5, a.getInstrutorId());
        }
        ps.setDate(6, Date.valueOf(a.getDataMatricula()));
    }

    private Aluno montar(ResultSet rs) throws SQLException {
        // getInt devolve 0 quando o campo e NULL, que e o nosso "sem instrutor"
        LocalDate data = rs.getDate("data_matricula").toLocalDate();
        return new Aluno(rs.getInt("id"), rs.getString("nome"), rs.getString("cpf"),
                rs.getString("telefone"), Plano.valueOf(rs.getString("plano")),
                rs.getInt("instrutor_id"), data);
    }

    private AcademiaException traduzir(SQLException e, String acao) {
        if (e.getErrorCode() == ERRO_CHAVE_DUPLICADA) {
            return new AcademiaException("Já existe um aluno com esse CPF.", e);
        }
        return new AcademiaException("Erro ao " + acao + " aluno: " + e.getMessage(), e);
    }
}
