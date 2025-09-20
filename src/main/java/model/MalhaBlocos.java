package model;

import util.Directions;

// Model de construção da Blocos da Malha
public class MalhaBlocos {

    //CARRO
    private Carro carro;

    //RUA
    private boolean entrada;
    private boolean saida;
    private Directions direcao;

    //MATRIZ
    private int idxLinha;
    private int idxColuna;

    public MalhaBlocos(boolean entrada, boolean saida, Directions direcao, int idxLinha, int idxColuna) {
        this.entrada = entrada;
        this.saida = saida;
        this.direcao = direcao;
        this.idxLinha = idxLinha;
        this.idxColuna = idxColuna;
    }

    public MalhaBlocos(Carro carro, boolean entrada, boolean saida, Directions direcao, int idxLinha, int idxColuna) {
        this.carro = carro;
        this.entrada = entrada;
        this.saida = saida;
        this.direcao = direcao;
        this.idxLinha = idxLinha;
        this.idxColuna = idxColuna;
    }

    public Carro getCarro() {
        return carro;
    }

    public void setCarro(Carro carro) {
        this.carro = carro;
    }

    public boolean isEntrada() {
        return entrada;
    }

    public void setEntrada(boolean entrada) {
        this.entrada = entrada;
    }

    public boolean isSaida() {
        return saida;
    }

    public void setSaida(boolean saida) {
        this.saida = saida;
    }

    public Directions getDirecao() {
        return direcao;
    }

    public void setDirecao(Directions direcao) {
        this.direcao = direcao;
    }

    public int getIdxLinha() {
        return idxLinha;
    }

    public void setIdxLinha(int idxLinha) {
        this.idxLinha = idxLinha;
    }

    public int getIdxColuna() {
        return idxColuna;
    }

    public void setIdxColuna(int idxColuna) {
        this.idxColuna = idxColuna;
    }

    public boolean isCruzamento(){
        return (direcao.name().contains("CRUZAMENTO"));
    }

}
