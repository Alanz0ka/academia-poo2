package academia.dao;

import academia.excecao.AcademiaException;
import academia.modelo.Instrutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InstrutorDAO implements Crud<Instrutor> {

    private static final int ERRO_CHAVE_DUPLICADA = 1062;

    @Override
    public void inserir(Instrutor i) throws AcademiaException {
        String sql = "INSERT INTO instrutor (nome, cpf, telefone, especialidade) VALUES (?, ?, ?, ?)";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, i.getNome().trim());
            ps.setString(2, i.getCpf());
            ps.setString(3, i.getTelefone());
            ps.setString(4, i.getEspecialidade());
            ps.executeUpdate();
            ResultSet chaves = ps.getGeneratedKeys();
            if (chaves.next()) {
                i.setId(chaves.getInt(1));
            }
        } catch (SQLException e) {
            throw traduzir(e, "incluir");
        }
    }

    @Override
    public void atualizar(Instrutor i) throws AcademiaException {
        String sql = "UPDATE instrutor SET nome = ?, cpf = ?, telefone = ?, especialidade = ? WHERE id = ?";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, i.getNome().trim());
            ps.setString(2, i.getCpf());
            ps.setString(3, i.getTelefone());
            ps.setString(4, i.getEspecialidade());
            ps.setInt(5, i.getId());
            if (ps.executeUpdate() == 0) {
                throw new AcademiaException("Instrutor não encontrado (id " + i.getId() + ").");
            }
        } catch (SQLException e) {
            throw traduzir(e, "alterar");
        }
    }

    @Override
    public void excluir(int id) throws AcademiaException {
        String sql = "DELETE FROM instrutor WHERE id = ?";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new AcademiaException("Instrutor não encontrado (id " + id + ").");
            }
        } catch (SQLException e) {
            throw traduzir(e, "excluir");
        }
    }

    @Override
    public Instrutor buscarPorId(int id) throws AcademiaException {
        String sql = "SELECT * FROM instrutor WHERE id = ?";
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
    public List<Instrutor> listar() throws AcademiaException {
        String sql = "SELECT * FROM instrutor ORDER BY nome";
        List<Instrutor> lista = new ArrayList<>();
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

    public List<Instrutor> buscarPorNome(String trecho) throws AcademiaException {
        String sql = "SELECT * FROM instrutor WHERE nome LIKE ? ORDER BY nome";
        List<Instrutor> lista = new ArrayList<>();
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

    private Instrutor montar(ResultSet rs) throws SQLException {
        return new Instrutor(rs.getInt("id"), rs.getString("nome"), rs.getString("cpf"),
                rs.getString("telefone"), rs.getString("especialidade"));
    }

    private AcademiaException traduzir(SQLException e, String acao) {
        if (e.getErrorCode() == ERRO_CHAVE_DUPLICADA) {
            return new AcademiaException("Já existe um instrutor com esse CPF.", e);
        }
        return new AcademiaException("Erro ao " + acao + " instrutor: " + e.getMessage(), e);
    }
}
