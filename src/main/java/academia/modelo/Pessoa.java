package academia.modelo;

import academia.excecao.DadosInvalidosException;
import academia.util.Validador;

/**
 * Classe abstrata: nao existe "uma pessoa" solta na academia, ela e sempre
 * um Aluno ou um Instrutor. Os dados comuns ficam aqui.
 */
public abstract class Pessoa {

    private int id;
    private String nome;
    private String cpf;
    private String telefone;

    public Pessoa(int id, String nome, String cpf, String telefone) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
    }

    // metodo abstrato: cada filho diz qual e o seu papel
    public abstract String getPapel();

    // metodo final: nenhum filho pode mudar a regra de validacao
    public final void validar() throws DadosInvalidosException {
        Validador.validarNome(nome);
        Validador.validarCpf(cpf);
        Validador.validarTelefone(telefone);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public String toString() {
        return nome;
    }
}
