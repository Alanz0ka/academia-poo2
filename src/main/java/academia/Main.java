package academia;

import academia.dao.BancoDeDados;
import academia.excecao.AcademiaException;
import academia.tela.TelaPrincipal;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            // cria as tabelas (se nao existirem) antes de abrir a tela
            BancoDeDados.preparar();
        } catch (AcademiaException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Erro de conexão", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TelaPrincipal().setVisible(true);
            }
        });
    }
}
