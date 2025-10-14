package model;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import util.Directions;

public class MalhaBlocos {

    private Carro carro;
    private final boolean entrada;
    private final boolean saida;
    private final Directions direcao;
    private final int linha;
    private final int coluna;

    private final boolean usarSemaforo;
    private Semaphore semaphore;
    private final Object monitor = new Object();
    private boolean ocupado = false;

    public MalhaBlocos(boolean entrada, boolean saida, Directions direcao, int linha, int coluna, boolean usarSemaforo) {
        this.entrada = entrada;
        this.saida = saida;
        this.direcao = direcao;
        this.linha = linha;
        this.coluna = coluna;
        this.usarSemaforo = usarSemaforo;

        if (isCruzamento() && usarSemaforo) {
            this.semaphore = new Semaphore(1);
        }
    }

    public synchronized Carro getCarro() { return carro; }
    public synchronized void setCarro(Carro carro) { this.carro = carro; }

    public boolean isEntrada() { return entrada; }
    public boolean isSaida() { return saida; }
    public Directions getDirecao() { return direcao; }
    public int getIdxLinha() { return linha; }
    public int getIdxColuna() { return coluna; }

    public boolean isCruzamento() { return direcao.name().contains("CRUZAMENTO"); }

    public boolean isUsarSemaforo() { return usarSemaforo; }
    public Semaphore getSemaphore() { return semaphore; }

    // Monitor
    public void entrarMonitor() throws InterruptedException {
        synchronized (monitor) {
            while (ocupado) monitor.wait();
            ocupado = true;
        }
    }

    public void sairMonitor() {
        synchronized (monitor) {
            ocupado = false;
            monitor.notify();
        }
    }



}
