package academia.modelo;

public class Instrutor extends Pessoa {

    private String especialidade;

    public Instrutor(int id, String nome, String cpf, String telefone, String especialidade) {
        super(id, nome, cpf, telefone);
        this.especialidade = especialidade;
    }

    @Override
    public String getPapel() {
        return "Instrutor";
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }
}
