package academia.modelo;

import java.time.LocalDate;

public class Aluno extends Pessoa implements Cobravel {

    private Plano plano;
    private int instrutorId; // 0 = sem instrutor
    private LocalDate dataMatricula;

    public Aluno(int id, String nome, String cpf, String telefone,
                 Plano plano, int instrutorId, LocalDate dataMatricula) {
        super(id, nome, cpf, telefone);
        this.plano = plano;
        this.instrutorId = instrutorId;
        this.dataMatricula = dataMatricula;
    }

    @Override
    public String getPapel() {
        return "Aluno";
    }

    @Override
    public double calcularMensalidade() {
        return plano.getValorMensal();
    }

    public Plano getPlano() {
        return plano;
    }

    public void setPlano(Plano plano) {
        this.plano = plano;
    }

    public int getInstrutorId() {
        return instrutorId;
    }

    public void setInstrutorId(int instrutorId) {
        this.instrutorId = instrutorId;
    }

    public LocalDate getDataMatricula() {
        return dataMatricula;
    }

    public void setDataMatricula(LocalDate dataMatricula) {
        this.dataMatricula = dataMatricula;
    }
}
