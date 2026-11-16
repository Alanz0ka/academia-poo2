package academia.modelo;

/** Planos da academia. Quanto maior o periodo, menor a mensalidade. */
public enum Plano {

    MENSAL("Mensal", 120.00, 1),
    TRIMESTRAL("Trimestral", 105.00, 3),
    ANUAL("Anual", 90.00, 12);

    // atributos final: nao mudam depois que o plano e criado
    private final String nome;
    private final double valorMensal;
    private final int meses;

    Plano(String nome, double valorMensal, int meses) {
        this.nome = nome;
        this.valorMensal = valorMensal;
        this.meses = meses;
    }

    public String getNome() {
        return nome;
    }

    public double getValorMensal() {
        return valorMensal;
    }

    public int getMeses() {
        return meses;
    }

    @Override
    public String toString() {
        return nome;
    }
}
