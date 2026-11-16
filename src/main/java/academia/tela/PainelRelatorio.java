package academia.tela;

import academia.dao.AlunoDAO;
import academia.dao.Conexao;
import academia.dao.InstrutorDAO;
import academia.excecao.AcademiaException;
import academia.modelo.Aluno;
import academia.modelo.Cobravel;
import academia.modelo.Instrutor;
import academia.modelo.Plano;

import javax.swing.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Aba com o resumo da academia. Usa List, Map, EnumMap, TreeMap e Comparator. */
public class PainelRelatorio extends JPanel {

    private AlunoDAO alunoDAO = new AlunoDAO();
    private InstrutorDAO instrutorDAO = new InstrutorDAO();

    private JTextArea areaTexto;
    private JScrollPane scroll;
    private JButton btnAtualizar;

    public PainelRelatorio() {
        setLayout(null);

        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        scroll = new JScrollPane(areaTexto);
        scroll.setBounds(20, 55, 795, 390);

        btnAtualizar = new JButton("Atualizar relatório");
        btnAtualizar.setBounds(20, 15, 180, 28);
        btnAtualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atualizar();
            }
        });

        add(btnAtualizar);
        add(scroll);
        atualizar();
    }

    public void atualizar() {
        try {
            areaTexto.setText(montarRelatorio(alunoDAO.listar(), instrutorDAO.listar()));
            areaTexto.setCaretPosition(0);
        } catch (AcademiaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String montarRelatorio(List<Aluno> alunos, List<Instrutor> instrutores) {
        // 1) alunos por plano: EnumMap, comeca com zero em todos os planos
        Map<Plano, Integer> porPlano = new EnumMap<>(Plano.class);
        for (Plano p : Plano.values()) {
            porPlano.put(p, 0);
        }

        // 2) alunos por instrutor: TreeMap deixa os nomes em ordem alfabetica
        Map<Integer, String> nomes = new HashMap<>();
        Map<String, Integer> porInstrutor = new TreeMap<>();
        for (Instrutor i : instrutores) {
            nomes.put(i.getId(), i.getNome());
            porInstrutor.put(i.getNome(), 0);
        }
        porInstrutor.put("(sem instrutor)", 0);

        // 3) cobrancas: a lista guarda Cobravel (a interface), nao Aluno
        List<Cobravel> cobrancas = new ArrayList<>();
        double receita = 0;

        for (Aluno a : alunos) {
            porPlano.put(a.getPlano(), porPlano.get(a.getPlano()) + 1);
            String instrutor = nomes.getOrDefault(a.getInstrutorId(), "(sem instrutor)");
            porInstrutor.put(instrutor, porInstrutor.get(instrutor) + 1);
            cobrancas.add(a);
        }
        for (Cobravel c : cobrancas) {
            receita += c.calcularMensalidade();
        }

        // 4) ranking de mensalidade: da maior para a menor, empate pelo nome
        List<Aluno> ranking = new ArrayList<>(alunos);
        Collections.sort(ranking, new Comparator<Aluno>() {
            @Override
            public int compare(Aluno a, Aluno b) {
                int comparacao = Double.compare(b.calcularMensalidade(), a.calcularMensalidade());
                if (comparacao != 0) {
                    return comparacao;
                }
                return a.getNome().compareToIgnoreCase(b.getNome());
            }
        });

        StringBuilder sb = new StringBuilder();
        sb.append("RESUMO DA ACADEMIA\n");
        sb.append("==================\n\n");
        sb.append("Total de alunos ........: ").append(alunos.size()).append("\n");
        sb.append("Total de instrutores ...: ").append(instrutores.size()).append("\n");
        sb.append(String.format("Receita mensal prevista : R$ %.2f%n", receita));
        if (!alunos.isEmpty()) {
            sb.append(String.format("Mensalidade média ......: R$ %.2f%n", receita / alunos.size()));
        }

        sb.append("\nALUNOS POR PLANO\n");
        for (Map.Entry<Plano, Integer> entrada : porPlano.entrySet()) {
            sb.append(String.format("  %-12s %d%n", entrada.getKey().getNome(), entrada.getValue()));
        }

        sb.append("\nALUNOS POR INSTRUTOR\n");
        for (Map.Entry<String, Integer> entrada : porInstrutor.entrySet()) {
            sb.append(String.format("  %-22s %d%n", entrada.getKey(), entrada.getValue()));
        }

        sb.append("\nMENSALIDADES (maior para menor)\n");
        for (Aluno a : ranking) {
            sb.append(String.format("  %-24s %-11s %s%n", a.getNome(), a.getPlano(), a.resumoCobranca()));
        }

        sb.append("\nConexões abertas com o banco nesta execução: ").append(Conexao.getConexoesAbertas());
        return sb.toString();
    }
}
