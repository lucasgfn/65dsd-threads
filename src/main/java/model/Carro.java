package model;

import controller.Controle;
import java.util.concurrent.TimeUnit;

public class Carro extends Thread {

    private final MalhaViaria malha;
    private MalhaBlocos posicaoAtual;
    private final String nome;
    private final Controle controle;
    private boolean rodando = true;

    public Carro(String nome, MalhaViaria malha, MalhaBlocos posicaoInicial, Controle controle) {
        this.nome = nome;
        this.malha = malha;
        this.posicaoAtual = posicaoInicial;
        this.controle = controle;
        posicaoInicial.setCarro(this);
    }

    @Override
    public void run() {
        try {
            while (rodando) {
                mover();
                controle.exibirMalha();
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            System.out.println(nome + " interrompido.");
        }
    }

    private void mover() throws InterruptedException {
        MalhaBlocos proximo = escolherProximoBloco();
        if (proximo == null) return;

        // Controle Semáforo ou Monitor
        if (proximo.isCruzamento()) {
            if (proximo.isUsarSemaforo()) {
                if (!proximo.getSemaphore().tryAcquire(50, TimeUnit.MILLISECONDS)) return;
            } else {
                proximo.entrarMonitor();
            }
        }

        synchronized (posicaoAtual) {
            if (proximo.getCarro() == null) {
                posicaoAtual.setCarro(null);
                proximo.setCarro(this);
                posicaoAtual = proximo;
            }
        }

        if (posicaoAtual.isSaida()) {
            parar();
            posicaoAtual.setCarro(null);
            if (proximo.isCruzamento()) {
                if (proximo.isUsarSemaforo()) proximo.getSemaphore().release();
                else proximo.sairMonitor();
            }
        }

        if (proximo.isCruzamento() && !posicaoAtual.isSaida()) {
            if (proximo.isUsarSemaforo()) proximo.getSemaphore().release();
            else proximo.sairMonitor();
        }
    }

    private MalhaBlocos escolherProximoBloco() {
        int i = posicaoAtual.getIdxLinha();
        int j = posicaoAtual.getIdxColuna();
        MalhaBlocos[][] matriz = malha.getMalha();
        var d = posicaoAtual.getDirecao();

        // Movimentos simples
        if (d.CIMA == 1 && i - 1 >= 0 && matriz[i-1][j].getCarro() == null) return matriz[i-1][j];
        if (d.BAIXO == 1 && i + 1 < matriz.length && matriz[i+1][j].getCarro() == null) return matriz[i+1][j];
        if (d.DIREITA == 1 && j + 1 < matriz[i].length && matriz[i][j+1].getCarro() == null) return matriz[i][j+1];
        if (d.ESQUERDA == 1 && j - 1 >= 0 && matriz[i][j-1].getCarro() == null) return matriz[i][j-1];

        return null;
    }

    public void parar() { rodando = false; }
    public String getNome() { return nome; }
}
