package model;

import model.Carro;
import util.Directions;

import java.util.concurrent.Semaphore;

public class MalhaBlocos {

    private Carro carro;
    private boolean entrada;
    private boolean saida;
    private Directions direcao;
    private int idxLinha;
    private int idxColuna;

    private Semaphore semaforo;

    public MalhaBlocos(boolean entrada, boolean saida, Directions direcao, int idxLinha, int idxColuna) {
        this.entrada = entrada;
        this.saida = saida;
        this.direcao = direcao;
        this.idxLinha = idxLinha;
        this.idxColuna = idxColuna;

        // se for cruzamento, cria um semáforo com 1 permissão
        if (isCruzamento()) {
            this.semaforo = new Semaphore(1);
        }
    }

    public Carro getCarro() { return carro; }
    public void setCarro(Carro carro) { this.carro = carro; }
    public boolean isEntrada() { return entrada; }
    public void setEntrada(boolean entrada) { this.entrada = entrada; }
    public boolean isSaida() { return saida; }
    public void setSaida(boolean saida) { this.saida = saida; }
    public Directions getDirecao() { return direcao; }
    public void setDirecao(Directions direcao) { this.direcao = direcao; }
    public int getIdxLinha() { return idxLinha; }
    public void setIdxLinha(int idxLinha) { this.idxLinha = idxLinha; }
    public int getIdxColuna() { return idxColuna; }
    public void setIdxColuna(int idxColuna) { this.idxColuna = idxColuna; }

    public boolean isCruzamento() {
        return (direcao.name().contains("CRUZAMENTO"));
    }

    public Semaphore getSemaphore() {
        return semaforo;
    }

    public void setSemaphore(Semaphore semaforo) {
        this.semaforo = semaforo;
    }
}
