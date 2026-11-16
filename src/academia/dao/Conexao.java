package academia.dao;

import academia.excecao.AcademiaException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Abre conexoes com o MySQL. Classe final, so com membros static. */
public final class Conexao {

    // dados do banco (podem ser trocados por variavel de ambiente, sem mexer no codigo)
    private static final String URL = pegar("ACADEMIA_DB_URL",
            "jdbc:mysql://localhost:3307/academia?useSSL=false&allowPublicKeyRetrieval=true");
    private static final String USUARIO = pegar("ACADEMIA_DB_USER", "academia");
    private static final String SENHA = pegar("ACADEMIA_DB_PASS", "academia123");

    // contador static: quantas conexoes o programa ja abriu (aparece na barra da tela)
    private static int conexoesAbertas = 0;

    private Conexao() {
    }

    private static String pegar(String variavel, String padrao) {
        String valor = System.getenv(variavel);
        return (valor == null || valor.isEmpty()) ? padrao : valor;
    }

    public static Connection getConexao() throws AcademiaException {
        try {
            Connection c = DriverManager.getConnection(URL, USUARIO, SENHA);
            conexoesAbertas++;
            return c;
        } catch (SQLException e) {
            throw new AcademiaException("Não foi possível conectar ao MySQL.\n"
                    + "Confira se o banco está rodando (iniciar-banco.sh).", e);
        }
    }

    public static int getConexoesAbertas() {
        return conexoesAbertas;
    }
}
