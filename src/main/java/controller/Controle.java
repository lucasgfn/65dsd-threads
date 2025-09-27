package controller;

import model.MalhaBlocos;
import model.MalhaViaria;
import util.Directions;
import view.View;
import java.io.IOException;

public class Controle {

    private View view;
    private MalhaViaria malhaViaria;

    public void setView(View view) {
        this.view = view;
    }


    public boolean criarMalhaViaria(String pathDoArquivo) {
        try {
            this.malhaViaria = new MalhaViaria(pathDoArquivo, 20); // 20 é um exemplo de limite de veículos
            System.out.println("Malha viária lida e criada com sucesso!");
            this.exibirMalha();
            return true; // Retorna true em caso de sucesso

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo da malha: " + e.getMessage());
            this.malhaViaria = null; // Garante que a malha inválida não seja usada
            this.view.getTextArea().setText(""); // Limpa a tela na view
            return false; // Retorna false em caso de erro
        }
    }

    public void exibirMalha() {
        if (malhaViaria == null || view == null) {
            return;
        }

        MalhaBlocos[][] malha = malhaViaria.getMalha();
        StringBuilder malhaComoTexto = new StringBuilder();

        for (int i = 0; i < malha.length; i++) {
            for (int j = 0; j < malha[i].length; j++) {
                MalhaBlocos bloco = malha[i][j];

                if (bloco == null || bloco.getDirecao() == Directions.VAZIO) {
                    malhaComoTexto.append("  ");
                } else if (bloco.getCarro() != null) {
                    malhaComoTexto.append("C ");
                } else {
                    switch (bloco.getDirecao()) {
                        case ESTRADA_CIMA: case CRUZAMENTO_CIMA:
                            malhaComoTexto.append("↑ ");
                            break;
                        case ESTRADA_BAIXO: case CRUZAMENTO_BAIXO:
                            malhaComoTexto.append("↓ ");
                            break;
                        case ESTRADA_ESQUERDA: case CRUZAMENTO_ESQUERDA:
                            malhaComoTexto.append("← ");
                            break;
                        case ESTRADA_DIREITA: case CRUZAMENTO_DIREITA:
                            malhaComoTexto.append("→ ");
                            break;
                        case CRUZAMENTO_CIMA_DIREITA:
                            malhaComoTexto.append("╔ ");
                            break;
                        case CRUZAMENTO_CIMA_ESQUERDA:
                            malhaComoTexto.append("╗ ");
                            break;
                        case CRUZAMENTO_BAIXO_DIREITA:
                            malhaComoTexto.append("╚ ");
                            break;
                        case CRUZAMENTO_BAIXO_ESQUERDA:
                            malhaComoTexto.append("╝ ");
                            break;
                        default:
                            malhaComoTexto.append("· ");
                            break;
                    }
                }
            }
            malhaComoTexto.append("\n");
        }
        view.getTextArea().setText(malhaComoTexto.toString());
    }

    // --- Métodos de simulação ---
    public void iniciarMonitor(int intervalo, int qtdCarros) {
        System.out.println("Monitor iniciado com intervalo: " + intervalo + " e carros: " + qtdCarros);
    }
    public void iniciarSemaforo(int intervalo, int qtdCarros) {
        System.out.println("Semáforo iniciado com intervalo: " + intervalo + " e carros: " + qtdCarros);
    }
    public void encerrar() {
        System.out.println("Encerrando simulação...");
    }
    public void aguardar() {
        System.out.println("Pausando/Continuando simulação...");
    }
}