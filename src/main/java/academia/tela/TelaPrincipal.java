package academia.tela;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/** Janela principal com as tres abas. */
public class TelaPrincipal extends JFrame {

    private JTabbedPane abas;
    private PainelAlunos painelAlunos;
    private PainelInstrutores painelInstrutores;
    private PainelRelatorio painelRelatorio;

    public TelaPrincipal() {
        setTitle("Academia - Cadastro de Alunos e Instrutores");
        setSize(850, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        painelAlunos = new PainelAlunos();
        painelInstrutores = new PainelInstrutores();
        painelRelatorio = new PainelRelatorio();

        abas = new JTabbedPane();
        abas.addTab("Alunos", painelAlunos);
        abas.addTab("Instrutores", painelInstrutores);
        abas.addTab("Relatório", painelRelatorio);

        // ao trocar de aba, recarrega os dados dela (ex.: instrutor novo aparece no combo dos alunos)
        abas.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int aba = abas.getSelectedIndex();
                if (aba == 0) {
                    painelAlunos.atualizar();
                } else if (aba == 1) {
                    painelInstrutores.atualizar();
                } else {
                    painelRelatorio.atualizar();
                }
            }
        });

        add(abas);
    }
}
