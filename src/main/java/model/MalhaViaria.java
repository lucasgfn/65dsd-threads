package model;

import io.MalhaReader;

import java.io.IOException;

// Model que forma toda a Malha
public class MalhaViaria {
    private final MalhaReader construirMatriz = new MalhaReader();   //Perguntar se eu posso posso ter + malhas ao mesmo tempo?
    private MalhaBlocos[][] malha;
    private final int qntLimiteVeiculos;

    public MalhaViaria(String pathMatriz, int qntLimiteVeiculos) throws IOException {
        this.malha = construirMatriz.lerInstancias(pathMatriz);
        this.qntLimiteVeiculos = qntLimiteVeiculos;
    }

    public MalhaBlocos[][] getMalha() {
        return malha;
    }

    public int getQntLimiteVeiculos() {
        return qntLimiteVeiculos;
    }
}
