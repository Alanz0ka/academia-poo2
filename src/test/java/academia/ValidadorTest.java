package academia;

import academia.excecao.DadosInvalidosException;
import academia.util.Validador;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidadorTest {

    @Test
    void cpfValidoPassa() {
        assertDoesNotThrow(() -> Validador.validarCpf("123.456.789-09"));
    }

    @Test
    void cpfComDigitoErradoERejeitado() {
        assertThrows(DadosInvalidosException.class, () -> Validador.validarCpf("12345678900"));
    }

    @Test
    void cpfComTodosOsDigitosIguaisERejeitado() {
        assertThrows(DadosInvalidosException.class, () -> Validador.validarCpf("11111111111"));
    }

    @Test
    void cpfCurtoERejeitado() {
        assertThrows(DadosInvalidosException.class, () -> Validador.validarCpf("123"));
    }

    @Test
    void nomeVazioERejeitado() {
        assertThrows(DadosInvalidosException.class, () -> Validador.validarNome("  "));
    }

    @Test
    void telefoneCurtoERejeitado() {
        assertThrows(DadosInvalidosException.class, () -> Validador.validarTelefone("9999"));
    }

    @Test
    void formataCpfETelefone() {
        assertEquals("123.456.789-09", Validador.formatarCpf("12345678909"));
        assertEquals("(82) 99111-0001", Validador.formatarTelefone("82991110001"));
        assertEquals("(82) 3333-4444", Validador.formatarTelefone("8233334444"));
    }
}
