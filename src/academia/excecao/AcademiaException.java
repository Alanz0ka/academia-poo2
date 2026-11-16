package academia.excecao;

/**
 * Excecao checada do sistema. Todo erro de banco ou de regra sobe como AcademiaException,
 * e a tela mostra a mensagem para o usuario.
 */
public class AcademiaException extends Exception {

    public AcademiaException(String mensagem) {
        super(mensagem);
    }

    public AcademiaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
