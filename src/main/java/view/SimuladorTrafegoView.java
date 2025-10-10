package view;

import controller.Controle;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;

public class SimuladorTrafegoView extends JFrame {

    private Controle controle;

    // Componentes da interface
    private JTextField txtCaminhoArquivo, txtQtdVeiculos, txtIntervalo;
    private JButton btnSelecionarArquivo, btnIniciarSimulacao, btnEncerrarInsercao, btnEncerrarSimulacao;
    private JRadioButton rbSemaforo, rbMonitor;
    private JTextArea areaMalha;
    private JScrollPane scrollPaneMalha;

    public SimuladorTrafegoView() {
        super("Simulador de Malha Viária");

        controle = new Controle();
        controle.setView(this);

        inicializarComponentes();
        configurarLayout();
        configurarListeners();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void inicializarComponentes() {
        txtCaminhoArquivo = new JTextField(30);
        txtCaminhoArquivo.setEditable(false);

        txtQtdVeiculos = new JTextField("10", 5);
        txtIntervalo = new JTextField("500", 5);

        btnSelecionarArquivo = new JButton("Selecionar Arquivo");
        btnIniciarSimulacao = new JButton("Iniciar Simulação");
        btnEncerrarInsercao = new JButton("Encerrar Inserção");
        btnEncerrarSimulacao = new JButton("Encerrar Simulação");

        rbSemaforo = new JRadioButton("Semáforo", true);
        rbMonitor = new JRadioButton("Monitor");
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbSemaforo);
        grupo.add(rbMonitor);

        areaMalha = new JTextArea(30, 70);
        areaMalha.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        areaMalha.setEditable(false);
        scrollPaneMalha = new JScrollPane(areaMalha);
    }

    private void configurarLayout() {
        JPanel painelArquivo = new JPanel(new BorderLayout(5,5));
        painelArquivo.setBorder(new TitledBorder("1. Seleção de Malha"));
        painelArquivo.add(txtCaminhoArquivo, BorderLayout.CENTER);
        painelArquivo.add(btnSelecionarArquivo, BorderLayout.EAST);

        JPanel painelConfiguracao = new JPanel(new FlowLayout());
        painelConfiguracao.setBorder(new TitledBorder("2. Parâmetros da Simulação"));
        painelConfiguracao.add(new JLabel("Qtd Veículos:"));
        painelConfiguracao.add(txtQtdVeiculos);
        painelConfiguracao.add(new JLabel("Intervalo (ms):"));
        painelConfiguracao.add(txtIntervalo);
        painelConfiguracao.add(rbSemaforo);
        painelConfiguracao.add(rbMonitor);

        JPanel painelControles = new JPanel(new FlowLayout());
        painelControles.setBorder(new TitledBorder("3. Controles"));
        painelControles.add(btnIniciarSimulacao);
        painelControles.add(btnEncerrarInsercao);
        painelControles.add(btnEncerrarSimulacao);

        JPanel painelSuperior = new JPanel(new BorderLayout());
        painelSuperior.add(painelArquivo, BorderLayout.NORTH);
        painelSuperior.add(painelConfiguracao, BorderLayout.CENTER);
        painelSuperior.add(painelControles, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(painelSuperior, BorderLayout.NORTH);
        add(scrollPaneMalha, BorderLayout.CENTER);
    }

    private void configurarListeners() {
        btnSelecionarArquivo.addActionListener(e -> selecionarArquivo());
        btnIniciarSimulacao.addActionListener(e -> iniciarSimulacao());
        btnEncerrarInsercao.addActionListener(e -> controle.encerrarInsercao());
        btnEncerrarSimulacao.addActionListener(e -> {
            controle.encerrarSimulacao();
            atualizarMalha(" ");
        });
    }

    private void selecionarArquivo() {
        JFileChooser chooser = new JFileChooser("./");
        int resultado = chooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File arquivo = chooser.getSelectedFile();
            txtCaminhoArquivo.setText(arquivo.getAbsolutePath());
            boolean sucesso = controle.criarMalha(arquivo.getAbsolutePath());
            if (!sucesso) {
                JOptionPane.showMessageDialog(this, "Erro ao ler a malha.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void iniciarSimulacao() {
        try {
            int qtdVeiculos = Integer.parseInt(txtQtdVeiculos.getText());
            int intervalo = Integer.parseInt(txtIntervalo.getText());
            controle.setMaxVeiculos(qtdVeiculos);
            controle.setIntervaloInsercao(intervalo);
            controle.setModoControle(rbSemaforo.isSelected());
            controle.iniciarSimulacao();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Qtd veículos e intervalo devem ser números inteiros.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void atualizarMalha(String texto) {
        SwingUtilities.invokeLater(() -> areaMalha.setText(texto));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimuladorTrafegoView().setVisible(true));
    }
}
