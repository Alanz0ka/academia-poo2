package academia.excecao;

/** Lancada quando o usuario digita algo invalido (nome vazio, CPF errado, etc). */
public class DadosInvalidosException extends AcademiaException {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
