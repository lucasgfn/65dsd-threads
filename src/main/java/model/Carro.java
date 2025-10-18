package model;

import controller.Controle;
import util.Directions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Carro extends Thread {

    private final MalhaViaria malha;
    private MalhaBlocos posicaoAtual;
    private final int idCarro;
    private final String nome;
    private final Controle controle;
    private boolean rodando = true;
    private final int velocidade;
    private final Random random = new Random();

    private Cruzamento cruzamentoReservado = null;
    private List<MalhaBlocos> caminhoReservado = null;

    public Carro(int idCarro, String nome, MalhaViaria malha, MalhaBlocos posicaoInicial, Controle controle) {
        this.nome = nome;
        this.idCarro = idCarro;
        this.malha = malha;
        this.posicaoAtual = posicaoInicial;
        this.controle = controle;
        posicaoInicial.setCarro(this);
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
            Thread.currentThread().interrupt();
            System.out.println(nome + " foi interrompido e finalizado.");
        } finally {
            // 💡 Segurança: garante liberação ao terminar
            if (cruzamentoReservado != null) {
                System.out.println(nome + " finalizando e liberando cruzamento " + cruzamentoReservado.getId());
                cruzamentoReservado.liberarCaminho();
            }
        }
    }

    private void mover() throws InterruptedException {
        MalhaBlocos proximoBloco = escolherProximoBloco();

        if (caminhoReservado != null && !caminhoReservado.isEmpty()) {
            proximoBloco = caminhoReservado.remove(0);
        } else {
            proximoBloco = escolherProximoBloco();
        }

        if (proximoBloco == null) {
            if (cruzamentoReservado != null) {
                System.out.println(nome + " sem movimento — liberando cruzamento " + cruzamentoReservado.getId());
                cruzamentoReservado.liberarCaminho();
                cruzamentoReservado = null;
            }
            return;
        }

        // Verifica Cruzamento
        if (proximoBloco.isCruzamento() && cruzamentoReservado == null) {
            Cruzamento cruzamento = proximoBloco.getCruzamentoPai();

            // Espera ativa para gerar logs
            while (!cruzamento.tryReservarCaminho()){
                //System.out.println(nome + " AGUARDANDO cruzamento " + cruzamento.getId());
                Thread.sleep(50);
            }

            cruzamentoReservado = cruzamento;
            caminhoReservado = calcularCaminhoNoCruzamento(proximoBloco);
            System.out.println(nome + " RESERVOU cruzamento " + cruzamento.getId());

            proximoBloco = caminhoReservado.remove(0);

        }


        if (proximoBloco.tryOcupar(this)) {
            MalhaBlocos blocoAnterior = posicaoAtual;
            posicaoAtual = proximoBloco;
            blocoAnterior.setCarro(null);

            if (caminhoReservado != null && !posicaoAtual.isCruzamento() && caminhoReservado.isEmpty()) {
                System.out.println(nome + " LIBEROU caminho do cruzamento " + cruzamentoReservado.getId());
                cruzamentoReservado.liberarCaminho();
                this.cruzamentoReservado = null;
                this.caminhoReservado = null;
            }
        }

        if (posicaoAtual.isSaida()) {
            parar();
            posicaoAtual.setCarro(null);
            controle.removerVeiculo(this);
        }
    }

    private MalhaBlocos escolherProximoBloco() {
        List<MalhaBlocos> possiveisMovimentos = new ArrayList<>();
        int i = posicaoAtual.getIdxLinha();
        int j = posicaoAtual.getIdxColuna();
        MalhaBlocos[][] matriz = malha.getMalha();
        Directions d = posicaoAtual.getDirecao();


        if (d.CIMA == 1 && i - 1 >= 0 && matriz[i-1][j].getCarro() == null) possiveisMovimentos.add(matriz[i-1][j]);
        if (d.BAIXO == 1 && i + 1 < matriz.length && matriz[i+1][j].getCarro() == null) possiveisMovimentos.add(matriz[i+1][j]);
        if (d.DIREITA == 1 && j + 1 < matriz[i].length && matriz[i][j+1].getCarro() == null) possiveisMovimentos.add(matriz[i][j+1]);
        if (d.ESQUERDA == 1 && j - 1 >= 0 && matriz[i][j-1].getCarro() == null) possiveisMovimentos.add(matriz[i][j-1]);

        if (possiveisMovimentos.isEmpty()) return null;
        return possiveisMovimentos.get(random.nextInt(possiveisMovimentos.size()));
    }

    /**
     * --- MÉTODO PRINCIPAL REFATORADO E SIMPLIFICADO ---
     * Calcula um caminho aleatório dentro de um cruzamento simplesmente seguindo as
     * direções permitidas de cada bloco e escolhendo aleatoriamente em bifurcações.
     * @param pontoDeEntrada O primeiro bloco do cruzamento onde o carro vai entrar.
     * @return Uma lista de MalhaBlocos representando o caminho traçado.
     */
    private List<MalhaBlocos> calcularCaminhoNoCruzamento(MalhaBlocos pontoDeEntrada) {
        List<MalhaBlocos> caminho = new ArrayList<>();
        MalhaBlocos blocoAtual = pontoDeEntrada;

        // Continua traçando o caminho enquanto estiver dentro do cruzamento
        while (blocoAtual != null && blocoAtual.isCruzamento()) {
            caminho.add(blocoAtual);
            blocoAtual = proximoBlocoDoCaminhoAleatorio(blocoAtual);
        }
        return caminho;
    }

    /**
     * --- NOVO MÉTODO AUXILIAR SIMPLIFICADO ---
     * A partir de um bloco, olha as direções permitidas por ele e escolhe
     * aleatoriamente uma delas para ser o próximo passo.
     * @param bloco O bloco atual no caminho.
     * @return O próximo MalhaBlocos no caminho.
     */
    private MalhaBlocos proximoBlocoDoCaminhoAleatorio(MalhaBlocos bloco) {
        List<MalhaBlocos> opcoes = new ArrayList<>();
        MalhaBlocos[][] matriz = malha.getMalha();
        int i = bloco.getIdxLinha();
        int j = bloco.getIdxColuna();
        Directions d = bloco.getDirecao();

        // Adiciona todos os movimentos permitidos pela direção do bloco atual
        if (d.CIMA == 1 && i - 1 >= 0) opcoes.add(matriz[i - 1][j]);
        if (d.BAIXO == 1 && i + 1 < matriz.length) opcoes.add(matriz[i + 1][j]);
        if (d.DIREITA == 1 && j + 1 < matriz[i].length) opcoes.add(matriz[i][j + 1]);
        if (d.ESQUERDA == 1 && j - 1 >= 0) opcoes.add(matriz[i][j - 1]);

        // Se houver opções, escolhe uma aleatoriamente.
        if (!opcoes.isEmpty()) {
            return opcoes.get(random.nextInt(opcoes.size()));
        }

        return null; 
    }

    public void parar() {
        this.rodando = false;
        this.interrupt();
    }

    public int getIdCarro() { return idCarro; }
    public String getNome() { return nome; }
}