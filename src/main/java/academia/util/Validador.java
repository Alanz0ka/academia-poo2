package academia.util;

import academia.excecao.DadosInvalidosException;

/**
 * Classe final (ninguem herda dela) com metodos static: nao precisa de objeto,
 * e so chamar Validador.validarCpf(...).
 */
public final class Validador {

    private static final int TAMANHO_CPF = 11;

    // construtor privado: a classe so tem metodos static, entao nao faz sentido criar objeto
    private Validador() {
    }

    public static void validarNome(String nome) throws DadosInvalidosException {
        if (nome == null || nome.trim().length() < 3) {
            throw new DadosInvalidosException("O nome deve ter pelo menos 3 letras.");
        }
    }

    public static void validarTelefone(String telefone) throws DadosInvalidosException {
        String digitos = somenteDigitos(telefone);
        if (digitos.length() != 10 && digitos.length() != 11) {
            throw new DadosInvalidosException("O telefone deve ter DDD + número (10 ou 11 dígitos).");
        }
    }

    public static void validarCpf(String cpf) throws DadosInvalidosException {
        String c = somenteDigitos(cpf);
        if (c.length() != TAMANHO_CPF) {
            throw new DadosInvalidosException("O CPF deve ter 11 dígitos.");
        }
        // 111.111.111-11 passa na conta, mas nao e um CPF de verdade
        boolean todosIguais = true;
        for (int i = 1; i < c.length(); i++) {
            if (c.charAt(i) != c.charAt(0)) {
                todosIguais = false;
            }
        }
        if (todosIguais) {
            throw new DadosInvalidosException("CPF inválido.");
        }
        if (calcularDigito(c, 9) != c.charAt(9) - '0' || calcularDigito(c, 10) != c.charAt(10) - '0') {
            throw new DadosInvalidosException("CPF inválido (dígito verificador não confere).");
        }
    }

    // calcula um digito verificador usando as 'quantidade' primeiras posicoes
    private static int calcularDigito(String cpf, int quantidade) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += (cpf.charAt(i) - '0') * (quantidade + 1 - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }

    public static String somenteDigitos(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replaceAll("[^0-9]", "");
    }

    public static String formatarCpf(String cpf) {
        String c = somenteDigitos(cpf);
        if (c.length() != TAMANHO_CPF) {
            return cpf;
        }
        return c.substring(0, 3) + "." + c.substring(3, 6) + "." + c.substring(6, 9) + "-" + c.substring(9);
    }

    public static String formatarTelefone(String telefone) {
        String t = somenteDigitos(telefone);
        if (t.length() == 11) {
            return "(" + t.substring(0, 2) + ") " + t.substring(2, 7) + "-" + t.substring(7);
        }
        if (t.length() == 10) {
            return "(" + t.substring(0, 2) + ") " + t.substring(2, 6) + "-" + t.substring(6);
        }
        return telefone;
    }
}
