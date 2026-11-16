package academia.tela;

import academia.dao.InstrutorDAO;
import academia.excecao.AcademiaException;
import academia.excecao.DadosInvalidosException;
import academia.modelo.Instrutor;
import academia.util.Validador;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/** Aba de cadastro de instrutores: incluir, alterar, excluir e consultar. */
public class PainelInstrutores extends JPanel {

    private InstrutorDAO dao = new InstrutorDAO();
    private List<Instrutor> instrutoresNaTabela = new ArrayList<>();
    private Instrutor selecionado = null;

    private JLabel lblNome, lblCpf, lblTelefone, lblEspecialidade, lblBusca;
    private JTextField txtNome, txtCpf, txtTelefone, txtEspecialidade, txtBusca;
    private JButton btnIncluir, btnAlterar, btnExcluir, btnLimpar, btnBuscar, btnTodos;
    private JTable tabela;
    private DefaultTableModel modelo;
    private JScrollPane scroll;

    public PainelInstrutores() {
        setLayout(null);

        lblNome = new JLabel("Nome:");
        lblNome.setBounds(20, 15, 60, 25);
        txtNome = new JTextField();
        txtNome.setBounds(85, 15, 260, 25);

        lblCpf = new JLabel("CPF:");
        lblCpf.setBounds(365, 15, 40, 25);
        txtCpf = new JTextField();
        txtCpf.setBounds(410, 15, 140, 25);

        lblTelefone = new JLabel("Telefone:");
        lblTelefone.setBounds(570, 15, 60, 25);
        txtTelefone = new JTextField();
        txtTelefone.setBounds(635, 15, 170, 25);

        lblEspecialidade = new JLabel("Especialidade:");
        lblEspecialidade.setBounds(20, 50, 100, 25);
        txtEspecialidade = new JTextField();
        txtEspecialidade.setBounds(125, 50, 220, 25);

        btnIncluir = new JButton("Incluir");
        btnIncluir.setBounds(20, 90, 100, 28);
        btnAlterar = new JButton("Alterar");
        btnAlterar.setBounds(130, 90, 100, 28);
        btnExcluir = new JButton("Excluir");
        btnExcluir.setBounds(240, 90, 100, 28);
        btnLimpar = new JButton("Limpar");
        btnLimpar.setBounds(350, 90, 100, 28);

        lblBusca = new JLabel("Buscar nome:");
        lblBusca.setBounds(465, 90, 90, 28);
        txtBusca = new JTextField();
        txtBusca.setBounds(550, 92, 100, 25);
        btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(655, 90, 80, 28);
        btnTodos = new JButton("Todos");
        btnTodos.setBounds(740, 90, 75, 28);

        // tabela que o usuario nao consegue editar na mao
        modelo = new DefaultTableModel(
                new String[]{"ID", "Nome", "CPF", "Telefone", "Especialidade"}, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
        tabela = new JTable(modelo);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getColumnModel().getColumn(0).setMaxWidth(40);
        int[] larguras = {40, 220, 120, 130, 150};
        for (int i = 0; i < larguras.length; i++) {
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }
        scroll = new JScrollPane(tabela);
        scroll.setBounds(20, 135, 795, 310);

        btnIncluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                incluir();
            }
        });
        btnAlterar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alterar();
            }
        });
        btnExcluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                excluir();
            }
        });
        btnLimpar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpar();
            }
        });
        btnBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscar();
            }
        });
        btnTodos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtBusca.setText("");
                atualizar();
            }
        });
        tabela.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int linha = tabela.getSelectedRow();
                if (!e.getValueIsAdjusting() && linha >= 0 && linha < instrutoresNaTabela.size()) {
                    selecionado = instrutoresNaTabela.get(linha);
                    txtNome.setText(selecionado.getNome());
                    txtCpf.setText(Validador.formatarCpf(selecionado.getCpf()));
                    txtTelefone.setText(Validador.formatarTelefone(selecionado.getTelefone()));
                    txtEspecialidade.setText(selecionado.getEspecialidade());
                }
            }
        });

        add(lblNome);
        add(txtNome);
        add(lblCpf);
        add(txtCpf);
        add(lblTelefone);
        add(txtTelefone);
        add(lblEspecialidade);
        add(txtEspecialidade);
        add(btnIncluir);
        add(btnAlterar);
        add(btnExcluir);
        add(btnLimpar);
        add(lblBusca);
        add(txtBusca);
        add(btnBuscar);
        add(btnTodos);
        add(scroll);

        atualizar();
    }

    private Instrutor lerFormulario(int id) {
        return new Instrutor(id, txtNome.getText().trim(),
                Validador.somenteDigitos(txtCpf.getText()),
                Validador.somenteDigitos(txtTelefone.getText()),
                txtEspecialidade.getText().trim());
    }

    private void incluir() {
        try {
            Instrutor i = lerFormulario(0);
            i.validar();
            dao.inserir(i);
            JOptionPane.showMessageDialog(this, "Instrutor incluído com sucesso.");
            limpar();
        } catch (AcademiaException ex) {
            mostrarErro(ex);
        } finally {
            atualizar();
        }
    }

    private void alterar() {
        try {
            if (selecionado == null) {
                throw new DadosInvalidosException("Selecione um instrutor na tabela.");
            }
            Instrutor i = lerFormulario(selecionado.getId());
            i.validar();
            dao.atualizar(i);
            JOptionPane.showMessageDialog(this, "Instrutor alterado com sucesso.");
            limpar();
        } catch (AcademiaException ex) {
            mostrarErro(ex);
        } finally {
            atualizar();
        }
    }

    private void excluir() {
        try {
            if (selecionado == null) {
                throw new DadosInvalidosException("Selecione um instrutor na tabela.");
            }
            int resposta = JOptionPane.showConfirmDialog(this,
                    "Excluir o instrutor " + selecionado.getNome() + "?\n"
                            + "Os alunos dele ficam sem instrutor.",
                    "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
            if (resposta == JOptionPane.YES_OPTION) {
                dao.excluir(selecionado.getId());
                limpar();
            }
        } catch (AcademiaException ex) {
            mostrarErro(ex);
        } finally {
            atualizar();
        }
    }

    private void buscar() {
        try {
            mostrar(dao.buscarPorNome(txtBusca.getText()));
        } catch (AcademiaException ex) {
            mostrarErro(ex);
        }
    }

    private void limpar() {
        selecionado = null;
        txtNome.setText("");
        txtCpf.setText("");
        txtTelefone.setText("");
        txtEspecialidade.setText("");
        tabela.clearSelection();
    }

    /** Recarrega a tabela com todos os instrutores do banco. */
    public void atualizar() {
        try {
            mostrar(dao.listar());
        } catch (AcademiaException ex) {
            mostrarErro(ex);
        }
    }

    private void mostrar(List<Instrutor> lista) {
        instrutoresNaTabela = lista;
        modelo.setRowCount(0);
        for (Instrutor i : lista) {
            modelo.addRow(new Object[]{i.getId(), i.getNome(), Validador.formatarCpf(i.getCpf()),
                    Validador.formatarTelefone(i.getTelefone()), i.getEspecialidade()});
        }
    }

    private void mostrarErro(AcademiaException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
    }
}
