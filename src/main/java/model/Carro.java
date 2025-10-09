package model;

import controller.Controle;
import util.Directions;
import model.MalhaBlocos;
import model.MalhaViaria;

import java.util.concurrent.Semaphore;

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
        MalhaBlocos proximoBloco = escolherProximoBloco();

        if (proximoBloco == null) {
            parar();
            return;
        }

        Semaphore semaforo = proximoBloco.getSemaphore();
        if (semaforo != null) {
            semaforo.acquire();
        }

        synchronized (proximoBloco) {
            if (proximoBloco.getCarro() == null) {
                Semaphore semaforoBlocoAtual = posicaoAtual.getSemaphore();

                posicaoAtual.setCarro(null);
                proximoBloco.setCarro(this);
                posicaoAtual = proximoBloco;

                if (semaforoBlocoAtual != null) {
                    semaforoBlocoAtual.release();
                }
            }
        }

        if (posicaoAtual.isSaida()) {
            posicaoAtual.setCarro(null);
            parar();
        }
    }

    private MalhaBlocos escolherProximoBloco() {
        int i = posicaoAtual.getIdxLinha();
        int j = posicaoAtual.getIdxColuna();
        MalhaBlocos[][] matriz = malha.getMalha();
        Directions d = posicaoAtual.getDirecao();

        MalhaBlocos proximo = null;

        if (d.CIMA == 1 && i - 1 >= 0) {
            proximo = matriz[i - 1][j];
            if(proximo.getCarro() == null) return proximo;
        }
        if (d.BAIXO == 1 && i + 1 < matriz.length) {
            proximo = matriz[i + 1][j];
            if(proximo.getCarro() == null) return proximo;
        }
        if (d.DIREITA == 1 && j + 1 < matriz[i].length) {
            proximo = matriz[i][j + 1];
            if(proximo.getCarro() == null) return proximo;
        }
        if (d.ESQUERDA == 1 && j - 1 >= 0) {
            proximo = matriz[i][j - 1];
            if(proximo.getCarro() == null) return proximo;
        }

        return null;
    }

    public void parar() {
        rodando = false;
    }

    public String getNome() {
        return nome;
    }
}