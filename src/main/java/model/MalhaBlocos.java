package model;

import java.util.concurrent.atomic.AtomicReference;
import util.Directions;

public class MalhaBlocos {

    private final int idxLinha;
    private final int idxColuna;
    private final Directions direcao;
    private final boolean isCruzamento;
    private final boolean isSaida;

    // --- MUDANÇA PRINCIPAL: Usar AtomicReference para garantir a movimentação segura sem locks ---
    private final AtomicReference<Carro> carro = new AtomicReference<>(null);

    private boolean reservado = false;
    private Cruzamento cruzamentoPai = null;

    public MalhaBlocos(int idxLinha, int idxColuna, Directions direcao, boolean isCruzamento, boolean isSaida) {
        this.idxLinha = idxLinha;
        this.idxColuna = idxColuna;
        this.direcao = direcao;
        this.isCruzamento = isCruzamento;
        this.isSaida = isSaida;
    }


    public Carro getCarro() {
        return carro.get();
    }

    /**
     * Define o carro neste bloco. Usado para inicialização e para desocupar um bloco.
     */
    public void setCarro(Carro carro) {
        this.carro.set(carro);
    }

    public boolean tryOcupar(Carro carroQueTentaOcupar) {
        // Tenta atomicamente mudar o valor de null para o novo carro.
        // Se o valor atual não for null, a operação falha e retorna false.
        return this.carro.compareAndSet(null, carroQueTentaOcupar);
    }


    public int getIdxLinha() { return idxLinha; }
    public int getIdxColuna() { return idxColuna; }
    public boolean isCruzamento() { return isCruzamento; }
    public Directions getDirecao() { return direcao; }
    public boolean isSaida() { return isSaida; }
    public boolean isReservado() { return reservado; }
    public void setReservado(boolean reservado) { this.reservado = reservado; }
    public Cruzamento getCruzamentoPai() { return cruzamentoPai; }
    public void setCruzamentoPai(Cruzamento cruzamentoPai) { this.cruzamentoPai = cruzamentoPai; }
}