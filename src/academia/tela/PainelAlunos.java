package academia.tela;

import academia.dao.AlunoDAO;
import academia.dao.InstrutorDAO;
import academia.excecao.AcademiaException;
import academia.excecao.DadosInvalidosException;
import academia.modelo.Aluno;
import academia.modelo.Instrutor;
import academia.modelo.Plano;
import academia.util.Validador;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Aba de cadastro de alunos: incluir, alterar, excluir e consultar. */
public class PainelAlunos extends JPanel {

    private AlunoDAO alunoDAO = new AlunoDAO();
    private InstrutorDAO instrutorDAO = new InstrutorDAO();

    // linhas que estao na tabela agora (a linha N da tabela e o aluno N da lista)
    private List<Aluno> alunosNaTabela = new ArrayList<>();
    // id do instrutor -> nome, para mostrar o nome na tabela em vez do numero
    private Map<Integer, String> nomesInstrutores = new HashMap<>();
    private Aluno selecionado = null;

    // item que representa "nenhum instrutor" no combo
    private final Instrutor semInstrutor = new Instrutor(0, "(sem instrutor)", "", "", "");
    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JLabel lblNome, lblCpf, lblTelefone, lblPlano, lblInstrutor, lblBusca;
    private JTextField txtNome, txtCpf, txtTelefone, txtBusca;
    private JComboBox<Plano> cbPlano;
    private JComboBox<Instrutor> cbInstrutor;
    private JButton btnIncluir, btnAlterar, btnExcluir, btnLimpar, btnBuscar, btnTodos;
    private JTable tabela;
    private DefaultTableModel modelo;
    private JScrollPane scroll;

    public PainelAlunos() {
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

        lblPlano = new JLabel("Plano:");
        lblPlano.setBounds(20, 50, 60, 25);
        cbPlano = new JComboBox<>(Plano.values());
        cbPlano.setBounds(85, 50, 150, 25);

        lblInstrutor = new JLabel("Instrutor:");
        lblInstrutor.setBounds(260, 50, 70, 25);
        cbInstrutor = new JComboBox<>();
        cbInstrutor.setBounds(335, 50, 215, 25);

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

        modelo = new DefaultTableModel(
                new String[]{"ID", "Nome", "CPF", "Telefone", "Plano", "Instrutor", "Mensalidade", "Matrícula"}, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
        tabela = new JTable(modelo);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getColumnModel().getColumn(0).setMaxWidth(40);
        int[] larguras = {35, 150, 125, 115, 75, 120, 90, 85};
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
                if (!e.getValueIsAdjusting() && linha >= 0 && linha < alunosNaTabela.size()) {
                    selecionado = alunosNaTabela.get(linha);
                    preencherFormulario(selecionado);
                }
            }
        });

        add(lblNome);
        add(txtNome);
        add(lblCpf);
        add(txtCpf);
        add(lblTelefone);
        add(txtTelefone);
        add(lblPlano);
        add(cbPlano);
        add(lblInstrutor);
        add(cbInstrutor);
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

    private Aluno lerFormulario(int id, LocalDate dataMatricula) {
        Plano plano = (Plano) cbPlano.getSelectedItem();
        Instrutor instrutor = (Instrutor) cbInstrutor.getSelectedItem();
        return new Aluno(id, txtNome.getText().trim(),
                Validador.somenteDigitos(txtCpf.getText()),
                Validador.somenteDigitos(txtTelefone.getText()),
                plano, instrutor.getId(), dataMatricula);
    }

    private void preencherFormulario(Aluno a) {
        txtNome.setText(a.getNome());
        txtCpf.setText(Validador.formatarCpf(a.getCpf()));
        txtTelefone.setText(Validador.formatarTelefone(a.getTelefone()));
        cbPlano.setSelectedItem(a.getPlano());
        // procura no combo o instrutor com o mesmo id
        cbInstrutor.setSelectedItem(semInstrutor);
        for (int i = 0; i < cbInstrutor.getItemCount(); i++) {
            if (cbInstrutor.getItemAt(i).getId() == a.getInstrutorId()) {
                cbInstrutor.setSelectedIndex(i);
            }
        }
    }

    private void incluir() {
        try {
            Aluno a = lerFormulario(0, LocalDate.now());
            a.validar();
            alunoDAO.inserir(a);
            JOptionPane.showMessageDialog(this, "Aluno incluído com sucesso.");
            limpar();
        } catch (AcademiaException ex) {
            mostrarErro(ex);
        } finally {
            // roda com ou sem erro: a tabela sempre volta a refletir o banco
            atualizar();
        }
    }

    private void alterar() {
        try {
            if (selecionado == null) {
                throw new DadosInvalidosException("Selecione um aluno na tabela.");
            }
            Aluno a = lerFormulario(selecionado.getId(), selecionado.getDataMatricula());
            a.validar();
            alunoDAO.atualizar(a);
            JOptionPane.showMessageDialog(this, "Aluno alterado com sucesso.");
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
                throw new DadosInvalidosException("Selecione um aluno na tabela.");
            }
            int resposta = JOptionPane.showConfirmDialog(this,
                    "Excluir o aluno " + selecionado.getNome() + "?",
                    "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
            if (resposta == JOptionPane.YES_OPTION) {
                alunoDAO.excluir(selecionado.getId());
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
            mostrar(alunoDAO.buscarPorNome(txtBusca.getText()));
        } catch (AcademiaException ex) {
            mostrarErro(ex);
        }
    }

    private void limpar() {
        selecionado = null;
        txtNome.setText("");
        txtCpf.setText("");
        txtTelefone.setText("");
        cbPlano.setSelectedIndex(0);
        cbInstrutor.setSelectedItem(semInstrutor);
        tabela.clearSelection();
    }

    /** Recarrega o combo de instrutores e a tabela com os dados atuais do banco. */
    public void atualizar() {
        try {
            carregarInstrutores();
            mostrar(alunoDAO.listar());
        } catch (AcademiaException ex) {
            mostrarErro(ex);
        }
    }

    private void carregarInstrutores() throws AcademiaException {
        Instrutor escolhido = (Instrutor) cbInstrutor.getSelectedItem();
        int idEscolhido = (escolhido == null) ? 0 : escolhido.getId();

        nomesInstrutores.clear();
        cbInstrutor.removeAllItems();
        cbInstrutor.addItem(semInstrutor);
        for (Instrutor i : instrutorDAO.listar()) {
            cbInstrutor.addItem(i);
            nomesInstrutores.put(i.getId(), i.getNome());
            if (i.getId() == idEscolhido) {
                cbInstrutor.setSelectedItem(i);
            }
        }
    }

    private void mostrar(List<Aluno> lista) {
        alunosNaTabela = lista;
        modelo.setRowCount(0);
        for (Aluno a : lista) {
            String instrutor = nomesInstrutores.getOrDefault(a.getInstrutorId(), "-");
            modelo.addRow(new Object[]{a.getId(), a.getNome(), Validador.formatarCpf(a.getCpf()),
                    Validador.formatarTelefone(a.getTelefone()), a.getPlano(), instrutor,
                    a.resumoCobranca(), a.getDataMatricula().format(formatoData)});
        }
    }

    private void mostrarErro(AcademiaException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
    }
}
