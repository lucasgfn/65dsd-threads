package model;

import controller.Controle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Carro extends Thread {

    private final MalhaViaria malha;
    private MalhaBlocos posicaoAtual;
    private final String nome;
    private final Controle controle;
    private boolean rodando = true;
    private final int velocidade;
    private final Random random = new Random();

    public Carro(String nome, MalhaViaria malha, MalhaBlocos posicaoInicial, Controle controle) {
        this.nome = nome;
        this.malha = malha;
        this.posicaoAtual = posicaoInicial;
        this.controle = controle;
        posicaoInicial.setCarro(this);
        //velocidades diferentes
        this.velocidade = 400 + random.nextInt(500);
    }

    @Override
    public void run() {
        try {
            while (rodando) {
                mover();
                controle.exibirMalha();
                Thread.sleep(velocidade);
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

        // Movimentação do carro
        // O synchronized aqui previne condições de corrida onde dois carros tentam entrar no mesmo bloco
        synchronized (malha) { // Sincroniza na malha para garantir a atomicidade da verificação e do movimento
            if (proximo.getCarro() == null) {
                posicaoAtual.setCarro(null);
                proximo.setCarro(this);
                MalhaBlocos blocoAnterior = posicaoAtual; // Salva a posição anterior para liberar o lock
                posicaoAtual = proximo;

                // Libera o lock do cruzamento *após* sair do bloco anterior
                // e) Não deve bloquear o cruzamento de outros veículos
                if (blocoAnterior.isCruzamento()) {
                    if (blocoAnterior.isUsarSemaforo()) {
                        blocoAnterior.getSemaphore().release();
                    } else {
                        blocoAnterior.sairMonitor();
                    }
                }
            } else {
                // Se o próximo bloco foi ocupado, libera o lock adquirido
                if (proximo.isCruzamento()) {
                    if (proximo.isUsarSemaforo()) {
                        proximo.getSemaphore().release();
                    } else {
                        proximo.sairMonitor();
                    }
                }
                return;
            }
        }


        if (posicaoAtual.isSaida()) {
            parar(); // Para a thread do carro
            posicaoAtual.setCarro(null);
            controle.removerVeiculo(this);
        }
    }

    private MalhaBlocos escolherProximoBloco() {
        int i = posicaoAtual.getIdxLinha();
        int j = posicaoAtual.getIdxColuna();
        MalhaBlocos[][] matriz = malha.getMalha();
        var d = posicaoAtual.getDirecao();
        List<MalhaBlocos> possiveisMovimentos = new ArrayList<>();

          if (d.CIMA == 1 && i - 1 >= 0 && matriz[i-1][j].getCarro() == null) {
            possiveisMovimentos.add(matriz[i-1][j]);
        }
        if (d.BAIXO == 1 && i + 1 < matriz.length && matriz[i+1][j].getCarro() == null) {
            possiveisMovimentos.add(matriz[i+1][j]);
        }
        if (d.DIREITA == 1 && j + 1 < matriz[i].length && matriz[i][j+1].getCarro() == null) {
            possiveisMovimentos.add(matriz[i][j+1]);
        }
        if (d.ESQUERDA == 1 && j - 1 >= 0 && matriz[i][j-1].getCarro() == null) {
            possiveisMovimentos.add(matriz[i][j-1]);
        }

        if (possiveisMovimentos.isEmpty()) {
            return null;
        }

        //escolha aleatória dos movimentos possíveis
        return possiveisMovimentos.get(random.nextInt(possiveisMovimentos.size()));
    }

    public void parar() { rodando = false; }
    public String getNome() { return nome; }
}