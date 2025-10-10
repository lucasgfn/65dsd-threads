package controller;

import model.Carro;
import model.MalhaBlocos;
import model.MalhaViaria;
import view.SimuladorTrafegoView;

import java.util.ArrayList;
import java.util.List;

public class Controle {

    private SimuladorTrafegoView view;
    private MalhaViaria malhaViaria;
    private boolean usarSemaforo = true;
    private int maxVeiculos = 10;
    private int intervaloInsercao = 500; // ms

    private final List<Carro> carros = new ArrayList<>();
    private boolean insercaoAtiva = false;
    private Thread threadInsercao;

    public void setView(SimuladorTrafegoView view) { this.view = view; }
    public void setModoControle(boolean semaforo) { this.usarSemaforo = semaforo; }
    public void setMaxVeiculos(int max) { this.maxVeiculos = max; }
    public void setIntervaloInsercao(int intervalo) { this.intervaloInsercao = intervalo; }

    public boolean criarMalha(String path) {
        try {
            this.malhaViaria = new MalhaViaria(path, maxVeiculos, usarSemaforo);
            atualizarView();
            return true;
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            return false;
        }
    }

    public void atualizarView() { exibirMalha(); }

    public void exibirMalha() {
        if (malhaViaria == null || view == null) return;

        MalhaBlocos[][] malha = malhaViaria.getMalha();
        StringBuilder sb = new StringBuilder();

        for (MalhaBlocos[] linha : malha) {
            for (MalhaBlocos bloco : linha) {
                String symbol = " ";
                if (bloco == null || bloco.getDirecao() == null) symbol = " ";
                else if (bloco.getCarro() != null) symbol = "\uD83D\uDE98";
                else switch (bloco.getDirecao()) {
                        case ESTRADA_CIMA, CRUZAMENTO_CIMA -> symbol = "↑";
                        case ESTRADA_BAIXO, CRUZAMENTO_BAIXO -> symbol = "↓";
                        case ESTRADA_ESQUERDA, CRUZAMENTO_ESQUERDA -> symbol = "←";
                        case ESTRADA_DIREITA, CRUZAMENTO_DIREITA -> symbol = "→";
                        case CRUZAMENTO_CIMA_DIREITA -> symbol = "╔";
                        case CRUZAMENTO_CIMA_ESQUERDA -> symbol = "╗";
                        case CRUZAMENTO_BAIXO_DIREITA -> symbol = "╚";
                        case CRUZAMENTO_BAIXO_ESQUERDA -> symbol = "╝";
                        default -> symbol = "·";
                    }
                sb.append(String.format("%-2s", symbol));
            }
            sb.append("\n");
        }
        view.atualizarMalha(sb.toString());
    }

    // inserção de veículos
    public void iniciarSimulacao() {
        if (malhaViaria == null) return;
        insercaoAtiva = true;

        threadInsercao = new Thread(() -> {
            while (insercaoAtiva) {
                synchronized (carros) {
                    // Insere apenas se não ultrapassar maxVeiculos
                    while (carros.size() < maxVeiculos) {
                        if (!inserirVeiculo()) break;
                    }
                }

                try { Thread.sleep(intervaloInsercao); }
                catch (InterruptedException e) { break; }
            }
        });
        threadInsercao.start();
    }

    private boolean inserirVeiculo() {
        MalhaBlocos[][] malha = malhaViaria.getMalha();
        for (int i = 0; i < malha.length; i++) {
            for (int j = 0; j < malha[i].length; j++) {
                MalhaBlocos bloco = malha[i][j];
                if (bloco != null && bloco.isEntrada() && bloco.getCarro() == null) {
                    Carro carro = new Carro("Carro" + carros.size(), malhaViaria, bloco, this);
                    bloco.setCarro(carro);
                    carros.add(carro);
                    carro.start();
                    return true;
                }
            }
        }
        return false;
    }
    public void encerrarInsercao() {
        insercaoAtiva = false;
        if (threadInsercao != null) threadInsercao.interrupt();
    }

    public void encerrarSimulacao() {
        encerrarInsercao();
        synchronized (carros) {
            for (Carro c : carros) c.parar();
            carros.clear();
        }
        atualizarView();
    }

    public void removerVeiculo(Carro carro) {
        synchronized (carros) {
            carros.remove(carro);
        }
    }
}
