package view;

import controller.Controle;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class SimuladorTrafegoView extends JFrame {

    // Enum para controlar os estados da UI de forma clara
    private enum EstadoUI {
        INICIAL, MALHA_CARREGADA, SIMULANDO
    }

    // Enum para diferenciar o tipo de simulação iniciada
    private enum TipoSimulacao {
        MONITOR, SEMAFORO
    }

    private Controle controle;

    // --- Componentes da UI ---
    // Painéis para organização
    private JPanel painelPrincipal;
    private JPanel painelConfiguracao;
    private JPanel painelControles;
    private JPanel painelArquivo;

    // Componentes de interação
    private JButton btnSelecionarMalha;
    private JTextField txtCaminhoArquivo;
    private JTextField txtQtdCarros;
    private JTextField txtIntervaloInsercao;
    private JButton btnIniciarMonitor;
    private JButton btnIniciarSemaforo;
    private JButton btnAguardar;
    private JButton btnEncerrar;
    private JTextArea areaMalha;
    private JScrollPane scrollPaneMalha;

    public SimuladorTrafegoView() {
        super("Simulador de Malha Viária"); // Define um título para a janela
        this.inicializarComponentes();
        this.configurarLayout();
        this.configurarListeners();
        this.iniciarControlador();

        configurarEstadoUI(EstadoUI.INICIAL); // Estado inicial da aplicação

        // Configurações da janela principal
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack(); // Ajusta o tamanho da janela aos componentes
        setLocationRelativeTo(null); // Centraliza a janela na tela
        setResizable(false);
    }

    private void iniciarControlador() {
        this.controle = new Controle();
        this.controle.setView(this);
    }


    private void inicializarComponentes() {
        // Painel Principal
        painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Seção de Arquivo
        painelArquivo = new JPanel(new BorderLayout(5, 5));
        painelArquivo.setBorder(new TitledBorder("1. Seleção da Malha"));
        btnSelecionarMalha = new JButton("Selecionar Arquivo...");
        txtCaminhoArquivo = new JTextField(30);
        txtCaminhoArquivo.setEditable(false);

        // Seção de Configuração da Simulação
        painelConfiguracao = new JPanel(new GridLayout(2, 2, 5, 5));
        painelConfiguracao.setBorder(new TitledBorder("2. Parâmetros da Simulação"));
        txtQtdCarros = new JTextField(10);
        txtIntervaloInsercao = new JTextField("500", 10);

        // Seção de Controles da Simulação
        painelControles = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        painelControles.setBorder(new TitledBorder("3. Ações"));
        btnIniciarMonitor = new JButton("Iniciar com Monitor");
        btnIniciarSemaforo = new JButton("Iniciar com Semáforo");
        btnAguardar = new JButton("Pausar Inserção");
        btnEncerrar = new JButton("Encerrar Simulação");

        // Área de Texto para a Malha
        areaMalha = new JTextArea(30, 70); // Tamanho ajustado
        areaMalha.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14)); // Fonte um pouco maior
        areaMalha.setEditable(false);
        scrollPaneMalha = new JScrollPane(areaMalha);
    }

    private void configurarLayout() {
        // Monta o painel de arquivo
        painelArquivo.add(txtCaminhoArquivo, BorderLayout.CENTER);
        painelArquivo.add(btnSelecionarMalha, BorderLayout.EAST);

        // Monta o painel de configuração
        painelConfiguracao.add(new JLabel("Quantidade de Carros:"));
        painelConfiguracao.add(txtQtdCarros);
        painelConfiguracao.add(new JLabel("Intervalo de Inserção (ms):"));
        painelConfiguracao.add(txtIntervaloInsercao);

        // Monta o painel de controles
        painelControles.add(btnIniciarMonitor);
        painelControles.add(btnIniciarSemaforo);
        painelControles.add(btnAguardar);
        painelControles.add(btnEncerrar);

        // Agrupa os painéis de configuração e controles
        JPanel painelSuperior = new JPanel(new BorderLayout(10,10));
        painelSuperior.add(painelArquivo, BorderLayout.NORTH);

        JPanel painelCentral = new JPanel();
        painelCentral.add(painelConfiguracao);
        painelCentral.add(painelControles);

        painelSuperior.add(painelCentral, BorderLayout.CENTER);

        // Adiciona tudo ao painel principal
        painelPrincipal.add(painelSuperior, BorderLayout.NORTH);
        painelPrincipal.add(scrollPaneMalha, BorderLayout.CENTER);

        // Adiciona o painel principal ao JFrame
        this.add(painelPrincipal);
    }

    private void configurarListeners() {
        btnSelecionarMalha.addActionListener(e -> selecionarArquivoMalha());
        btnIniciarMonitor.addActionListener(e -> iniciarSimulacao(TipoSimulacao.MONITOR));
        btnIniciarSemaforo.addActionListener(e -> iniciarSimulacao(TipoSimulacao.SEMAFORO));
        btnEncerrar.addActionListener(e -> encerrarSimulacao());
        btnAguardar.addActionListener(e -> aguardarInsercao());
    }

    private void selecionarArquivoMalha() {
        JFileChooser fileChooser = new JFileChooser("./"); // Inicia no diretório do projeto
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int resultado = fileChooser.showOpenDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File arquivo = fileChooser.getSelectedFile();
            txtCaminhoArquivo.setText(arquivo.getName()); // Mostra apenas o nome do arquivo

            boolean sucesso = controle.criarMalhaViaria(arquivo.getAbsolutePath());

            if (sucesso) {
                configurarEstadoUI(EstadoUI.MALHA_CARREGADA);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Falha ao ler ou processar o arquivo da malha.\nVerifique o console para mais detalhes.",
                        "Erro de Leitura", JOptionPane.ERROR_MESSAGE);
                limparSelecao();
            }
        } else {
            limparSelecao();
        }
    }

    private void iniciarSimulacao(TipoSimulacao tipo) {
        String qtdCarrosStr = txtQtdCarros.getText();
        String intervaloStr = txtIntervaloInsercao.getText();

        if (qtdCarrosStr.isEmpty() || intervaloStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Preencha a quantidade de carros e o intervalo antes de iniciar!",
                    "Dados Incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int qtdCarros = Integer.parseInt(qtdCarrosStr);
            int intervalo = Integer.parseInt(intervaloStr);

            configurarEstadoUI(EstadoUI.SIMULANDO);

            if (tipo == TipoSimulacao.MONITOR) {
                controle.iniciarMonitor(intervalo, qtdCarros);
            } else {
                controle.iniciarSemaforo(intervalo, qtdCarros);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Os valores para carros e intervalo devem ser números inteiros.",
                    "Entrada Inválida", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void encerrarSimulacao() {
        controle.encerrar();
        iniciarControlador(); // Reinicia o controlador para uma nova simulação
        configurarEstadoUI(EstadoUI.INICIAL);
    }

    private void aguardarInsercao() {
        controle.aguardar();
        btnAguardar.setEnabled(false);
        btnAguardar.setText("Inserção Pausada");
    }

    private void limparSelecao() {
        txtCaminhoArquivo.setText("");
        areaMalha.setText("");
        configurarEstadoUI(EstadoUI.INICIAL);
    }

    private void configurarEstadoUI(EstadoUI estado) {
        switch (estado) {
            case INICIAL:
                btnSelecionarMalha.setEnabled(true);
                txtCaminhoArquivo.setText("");
                txtQtdCarros.setText("");
                txtQtdCarros.setEnabled(false);
                txtIntervaloInsercao.setEnabled(false);

                btnIniciarMonitor.setEnabled(false);
                btnIniciarSemaforo.setEnabled(false);
                btnAguardar.setEnabled(false);
                btnAguardar.setText("Pausar Inserção");
                btnEncerrar.setEnabled(false);

                areaMalha.setText("");
                break;

            case MALHA_CARREGADA:
                btnSelecionarMalha.setEnabled(true);
                txtQtdCarros.setEnabled(true);
                txtIntervaloInsercao.setEnabled(true);

                btnIniciarMonitor.setEnabled(true);
                btnIniciarSemaforo.setEnabled(true);
                btnAguardar.setEnabled(false);
                btnEncerrar.setEnabled(false);
                break;

            case SIMULANDO:
                btnSelecionarMalha.setEnabled(false);
                txtQtdCarros.setEnabled(false);
                txtIntervaloInsercao.setEnabled(false);

                btnIniciarMonitor.setEnabled(false);
                btnIniciarSemaforo.setEnabled(false);
                btnAguardar.setEnabled(true);
                btnEncerrar.setEnabled(true);
                break;
        }
    }

    // --- Métodos Públicos para o Controlador ---
    public void atualizarMalha(String textoMalha) {
        // Garante que a atualização da UI ocorra na thread de eventos do Swing
        SwingUtilities.invokeLater(() -> {
            areaMalha.setText(textoMalha);
        });
    }

    public static void main(String args[]) {
        // Garante que a UI seja criada na Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new SimuladorTrafegoView().setVisible(true);
        });
    }
}