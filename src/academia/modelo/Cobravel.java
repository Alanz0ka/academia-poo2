package academia.modelo;

/** Interface para tudo que gera cobranca mensal. */
public interface Cobravel {

    double calcularMensalidade();

    // metodo default: quem implementa ja ganha esse texto pronto
    default String resumoCobranca() {
        return String.format("R$ %.2f", calcularMensalidade());
    }
}
