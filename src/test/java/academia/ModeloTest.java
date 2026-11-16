package academia;

import academia.excecao.DadosInvalidosException;
import academia.modelo.Aluno;
import academia.modelo.Cobravel;
import academia.modelo.Instrutor;
import academia.modelo.Plano;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModeloTest {

    @Test
    void mensalidadeVemDoPlano() {
        Aluno a = new Aluno(0, "Teste Modelo", "39053344705", "82900000000", Plano.ANUAL, 0, LocalDate.now());
        assertEquals(90.00, a.calcularMensalidade());
    }

    @Test
    void alunoEUmaPessoaECobravel() {
        Aluno a = new Aluno(0, "Teste Modelo", "39053344705", "82900000000", Plano.MENSAL, 0, LocalDate.now());
        Cobravel c = a; // Aluno implementa a interface Cobravel
        assertEquals("Aluno", a.getPapel());
        assertTrue(c.resumoCobranca().contains("120"));
    }

    @Test
    void instrutorTemOutroPapel() {
        Instrutor i = new Instrutor(0, "Carlos Mendes", "12345678909", "82991110001", "Musculação");
        assertEquals("Instrutor", i.getPapel());
    }

    @Test
    void validarAceitaDadosCertosERejeitaCpfErrado() {
        Aluno certo = new Aluno(0, "Fulano de Tal", "39053344705", "82900000000", Plano.MENSAL, 0, LocalDate.now());
        Aluno errado = new Aluno(0, "Fulano de Tal", "123", "82900000000", Plano.MENSAL, 0, LocalDate.now());
        assertDoesNotThrow(certo::validar);
        assertThrows(DadosInvalidosException.class, errado::validar);
    }
}
